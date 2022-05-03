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
        messageService.contacts(map.get("userId"));
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

}
