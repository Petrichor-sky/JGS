package com.itheima.controller;

import com.itheima.dto.RecommendUserDto;
import com.itheima.service.TanHuaService;
import com.itheima.vo.PageResult;
import com.itheima.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
