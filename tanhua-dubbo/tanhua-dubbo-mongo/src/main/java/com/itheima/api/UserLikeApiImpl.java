package com.itheima.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.mongo.UserLike;
import com.itheima.pojo.Count;
import com.itheima.pojo.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class UserLikeApiImpl implements UserLikeApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存或者修改
     *
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据不同条件进行查询
     *
     * @param type
     * @param userInfo
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<UserLike> findList(String type, UserInfo userInfo, Long userId, Integer page, Integer pageSize) {
        //构造分页和查询条件
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);

        if ("1".equals(type)) {
            List<UserLike> vos = new ArrayList<>();
            for (UserLike userLike : userLikeList) {
                Query query2 = Query.query(Criteria.where("userId").is(userLike.getLikeUserId())
                        .and("likeUserId").is(userId)
                        .and("isLike").is(true)).with(pageable);
                boolean exists = mongoTemplate.exists(query2, UserLike.class);
                if (exists) {
                    vos.add(userLike);
                }
            }
            return vos;
        } else if ("2".equals(type)) {
            query.with(pageable);
            //我喜欢的数据
            return mongoTemplate.find(query, UserLike.class);
        }
        return null;
    }

    @Override
    public List<UserLike> findInfoByType3(String type, UserInfo userInfo, Long userId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        //喜欢我的数据
        Query query1 = Query.query(Criteria.where("likeUserId").is(userId)
                .and("isLike").is(true)).with(pageable);
        return mongoTemplate.find(query1, UserLike.class);
    }

    /**
     * 根据用户id和参观者的id查询是否喜欢
     *
     * @param userId
     * @param visitorUserId
     * @return
     */
    @Override
    public Boolean findByVisitorId(Long userId, Long visitorUserId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("visitorUserId").is(visitorUserId)
                .and("isLike").is(true));
        return mongoTemplate.exists(query, UserLike.class);
    }

    /**
     * 查询不同条件的统计
     *
     * @param userId
     * @return
     */
    @Override
    public Count CountByUserId(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("isLike").is(true));

        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        Count count = new Count();
        //我喜欢的数量
        count.setLoveCount(userLikeList.size());
        List<Long> likeUserIds = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);

        Query query2 = Query.query(Criteria.where("userId").in(likeUserIds)
                .and("likeUserId").is(userId)
                .and("isLike").is(true));
        long eachLoveCount = mongoTemplate.count(query2, UserLike.class);
        //互相喜欢的数量
        count.setEachLoveCount(Convert.toInt(eachLoveCount));
        Query query1 = Query.query(Criteria.where("likeUserId").is(userId)
                .and("isLike").is(true));
        long fanCount = mongoTemplate.count(query1, UserLike.class);
        count.setFanCount(Convert.toInt(fanCount));
        return count;
    }

    @Override
    public Boolean hasEachLove(Long userId, Long likeUserId) {
        Query query2 = Query.query(Criteria.where("userId").is(userId)
                .and("likeUserId").is(likeUserId).and("isLike").is(true));
        return mongoTemplate.exists(query2, UserLike.class);
    }
}
