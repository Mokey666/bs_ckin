package com.service.Impl;

import com.baidu.aip.util.Base64Util;
import com.common.Allocation;
import com.common.ServerResponse;
import com.dao.GroupMapper;
import com.dao.StudentGroupMapper;
import com.pojo.Group;
import com.pojo.Location;
import com.pojo.Sign;
import com.redis.CodeKey;
import com.redis.RedisService;
import com.service.IStudentService;
import com.util.DistanceUtil;
import com.util.FaceUtil;
import com.vo.UserVO;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import org.apache.catalina.Server;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.classfile.Code;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class IStudentServiceImpl implements IStudentService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private StudentGroupMapper studentGroupMapper;
    @Autowired
    private GroupMapper groupMapper;

    //加入讨论组
    public ServerResponse<Group> joinClassGroup(String studentId, int groupId) {
        int rs = studentGroupMapper.insert(studentId, groupId);
        if (rs == 0) {
            return ServerResponse.creatByErrorMessage("加入失败");
        }
        Group group = groupMapper.selectGroupByGroupId(groupId);
        if (group == null) {
            return ServerResponse.creatByErrorMessage("不存在该讨论组");
        }
        return ServerResponse.creatBySuccess("加入成功", group);
    }

    //学生获得所有讨论组信息
    public ServerResponse<List<Group>> getAllGroup(String studentId) {
        List<Group> groups = groupMapper.selectAllGroupByStudenId(studentId);
        return ServerResponse.creatBySuccess(groups);
    }

    //定位签到
    public ServerResponse<String> joinCheckByLocation(Location location, String studentId, int groupId) {
        Sign sign = redisService.get(CodeKey.signKey, ""+groupId, Sign.class);

        if(sign == null){
            return ServerResponse.creatByErrorMessage("未发布签到");
        }
        Timestamp creatTime = new Timestamp(System.currentTimeMillis());
        Map<String, Integer> map = new HashMap<>();
        //获取签到表
        map = redisService.get(CodeKey.singsKey,""+groupId,Map.class);

        String userId = StringUtils.EMPTY;
        //查看表中是否有该同学
        for (String x : map.keySet()){
            if (x.equals(studentId)){
                 userId = x;
            }
        }
        if(userId == ""){
            return ServerResponse.creatByErrorMessage("该同学不在签到名单中");
        }
        if ((Long) creatTime.getTime() - (Long) sign.getCreateTime().getTime() > sign.getLimitTime() * 60 * 1000) {
            return ServerResponse.creatByErrorMessage("签到已经截止");
        }
        if (!isPointOnLine(location, sign.getLocation())) {
            return ServerResponse.creatByErrorMessage("不在签到范围内");
        }
        //将该同学置为已签到 1
        map.put(studentId,1);
        redisService.set(CodeKey.singsKey,""+groupId,map);
        return ServerResponse.creatBySuccessMessage("签到成功");
    }


    //todo 人脸识别签到
    public ServerResponse<String> joinCheckByFace(byte[] image, String studentId, int groupId){
        Sign sign = redisService.get(CodeKey.signKey, ""+groupId, Sign.class);
        System.out.println(sign.toString());
        if(sign == null){
            return ServerResponse.creatByErrorMessage("未发布签到");
        }
        Timestamp creatTime = new Timestamp(System.currentTimeMillis());
        Map<String, Integer> map = new HashMap<>();
        //获取签到表
        map = redisService.get(CodeKey.singsKey,""+groupId,Map.class);
        String userId = StringUtils.EMPTY;
        //查看表中是否有该同学
        for (String x : map.keySet()){
            if (x.equals(studentId)){
                userId = x;
            }
        }
        if(userId == null){
            return ServerResponse.creatByErrorMessage("该同学不在签到名单中");
        }
        if ((Long) creatTime.getTime() - (Long) sign.getCreateTime().getTime() > sign.getLimitTime() * 60 * 1000) {
            return ServerResponse.creatByErrorMessage("签到已经截止");
        }

        //人脸搜索
        String rsImage = Base64Util.encode(image);
        JSONObject jsonObject = FaceUtil.faceSearch(rsImage, studentId);

        Object rs = jsonObject.get("result");
        if (rs.equals(null)){
            return ServerResponse.creatByErrorMessage("找不到该用户的人脸信息");
        }
        double score = jsonObject.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getDouble("score");


        map.put(userId,1);
        redisService.set(CodeKey.singsKey,""+groupId,map);

        return ServerResponse.creatBySuccessMessage("签到成功");
    }

    //判断是否在范围内
    private boolean isPointOnLine(Location fromlocation, Location toLocation){
        System.out.println(fromlocation.getLatitude() + "-"+fromlocation.getLongitude() + " : " + toLocation.getLatitude()+"-"+toLocation.getLongitude());
        Double r1 = DistanceUtil.getDistance(fromlocation.getLongitude(),fromlocation.getLatitude(), toLocation.getLongitude(), toLocation.getLatitude());
        System.out.println(r1);
        if (r1 > Allocation.DISTANCE){
            return false;
        }else {
            return true;
        }
    }

}

