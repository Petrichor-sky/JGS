package com.itheima.vo;

import com.itheima.mongo.Movement;
import com.itheima.mongo.UserLocation;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.RelativeDateFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementsVo  implements Serializable {

    private String id; //动态id

    private Long userId; //用户id
    private String avatar; //头像
    private String nickname; //昵称
    private String gender; //性别 man woman
    private Integer age; //年龄
    private String[] tags; //标签


    private String textContent; //文字动态
    private String[] imageContent; //图片动态
    private String distance; //距离
    private String createDate; //发布时间 如: 10分钟前
    private Integer likeCount; //点赞数
    private Integer commentCount; //评论数
    private Integer loveCount; //喜欢数

    private Integer topState = 1;//0表示未置顶
    private Integer hasLiked; //是否点赞（1是，0否）
    private Integer hasLoved; //是否喜欢（1是，0否）


    public static MovementsVo init(UserInfo userInfo, Movement item) {
        MovementsVo vo = new MovementsVo();
        //设置动态数据
        BeanUtils.copyProperties(item, vo);
        vo.setId(item.getId().toHexString());
        //设置用户数据
        BeanUtils.copyProperties(userInfo, vo);
        if(!StringUtils.isEmpty(userInfo.getTags())) {
            vo.setTags(userInfo.getTags().split(","));
        }
        //图片列表
        vo.setImageContent(item.getMedias().toArray(new String[]{}));
        //距离
        vo.setDistance("500米");
        Date date = new Date(item.getCreated());
        vo.setCreateDate(RelativeDateFormat.format(date));
        //设置是否点赞(后续处理)
        vo.setHasLoved(0);
        vo.setHasLiked(0);
        return vo;
    }

    public static String getDistance(UserLocation user1, UserLocation user2){
        GlobalCoordinates source = new GlobalCoordinates(user1.getLocation().getX(),user2.getLocation().getY());
        GlobalCoordinates target = new GlobalCoordinates(user1.getLocation().getX(),user2.getLocation().getY());
        double meter = getDistanceMeter(source, target, Ellipsoid.WGS84);
        long round = Math.round(meter);
        if (round < 1000 || round >= 0){
            return round + "米";
        }else {
            return Math.round(meter / 1000) + "千米";
        }
    }
    public static double getDistanceMeter(GlobalCoordinates gpsFrom,GlobalCoordinates gpsTo,Ellipsoid ellipsoid){
        GeodeticCurve geodeticCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid,gpsFrom,gpsTo);
        return geodeticCurve.getEllipsoidalDistance();
    }
}