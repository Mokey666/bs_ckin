package com.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redis.CodeKey;
import com.redis.RedisService;
import org.apache.ibatis.annotations.Insert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{userId}/{groupId}")
public class IWebSocketServiceImpl {

    @Autowired
    private RedisService redisService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**     * 在线人数     */
    public static int onlineNumber = 0;
    /**     * 以用户的姓名为key，WebSocket为对象保存起来     */
    private static Map<Integer,Map<String, IWebSocketServiceImpl>> groupClients = new ConcurrentHashMap<>();
    /**     * 会话     */
    private Session session;
    /**     * 用户名称     */
    private String userId;
    /**     * 建立连接     *     * @param session     */
    private int groupId;

    private static Map<Integer,Integer> groupUsers = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId,@PathParam("groupId") int groupId)    {
        this.userId = userId;
        this.session = session;
        this.groupId = groupId;

        //该组在线人数增加
        if (!groupUsers.containsKey(groupId)){
            int num = 1;
            groupUsers.put(groupId,num);
        }else {
            groupUsers.put(groupId,groupUsers.get(groupId)+1);
        }

        //全部人数怎增加
        addOnlineCount();
        logger.info("现在来连接的客户属于:"+groupId+ "组" + " ID：" + userId);
        logger.info("有新连接加入！ 当前该组在线人数" + groupUsers.get(groupId));

        if (!groupClients.containsKey(groupId)) {
            Map<String, IWebSocketServiceImpl> clients = new ConcurrentHashMap<>();
            clients.put(userId, this);
            groupClients.put(groupId, clients);
        }else {
            if (!groupClients.get(groupId).containsKey(userId)){
                groupClients.get(groupId).put(userId,this);
            }else {
                groupClients.get(groupId).remove(userId);
                groupClients.get(groupId).put(userId,this);
            }
        }

        try {
//            //todo 推送历史信息
//            Map<String, String> message = new HashMap<>();
//            if(redisService.exists(CodeKey.historicRecordKey,""+groupId)) {
//                message = redisService.get(CodeKey.historicRecordKey,""+groupId,Map.class);
//                sendMessageTo(message.toString(), userId);
//            }
            sendMessageAll(userId+"上线了",groupId);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("推送历史信息出现错误");
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误"+error.getMessage());        //error.printStackTrace();
    }

    /**     * 连接关闭     */
    @OnClose
    public void onClose()    {
        groupClients.get(groupId).remove(this);
        groupUsers.put(groupId,groupUsers.get(groupId)-1);
        subOnlineCount();
        logger.info("有连接关闭！ 当前该组在线人数" + groupUsers.get(groupId));
    }

    /**     * 收到客户端的消息     *
     *  * @param message 消息     *
     *  @param session 会话     */
    @OnMessage
    public void onMessage(String message, Session session)    {
        logger.info("来自客户端消息：" + message + "   客户端的id是：" + userId);
        try {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = JSON.parseObject(message);
            //发送者
            String fromUserId = jsonObject.getString("fromUserId");
            //messagetype 1 代表普通消息 2 代表发布签到 3 代表签到
            String messagetype = jsonObject.getString("messagetype");
            //具体消息
            String mess = jsonObject.getString("message");

            //对客户端发来的消息类型进行判断
            if (messagetype.equals("1")){
                map.put("messagetype",1);
                map.put("fromUserId",fromUserId);
                map.put("message",mess);
                //将消息保存
                Map<String , String> historicMessage = redisService.get(CodeKey.historicRecordKey,""+groupId,Map.class);
                historicMessage.put(fromUserId,mess);
                redisService.set(CodeKey.historicRecordKey,""+groupId,historicMessage);
                sendMessageAll(map.toString(),groupId);
            }else if (messagetype.equals("2")){
                map.put("messagetype",2);
                map.put("fromUserId",fromUserId);
                map.put("message",mess);
                sendMessageAll(map.toString(),groupId);
            }else {
                map.put("message",3);
                map.put("fromUserId",fromUserId);
                map.put("message",mess);
                sendMessageAll(message.toString(),groupId);
            }
        } catch (Exception e){
            logger.info("发生了错误了");
        }
    }


    //发送消息给组内有人
    public void sendMessageAll(String message,int groupId) throws IOException {
        for (IWebSocketServiceImpl item : groupClients.get(groupId).values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    //返回在线人数
    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }

    //在线用户增加
    private static synchronized void addOnlineCount(){
       onlineNumber++;
    }

    //在线用户减少
    private static synchronized void subOnlineCount(){
        onlineNumber--;
    }


}
