package com.controller;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    IUserService iUserService;

    //用户登录
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String userId, String password, HttpSession session){
        ServerResponse<User> serverResponse = iUserService.login(userId,password);
        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        //todo  根据用户的role 判断重定向到学生或者老师页面。
        return serverResponse;
    }

    //退出登录
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        if (session.getAttribute(Const.CURRENT_USER) == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.creatBySuccess();
    }

    //获取用户信息
    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        return ServerResponse.creatBySuccess(user);
    }

    //用户注册
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }

    //获取验证码
    @RequestMapping(value = "creatCheckCode.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> creatCheckCode(HttpServletResponse response, String userId){
        ServerResponse<BufferedImage> bi = iUserService.creatCheckCode(userId);
        if (bi != null) {
            try {
                OutputStream out = response.getOutputStream();
                ImageIO.write(bi.getData(), "JPG", out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ServerResponse.creatByErroe();

    }

    //检查验证正确并产生token
    @RequestMapping(value = "checkCode.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkCode(String userId, String code){
        return iUserService.checkCodeAndCreatToken(userId,code);
    }

    //忘记密码找回
    @RequestMapping(value = "modifyPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> modifyPassword(String userId, String newPassword, String token){
        return iUserService.modifyPassword(userId,token,newPassword);
    }

    //登录状态找回密码
    @RequestMapping(value = "restPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> restPassword(HttpSession session, String newPassword, String oldPassword){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        return iUserService.restPassword(user,oldPassword,newPassword);
    }

    //更新用户信息
    @RequestMapping(value = "updateUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        user.setUid(currentUser.getUid());
        user.setUname(currentUser.getUname());
        user.setRole(currentUser.getRole());

        ServerResponse<User> serverResponse = iUserService.updateUserInfo(user);
        if (serverResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,user);
        }
        return serverResponse;
    }

    //获取用户具体信息
    @RequestMapping(value = "get_user_Info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_user_Info(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要要强制登陆");
        }
        return iUserService.getInformation(user.getUid());
    }
}
