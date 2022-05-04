package com.itheima.api;

import com.itheima.mongo.UserLike;
import com.itheima.pojo.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;

@DubboService
public class UserLikeApiImpl implements UserLikeApi{
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存或者修改
     * @param userId
     * @param likeUserId
     * @param isLike
     * @return
     */
    @Override
    public boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike) {
        try {
            //根据条件进行查询
            Query query = Query.query(Criteria.where("userId").is(userId)
                    .and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            if (ObjectUtils.isEmpty(userLike)) {
                //如果不存在的话则进行保存操作
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setIsLike(isLike);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                //执行保存操作
                mongoTemplate.save(userLike);
            } else {
                //如果存在的话，则执行修改操作
                Update update = Update.update("isLike", isLike).set("updated", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, UserLike.class);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
