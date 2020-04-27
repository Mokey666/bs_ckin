package com.common;


import com.baidu.aip.face.AipFace;

public class BaiduFaceAPI {
    //设置APPID/AK/SK
    private static final String APP_ID = "18654088";
    private static final String API_KEY = "Xh8PbQLhvGNsn4Mm1TH3eN6O";
    private static final String SECRET_KEY = "HD85ibgbNLjcKAWo1cCb4RLL6R1AFDce";
    //定义AipFace
    private static AipFace client = null;
    /**
     * * 构造函数，实例化AipFace
     *  */
//    public static AipFace BaiduFaceAPI(){
//        client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
//        // 可选：设置网络连接参数
//        client.setConnectionTimeoutInMillis(2000);//建立连接的超时时间
//        client.setSocketTimeoutInMillis(60000);//通过打开的连接传输数据的超时时间（单位：毫秒）
//        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
//
//        return client;
//    }

    public static AipFace getClient(){
        client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
//        client.setConnectionTimeoutInMillis(2000);//建立连接的超时时间
//        client.setSocketTimeoutInMillis(60000);
        return client;
    }

}
