package com.service;

import com.common.ServerResponse;
import com.pojo.Group;
import com.pojo.Location;

import java.util.List;

public interface IStudentService {
    public ServerResponse<Group> joinClassGroup(String studentId, int groupId);
    public ServerResponse<List<Group>> getAllGroup(String studentId);
    public ServerResponse<String> joinCheckByLocation(Location location, String studentId, int groupId);
    public ServerResponse<String> joinCheckByFace(byte[] image, String studentId, int groupId);
}