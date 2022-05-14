package com.itheima.api;

import com.itheima.mongo.MockMovement;
import com.itheima.pojo.Totals;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class MockMovementApiImpl implements MockMovementApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(MockMovement movement) {
        mongoTemplate.save(movement);
    }

    //消息翻页
    @Override
    public List<MockMovement> findMovementByPage(Long uid, String state, Integer page, Integer pageSize, String sortProp, String sortOrder, Long sd, Long ed, String id) {


        Criteria criteria = new Criteria();
        Query query = new Query();
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if (!ObjectUtils.isEmpty(uid)) {
            criteria.and("userId").is(uid);
        }
        if (!StringUtils.isEmpty(sortProp) && "ascending".equals(sortOrder)) {
            sortProp = sortProp.trim();
            sortOrder = sortOrder.trim();
            //采用升序的方式排序
            query.addCriteria(criteria).with(Sort.by(Sort.Order.asc(sortProp)));
        }
        if (!StringUtils.isEmpty(sortProp) && "descending".equals(sortOrder)) {
            //采用升序的方式排序
            sortOrder = sortOrder.trim();
            query.addCriteria(criteria).with(Sort.by(Sort.Order.desc(sortProp)));
        }
        if (!ObjectUtils.isEmpty(sd) && !ObjectUtils.isEmpty(ed)) {
            query.addCriteria(Criteria.where("createDate").gte(sd).lte(ed));
        }
        if (!StringUtils.isEmpty(id)) {
            query.addCriteria(Criteria.where("id").is(new ObjectId(id)));
        }
        if (!StringUtils.isEmpty(state)) {
            query.addCriteria(Criteria.where("state").is(state));
        }
        query.with(pageable);
        return mongoTemplate.find(query, MockMovement.class);
    }

    @Override
    public List<Totals> Count() {
        List<Totals> list = new ArrayList<>();
        Integer all = Math.toIntExact(mongoTemplate.count(new Query(), MockMovement.class));
        Totals t0 = new Totals("全部", "all", all);
        Query query3 = Query.query(Criteria.where("state").is("3"));
        Integer count3 = Math.toIntExact(mongoTemplate.count(query3, MockMovement.class));
        Totals t3 = new Totals("待审核", "3", count3);
        Query query4 = Query.query(Criteria.where("state").is("4"));
        Integer count4 = Math.toIntExact(mongoTemplate.count(query4, MockMovement.class));
        Totals t4 = new Totals("已通过", "4", count4);
        Query query5 = Query.query(Criteria.where("state").is("5"));
        Integer count5 = Math.toIntExact(mongoTemplate.count(query5, MockMovement.class));
        Totals t5 = new Totals("已驳回", "5", count5);
        list.add(t0);
        list.add(t3);
        list.add(t4);
        list.add(t5);
        return list;
    }

    /**
     * 审核操作
     * @param objectId
     * @param state
     */
    @Override
    public void updateStateById(ObjectId objectId, String state) {
        Query query = Query.query(Criteria.where("id").is(objectId));
        Update update = Update.update("state",state);
        mongoTemplate.updateFirst(query,update,MockMovement.class);
    }

    /**
     * 消息置顶
     * @param movementId
     */
    @Override
    public void updateTopState(String movementId) {
        Query query = Query.query(Criteria.where("id").is(new ObjectId(movementId)));
        Update update = Update.update("topState",2);
        mongoTemplate.updateFirst(query,update,MockMovement.class);
    }

    /**
     * 消息取消置顶
     * @param movementId
     */
    @Override
    public void downTopState(String movementId) {
        Query query = Query.query(Criteria.where("id").is(new ObjectId(movementId)));
        Update update = Update.update("topState",1);
        mongoTemplate.updateFirst(query,update,MockMovement.class);
    }

    /**
     * 根据id进行查询消息
     * @param movementId
     * @return
     */
    @Override
    public MockMovement findByMoveId(String movementId) {
        Query query = Query.query(Criteria.where("id").is(new ObjectId(movementId)));
        return mongoTemplate.findOne(query,MockMovement.class);
    }
}
