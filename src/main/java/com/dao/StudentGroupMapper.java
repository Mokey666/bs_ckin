package com.dao;

import com.vo.UserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentGroupMapper {
    @Insert("insert into bs_student_group (studentid, groupid) values (#{studentId}, #{groupId})")
    int insert(@Param("studentId") String studentId, @Param("groupId") int groupId);
    @Select("select u.uid from bs_user u left join bs_student_group c on u.uid=c.studentid where c.groupid=#{groupId} ")
    List<String> selectUserVOByGroupId(@Param("groupId") int groupId);
    @Select("select count(0) from bs_student_group where studentid=#{studentId} and groupid=#{groupId}")
    int selectByUserIdAndGroupId(@Param("studentId")String studentId, @Param("groupId")int groupId);
}
