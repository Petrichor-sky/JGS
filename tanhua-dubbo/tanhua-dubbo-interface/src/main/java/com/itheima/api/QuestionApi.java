package com.itheima.api;

import com.itheima.pojo.Question;

public interface QuestionApi {

    void save(Question question);

    Question findByUserId(Long id);

    void update(Question question);
}
