package com.service.Impl;

import com.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{userId}/{groupId}")
public class IWebSocketServiceImpl {

    @Autowired
    private RedisService redisService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static int onlineNumber = 0;

    private static Map<Integer,Map<String, IWebSocketServiceImpl>> groupClients = new ConcurrentHashMap<>();

    private Session session;

    private String userId;

    private int groupId;


    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId,@PathParam("groupId") int groupId)    {
        this.userId = userId;
        this.session = session;
        this.groupId = groupId;

        logger.info(userId + "加入" + groupId);

        if (!groupClients.containsKey(groupId)) {
            Map<String, IWebSocketServiceImpl> clients = new ConcurrentHashMap<>();
            clients.put(userId, this);
            groupClients.put(groupId, clients);
        }else {
            if (!groupClients.get(groupId).containsKey(userId)){
                groupClients.get(groupId).put(userId,this);
            }else{
                groupClients.get(groupId).remove(userId);
                groupClients.get(groupId).put(userId,this);
            }
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误"+error.getMessage());        //error.printStackTrace();
    }

    /**     * 连接关闭     */
    @OnClose
    public void onClose()    {
        //移除这个连接
        if (groupClients.get(groupId).containsKey(userId)) {
            groupClients.get(groupId).remove(userId);
            subOnlineCount();
        }
    }

    /**     * 收到客户端的消息     *
     *  * @param message 消息     *
     *  @param session 会话     */

    @OnMessage
    public void onMessage(String message, Session session)    {
        logger.info("来自客户端消息：" + message + "   客户端的id是：" + userId);
        String rs = userId + ":" + message;
        try {
            sendMessageAll(rs,groupId);
        } catch (IOException e) {
            logger.info("发生错误");
            e.printStackTrace();
        }

    }


    //发送消息给组内有人
    public void sendMessageAll(String message,int groupId) throws IOException {
        logger.info("返回：" + message);
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
