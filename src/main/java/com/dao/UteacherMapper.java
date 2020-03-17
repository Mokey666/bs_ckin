package com.dao;

import com.pojo.Uteacher;

public interface UteacherMapper {
    int deleteByPrimaryKey(String tid);

    int insert(Uteacher record);

    int insertSelective(Uteacher record);

    Uteacher selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(Uteacher record);

    int updateByPrimaryKey(Uteacher record);
}