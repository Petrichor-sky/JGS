package com.itheima.api;

import com.itheima.chuanyin.SoulOptions;
import com.itheima.mapper.SoulOptionsMapper;
import com.itheima.vo.OptionsVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class SoulOptionsServiceImpl implements SoulOptionsApi{

    @Autowired
    private SoulOptionsMapper optionsMapper;
    @Override
    public List<OptionsVo> findByQuestionId(String id) {
        return optionsMapper.findByQuestionId(id);
    }

    @Override
    public SoulOptions findByOptionId(String optionId) {
       /* LambdaQueryWrapper<SoulOptions> qw = new LambdaQueryWrapper<>();
        qw.eq(SoulOptions::getQuestionId,questionId);
        qw.eq(SoulOptions::getId,optionId);*/
        return optionsMapper.findByOptionId(optionId);
    }
}
