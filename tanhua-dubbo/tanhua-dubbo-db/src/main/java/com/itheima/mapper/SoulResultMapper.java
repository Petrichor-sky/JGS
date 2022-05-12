package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.chuanyin.SoulResult;
import com.itheima.vo.ConclusionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SoulResultMapper extends BaseMapper<SoulResult> {


    @Select("SELECT cover,conclusion FROM tb_soul_result WHERE paper_Id = #{paperId} AND score = #{score}")
    ConclusionVo findByPaperIdAndScore(@Param("paperId") Long paperId, @Param("score") String scoreStr);
}
