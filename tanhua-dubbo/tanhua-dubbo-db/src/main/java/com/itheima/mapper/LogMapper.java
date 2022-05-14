package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.pojo.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogMapper extends BaseMapper<Log> {

    @Select("SELECT DISTINCT `user_id` FROM tb_log WHERE log_time BETWEEN #{start} AND #{end}")
    List<Long> findLogByTimeAndType(@Param("start") String start,@Param("end") String end);
}
