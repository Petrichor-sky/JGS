package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.pojo.Analysis;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {
    //定时统计log表中的数据，保存或者更新analysis表中的数据

    @Autowired
    private LogMapper logMapper;
    @Autowired
    private AnalysisMapper analysisMapper;

    public void analysis() throws ParseException {
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yestodayStr =  DateUtil.yesterday().toString("yyyy-MM-dd"); //工具类
        //String todayStr = "2022-05-12";
        //String yestodayStr = "2022-05-11";
        //1、统计每日注册用户数
        Integer numRegistered = logMapper.queryByTypeAndLogTime("0102",todayStr);
        //2、统计每日登陆用户
        Integer numLogin = logMapper.queryByTypeAndLogTime("0101",todayStr);
        //3、统计活跃的用户数
        Integer numActive = logMapper.queryByLogTime(todayStr);
        //4、统计次日留存的用户数
        Integer numRetention1d = logMapper.queryNumRetention1d(todayStr, yestodayStr);
        //5、根据当前时间查询AnalysisByDay数据
        QueryWrapper<Analysis> qw = new QueryWrapper<>();
        Date todatDate = new SimpleDateFormat("yyyy-MM-dd").parse(todayStr);
        qw.eq("record_date", todatDate);

        Analysis analysis = analysisMapper.selectOne(qw);
        if(analysis == null) {
            //7、如果不存在，保存
            analysis = new Analysis();
            analysis.setRecordDate(todatDate);
            analysis.setNumRegistered(numRegistered);
            analysis.setNumLogin(numLogin);
            analysis.setNumActive(numActive);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setCreated(new Date());
            analysis.setUpdated(new Date());
            analysisMapper.insert(analysis);
        }else{
            //8、如果存在，更新
            analysis.setNumRegistered(numRegistered);
            analysis.setNumLogin(numLogin);
            analysis.setNumActive(numActive);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setUpdated(new Date());
            analysisMapper.updateById(analysis);
        }
    }
    /**
     * 累计用户数
     * @return
     */
    public Long CountCumulativeUsers() {
        return analysisMapper.CountCumulativeUsers();
    }

    /**
     * 过去30天的活跃用户
     * @param today
     * @param beforeMonth
     * @return
     */
    public Long QueryActiveCount(String today,String beforeMonth) {
        return analysisMapper.QueryUserCount(today,beforeMonth);
    }

    /**
     * 今日注册次数
     * @param today
     * @return
     */
    public Long QueryNewUsersCount(String today) {
        return analysisMapper.QueryNewUsersCount(today);
    }

    /**
     * 今日登录次数
     * @param today
     * @return
     */
    public Long QueryTodayLoginTimes(String today) {
        return analysisMapper.QueryTodayLoginTimes(today);
    }

    /**
     * 获取统计数据
     * @param type
     * @param start
     * @param end
     * @return
     */
    public Map<String, List<Map<String, Object>>> countByTimeAndType(String type, String start, String end,String lastYearStart,String lastYearEnd) {
        if ("101".equals(type)){
            type = "num_registered";
        }else if ("102".equals(type)){
            type = "num_active";
        }else if ("103".equals(type)){
            type = "num_retention1d";
        }else {
            return null;
        }
        Map<String,List<Map<String, Object>>> map = new HashMap<>();
        List<Map<String, Object>> thisYearList = analysisMapper.QueryList(type,start,end);
        List<Map<String, Object>> lastYearList = analysisMapper.QueryList(type,lastYearStart,lastYearEnd);
        map.put("thisYear",thisYearList);
        map.put("lastYear",lastYearList);
        return map;
    }
}
