package com.itheima.test;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
public class DateTest {



    @Test
    public void test(){
        DateTime dateTime = DateUtil.parseDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        Date date = new Date(1652115368141L);
        String end = new SimpleDateFormat("yyyy-MM-dd").format(date);
        System.out.println(end);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,-1);
        Date time = calendar.getTime();
        String format = new SimpleDateFormat("yyyy-MM-dd").format(time);
        //System.out.println(format);

        String lastYearStart = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.offset(date, DateField.YEAR, -1));
        System.out.println(lastYearStart);
        //String lastYearStart = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.offset(date, DateField.YEAR, -1));


        String start = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.beginOfYear(new Date()));
        //String ed = "2021-05-05";
        //String sd = "2020-05-05";
        //System.out.println(sd.compareTo(ed));
    }
}
