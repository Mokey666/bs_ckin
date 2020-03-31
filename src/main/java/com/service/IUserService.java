package com.service;

import com.common.ServerResponse;
import com.pojo.User;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

public interface IUserService {
    public ServerResponse<User> login(String userId, String password);
    public ServerResponse<String> register(User user);
    public ServerResponse<String> modifyPassword(String userId, String token, String newPassword);
    public ServerResponse<BufferedImage> creatCheckCode(String userId);
    public ServerResponse<String> checkValid(String str, String type);
    public ServerResponse<String> checkCodeAndCreatToken(HttpServletResponse response, String userId, String code);
    public ServerResponse<String> restPassword(User user, String oldPassword, String newPassword);
    public ServerResponse<User> updateUserInfo(User user);
    public ServerResponse<User> getInformation(String userId);
}
