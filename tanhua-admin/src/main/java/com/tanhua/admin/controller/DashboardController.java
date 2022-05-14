package com.tanhua.admin.controller;

import com.tanhua.admin.service.DashboardService;
import com.itheima.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    /**
     * 新增、活跃用户、次日留存率
     * @param
     * @return
     */
    @GetMapping("users")
    public ResponseEntity<Map<String,List<Map<String,Object>>>> getCount(Long sd,Long ed,String type){
        Map<String, List<Map<String,Object>>> map = dashboardService.getCount(sd,ed,type);
        return ResponseEntity.ok(map);
    }

    /**
     * 概要统计信息
     */
    @GetMapping("summary")
    public ResponseEntity<AnalysisSummaryVo> summary(){
        AnalysisSummaryVo vo = dashboardService.summary();
        return ResponseEntity.ok(vo);
    }

    /**
     * 注册用户分布，行业top、年龄、性别、地区
     */
    @GetMapping("distribution")
    public ResponseEntity<Map<String,List<Map<String,Object>>>> distribution(Long sd,Long ed){
        Map<String,List<Map<String,Object>>> map = dashboardService.distribution(sd,ed);
        return ResponseEntity.ok(map);
    }
}
