package com.tanhua.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.pojo.Analysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisMapper extends BaseMapper<Analysis> {
    @Select("select sum(num_registered) from tb_analysis")
    Long CountCumulativeUsers();

    @Select("SELECT SUM(num_active) FROM tb_analysis WHERE record_date BETWEEN #{beforeMonth} AND #{today}")
    Long QueryUserCount(@Param("today") String today, @Param("beforeMonth") String beforeMonth);

    @Select("SELECT num_registered FROM tb_analysis WHERE record_date = #{today}")
    Long QueryNewUsersCount(@Param("today")String today);

    @Select("SELECT num_login FROM tb_analysis WHERE record_date = #{today}")
    Long QueryTodayLoginTimes(@Param("today") String today);

    @Select("SELECT record_date AS title,${type} AS amount FROM tb_analysis WHERE record_date BETWEEN #{start} AND #{end}")
    List<Map<String, Object>> QueryList(@Param("type") String type,@Param("start") String start,@Param("end") String end);
}
