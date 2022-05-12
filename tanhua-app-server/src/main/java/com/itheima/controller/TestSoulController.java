package com.itheima.controller;

import com.itheima.chuanyin.Answers;
import com.itheima.service.TestSoulService;
import com.itheima.vo.ConclusionVo;
import com.itheima.vo.PaperListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("report/{id}")
    public ResponseEntity<ConclusionVo> getResult(@PathVariable("id")String reportId){
        ConclusionVo vo = testSoulService.getResult(reportId);
        return ResponseEntity.ok(vo);
    }

    @PostMapping
    public ResponseEntity<String> submitTestSoul(@RequestBody Map<String,List<Answers>> map){
        String reportId = testSoulService.submitTestSoul(map);
        return ResponseEntity.ok(reportId);
    }
}
