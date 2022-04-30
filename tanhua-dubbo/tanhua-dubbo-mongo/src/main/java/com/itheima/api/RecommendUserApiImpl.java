package com.itheima.api;

import com.itheima.mongo.RecommendUser;
import com.itheima.vo.PageResult;
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
public class RecommendUserApiImpl implements RecommendUserApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public RecommendUser findRecommendUserServiceByUserId(Long id) {
        Criteria criteria = Criteria.where("toUserId").is(id);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score")))
                .limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    /**
     * 根据id查询所有推荐者信息
     * @param toUserId  登录者id
     * @param page  当前页面
     * @param pageSize  每页显示条目数
     * @return
     */
    @Override
    public List<RecommendUser> findByUserId(Long toUserId, Integer page, Integer pageSize) {
        //分页条件对象
        Pageable pageable = PageRequest.of(page - 1, pageSize,Sort.by(Sort.Order.desc("score")));
        Query query = Query.query(Criteria.where("toUserId").is(toUserId)).with(pageable);
        return mongoTemplate.find(query,RecommendUser.class);
    }
}
