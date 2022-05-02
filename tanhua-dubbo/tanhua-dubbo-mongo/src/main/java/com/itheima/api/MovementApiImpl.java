package com.itheima.api;

import cn.hutool.core.collection.CollUtil;
import com.itheima.mongo.Movement;
import com.itheima.mongo.MovementTimeLine;
import com.itheima.utils.IdWorker;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi {
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Movement saveMovements(Movement movement) {
        //完善信息
        movement.setPid(idWorker.getNextId("movement"));
        movement.setCreated(System.currentTimeMillis());
        movement.setLatitude("121.588627");
        movement.setLocationName("中国北京");
        //执行保存操作
        return mongoTemplate.insert(movement);
    }

    @Override
    public List<Movement> findByUserId(Long userId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageable);
        return mongoTemplate.find(query, Movement.class);
    }

    @Override
    public List<Movement> findByMoveIds(List<Object> movementIds) {
        Query query = Query.query(Criteria.where("id").in(movementIds)).with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Movement.class);
    }

    @Override
    public List<Movement> findByFriendId(Long uid, Integer page, Integer pageSize) {
        //查询好友时间线表
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("friendId").is(uid)).with(pageable);
        List<MovementTimeLine> lines = mongoTemplate.find(query, MovementTimeLine.class);
        //获取动态的id集合
        List<Object> movementIds = CollUtil.getFieldValues(lines, "movementId");
        //根据动态的id查询动态的详情
        Query movementQuery = Query.query(Criteria.where("id").in(movementIds));
        return mongoTemplate.find(movementQuery,Movement.class);
    }

    @Override
    public List<Movement> randomMovements(Integer pageSize) {
        //1.设置统计对象，设置统计参数，Movement.class操作某一个表,
        TypedAggregation<Movement> aggregation = Aggregation.newAggregation(Movement.class,Aggregation.sample(pageSize));
        //2.调用方法，统计相关的数据,Movement.class指定返回什么类型的对象
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation,Movement.class);
        return results.getMappedResults();
    }

    @Override
    public List<Movement> findMoveByPids(List<Long> pids) {
        Query query = Query.query(Criteria.where("pid").in(pids));
        return mongoTemplate.find(query,Movement.class);
    }

    @Override
    public Movement findByMoveId(String movementId) {
        Query query = Query.query(Criteria.where("id").is(movementId));
        return mongoTemplate.findOne(query,Movement.class);
    }

    @Override
    public Integer countByUserId(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return Math.toIntExact(mongoTemplate.count(query, Movement.class));
    }
}
