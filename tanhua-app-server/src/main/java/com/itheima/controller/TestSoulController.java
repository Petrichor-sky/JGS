package com.itheima.controller;

import com.itheima.service.TestSoulService;
import com.itheima.vo.PaperListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("testSoul")
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;

    /**
     * 问卷列表
     * @return
     */
    @GetMapping
    public ResponseEntity<List<PaperListVo>> getTestSoul(){
        List<PaperListVo> list= testSoulService.getTestSoul();
        return ResponseEntity.ok(list);
    }

 /*   @GetMapping("report/{id}")
    public ResponseEntity<QuestionsResultVo> getResult(@PathVariable("id")String reportId){
        //QuestionsResultVo vo = testSoulService.getResult(reportId);
        //return ResponseEntity.ok(vo);
        return null;

    }*/
}
