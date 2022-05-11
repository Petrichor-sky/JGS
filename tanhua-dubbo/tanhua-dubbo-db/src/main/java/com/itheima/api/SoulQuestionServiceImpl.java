package com.itheima.api;

import com.itheima.chuanyin.SoulQuestion;
import com.itheima.mapper.SoulQuestionMapper;
import com.itheima.vo.QuestionsVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class SoulQuestionServiceImpl implements SoulQuestionApi{
    @Autowired
    private SoulQuestionMapper questionMapper;
    @Override
    public List<QuestionsVo> findByPaperId(String paperId) {
        return questionMapper.findByPaperId(paperId);
    }

    @Override
    public SoulQuestion findById(Long questionId) {
        return questionMapper.selectById(questionId);
    }
}
