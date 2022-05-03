package com.itheima.controller;

import com.itheima.service.HuanXinService;
import com.itheima.vo.HuanXinUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("huanxin")
public class HuanXinController {
    @Autowired
    private HuanXinService huanXinService;

    @GetMapping("user")
    public ResponseEntity getHuanXinUserVo(){
        HuanXinUserVo vo = huanXinService.findHuanXinUserVo();
        return ResponseEntity.ok(vo);
    }
}
