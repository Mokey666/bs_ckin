package com.service;

import com.common.ServerResponse;
import org.json.JSONObject;

public interface IFaceService {
    public ServerResponse<JSONObject> faceRegister(byte[] image, String userId,String username);
    public ServerResponse<String> faceDelete(String userId);
    public ServerResponse<JSONObject> faceSearch(byte[] image);
    public ServerResponse<JSONObject> faceUpdate(String username,byte[] image,String userId);
}
