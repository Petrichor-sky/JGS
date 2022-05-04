package com.itheima.controller;

import com.itheima.dto.RecommendUserDto;
import com.itheima.pojo.Question;
import com.itheima.service.TanHuaService;
import com.itheima.vo.NearUserVo;
import com.itheima.vo.PageResult;
import com.itheima.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    /**
     * 左滑右滑
     */
    @GetMapping("cards")
    public ResponseEntity  queryCardList(){
        List<TodayBest> list = tanHuaService.queryCardList();
        return ResponseEntity.ok(list);
    }

    /**
     * 喜欢
     */
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable("id") Long likeUserId){
        tanHuaService.love(likeUserId);
        return ResponseEntity.ok(null);

    }

    /**
     * 不喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity disLove(@PathVariable("id") Long likeUserId){
        tanHuaService.disLove(likeUserId);
        return ResponseEntity.ok(null);

    }

    /**
     * 搜附近
     */
    @GetMapping("search")
    public ResponseEntity<List<NearUserVo>> search(@RequestParam(value = "gender") String gender,
                                             @RequestParam(defaultValue = "2000") String distance){
        List<NearUserVo> list = tanHuaService.search(gender,distance);
        return ResponseEntity.ok(list);
    }
}
