package com.common;


import com.baidu.aip.face.AipFace;

public class BaiduFaceAPI {
    //设置APPID/AK/SK
    private static final String APP_ID = "18654088";
    private static final String API_KEY = "Xh8PbQLhvGNsn4Mm1TH3eN6O";
    private static final String SECRET_KEY = "HD85ibgbNLjcKAWo1cCb4RLL6R1AFDce";
    //定义AipFace
    private static AipFace client = null;
    public static AipFace getClient(){
        if(client == null) {
            client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
            return client;
        }
        return client;
    }
}
