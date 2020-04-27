package com.controller;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IFaceService;
import org.json.JSONObject;
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
        if (user.getRole() != 1){
            return ServerResponse.creatByErrorMessage("用户无权限");
        }

        byte[] rsImage = null;
        try {
            rsImage = image.getBytes();
        }catch (IOException e){
            e.printStackTrace();
        }

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
        if (user.getRole() != 1){
            return ServerResponse.creatByErrorMessage("用户无权限");
        }

        byte[] rsImage = null;
        try {
            rsImage = image.getBytes();
        }catch (IOException e){
            e.printStackTrace();
        }

        return iFaceService.faceUpdate(user.getUname(),rsImage,user.getUid());
    }


    //人脸删除
    //url:/face/face_delete.do
    //通过id进行人脸信息删除
    @ResponseBody
    @RequestMapping(value = "face_delete.do", method = RequestMethod.POST)
    public ServerResponse<String> faecDelete(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 1){
            return ServerResponse.creatByErrorMessage("用户无权限");
        }

        return iFaceService.faceDelete(user.getUid());
    }
}
