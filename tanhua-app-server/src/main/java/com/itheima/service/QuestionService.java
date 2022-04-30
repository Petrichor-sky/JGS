package com.itheima.service;

import com.itheima.api.QuestionApi;
import com.itheima.pojo.Question;
import com.itheima.utils.ThreadLocalUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class QuestionService {

    @DubboReference
    private QuestionApi questionApi;

    /**
     * 设置陌生人问题
     * @param content
     * @return
     */
    public ResponseEntity save(String content) {
        //获取当前登录的id
        Long id = ThreadLocalUtils.get();
        //通过id进行查询
        Question question = questionApi.findByUserId(id);
        if (ObjectUtils.isEmpty(question)){
            //如果为空的话执行保存操作
            question = new Question();
            question.setUserId(id);
            question.setTxt(content);
            questionApi.save(question);
        }else {
            //如果不为空的话执行修改操作
            question.setTxt(content);
            questionApi.update(question);
        }

        return ResponseEntity.ok(null);
    }
}
