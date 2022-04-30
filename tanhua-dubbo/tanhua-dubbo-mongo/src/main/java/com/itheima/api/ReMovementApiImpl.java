package com.itheima.api;

import com.itheima.mongo.RecommendMovement;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class ReMovementApiImpl implements ReMovementApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 推荐动态的实现
     * @param uid
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<RecommendMovement> findMoveByUserId(Long uid, Integer page, Integer pageSize) {
        //分页对象
        Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").in(uid)).with(pageable);
        return mongoTemplate.find(query,RecommendMovement.class);
    }
}
