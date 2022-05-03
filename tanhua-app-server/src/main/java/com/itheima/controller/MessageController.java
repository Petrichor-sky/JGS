package com.itheima.controller;

import com.itheima.service.MessageService;
import com.itheima.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("userinfo")
    public ResponseEntity userinfo(String huanxinId){
        UserInfoVo vo = messageService.findUserInfoByHuanxin(huanxinId);
        return ResponseEntity.ok(vo);
    }

}
