package com.itheima.controller;

import com.itheima.dto.RecommendUserDto;
import com.itheima.pojo.Question;
import com.itheima.service.TanHuaService;
import com.itheima.vo.PageResult;
import com.itheima.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("tanhua")
public class TanHuaController {

    @Autowired
    private TanHuaService tanHuaService;

    @GetMapping("todayBest")
    public ResponseEntity<TodayBest> todayBest(){
        TodayBest todayBest = tanHuaService.todayBest();
        return ResponseEntity.ok(todayBest);
    }

    /**
     * 推荐朋友
     * @param recommendUserDto
     * @return
     */
    @GetMapping("recommendation")
    public ResponseEntity<PageResult> recommendation(RecommendUserDto recommendUserDto){
        PageResult result = tanHuaService.findByRecommendation(recommendUserDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 佳人信息
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long id){
        TodayBest todayBest = tanHuaService.findPersonalInfoById(id);
        return ResponseEntity.ok(todayBest);
    }
    /**
     * 查询陌生人问题
     */
    @GetMapping("strangerQuestions")
    public ResponseEntity<String> strangerQuestions(Long userId){
        String question = tanHuaService.getStrangerQuestions(userId);
        return ResponseEntity.ok(question);
    }

    /**
     * 回复陌生人问题
     *
     */
    @PostMapping("strangerQuestions")
    public ResponseEntity strangerQuestions(@RequestBody Map<String,String> params){
        tanHuaService.strangerQuestions(params);
        return ResponseEntity.ok(null);
    }

}
