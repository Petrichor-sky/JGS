package com.itheima.api;

import com.itheima.mongo.Friend;
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
public class FriendApiImpl implements FriendApi{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long friendId) {
        Query query1 = Query.query(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        Friend friend1 = mongoTemplate.findOne(query1, Friend.class);
        if (ObjectUtils.isEmpty(friend1)){
            //如果不存在的话，则添加自己的数据
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            //执行添加操作
            mongoTemplate.save(friend);
        }
        Query query2 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(userId));
        Friend friend2 = mongoTemplate.findOne(query2, Friend.class);
        if (ObjectUtils.isEmpty(friend2)){
            //如果不存在的话，则添加自己的数据
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());
            //执行添加操作
            mongoTemplate.save(friend);
        }
    }

    /**
     * 根据登陆者id查询对应的好友数据
     * @param userId
     * @return
     */
    @Override
    public List<Friend> findByUserId(Long userId,Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageable);
        return mongoTemplate.find(query,Friend.class);
    }

    /**
     * 删除环信好友
     */
    @Override
    public void delete(Long userId, Long friendId) {
        //设置条件
        Query query1 = Query.query(Criteria.where("userId").is(userId).and("friendId").is(friendId));
        boolean exists1 = mongoTemplate.exists(query1, Friend.class);
        if (exists1){
            mongoTemplate.remove(query1,Friend.class);
        }
        Query query2 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(userId));
        boolean exists2 = mongoTemplate.exists(query2, Friend.class);
        if (exists2){
            mongoTemplate.remove(query2,Friend.class);
        }
    }
}
