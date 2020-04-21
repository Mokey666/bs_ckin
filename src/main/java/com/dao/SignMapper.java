package com.dao;

import com.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SignMapper {
    @Select("select u.uname, u.uid from bs_user u left join bs_sign s on u.uid=s.studentid where c.groupid=#{groupId}")
    List<UserVO> selectByGroupId(@Param("groupId") int groupId);
}
