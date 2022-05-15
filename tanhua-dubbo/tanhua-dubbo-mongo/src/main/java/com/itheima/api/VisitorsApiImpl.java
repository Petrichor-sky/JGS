package com.itheima.api;

import com.itheima.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ObjectUtils;

import java.util.List;

@DubboService
public class VisitorsApiImpl implements VisitorsApi{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Visitors> findVisitorsByUserId(Long userId,Integer page,Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1,pageSize,Sort.by(Sort.Order.desc("score")));
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageable);
        return mongoTemplate.find(query,Visitors.class);
    }

    /**
     * 保存访客数据
     * 同一个用户，一天之内只能保存一次访问数据
     * @param visitors
     */
    @Override
    public void save(Visitors visitors) {
        if(ObjectUtils.nullSafeEquals(visitors.getVisitorUserId(),visitors.getUserId())){
            return;
        }
        //1.查询访客数据
        Query query = Query.query(Criteria.where("userId").is(visitors.getUserId())
                .and("visitorUserId").is(visitors.getVisitorUserId())
                .and("visitDate").is(visitors.getVisitDate()));
        //2.如果不存在的话，则保存
        if (!mongoTemplate.exists(query,Visitors.class)){
            mongoTemplate.save(visitors);
        }
    }

    /**
     * 查询首页访客列表
     * @param date
     * @param userId
     * @return
     */
    @Override
    public List<Visitors> queryMyVisitors(Long date, Long userId) {
        Criteria criteria = Criteria.where("userId").is(userId).and("visitorUserId").ne(userId);
        if (date != null){
            criteria.and("date").gt(date);
        }
        Query query = Query.query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));
        return mongoTemplate.find(query,Visitors.class);
    }















   /* @Override
    public String save(Long userId, Long visitorUserId, String from) {
        //校验
        if (!ObjectUtil.isAllEmpty(userId,visitorUserId,from)){
            return null;
        }
        //查询访客用户在今天是否已经记录过， 如果是，则不在记录
        String today = DateUtil.today();
        long minDate = DateUtil.parseDateTime(today + "00:00:00").getTime();
        long maxDate = DateUtil.parseDateTime(today + "23:59:59").getTime();

        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("visitorUserId").is(visitorUserId).andOperator(Criteria.where("date").gte(minDate),
                        Criteria.where("date").lte(maxDate)));
        long count = mongoTemplate.count(query, Visitors.class);
        if (count>0){
            //今天已经记录过的
            return null;
        }
        Visitors visitors = new Visitors();
        visitors.setUserId(userId);
        visitors.setVisitorUserId(visitorUserId);
        visitors.setFrom(from);
        visitors.setDate(System.currentTimeMillis());
        visitors.setId(ObjectId.get());
        //存储数据
        mongoTemplate.save(visitors);
        return visitors.getId().toHexString();
    }

    @Override
    public List<Visitors> queryMyVisitor(Long userId) {

        return null;
    }*/
}
