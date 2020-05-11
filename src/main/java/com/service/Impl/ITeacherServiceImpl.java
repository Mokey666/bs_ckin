package com.service.Impl;

import com.common.ServerResponse;
import com.dao.ClassMapper;
import com.dao.GroupMapper;
import com.dao.SignMapper;
import com.dao.StudentGroupMapper;
import com.pojo.Group;
import com.pojo.Sign;
import com.redis.CodeKey;
import com.redis.RedisService;
import com.service.ITeacherService;
import com.util.ClassGroupIdUtil;
import com.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ITeacherServiceImpl implements ITeacherService {

    @Autowired
    ClassMapper classMapper;
    @Autowired
    SignMapper signMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    StudentGroupMapper studentGroupMapper;

    public CodeKey signKey;

    //发布签到
    public ServerResponse<Sign> publishSign(Sign sign){
        int rs = groupMapper.selectByGroupId(sign.getGroupId());
        if (rs == 0){
            return ServerResponse.creatByErrorMessage("该讨论组不存在");
        }
        Timestamp creatTime = new Timestamp(System.currentTimeMillis());
        sign.setCreateTime(creatTime);
        //将签到信息放入redis中
      //   signKey = new CodeKey(sign.getLimitTime()*60,"sign");
        redisService.set(CodeKey.signKey, ""+sign.getGroupId(),sign);
        //创建一个签到记录表
        Map<String, Integer> map = new HashMap<>();
        for (String x : listCheckUser(sign.getGroupId())){
            map.put(x,0);
        }

        //将签到表放入redis
        redisService.set(CodeKey.singsKey,""+sign.getGroupId(),map);
        return ServerResponse.creatBySuccess(sign);
    }


    //获取应该获得签到的学生名单
    private List<String> listCheckUser(int groupId){
        List<String> usersId = studentGroupMapper.selectUserVOByGroupId(groupId);
        return usersId;
    }

    //查看签到的记录
    public ServerResponse<Map<String,Integer>> getCheckUsers(int groupId){
        Map<String, Integer> map = new HashMap<>();
        map = redisService.get(CodeKey.singsKey,""+groupId,Map.class);
        return ServerResponse.creatBySuccess(map);
    }

    //创建课堂讨论组
    public ServerResponse<Integer> creatGroup(String teacherId, String className,String message){
        if (StringUtils.isEmpty(className)){
            return ServerResponse.creatByErrorMessage("课程名称不能为空");
        }
        int rs = groupMapper.selectGroupByClassName(className);

        if (rs > 0){
            return ServerResponse.creatByErrorMessage("课堂已存在");
        }

        int groupId = ClassGroupIdUtil.creatRandom();
        while (!groupIdIsTrue(groupId)){
            groupId = ClassGroupIdUtil.creatRandom();
        }
        int result = groupMapper.insert(teacherId,className,groupId,message);
        if (result>0){
            return ServerResponse.creatBySuccess("讨论组生成成功",groupId);
        }
        return ServerResponse.creatByErrorMessage("讨论组生成失败");
    }

    //查看所有讨论组
    public ServerResponse<List<Group>> getAllgroupId(String teacherId){
        List<Group> groups = groupMapper.selectAllGroupByTeacherId(teacherId);
        return ServerResponse.creatBySuccess(groups);
    }

    private boolean groupIdIsTrue(int groupId){
        int rs = groupMapper.selectByGroupId(groupId);
        if (rs > 0){
            return false;
        }
        return true;
    }
}

