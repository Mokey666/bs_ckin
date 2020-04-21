package com.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redis.CodeKey;
import com.redis.RedisService;
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
@ServerEndpoint("/websocket/{groupId}/{userId}")
public class IWebSocketServiceImpl {
    @Autowired
    private RedisService redisService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**     * 在线人数     */
    public static int onlineNumber = 0;
    /**     * 以用户的姓名为key，WebSocket为对象保存起来     */
    private static Map<String, IWebSocketServiceImpl> clients = new ConcurrentHashMap<String, IWebSocketServiceImpl>();
    /**     * 会话     */
    private Session session;
    /**     * 用户名称     */
    private String userId;
    /**     * 建立连接     *     * @param session     */
    private int groupId;



    @OnOpen
    public void onOpen(@PathParam("userId")String userId, Session session,@PathParam("groupId") int groupId)    {
        this.userId = userId;
        this.session = session;
        this.groupId = groupId;
        logger.info("现在来连接的客户id："+session.getId()+"用户名："+userId);
        logger.info("有新连接加入！ 当前在线人数" + onlineNumber);

        if (clients.containsKey(userId)){
            clients.remove(userId);
            clients.put(userId,this);
        }else {
            clients.put(userId, this);
        }
        addOnlineCount();
        try {
            //todo 推送历史信息
            Map<String, String> message = redisService.get(CodeKey.historicRecordKey,""+groupId,Map.class);
            sendMessageTo(message.toString(),userId);
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
        if (clients.containsKey(userId)){
            clients.remove(userId);
            subOnlineCount();
        }else {
            logger.info("该用户ID不存在");
        }
        logger.info("有连接关闭！ 当前在线人数" + onlineNumber);
    }

    /**     * 收到客户端的消息     *
     *  * @param message 消息     *
     *  @param session 会话     */
    @OnMessage
    public void onMessage(String message, Session session)    {
        logger.info("来自客户端消息：" + message+"客户端的id是："+session.getId());
        try {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = JSON.parseObject(message);
            //发送者
            String fromUserId = jsonObject.getString("fromUserId");
            //messagetype 1 代表普通消息 2 代表发布签到 3 代表签到 4 代表上传文件 5 代表下载文件
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
                sendMessageAll(map.toString(),fromUserId);
            }else if (messagetype.equals("2")){
                map.put("messagetype",2);
                map.put("fromUserId",fromUserId);
                map.put("message",mess);
                sendMessageAll(map.toString(),fromUserId);
            }else {
                map.put("message",3);
                map.put("fromUserId",fromUserId);
                map.put("message",mess);
                sendMessageAll(message.toString(),fromUserId);
            }

            //判断是否文件

        } catch (Exception e){
            logger.info("发生了错误了");
        }
    }

    //

    //发送消息给个人，可以不用
    public void sendMessageTo(String message, String ToUserId) throws IOException {
        for (IWebSocketServiceImpl item : clients.values()) {
            if (item.userId.equals(ToUserId) ) {
                item.session.getAsyncRemote().sendText(message);
                break;
            }
        }
    }

    //发送消息给所有人
    public void sendMessageAll(String message,String FromUserName) throws IOException {
        for (IWebSocketServiceImpl item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    //返回在线人数
    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }

    //在线用户增加
    private static synchronized void addOnlineCount(){
       onlineNumber--;
    }

    //在线用户减少
    private static synchronized void subOnlineCount(){
        onlineNumber--;
    }
}
