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
    /**
     * 评论点赞
     */
    @GetMapping("/{id}/like")
    public ResponseEntity<Integer> like(@PathVariable("id") String commentId){
        Integer count = commentService.likeComment(commentId);
        return ResponseEntity.ok(count);
    }

    /**
     * 取消评论点赞
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity<Integer> dislike(@PathVariable("id") String commentId){
        Integer count = commentService.dislikeComment(commentId);
        return ResponseEntity.ok(count);
    }

}
