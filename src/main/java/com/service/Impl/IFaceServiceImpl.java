package com.service.Impl;

import com.baidu.aip.util.Base64Util;
import com.common.ServerResponse;
import com.dao.UserMapper;
import com.redis.CodeKey;
import com.redis.RedisService;
import com.service.IFaceService;
import com.util.FaceUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IFaceServiceImpl implements IFaceService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisService iRedisServer;

    //人脸注册
    public ServerResponse<JSONObject> faceRegister(byte[] image, String userId,String usernmae){
        //将byte[] 转换为 string
        String rsimage = Base64Util.encode(image);
        //检测用户是否已经注册人脸信息
        JSONObject rs = FaceUtil.faceSelect(userId);
        String result = rs.get("error_code").toString();
        if (!result.equals("223103")){
            return ServerResponse.creatByErrorMessage("用户人脸信息已存在");
        }

        //检测是否用和用户人脸信息相似的人脸
        JSONObject result2 = FaceUtil.faceSearch(rsimage);
        String str = result2.get("result").toString();
        if (!str.equals("null")){
            JSONObject jsonObject = result2.getJSONObject("result");
            JSONArray jsonArray = jsonObject.getJSONArray("user_list");
            for (int i = 0; i < jsonArray.length(); i++){
                if (jsonArray != null) {
                    Double score = jsonArray.getJSONObject(i).getDouble("score");
                    if (score > 80.0) {
                        return ServerResponse.creatByErrorMessage("存在高度相似的人脸信息导致注册失败");
                    }
                }
            }

        }
        // 人脸注册
        JSONObject res = FaceUtil.faceRegister(rsimage,userId,usernmae);
        String face_token = res.getJSONObject("result").get("face_token").toString();

        iRedisServer.set(CodeKey.faceKey,""+userId,face_token);
        return ServerResponse.creatBySuccess(res);
    }

    //人脸搜索
    public ServerResponse<JSONObject> faceSearch(byte[] image){
        String rsImage = Base64Util.encode(image);
        JSONObject res = FaceUtil.faceSearch(rsImage);

        String str = res.get("result").toString();
        if (!str.equals("null")){
            JSONObject jsonObject = res.getJSONObject("result");
            JSONArray jsonArray = jsonObject.getJSONArray("user_list");
            for (int i = 0; i < jsonArray.length(); i++){
                if (jsonArray != null) {
                    Double score = jsonArray.getJSONObject(i).getDouble("score");
                    if (score > 80.0) {
                        return ServerResponse.creatBySuccess("搜索成功",res);
                    }
                }
            }
        }
        return ServerResponse.creatByErrorMessage("为搜索到用户人脸信息");
    }

    //人脸信息更新
    //若是没有该用户的人脸信息，则直接注册该用户
    public ServerResponse<JSONObject> faceUpdate(String username,byte[] image,String userId){

        //更新用户人脸信息，若是没有直接注册.
        JSONObject res = FaceUtil.faceUpdate(username,Base64Util.encode(image),userId);
        if (res == null){
            return ServerResponse.creatByErrorMessage("更新失败");
        }
        return ServerResponse.creatBySuccess(res);
    }

    //删除人脸信息
    public ServerResponse<String> faceDelete(String userId){
        //获取faceToken
        String faceToken = iRedisServer.get(CodeKey.faceKey,""+userId,String.class);

        String rs = FaceUtil.faceDelete(userId,faceToken).get("error_code").toString();
        if (!rs.equals("0")){
            if (FaceUtil.faceSelect(userId).get("error_code").toString().equals("223103")){
                return ServerResponse.creatByErrorMessage("用户人脸信息不存在");
            }else {
                return ServerResponse.creatByErrorMessage("faceToken失效或者不存在");
            }
        }
        return ServerResponse.creatBySuccess("删除成功");
    }
}
