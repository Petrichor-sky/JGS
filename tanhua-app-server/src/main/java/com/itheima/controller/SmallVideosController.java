package com.itheima.controller;

import com.itheima.service.SmallVideosService;
import com.itheima.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideosController {

    @Autowired
    private SmallVideosService videosService;

    /**
     * 发布视频
     * @param videoThumbnail
     * @param videoFile
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        videosService.saveVideos(videoThumbnail,videoFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 小视频列表
     */
    @GetMapping
    public ResponseEntity<PageResult> queryVideoList(@RequestParam(defaultValue = "1")Integer page,
                                                     @RequestParam(defaultValue = "10")Integer pageSize){
        PageResult result = videosService.queryVideoList(page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 视频点赞
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Integer> likeVideo(@PathVariable("id") String videoId){
        Integer count = videosService.likeVideo(videoId);
        return ResponseEntity.ok(count);
    }
    /**
     * 取消视频点赞
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity<Integer> disLikeVideo(@PathVariable("id")String videoId){
        Integer count = videosService.disLikeVideo(videoId);
        return ResponseEntity.ok(count);

    }

    /**
     * 评论列表
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<PageResult> getComments(@RequestParam(defaultValue = "1")Integer page,
                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                  @PathVariable("id") String videoId){
        PageResult result = videosService.getComments(videoId,page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 发布评论
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity saveComment(@PathVariable("id")String videoId,@RequestBody Map<String,String> map){
        String comment = map.get("comment");
        videosService.saveComment(videoId,comment);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频用户关注
     */
    @PostMapping("/{uid}/userFocus")
    public ResponseEntity saveUserFocus(@PathVariable("uid")Long followUserId){
        videosService.saveUserFocus(followUserId);
        return ResponseEntity.ok(null);
    }
    /**
     * 视频用户关注取消
     */
    @PostMapping("/{uid}/userUnFocus")
    public ResponseEntity saveUserUnFocus(@PathVariable("uid")Long followUserId){
        videosService.saveUserUnFocus(followUserId);
        return ResponseEntity.ok(null);
    }




}