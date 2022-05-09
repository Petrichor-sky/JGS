package com.tanhua.admin.controller;

import com.itheima.vo.AdminVo;
import com.tanhua.admin.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {
    @Autowired
    private SystemService systemService;

    /**
     * 用户登录验证码图片
     */
    @GetMapping("verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        systemService.verification(uuid,response);
    }

    /**
     * 用户登录
     */
    @PostMapping("login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody Map<String,String> params){
        Map<String,Object> map = systemService.login(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 用户基本信息
     */
    @PostMapping("profile")
    public ResponseEntity<AdminVo> profile(){
        AdminVo vo = systemService.getProfile();
        return ResponseEntity.ok(vo);
    }

    /**
     * 用户登出
     */
    @PostMapping("logout")
    public ResponseEntity logout(HttpServletRequest request){
        systemService.logout(request);
        return ResponseEntity.ok(null);
    }



}
