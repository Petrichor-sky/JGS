package com.itheima.api;

import com.itheima.mongo.FocusUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DubboService
public class FocusUserApiImpl implements FocusUserApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Boolean hasFocus(Long followUserId, Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("followUserId").is(followUserId));
        return mongoTemplate.exists(query, FocusUser.class);
    }

    /**
     * 保存关注数据
     * @param followUserId
     * @param userId
     */
    @Override
    public void save(Long followUserId, Long userId) {
        Query query = Query.query(Criteria.where("followUserId").is(followUserId)
                .and("userId").is(userId));
        boolean exists = mongoTemplate.exists(query, FocusUser.class);
        if (!exists){
            FocusUser user = new FocusUser();
            user.setUserId(userId);
            user.setFollowUserId(followUserId);
            user.setCreated(System.currentTimeMillis());
            mongoTemplate.save(user);
        }
    }

    /**
     * 删除关注数据
     * @param followUserId
     * @param userId
     */
    @Override
    public void delete(Long followUserId, Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("followUserId").is(followUserId));
        mongoTemplate.remove(query,FocusUser.class);
    }
}
