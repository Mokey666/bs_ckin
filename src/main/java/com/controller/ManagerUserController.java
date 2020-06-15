package com.controller;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IManagerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manager/")
public class ManagerUserController {

    @Autowired
    IManagerUserService iManagerUserService;

    @ResponseBody
    @RequestMapping(value = "get_all_user_info.do", method = RequestMethod.GET)
    public ServerResponse<List<User>> getAllUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() == null || user.getRole() != 0){
            return ServerResponse.creatByErrorMessage("用户无权限");
        }

        return iManagerUserService.getAllUserInfo();
    }


    @ResponseBody
    @RequestMapping(value = "delete_user.do", method = RequestMethod.POST)
    public ServerResponse<String> deleteUser(HttpSession session, String userId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() == null || user.getRole() != 0){
            return ServerResponse.creatByErrorMessage("用户无权限");
        }

        return iManagerUserService.deleteUser(userId);
    }
}
