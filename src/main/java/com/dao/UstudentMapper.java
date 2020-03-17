package com.dao;

import com.pojo.Ustudent;

public interface UstudentMapper {
    int deleteByPrimaryKey(String sid);

    int insert(Ustudent record);

    int insertSelective(Ustudent record);

    Ustudent selectByPrimaryKey(String sid);

    int updateByPrimaryKeySelective(Ustudent record);

    int updateByPrimaryKey(Ustudent record);
}