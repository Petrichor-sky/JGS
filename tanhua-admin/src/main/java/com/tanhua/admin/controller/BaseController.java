package com.tanhua.admin.controller;

import com.tanhua.admin.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("base")
public class BaseController {

    @Autowired
    private BaseService baseService;

    @GetMapping("citys")
    public ResponseEntity<List<Map<String,String>>> getCitys(){
        List<Map<String,String>> list = baseService.getCitys();
        return ResponseEntity.ok(list);
    }
}
