package com.itheima.api;

import com.itheima.mongo.Visitors;

import java.util.List;

public interface VisitorsApi {
    List<Visitors> findVisitorsByUserId(Long uid);

   /* //保存数据，我的id，访客的id，来源
    String save(Long userId,Long visitorUserId,String from);
    //查询我的访客
    List<Visitors> queryMyVisitor(Long userId);*/

    //保存访客数据
    void save(Visitors visitors);

    //查询首页访客列表
    List<Visitors> queryMyVisitors(Long date, Long userId);
}
