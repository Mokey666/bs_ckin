package com.service;

import com.common.ServerResponse;
import com.pojo.Group;
import com.pojo.Sign;
import com.vo.UserVO;

import java.util.List;
import java.util.Map;


public interface ITeacherService {
    public ServerResponse<Integer> creatGroup(String teacherId, String className,String message);
    public ServerResponse<Sign> publishSign(Sign sign);
    public ServerResponse<Map<UserVO,Integer>> getCheckUsers(int groupId);
    public ServerResponse<List<Group>> getAllgroupId(String teacherId);
}
