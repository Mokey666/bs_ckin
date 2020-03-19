package com.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ClassMapper {
    @Select("select classname from bs_class where classid=#{classId}")
    String selectClassNameById(Integer classId);
}
