package com.itheima.controller;

import com.itheima.mongo.Movement;
import com.itheima.service.MovementService;
import com.itheima.vo.MovementsVo;
import com.itheima.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("movements")
public class MovementController {

    @Autowired
    private MovementService movementService;

    /**
     * 发布动态
     * @param movement
     * @param imageContent
     * @return
     */
    @PostMapping
    public ResponseEntity movements(Movement movement, MultipartFile[] imageContent){
        movementService.saveMovements(movement,imageContent);
        return ResponseEntity.ok(null);
    }

    /**
     * 我的动态
     * @param page 当前页
     * @param pageSize 每页显示的条目数
     * @param userId 用户id
     * @return
     */
    @GetMapping("all")
    public ResponseEntity<PageResult> movements(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                Long userId){
        PageResult result = movementService.all(page,pageSize,userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取好友动态
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResult> getFriendMovements(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer pageSize){
        PageResult result = movementService.getFriendMovements(page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 推荐动态
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("recommend")
    public ResponseEntity<PageResult> recommend(@RequestParam(defaultValue = "1")Integer page,
                                                @RequestParam(defaultValue = "10") Integer pageSize){
        PageResult result = movementService.recommend(page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据id进行查询
     * @param movementId
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<MovementsVo> findMovementById(@PathVariable("id")String movementId){
        MovementsVo movementsVo = movementService.findMovementById(movementId);
        return ResponseEntity.ok(movementsVo);
    }
}
