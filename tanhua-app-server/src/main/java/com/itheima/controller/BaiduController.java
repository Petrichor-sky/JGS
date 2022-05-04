package com.itheima.controller;

import com.itheima.service.BaiduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("baidu")
public class BaiduController {
    @Autowired
    private BaiduService baiduService;

    @PostMapping("location")
    public ResponseEntity location(@RequestBody Map<String,String> map){
        baiduService.updateLocation(map);
        return ResponseEntity.ok(null);
    }

}
