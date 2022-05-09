package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.pojo.Analysis;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AnalysisService {
    //定时统计log表中的数据，保存或者更新analysis表中的数据

    @Autowired
    private LogMapper logMapper;
    @Autowired
    private AnalysisMapper analysisMapper;

    public void analysis() throws ParseException {

        //String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //String yestodayStr =  DateUtil.yesterday().toString("yyyy-MM-dd"); //工具类
        String todayStr = "2022-05-10";
        String yestodayStr = "2022-05-09";
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
     * 新增、活跃用户、次日留存率
     *
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    public Map<String,List<Map<String,Object>>> CountByTypeAndTime(String type, Date startDate, Date endDate) {
        try {
            //去年开始时间
            Calendar thisYearStart = Calendar.getInstance();
            thisYearStart.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(Calendar.getInstance().get(Calendar.YEAR) + "-01-01"));
            //今年结束时间
            Calendar thisYearEnd = Calendar.getInstance();
            thisYearEnd.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(Calendar.getInstance().get(Calendar.YEAR) + "-12-31"));
            //去年开始时间
            Calendar lastYearStart = Calendar.getInstance();
            lastYearStart.setTime(new SimpleDateFormat("yyyy-MM-dd").parse((Calendar.getInstance().get(Calendar.YEAR)-1) + "-01-01"));
            //去年结束时间
            Calendar lastYearEnd = Calendar.getInstance();
            lastYearEnd.setTime(new SimpleDateFormat("yyyy-MM-dd").parse((Calendar.getInstance().get(Calendar.YEAR)-1) + "-12-31"));
            //筛选开始时间
            Calendar checkStart = Calendar.getInstance();
            checkStart.setTime(startDate);
            //筛选结束时间
            Calendar checkEnd= Calendar.getInstance();
            checkEnd.setTime(endDate);
            //构建条件
            LambdaQueryWrapper<Analysis> queryWrapper = new LambdaQueryWrapper<>();
            //全部为去年数据
            if (checkEnd.before(lastYearEnd)){
                queryWrapper.between(Analysis::getRecordDate,startDate,endDate);
                //全部为去年数据
                List<Analysis> analysisList = analysisMapper.selectList(queryWrapper);
                Map<String,Object> map = new HashMap<>();
                List<Map<String,Object>> list = new ArrayList<>();
                for (Analysis analysis : analysisList) {
                    if ("101".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumRegistered());
                        list.add(map);
                    }
                    if ("102".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumActive());
                        list.add(map);
                    }
                    if ("103".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumRetention1d());
                        list.add(map);
                    }
                }
                Map<String,List<Map<String,Object>>> params = new HashMap<>();
                params.put("thisYear",null);
                params.put("lastYear",list);
                return params;

            }
            //全部为今年数据
            if (checkStart.after(thisYearStart)){
                //全部为今年数据
                queryWrapper.between(Analysis::getRecordDate,startDate,endDate);
                List<Analysis> analysisList = analysisMapper.selectList(queryWrapper);
                Map<String,Object> map = new HashMap<>();
                List<Map<String,Object>> list = new ArrayList<>();
                for (Analysis analysis : analysisList) {
                    if ("101".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumRegistered());
                        list.add(map);
                    }
                    if ("102".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumActive());
                        list.add(map);
                    }
                    if ("103".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumRetention1d());
                        list.add(map);
                    }
                }
                Map<String,List<Map<String,Object>>> params = new HashMap<>();
                params.put("thisYear",list);
                params.put("lastYear",null);
                return params;
            }
            //一部分为纪今年的数据，一部分为去年的数据
            if (checkStart.before(lastYearEnd) && checkEnd.before(lastYearEnd)){
                //一部分为去年数据，一部分为今年数据
                queryWrapper.between(Analysis::getRecordDate, startDate, lastYearEnd);
                List<Analysis> analysisList = analysisMapper.selectList(queryWrapper);
                Map<String,Object> map = new HashMap<>();
                List<Map<String,Object>> list = new ArrayList<>();
                for (Analysis analysis : analysisList) {
                    if ("101".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumRegistered());
                        list.add(map);
                    }
                    if ("102".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumActive());
                        list.add(map);
                    }
                    if ("103".equals(type)){
                        map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map.put("amount",analysis.getNumRetention1d());
                        list.add(map);
                    }
                }

                LambdaQueryWrapper<Analysis> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.between(Analysis::getRecordDate, thisYearStart, endDate);
                List<Analysis> analysisList2 = analysisMapper.selectList(queryWrapper2);
                Map<String,Object> map2 = new HashMap<>();
                List<Map<String,Object>> list2 = new ArrayList<>();
                for (Analysis analysis : analysisList2) {
                    if ("101".equals(type)){
                        map2.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map2.put("amount",analysis.getNumRegistered());
                        list2.add(map);
                    }
                    if ("102".equals(type)){
                        map2.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map2.put("amount",analysis.getNumActive());
                        list2.add(map);
                    }
                    if ("103".equals(type)){
                        map2.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                        map2.put("amount",analysis.getNumRetention1d());
                        list2.add(map);
                    }
                }
                Map<String,List<Map<String,Object>>> params = new HashMap<>();
                params.put("thisYear",list2);
                params.put("lastYear",list);
                return params;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, List<Map<String, Object>>> countByTypeAndTime(String type, Date startDate, Date endDate) {
        LambdaQueryWrapper<Analysis> queryWrapper = new LambdaQueryWrapper<>();
        //全部为今年数据
        queryWrapper.between(Analysis::getRecordDate,startDate,endDate);
        List<Analysis> analysisList = analysisMapper.selectList(queryWrapper);
        Map<String,Object> map = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        for (Analysis analysis : analysisList) {
            if ("101".equals(type)){
                map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                map.put("amount",analysis.getNumRegistered());
                list.add(map);
            }
            if ("102".equals(type)){
                map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                map.put("amount",analysis.getNumActive());
                list.add(map);
            }
            if ("103".equals(type)){
                map.put("title",new SimpleDateFormat("yyyy-MM-dd").format(analysis.getRecordDate()));
                map.put("amount",analysis.getNumRetention1d());
                list.add(map);
            }
        }
        Map<String,List<Map<String,Object>>> params = new HashMap<>();
        params.put("thisYear",list);
        params.put("lastYear",null);
        return params;
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
    public Map<String, List<Map<String, Object>>> countByTimeAndType(String type, String start, String end) {
        String thisYear = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.beginOfYear(new Date()));
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
        List<Map<String, Object>> list = analysisMapper.QueryList(type,start,end);

        if (thisYear.compareTo(end) > 0){
            //去年的数据
            map.put("thisYear",null);
            map.put("lastYear",list);
        }else if (start.compareTo(thisYear) > 0){
            //今年的数据
            map.put("thisYear",list);
            map.put("lastYear",null);
        }else {
            //去年和今年的数据
            List<Map<String, Object>> lastYearList = analysisMapper.QueryList(type, start, thisYear);
            List<Map<String, Object>> thisYearList = analysisMapper.QueryList(type, thisYear, end);
            map.put("thisYear",thisYearList);
            map.put("lastYear",lastYearList);
        }
        return map;
    }
}
