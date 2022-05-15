package com.tanhua.admin.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.itheima.api.LogApi;
import com.itheima.api.MockUserInfoApi;
import com.itheima.api.UseTimeLogApi;
import com.itheima.vo.AnalysisSummaryVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private AnalysisService analysisService;
    @DubboReference
    private MockUserInfoApi mockUserInfoApi;
    @DubboReference
    private UseTimeLogApi useTimeLogApi;

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(DateUtil.date());
        String yesterday = simpleDateFormat.format(DateUtil.yesterday());
        String beforeWeek = simpleDateFormat.format(DateUtil.lastWeek());
        String beforeMonth = simpleDateFormat.format(DateUtil.lastMonth());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.yesterday());
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String beforeYesterday = simpleDateFormat.format(calendar.getTime());
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
        //昨日的活跃用户
        vo.setActiveUsersYesterday(analysisService.QueryActiveCount(yesterday,yesterday));
        //昨日的活跃用户环比
        vo.setActiveUsersYesterdayRate(compare(vo.getActiveUsersYesterday(),analysisService.QueryActiveCount(beforeYesterday,beforeYesterday)));
        //近7天的平均日使用时间
        vo.setUseTimePassWeek(Convert.toLong(useTimeLogApi.CountUseTime(today,beforeWeek) / 7));
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

    /**
     * 注册用户分布，行业top、年龄、性别、地区
     * @param sd
     * @param ed
     * @return
     */
    @DubboReference
    private LogApi logApi;
    public Map<String, List<Map<String, Object>>> distribution(Long sd, Long ed) {
        Date startDate = new Date(sd);
        Date endDate = new Date(ed);
        String start = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        String end = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        //根据时间查找对应的注册用户的Id
        List<Long> userIds = logApi.findLogByTimeAndType(start,end);
        return mockUserInfoApi.countByTime(userIds);
    }
}
