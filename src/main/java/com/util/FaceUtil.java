package com.util;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;
import com.common.BaiduFaceAPI;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class FaceUtil {

    private static AipFace client = BaiduFaceAPI.getClient();
    //人脸注册
    public static JSONObject faceRegister(String image,String userId,String user_info){
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_info",user_info);
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("action_type", "REPLACE");

        String imageType = "BASE64";
        String groupId = "group_id0";

        // 人脸注册
        JSONObject res = client.addUser(image, imageType, groupId, userId, options);
        return res;
    }

    //人脸更新
    public static JSONObject faceUpdate(String user_info,String image,String userId){
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_info", user_info);
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("action_type", "REPLACE");

        String imageType = "BASE64";
        String groupId = "group_id0";


        // 人脸更新
        JSONObject res = client.updateUser(image, imageType, groupId, userId, options);
        return res;
    }

    //人脸删除
    public static JSONObject faceDelete(String userId,String faceToken) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        String groupId = "group_id0";

        // 人脸删除
        JSONObject res = client.faceDelete(userId, groupId, faceToken, options);
        return res;
    }

    //用户信息查询
    public static JSONObject faceSelect(String userId) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        String groupId = "group_id0";

        // 用户信息查询
        JSONObject res = client.getUser(userId, groupId, options);
        return res;
    }
    //人脸检测
    public static JSONObject faceCheck(String image) {

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "10");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        String imageType = "BASE64";

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        return res;
    }

    //人脸搜索
    public static JSONObject faceSearch(String image) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();


        options.put("max_face_num", "1");
        options.put("match_threshold", "80");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("user_id", null);
        //返回score最高的那个人
        options.put("max_user_num", "1");


        String imageType = "BASE64";
        String groupIdList = "group_id0";

        // 人脸搜索
        JSONObject res = client.search(image, imageType, groupIdList, options);
        return res;
    }

    public static JSONObject faceSearch(String image,String userId) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();


        options.put("max_face_num", "1");
        options.put("match_threshold", "80");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("user_id", userId);
        //返回score最高的那个人
        options.put("max_user_num", "1");


        String imageType = "BASE64";
        String groupIdList = "group_id0";

        // 人脸搜索
        JSONObject res = client.search(image, imageType, groupIdList, options);
        return res;
    }

    @Test
    public void test() {
        BaiduFaceAPI baiduFaceAPI = new BaiduFaceAPI();
        AipFace aipFace = baiduFaceAPI.getClient();
        File file = new File("C:\\Users\\侯泽明\\Desktop\\1.jpg");
        FileInputStream is = null;
        String str = null;
        try{
            is = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            is.read(buffer);
            is.close();
            str = Base64Util.encode(buffer);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //faceRegister(str,"03161330","hzm");
        JSONObject jsonObject = FaceUtil.faceSearch(str);
        double score = jsonObject.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getDouble("score");
        System.out.println(jsonObject.toString(2));
        System.out.println(score);
//        JSONObject rs = FaceUtil.faceSearch(str);
//        System.out.println(rs.getString("result"));
//        if (rs.get("result").equals("null")) {
//            System.out.println("1");
//        }
       // System.out.println(rs.getJSONObject("user_list"));

    }
}
