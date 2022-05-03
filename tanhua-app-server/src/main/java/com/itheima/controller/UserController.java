package com.itheima.controller;

import com.itheima.pojo.UserInfo;
import com.itheima.service.UserInfoService;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 发送验证码功能
     * @param map
     */
    @PostMapping("login")
    public ResponseEntity login(@RequestBody Map<String,String> map){
        userService.login(map);
        return ResponseEntity.ok(null);
    }

    /**
     * 登录验证码校验的过程
     * @param map
     */
    @PostMapping("loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String,String> map){
        return userService.loginVerification(map);
    }

    /**
     * 用户个人资料
     */
    @PostMapping("loginReginfo")
    public ResponseEntity loginRegInfo(@RequestBody UserInfo userInfo){
        userService.loginReginfo(userInfo);
        return ResponseEntity.ok(null);
    }

    /**
     * 上传头像
     */
    @PostMapping("loginReginfo/head")
    public ResponseEntity uploadHead(MultipartFile headPhoto){
        return userService.uploadHead(headPhoto);
    }

}
