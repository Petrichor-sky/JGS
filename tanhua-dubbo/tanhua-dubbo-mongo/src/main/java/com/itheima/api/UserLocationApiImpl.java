package com.itheima.api;

import cn.hutool.core.collection.CollUtil;
import com.itheima.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;

import java.util.List;

@DubboService
public class UserLocationApiImpl implements UserLocationApi{
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 更新地址位置
     * @param userId
     * @param longitude
     * @param latitude
     * @param address
     * @return
     */
    @Override
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            //根据用户查询位置信息
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
            if (ObjectUtils.isEmpty(userLocation)) {
                userLocation = new UserLocation();
                userLocation.setUserId(userId);
                userLocation.setAddress(address);
                userLocation.setCreated(System.currentTimeMillis());
                userLocation.setUpdated(System.currentTimeMillis());
                userLocation.setLastUpdated(System.currentTimeMillis());
                userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
                mongoTemplate.save(userLocation);
            } else {
                //如果存在，则更新
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", userLocation.getUpdated())
                        .set("address",address);
                mongoTemplate.updateFirst(query, update, UserLocation.class);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据位置搜索附近的人
     * @param userId
     * @param metre
     * @return
     */
    @Override
    public List<Long> queryNearUser(Long userId, Double metre) {
        //根据id查询用户的位置信息
        Query query = Query.query(Criteria.where("userId").is(userId));
        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
        if (ObjectUtils.isEmpty(userLocation)){
            return null;
        }
        //2.以用户的当前位置为原点
        GeoJsonPoint point = userLocation.getLocation();
        //3.绘制半径
        Distance distance = new Distance(metre / 1000, Metrics.KILOMETERS);
        //4.绘制圆形
        Circle circle = new Circle(point,distance);
        //5.查询
        Query locationQuery = Query.query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> list = mongoTemplate.find(locationQuery, UserLocation.class);
        return CollUtil.getFieldValues(list,"userId",Long.class);
    }
}
