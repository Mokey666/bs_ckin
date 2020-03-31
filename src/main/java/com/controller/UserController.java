package com.controller;

import com.common.Const;
import com.common.ResponseCode;
import com.common.ServerResponse;
import com.pojo.User;
import com.redis.CodeKey;
import com.redis.RedisService;
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
    @Autowired
    RedisService redisService;

    //用户登录 url:  /user/login.do
    //通过获取用户ID(学号或者账号)和密码来进行登录
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String userId, String password, HttpSession session){
        ServerResponse<User> serverResponse = iUserService.login(userId,password);
        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        //todo  根据用户的role 判断重定向到学生或者老师页面。
//        User user = serverResponse.getData();
//        if (user.getRole() > 1 || user.getRole() < 0 ){
//            return ServerResponse.creatByErrorMessage("用户信息非法");
//        }
//        if (user.getRole() == 1){ //代表学生
//            return null; //跳转到学生页面
//        }
        return serverResponse;//跳转到老师页面
    }

    //退出登录 url：/user/logout.do
    //不需要获取什么
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        if (session.getAttribute(Const.CURRENT_USER) == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.creatBySuccess();
    }

    //获取用户信息 url：/user/getUserInfo.do
    //不需要获取什么
    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        return ServerResponse.creatBySuccess(user);
    }

    //用户注册 url：/user/register.do
    //通过获取用户信息（用户账号（学号），密码，email，手机号，性别，权限（老师为2，学生为1，管理员0），头像图片（这个需要一个默认的图片））进行用户注册
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    //这个不需要
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }

    //获取验证码 url：/user/creatCheckCode.do
    //在用户忘记密码的时候，通过用户的账号（学号或者工号）来生成一个验证码，展示到前端页面。
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

    //检查验证正确并产生token url：/user/checkCode.do
    //忘记密码的部分：这里通过用户Id和验证码生成一个token码，返回给后台
    @RequestMapping(value = "checkCode.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkCode(HttpServletResponse response, String userId, String code){
        return iUserService.checkCodeAndCreatToken(response,userId,code);
    }

    //忘记密码找回 url：/user/modifyPassword.do
    //传入信息（验证码输入正确后产生的token、用户ID，和新的密码）给后台来进行密码的修改
    @RequestMapping(value = "modifyPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> modifyPassword(String userId, String newPassword, String token){
        return iUserService.modifyPassword(userId,token,newPassword);
    }

    //登录状态找回密码 url：/user/restPassword.do
    //在登录状态下修改密码：只需要传入旧密码和新密码 进行修改密码
    @RequestMapping(value = "restPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> restPassword(HttpSession session, String newPassword, String oldPassword){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        return iUserService.restPassword(user,oldPassword,newPassword);
    }

    //更新用户信息 url：/user/updateUserInfo.do
    //用户信息的更改：获取用户修改的信息（密码，email，手机号，性别，头像图片）来进行用户信息的修改
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

    //获取用户具体信息 url：/user/get_user_Info.do
    //获取当前登录用户的信息
    @RequestMapping(value = "get_user_Info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_user_Info(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要要强制登陆");
        }
        return iUserService.getInformation(user.getUid());
    }


    @RequestMapping(value = "redis.do", method = RequestMethod.GET)
    @ResponseBody
    public Boolean test(){
        return redisService.set(CodeKey.codeKey,"hzm",1);
    }
}
