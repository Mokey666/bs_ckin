package com.dao;

import com.pojo.Group;
import com.vo.UserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentGroupMapper {
    @Select("select u.uid u.uname from bs_user u left join bs_classgroup c on u.uid=c.studentid where c.groupid=#{groupId}")
    List<UserVO> selectUserIdByGroupId(@Param("groupId") int groupId);
    @Insert("insert into bs_student_group (studentid, groupid) values (#{studentId}, #{groupId})")
    int insert(@Param("studentId") String studentId, @Param("groupId") int groupId);

}
