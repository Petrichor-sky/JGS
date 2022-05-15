package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.pojo.UseTimeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UseTimeLogMapper extends BaseMapper<UseTimeLog> {

    @Select("SELECT SUM(use_time) FROM tb_use_time_log WHERE log_date BETWEEN #{beforeWeek} AND #{today}")
    Long CountUseTime(@Param("today") String today,@Param("beforeWeek")  String beforeWeek);
}
