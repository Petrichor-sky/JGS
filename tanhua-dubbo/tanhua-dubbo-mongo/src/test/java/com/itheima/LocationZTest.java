package com.itheima;

import com.itheima.api.UserLocationApi;
import com.itheima.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootTest
public class LocationZTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @DubboReference
    private UserLocationApi userLocationApi;


    @Test
    public void test(){
        Query query = Query.query(Criteria.where("userId").is(106));
        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
        double x = userLocation.getLocation().getX();
        System.out.println(x);
        GlobalCoordinates source = new GlobalCoordinates(116.353885,40.065911);
        GlobalCoordinates target = new GlobalCoordinates(116.352115,40.067441);
        double meter = getDistanceMeter(source, target, Ellipsoid.WGS84);
        long round = Math.round(meter);
        System.out.println(round + "米");
    }
    public static double getDistanceMeter(GlobalCoordinates gpsFrom,GlobalCoordinates gpsTo,Ellipsoid ellipsoid){
        GeodeticCurve  geodeticCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid,gpsFrom,gpsTo);
        return geodeticCurve.getEllipsoidalDistance();
    }
}
