package com.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.ServerResponse;
import com.dao.UserMapper;
import com.redis.CodeKey;
import com.redis.RedisService;
import com.service.IFaceService;
import com.util.FaceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IFaceServiceImpl implements IFaceService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisService iRedisServer;

    //人脸注册
    public ServerResponse<JSONObject> faceRegister(String image64, String userId,String usernmae){
        //检测用户是否已经注册人脸信息
        JSONObject rs = FaceUtil.faceSelect(userId);
        String result = rs.get("error_code").toString();
        if (!result.equals("223103")){
            return ServerResponse.creatByErrorMessage("用户人脸信息已存在");
        }

        //检测是否用和用户人脸信息相似的人脸
        JSONObject result2 = FaceUtil.faceSearch(image64);
        Object str = result2.get("result");
        if (str != null){
            JSONObject jsonObject = result2.getJSONObject("result");
            JSONArray jsonArray = jsonObject.getJSONArray("user_list");
            Double score = jsonArray.getJSONObject(0).getDouble("score");
            if (score > 80.0) {
                return ServerResponse.creatByErrorMessage("存在高度相似的人脸信息导致注册失败");
            }
        }
        // 人脸注册
        JSONObject res = FaceUtil.faceRegister(image64,userId,usernmae);
        if (res == null){
            return ServerResponse.creatByErrorMessage("服务器出现异常");
        }
        String face_token = res.getJSONObject("result").get("face_token").toString();
        iRedisServer.set(CodeKey.faceKey,""+userId,face_token);
        return ServerResponse.creatBySuccess(res);
    }

    //人脸搜索
    public ServerResponse<JSONObject> faceSearch(String image64){
        //String rsImage = Base64Util.encode(image64);
        JSONObject res = FaceUtil.faceSearch(image64);

        String str = res.get("result").toString();
        if (!str.equals("null")){
            JSONObject jsonObject = res.getJSONObject("result");
            JSONArray jsonArray = jsonObject.getJSONArray("user_list");
            Double score = jsonArray.getJSONObject(0).getDouble("score");
            if (score > 80.0) {
                return ServerResponse.creatBySuccess("搜索成功",res);
            }
        }
        return ServerResponse.creatByErrorMessage("为搜索到用户人脸信息");
    }

    //人脸信息更新
    //若是没有该用户的人脸信息，则直接注册该用户
    public ServerResponse<JSONObject> faceUpdate(String username,String image64,String userId){
        //更新用户人脸信息，若是没有直接注册.
        JSONObject res = FaceUtil.faceUpdate(username,image64,userId);
        if (res == null){
            return ServerResponse.creatByErrorMessage("服务器出现异常");
        }
        String face_token = res.getJSONObject("result").get("face_token").toString();
        iRedisServer.set(CodeKey.faceKey,""+userId,face_token);
        if (res == null){
            return ServerResponse.creatByErrorMessage("更新失败");
        }
        return ServerResponse.creatBySuccess(res);
    }

    //删除人脸信息
    public ServerResponse<String> faceDelete(String userId){
        //获取faceToken
        String faceToken = iRedisServer.get(CodeKey.faceKey,""+userId,String.class);
        String rs = FaceUtil.faceDelete(userId,faceToken).getString("error_code");
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
