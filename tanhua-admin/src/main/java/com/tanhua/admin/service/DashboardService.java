package com.tanhua.admin.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.itheima.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 新增、活跃用户、次日留存率
     * @param
     * @return
     */
    public Map<String,List<Map<String,Object>>> getCount(Long sd,Long ed,String type){
        Date startDate = new Date(sd);
        Date endDate = new Date(ed);
        String start = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        String end = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        String lastYearStart = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.offset(startDate, DateField.YEAR, -1));
        String lastYearEnd = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.offset(endDate, DateField.YEAR, -1));
       return analysisService.countByTimeAndType(type,start,end,lastYearStart,lastYearEnd);

    }

    /**
     * 概要统计信息
     * @return
     */
    public AnalysisSummaryVo summary() {
        //创建对象
        AnalysisSummaryVo vo = new AnalysisSummaryVo();
        //获取当前Date时间
        String today = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.date());
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.yesterday());
        String beforeWeek = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.lastWeek());
        String beforeMonth = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.lastMonth());
        //获取累计用户
        vo.setCumulativeUsers(analysisService.CountCumulativeUsers());
        //过去30天活跃用户
        vo.setActivePassMonth(analysisService.QueryActiveCount(today,beforeMonth));
        //过去7天的活跃用户
        vo.setActivePassWeek(analysisService.QueryActiveCount(today,beforeWeek));
        //今日的活跃用户
        vo.setActiveUsersToday(analysisService.QueryActiveCount(today,today));
        //今日新增用户
        vo.setNewUsersToday(analysisService.QueryNewUsersCount(today));
        //今日登录次数
        vo.setLoginTimesToday(analysisService.QueryTodayLoginTimes(today));
        //今日活跃用户环比
        vo.setActiveUsersTodayRate(compare(vo.getActiveUsersToday(),analysisService.QueryActiveCount(yesterday,yesterday)));
        //今日新增用户环比
        vo.setNewUsersTodayRate(compare(vo.getNewUsersToday(),analysisService.QueryNewUsersCount(yesterday)));
        //今日登录次数环比
        vo.setLoginTimesTodayRate(compare(vo.getLoginTimesToday(),analysisService.QueryTodayLoginTimes(yesterday)));
        //返回结果
        return vo;
    }

    /**
     * 比较环比增长
     * @param todayCount,
     * @param yesterdayCount
     * @return
     */
    private BigDecimal compare(Long todayCount, Long yesterdayCount) {
        BigDecimal result;
        if (yesterdayCount == 0){
            //如果昨日数据为0的话，则为倍数增长
            result = new BigDecimal((todayCount - yesterdayCount) * 100);
        }else {
            //如果昨日数据不为空的话，则环比增长
            result = BigDecimal.valueOf((todayCount - yesterdayCount) * 100)
                    .divide(BigDecimal.valueOf(yesterdayCount),2,BigDecimal.ROUND_HALF_DOWN);
        }
        return result;
    }
}
