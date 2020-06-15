package com.service;

import com.common.ServerResponse;
import com.pojo.Group;
import com.pojo.Location;

import java.util.List;

public interface IStudentService {
    public ServerResponse<Group> joinClassGroup(String studentId, Integer groupId);
    public ServerResponse<List<Group>> getAllGroup(String studentId);
    public ServerResponse<String> joinCheckByLocation(Double fromLongitude, Double fromLatitude, String studentId, Integer groupId);
    public ServerResponse<String> joinCheckByFace(String image64, String studentId, Integer groupId);
}
