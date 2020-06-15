package com.service;

import com.common.ServerResponse;
import com.pojo.User;

import java.util.List;

public interface IManagerUserService {
    public ServerResponse<List<User>> getAllUserInfo();
    public ServerResponse<String> deleteUser(String userId);
}
