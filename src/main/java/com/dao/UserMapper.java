package com.dao;

import com.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper {
    int deleteByPrimaryKey(String uid);
    @Insert("insert into bs_user (uid, uname, email, phone, sex, password, image, role) values (#{uid}, #{uname}, #{email},  #{phone}, #{sex}, #{password}, #{image}, #{role})")
    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String uid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    @Select("select count(1) from bs_user where uid=#{userId}")
    int selectById(@Param("userId") String userId);
    @Select("select uid, uname, email, phone, sex, password, image, role from bs_user where uid=#{userId} and password=#{password}")
    User selectLogin(@Param("userId") String userId, @Param("password") String password);

    int selectByIdAndEmail(@Param("userId") String userId, @Param("email") String email);
    @Update("update bs_user set password=#{newPassoword} where uid=#{userId}")
    int modifyPassword(@Param("userId") String userId, @Param("newPassword") String newPassword);
    @Select("select count(1) from bs_user where uid=#{userId} and password=#{password}")
    int selectByIdAndPassword(@Param("userId") String userId, @Param("password") String password);
    @Select("select count(1) from bs_user where uid!=#{userId} and email!=#{email}")
    int checkEmailByUserId(@Param("userId") String userId, @Param("email") String email);
}