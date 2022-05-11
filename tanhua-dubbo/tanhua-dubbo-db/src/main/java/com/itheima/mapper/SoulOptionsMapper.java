package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.chuanyin.SoulOptions;
import com.itheima.vo.OptionsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ZJWzxy
 */
@Mapper
public interface SoulOptionsMapper extends BaseMapper<SoulOptions> {

    @Select("SELECT id,`option` FROM tb_soul_question_option WHERE question_id = #{id}")
    List<OptionsVo> findByQuestionId(@Param("id") String id);

    @Select("SELECT * FROM tb_soul_question_option WHERE id = #{id}")
    SoulOptions findByOptionId(@Param("id") String optionId);
}
