package com.itheima.api;

import com.itheima.mongo.Visitors;

import java.util.List;

public interface VisitorsApi {
    List<Visitors> findVisitorsByUserId(Long uid);
}
