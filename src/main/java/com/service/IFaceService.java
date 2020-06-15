package com.service;

import com.common.ServerResponse;
import com.alibaba.fastjson.JSONObject;

public interface IFaceService {
    public ServerResponse<JSONObject> faceRegister(String image64, String userId, String username);
    public ServerResponse<String> faceDelete(String userId);
    public ServerResponse<JSONObject> faceSearch(String image64);
    public ServerResponse<JSONObject> faceUpdate(String username,String image64,String userId);
}
