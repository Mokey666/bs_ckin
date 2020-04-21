package com.service;

import com.common.ServerResponse;
import com.pojo.Group;
import com.pojo.Sign;
import com.vo.UserSign;

import javax.servlet.http.HttpSession;
import java.util.List;


public interface ITeacherService {
    public ServerResponse<Integer> creatGroup(String teacherId, String className,String message);
    public ServerResponse<Sign> publishSign(Sign sign);
    public ServerResponse<List<UserSign>> getCheckUsers(int groupId);
    public ServerResponse<List<Group>> getAllgroupId(String teacherId);
}
