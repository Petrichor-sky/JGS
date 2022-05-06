package com.itheima.controller;

import com.itheima.service.MessageService;
import com.itheima.vo.PageResult;
import com.itheima.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    /**
     *
     * @param huanxinId
     * @return
     */
    @GetMapping("userinfo")
    public ResponseEntity userinfo(String huanxinId){
        UserInfoVo vo = messageService.findUserInfoByHuanxin(huanxinId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 添加好友
     * @param
     * @return
     */
    @PostMapping("contacts")
    public ResponseEntity contacts(@RequestBody Map<String,String> map){
        Long friendId = Long.valueOf(map.get("userId"));
        messageService.contacts(friendId);
        return ResponseEntity.ok(null);
    }

    /**
     * 联系人列表
     * @param
     * @return
     */
    @GetMapping("contacts")
    public ResponseEntity<PageResult> contactsList(@RequestParam(defaultValue = "1")Integer page,
                                                   @RequestParam(defaultValue = "10")Integer pageSize,
                                                   String keyword){
        PageResult result = messageService.findContactsList(keyword,page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 评论列表
     */
    @GetMapping("comments")
    public ResponseEntity<PageResult> getComments(@RequestParam(defaultValue = "1")Integer page,
                                                  @RequestParam(defaultValue = "10")Integer pageSize){
        PageResult result = messageService.getComments(page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 点赞列表
     */
    @GetMapping("likes")
    public ResponseEntity<PageResult> getLikes(@RequestParam(defaultValue = "1")Integer page,
                                                  @RequestParam(defaultValue = "10")Integer pageSize){
        PageResult result = messageService.getLikes(page,pageSize);
        return ResponseEntity.ok(result);
    }
    /**
     * 喜欢列表
     */
    @GetMapping("loves")
    public ResponseEntity<PageResult> getLoves(@RequestParam(defaultValue = "1")Integer page,
                                               @RequestParam(defaultValue = "10")Integer pageSize){
        PageResult result = messageService.getLoves(page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 公告列表
     */
    @GetMapping("announcements")
    public ResponseEntity<PageResult> getAnnouncements(@RequestParam(defaultValue = "1")Integer page,
                                                       @RequestParam(defaultValue = "10")Integer pageSize){
        PageResult result = messageService.getAnnouncements(page,pageSize);
        return ResponseEntity.ok(result);
    }

}
