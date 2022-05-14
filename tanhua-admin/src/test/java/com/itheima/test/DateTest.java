package com.itheima.test;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class DateTest {



    @Test
    public void test(){
        Date date = new Date();
        long time = date.getTime();
        System.out.println(time);
        try{
            long time1 = new SimpleDateFormat("yyyy-MM-dd").parse("2022-05-13").getTime();
            long time2 = new SimpleDateFormat("yyyy-MM-dd").parse("2022-05-15").getTime();
            System.out.println(time1);
            System.out.println(time2);

        }catch (Exception e){
            e.printStackTrace();
        }



        //DateTime dateTime = DateUtil.parseDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        //Date date = new Date(1652115368141L);
        //String end = new SimpleDateFormat("yyyy-MM-dd").format(date);
        //System.out.println(end);
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(date);
        //calendar.add(Calendar.YEAR,-1);
       // Date time = calendar.getTime();
        //String format = new SimpleDateFormat("yyyy-MM-dd").format(time);
        //System.out.println(format);
        //String lastYearStart = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.offset(date, DateField.YEAR, -1));
        //System.out.println(lastYearStart);
        //String lastYearStart = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.offset(date, DateField.YEAR, -1));
        //String start = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.beginOfYear(new Date()));
        //System.out.println(start);
      /*  try {
            String ed = "2020-05-05";
            System.out.println(Convert.toInt(new SimpleDateFormat("yyyy-MM-dd").parse(ed).getTime()));
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //String ed = "2021-05-05";
        //String sd = "2020-05-05";
        //System.out.println(sd.compareTo(ed));
        //String today = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.date());
        //String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.yesterday());

     /*   Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.yesterday());
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        String format = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        System.out.println(format);*/


    /*    try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(yesterday);
            System.out.println(date);
            System.out.println(yesterdayTime);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(today);
        System.out.println(yesterday);*/


    }
}
