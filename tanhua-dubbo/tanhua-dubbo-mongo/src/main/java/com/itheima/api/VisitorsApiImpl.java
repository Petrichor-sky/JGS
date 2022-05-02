package com.itheima.api;

import com.itheima.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class VisitorsApiImpl implements VisitorsApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<Visitors> findVisitorsByUserId(Long uid) {
        Query query = Query.query(Criteria.where("visitorUserId").is(uid));
        return mongoTemplate.find(query,Visitors.class);
    }
}
