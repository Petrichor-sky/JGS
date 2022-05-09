package com.itheima.test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class DateTest {



    @Test
    public void test(){
        DateTime dateTime = DateUtil.parseDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        //DateTime dateTime = DateUtil.parseDate("2020-09-08");
        /*DateTime yesterday = DateUtil.yesterday();
        System.out.println(dateTime);
        System.out.println(yesterday);*/
        //System.out.println(DateUtil.dayOfWeek(new Date()));
    /*    String format3 = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.date());
        String format = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.lastWeek());
        String format2 = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.lastMonth());
        System.out.println(format3);
        System.out.println(format);
        System.out.println(format2);*/
        //System.out.println(DateUtil.beginOfYear(new Date()));
        String end = new SimpleDateFormat("yyyy-MM-dd").format(new Date(1652115368141L));
        String start = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.beginOfYear(new Date()));
        String ed = "2021-05-05";
        String sd = "2020-05-05";
        System.out.println(sd.compareTo(ed));
    }
}
