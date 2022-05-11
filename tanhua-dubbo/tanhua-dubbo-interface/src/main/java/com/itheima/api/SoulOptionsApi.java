package com.itheima.api;

import com.itheima.chuanyin.SoulOptions;
import com.itheima.vo.OptionsVo;

import java.util.List;

public interface SoulOptionsApi {
    List<OptionsVo> findByQuestionId(String id);

    SoulOptions findByOptionId(String optionId);
}
