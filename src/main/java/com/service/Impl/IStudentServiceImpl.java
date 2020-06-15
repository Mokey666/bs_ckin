package com.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.common.Allocation;
import com.common.ServerResponse;
import com.dao.GroupMapper;
import com.dao.StudentGroupMapper;
import com.pojo.Group;
import com.pojo.Sign;
import com.redis.CodeKey;
import com.redis.RedisService;
import com.service.IStudentService;
import com.util.DistanceUtil;
import com.util.FaceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IStudentServiceImpl implements IStudentService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private StudentGroupMapper studentGroupMapper;
    @Autowired
    private GroupMapper groupMapper;

    //加入讨论组
    public ServerResponse<Group> joinClassGroup(String studentId, Integer groupId) {
        int rs2 = studentGroupMapper.selectByUserIdAndGroupId(studentId,groupId);
        if (rs2 > 0){
            return ServerResponse.creatByErrorMessage("以加入该讨论组");
        }

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
    public ServerResponse<String> joinCheckByLocation(Double fromLongitude, Double fromLatitude, String studentId, Integer groupId) {
        Sign sign = redisService.get(CodeKey.signKey, ""+groupId, Sign.class);

        if(sign == null){
            return ServerResponse.creatByErrorMessage("未发布签到");
        }
        //获取签到表
        Map<String, Integer> map = new HashMap<>();
        map = redisService.get(CodeKey.singsKey,""+groupId,Map.class);

        Timestamp creatTime = new Timestamp(System.currentTimeMillis());
        String userId = StringUtils.EMPTY;
        //查看表中是否有该同学
        for (String x : map.keySet()){
            if (x.equals(studentId)){
                 userId = x;
            }
        }
        if (map.get(userId) == 1){
            return ServerResponse.creatBySuccessMessage("已签到");
        }

        if(userId == ""){
            return ServerResponse.creatByErrorMessage("该同学不在签到名单中");
        }
        if ((Long) creatTime.getTime() - (Long) sign.getCreateTime().getTime() > sign.getLimitTime() * 60 * 1000) {
            return ServerResponse.creatByErrorMessage("签到已经截止");
        }
        if (!(isPointOnLine(fromLongitude, fromLatitude, sign.getLongitude(),sign.getLatitude()))) {
            return ServerResponse.creatByErrorMessage("不在签到范围内");
        }
        //将该同学置为已签到 1
        map.put(studentId,1);
        redisService.set(CodeKey.singsKey,""+groupId,map);
        return ServerResponse.creatBySuccessMessage("签到成功");
    }


    //todo 人脸识别签到
    public ServerResponse<String> joinCheckByFace(String image64, String studentId, Integer groupId){
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

        if (map.get(userId) == 1){
            return ServerResponse.creatBySuccessMessage("已签到");
        }

        if(userId == null){
            return ServerResponse.creatByErrorMessage("该同学不在签到名单中");
        }
        if ((Long) creatTime.getTime() - (Long) sign.getCreateTime().getTime() > sign.getLimitTime() * 60 * 1000) {
            return ServerResponse.creatByErrorMessage("签到已经截止");
        }

        //人脸搜索
        JSONObject jsonObject = FaceUtil.faceSearch(image64, studentId);

        Object rs = jsonObject.get("result");
        if (rs == null){
            return ServerResponse.creatByErrorMessage("人脸识别失败");
        }

        double score = jsonObject.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getDouble("score");

        if (score<80){
            return ServerResponse.creatByErrorMessage("人脸识别失败");
        }
        map.put(userId,1);
        redisService.set(CodeKey.singsKey,""+groupId,map);

        return ServerResponse.creatBySuccessMessage("签到成功");
    }

    //判断是否在范围内
    private boolean isPointOnLine(Double fromLongitude, Double fromLatitude, Double toLongitude,Double toLatitude){
        Double rs = DistanceUtil.getDistance(fromLongitude,fromLatitude, toLongitude, toLatitude);
        if (rs > Allocation.DISTANCE){
            return false;
        }else {
            return true;
        }
    }


}

