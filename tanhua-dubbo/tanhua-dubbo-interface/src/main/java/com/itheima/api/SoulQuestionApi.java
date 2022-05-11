package com.itheima.api;

import com.itheima.chuanyin.SoulQuestion;
import com.itheima.vo.QuestionsVo;

import java.util.List;

public interface SoulQuestionApi {
    List<QuestionsVo> findByPaperId(String valueOf);

    SoulQuestion findById(Long questionId);
}
