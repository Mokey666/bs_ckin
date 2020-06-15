package com.controller;

import com.baidu.aip.util.Base64Util;
import com.common.Const;
import com.common.ServerResponse;
import com.pojo.Group;
import com.pojo.Location;
import com.pojo.User;
import com.service.IStudentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/student/")
public class StudentController {

    @Autowired
    IStudentService iStudentService;

    //加入讨论组
    //url：/student/join_class_group.do
    //通过组号来加入讨论组并返回讨论组的信息
    @ResponseBody
    @RequestMapping(value = "join_class_group.do",method = RequestMethod.POST)
    public ServerResponse<Group> joinClassGroup(HttpSession session, Integer groupId){
        if (groupId == null){
            return ServerResponse.creatByErrorMessage("请输入讨论组组号");
        }
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 1){
            return ServerResponse.creatByErrorMessage("用户没有权限");
        }
        return iStudentService.joinClassGroup(user.getUid(), groupId);
    }

    //获取学生加入的所有讨论组
    //url：/student/get_all_group.do
    //根据学生id获取该学生参加的所有讨论组
    @ResponseBody
    @RequestMapping(value = "get_all_group.do",method = RequestMethod.POST)
    public ServerResponse<List<Group>> getAllGroup(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 1){
            return ServerResponse.creatByErrorMessage("用户没有权限");
        }
        return iStudentService.getAllGroup(user.getUid());
    }

    //定位签到
    //这里可以把Location信息放到session中
    //url:  /student/join_check_location.do
    //通过传入学生位置信息和组号，来进行定位签到。
    @ResponseBody
    @RequestMapping(value = "join_check_location.do",method = RequestMethod.POST)
    public ServerResponse<String> joinCheckByLocation(Double longitude, Double latitude, HttpSession session, Integer groupId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 1){
            return ServerResponse.creatByErrorMessage("用户没有权限");
        }
        return iStudentService.joinCheckByLocation(longitude,latitude,user.getUid(),groupId);
    }

    //人脸识别签到
    //url: /student/join_check_face.do
    //通过前端传过来的人脸照片信息（文件的形式）和组号 来进行定位签到
    @ResponseBody
    @RequestMapping(value = "join_check_face.do", method = RequestMethod.POST)
    public ServerResponse<String> joinCheckByFace(MultipartFile image, HttpSession session, Integer groupId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 1){
            return ServerResponse.creatByErrorMessage("用户没有权限");
        }
        if (image == null){
            return ServerResponse.creatByErrorMessage("请上传照片");
        }

        byte[] bt = null;
        try {
            bt = image.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String rsImage = Base64Util.encode(bt);

        return iStudentService.joinCheckByFace(rsImage,user.getUid(),groupId);
    }

}
