package com.dao;

import com.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into bs_user (uid, uname, email, phone, sex, password, image, role) values (#{uid}, #{uname}, #{email}, #{phone}, #{sex}, #{password}, #{image}, #{role})")
    int insert(User record);
    @Select("select uid, uname, email, phone, sex, password, image, role from bs_user where uid=#{uid} ")
    User selectByPrimaryKey(String uid);
    @Update("update bs_user set uname=#{uname}, email=#{email}, phone=#{phone}, sex=#{sex}, password=#{password}, image=#{image}, role=#{role} where uid=#{uid}")
    int updateByPrimaryKeySelective(User record);
    @Select("select count(1) from bs_user where uid=#{userId}")
    int selectById(@Param("userId") String userId);
    @Select("select uid, uname, email, phone, sex, password, image, role from bs_user where uid=#{userId} and password=#{password}")
    User selectLogin(@Param("userId") String userId, @Param("password") String password);
    int selectByIdAndEmail(@Param("userId") String userId, @Param("email") String email);
    @Update("update bs_user set password=#{newPassword} where uid=#{userId}")
    int modifyPassword(@Param("userId") String userId, @Param("newPassword") String newPassword);
    @Select("select count(1) from bs_user where uid=#{userId} and password=#{password}")
    int selectByIdAndPassword(@Param("userId") String userId, @Param("password") String password);
    @Select("select count(1) from bs_user where uid!=#{userId} and email!=#{email}")
    int checkEmailByUserId(@Param("userId") String userId, @Param("email") String email);
    @Select("select uid, uname, email, phone, sex, password, image, role from bs_user")
    List<User> getAllUser();
    @Delete("delete from bs_user where uid=#{userId}")
    int deleteByUserId(@Param("userId") String userId);
}