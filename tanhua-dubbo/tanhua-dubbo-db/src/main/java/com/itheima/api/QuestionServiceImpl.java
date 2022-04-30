package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mapper.QuestionMapper;
import com.itheima.pojo.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionServiceImpl implements QuestionApi {
    @Autowired
    private QuestionMapper questionMapper;

    //设置陌生人问题
    @Override
    public void save(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public Question findByUserId(Long id) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getUserId,id);
        return questionMapper.selectOne(queryWrapper);
    }

    @Override
    public void update(Question question) {
        questionMapper.updateById(question);
    }
}
