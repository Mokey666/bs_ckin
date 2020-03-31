package com.service.Impl;

import com.common.Const;
import com.common.ServerResponse;
import com.dao.UserMapper;
import com.pojo.User;
import com.redis.CodeKey;
import com.redis.RedisService;
import com.redis.TokenKey;
import com.service.IUserService;
import com.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;

@Service
public class IUserServiceImpl implements IUserService {

    private static final String TOKEN = "token";

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisService redisService;
    //用户登录
    public ServerResponse<User> login(String userId, String password){
        int result = userMapper.selectById(userId);
        if (result == 0){
            return ServerResponse.creatByErrorMessage("用户不存在");
        }
        String MD5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(userId,MD5password);
        if(user == null){
            return ServerResponse.creatByErrorMessage("密码错误");
        }
        user.setPassword("****");
        return ServerResponse.creatBySuccess("登陆成功",user);
    }
    //注册
    public ServerResponse<String> register(User user){
        ServerResponse serverResponse = this.checkValid(user.getUid(),Const.USERNAME);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        serverResponse = this.checkValid(user.getPhone().toString(),Const.PHONE);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int result = userMapper.insert(user);
        if(result == 0){
            return ServerResponse.creatByErrorMessage("注册失败");
        }
        return ServerResponse.creatBySuccess("注册成功");
    }


    public ServerResponse<String> checkValid(String str, String type){
        if (StringUtils.isBlank(str)){
            return ServerResponse.creatByErrorMessage("传参错误");
        }else{
            if (Const.USERNAME.equals(type)){
                int result = userMapper.selectById(str);
                if (result > 0){
                    return ServerResponse.creatByErrorMessage("用户名已存在");
                }
            }
            if(Const.PHONE.equals(type)){
                if (str.length() != 11){
                    return ServerResponse.creatByErrorMessage("手机号格式错误");
                }
            }
        }
        return ServerResponse.creatBySuccessMessage("校验成功");
    }

    //生成验证码
    public ServerResponse<BufferedImage> creatCheckCode(String userId){
        //检查用户是否存在
        int result = userMapper.selectById(userId);
        if (result == 0){
            return ServerResponse.creatByErrorMessage("用户不存在");
        }
        //生成验证码并放入redis中
        BufferedImage rsbi = creatVerifyCode(userId);
        if (rsbi == null){
            return ServerResponse.creatByErroe();
        }else {
            return ServerResponse.creatBySuccess(rsbi);
        }
    }

    //判断验证码并生成token放入cookie中
    public ServerResponse<String> checkCodeAndCreatToken(HttpServletResponse response, String userId, String code){
        // 检查redis中的验证码是否正确
        String oldCode = redisService.get(CodeKey.codeKey,""+userId,String.class);
        if (!oldCode.equals(code)){
            return ServerResponse.creatByErrorMessage("验证码错误");
        }
        //生成token
        String token = UUID.randomUUID().toString();
        // 将token放入redis
        redisService.set(TokenKey.tokenKey,""+userId,token);
        //将token放入cookie
        addCookie(response,token);
        return ServerResponse.creatBySuccess(token);
    }

    //修改密码
    public ServerResponse<String> modifyPassword(String userId,String token,String newPassword){
        // 检查redis中的token
        String oldToken = redisService.get(TokenKey.tokenKey,""+userId, String.class);
        if (!oldToken.equals(token)){
            return ServerResponse.creatByErrorMessage("token过期或者错误");
        }
        int resultCount = userMapper.modifyPassword(userId,MD5Util.MD5EncodeUtf8(newPassword));
        if (resultCount == 0){
            return ServerResponse.creatByErrorMessage("修改失败");
        }
        return ServerResponse.creatBySuccessMessage("修改成功");
    }

    //在线修改密码
    public ServerResponse<String> restPassword(User user, String oldPassword, String newPassword){
        int result = userMapper.selectByIdAndPassword(user.getUid(),MD5Util.MD5EncodeUtf8(oldPassword));
        if (result == 0){
            return ServerResponse.creatByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int rs = userMapper.updateByPrimaryKeySelective(user);
        if (rs > 0){
            return ServerResponse.creatByErrorMessage("密码修改成功");
        }
        return ServerResponse.creatBySuccessMessage("密码修改失败");
    }

    //更新用户信息
    public ServerResponse<User> updateUserInfo(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getUid());
        if (resultCount > 0){
            return ServerResponse.creatByErrorMessage("该email已存在");
        }
        User updataUser = new User();
        updataUser.setUname(user.getUname());
        updataUser.setUid(user.getUid());
        updataUser.setEmail(user.getEmail());


        int updataCount = userMapper.updateByPrimaryKeySelective(updataUser);
        if (updataCount > 0){
            return ServerResponse.creatBySuccess("更新个人信息成功",updataUser);
        }

        return ServerResponse.creatByErrorMessage("更新个人信息失败");
    }

    //获取用户详细信息
    public ServerResponse<User> getInformation(String userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.creatByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess(user);
    }

    private void addCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(TOKEN,token);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    //生成验证码
    private BufferedImage creatVerifyCode(String userId){

        BufferedImage bi = new BufferedImage(150,30,4);

        Graphics graphics = bi.getGraphics();
        graphics.setColor(new Color(100, 230, 200)); // 使用RGB设置背景颜色
        graphics.fillRect(0, 0, 100, 30); // 填充矩形区域

        // 验证码中所使用到的字符
        char[] codeChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        String captcha = ""; // 存放生成的验证码
        Random random = new Random();

        for(int i = 0; i < 5; i++) { // 循环将每个验证码字符绘制到图片上
            int index = random.nextInt(codeChar.length);
            // 随机生成验证码颜色
            graphics.setColor(new Color(random.nextInt(150), random.nextInt(200), random.nextInt(255)));
            // 将一个字符绘制到图片上，并制定位置（设置x,y坐标）
            graphics.drawString(codeChar[index] + "", (i * 20) + 10, 20);
            captcha += codeChar[index];
        }
        //redis.set(captha);
        //将验证码放到session或者redis中
        System.out.println(captcha);
        boolean rs = redisService.set(CodeKey.codeKey,""+userId,captcha);
        if (rs == false){
            return null;
        }else {
            return bi;
        }
    }

}
