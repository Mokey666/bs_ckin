package com.dao;

import com.pojo.Group;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMapper {
    @Select("select count(0) from bs_classgroup where groupid=#{groupId}")
    int selectByGroupId(@Param("groupId") Integer groupId);
    @Insert("insert into bs_classgroup (teacherid, groupid, classname, message) values (#{teacherId}, #{groupId}, #{className}, #{message})")
    int insert(@Param("teacherId") String teacherId, @Param("className") String className, @Param("groupId") int groupId,@Param("message") String message);
    @Select("select groupId, teacherId, className, message from bs_classgroup where groupid=#{groupId}")
    Group selectGroupByGroupId(int groupId);
    @Select("select g.groupId, g.teacherId, g.className, g.message from bs_classgroup g left join bs_student_group sg on g.groupid=sg.groupid where sg.studentid=#{studentId}")
    List<Group>selectAllGroupByStudenId(@Param("studentId") String studentId);
    @Select("select groupId, teacherId, className, message from bs_classgroup where teacherId=#{teacherId}")
    List<Group>selectAllGroupByTeacherId(@Param("teacherId")String teacherId);
    @Select("select count(0) from bs_classgroup where classname=#{classname}")
    int selectGroupByClassName(@Param("classname") String className);
}
