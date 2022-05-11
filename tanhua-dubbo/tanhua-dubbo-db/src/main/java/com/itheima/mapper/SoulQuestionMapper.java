package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.chuanyin.SoulQuestion;
import com.itheima.vo.QuestionsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ZJWzxy
 */
@Mapper
public interface SoulQuestionMapper extends BaseMapper<SoulQuestion> {

    @Select("SELECT id,question FROM tb_soul_question WHERE paper_id = #{id}")
    List<QuestionsVo> findByPaperId(@Param("id") String paperId);
}
