package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.util.Base64Util;
import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/face/")
public class FaceController {

    @Autowired
    IFaceService iFaceService;
    //人脸注册
    //url:/face/face_register.do
    //通过上传人脸信息（文件形式）进行当前用户的人脸信息注册.
    @ResponseBody
    @RequestMapping(value = "face_register.do", method = RequestMethod.POST)
    public ServerResponse<JSONObject> faceRegister(HttpSession session, MultipartFile image){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if(image == null){
            return ServerResponse.creatBySuccessMessage("未上传照片");
        }

        byte[] bt = null;
        try {
            bt = image.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String rsImage = Base64Util.encode(bt);
        return iFaceService.faceRegister(rsImage,user.getUid(),user.getUname());
    }

    //人脸信息更新
    //url:/face/face_update.do
    //通过上传人脸信息（文件形式）进行当前用户的人脸信息更新.
    @ResponseBody
    @RequestMapping(value = "face_update.do", method = RequestMethod.POST)
    public ServerResponse<JSONObject> faceUpdate(HttpSession session, MultipartFile image){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if(image == null){
            return ServerResponse.creatByErrorMessage("未上传照片");
        }

        byte[] bt = null;
        try {
            bt = image.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String rsImage = Base64Util.encode(bt);

        return iFaceService.faceUpdate(user.getUname(),rsImage,user.getUid());
    }


    //人脸删除
    //url:/face/face_delete.do
    //通过id进行人脸信息删除
    @ResponseBody
    @RequestMapping(value = "face_delete.do", method = RequestMethod.POST)
    public ServerResponse<String> faceDelete(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }

        return iFaceService.faceDelete(user.getUid());
    }


    @ResponseBody
    @RequestMapping(value = "test.do", method = RequestMethod.POST)
    public ServerResponse<String> test(MultipartFile file){
        if(file == null){
            return ServerResponse.creatByErrorMessage("失败");
        }
        return ServerResponse.creatBySuccessMessage("成功");
    }

}
