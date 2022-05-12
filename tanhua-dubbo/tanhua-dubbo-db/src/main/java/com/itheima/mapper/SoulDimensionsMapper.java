package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.chuanyin.SoulDimensions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SoulDimensionsMapper extends BaseMapper<SoulDimensions> {

    @Select("SELECT `key`,`value` FROM tb_soul_dimensions WHERE score = #{scoreStr}")
    List<SoulDimensions> findByScore(@Param("scoreStr") String scoreStr);
}
