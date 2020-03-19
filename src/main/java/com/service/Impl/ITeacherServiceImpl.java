package com.service.Impl;

import com.common.Const;
import com.common.ServerResponse;
import com.dao.ClassMapper;
import com.pojo.Sign;
import com.pojo.User;
import com.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

public class ITeacherServiceImpl implements ITeacherService {

    @Autowired
    ClassMapper classMapper;
    //todo 生成签到
    public ServerResponse<String> publishSign(Sign sign, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }
        if (user.getRole() != 2){
            return ServerResponse.creatByErrorMessage("用户没有权限");
        }
        sign.setTeacherId(user.getUid());
        Timestamp creatTime = new Timestamp(System.currentTimeMillis());
        sign.setCreateTime(creatTime);
        String className = classMapper.selectClassNameById(sign.getClassId());
        sign.setClassName(className);
        return null;
    }
}
