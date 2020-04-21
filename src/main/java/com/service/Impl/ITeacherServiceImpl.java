package com.service.Impl;

import com.common.Const;
import com.common.ServerResponse;
import com.dao.ClassMapper;
import com.dao.GroupMapper;
import com.dao.SignMapper;
import com.dao.StudentGroupMapper;
import com.pojo.Group;
import com.pojo.Sign;
import com.pojo.User;
import com.redis.CodeKey;
import com.redis.RedisService;
import com.service.ITeacherService;
import com.util.ClassGroupIdUtil;
import com.vo.UserSign;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

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


    //生成签到
    public ServerResponse<Sign> publishSign(Sign sign){
        int rs = groupMapper.selectByGroupId(sign.getGroupId());
        if (rs == 0){
            return ServerResponse.creatByErrorMessage("该讨论组不存在");
        }
        Timestamp creatTime = new Timestamp(System.currentTimeMillis());
        sign.setCreateTime(creatTime);
        redisService.set(CodeKey.signKey, ""+sign.getGroupId(),sign);
        return ServerResponse.creatBySuccess(sign);
    }


    //获取应该获得签到的学生名单
    private List<String> listCheckUser(int groupId){
        List<String> usersId = studentGroupMapper.selectUserVoByGroupId(groupId);
        return usersId;
    }


    //查看签到的记录
    public ServerResponse<List<UserSign>> getCheckUsers(int groupId){
        List<UserSign> userSign = redisService.get(CodeKey.signKey,""+groupId,List.class);
        return ServerResponse.creatBySuccess(userSign);
    }

    //创建课堂讨论组
    public ServerResponse<Integer> creatGroup(String teacherId, String className,String message){
        int groupId = ClassGroupIdUtil.creatRandom();
        while (!groupIdIsTrue(groupId)){
            groupId = ClassGroupIdUtil.creatRandom();
        }
        int rs = groupMapper.selectGroupByClassName(className);
        System.out.println(rs);
        if (rs > 0){
            return ServerResponse.creatByErrorMessage("课堂已存在");
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

