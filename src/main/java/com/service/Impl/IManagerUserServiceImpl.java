package com.service.Impl;

import com.common.ServerResponse;
import com.dao.UserMapper;
import com.pojo.User;
import com.service.IManagerUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IManagerUserServiceImpl implements IManagerUserService {

    UserMapper userMapper;

    public ServerResponse<List<User>> getAllUserInfo(){
        List<User> users = userMapper.getAllUser();

        return ServerResponse.creatBySuccess(users);
    }

    public ServerResponse<String> deleteUser(String userId){
        int rs = userMapper.deleteByUserId(userId);
        if (rs > 0){
            return ServerResponse.creatBySuccessMessage("删除成功");
        }
        return ServerResponse.creatByErrorMessage("删除失败");
    }

}
