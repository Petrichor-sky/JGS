package com.itheima.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.mongo.Friend;
import com.itheima.mongo.MovementTimeLine;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class TimeLineApiImpl implements TimeLineApi{
    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveTimeLine(Long userId, ObjectId id) {
        //查询当前用户好友的数据
        Query query = Query.query(Criteria.where("userId").is(userId));
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        //拿到所有的用户id
        List<Object> friendId = CollUtil.getFieldValues(friendList, "friendId");
        for (Object fid : friendId) {
            //创建时间线对象
            MovementTimeLine timeLine = new MovementTimeLine();
            timeLine.setMovementId(id);
            timeLine.setUserId(userId);
            timeLine.setFriendId(Convert.toLong(fid));
            timeLine.setCreated(System.currentTimeMillis());
            mongoTemplate.insert(timeLine);
        }
    }

    @Override
    public List<MovementTimeLine> findMovByFid(Long uid, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("friendId").is(uid)).with(pageable);
        return mongoTemplate.find(query, MovementTimeLine.class);
    }

    @Override
    public List<MovementTimeLine> findByFid(Long uid, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1,pageSize,Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("friendId").is(uid)).with(pageable);
        return mongoTemplate.find(query,MovementTimeLine.class);
    }
}
