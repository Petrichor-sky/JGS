package com.itheima.controller;

import com.itheima.service.CommentService;
import com.itheima.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表
     * @param page
     * @param pageSize
     * @param movementId
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResult> getCommemts(@RequestParam(defaultValue = "1")Integer page,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                String movementId){
        PageResult result = commentService.getCommemts(movementId,page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 发布评论
     * @return
     */
    @PostMapping
    public ResponseEntity saveCommemts(@RequestBody Map<String,String> map){
        commentService.saveCommemts(map);
        return ResponseEntity.ok(null);
    }

}
