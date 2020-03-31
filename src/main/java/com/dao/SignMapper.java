package com.dao;

import com.pojo.User;
import com.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SignMapper {
    @Select("select u.uname, u.uid from bs_user u left join bs_sign si on u.uid=si.studentId where si.classid=#{classId} and si.week=#{week} and si.order=#{order}")
    List<UserVO> selectByClassIdAndWeekAndOrder(@Param("classId") Integer classId, @Param("week")Integer week, @Param("order")Integer order);
}
