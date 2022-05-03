package com.itheima.vo;

import com.itheima.mongo.RecommendUser;
import com.itheima.pojo.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class TodayBest {
    private Long id; //用户id
    private String avatar;
    private String nickname;
    private String gender; //性别 man woman
    private Integer age;
    private String[] tags;
    private Long fateValue; //缘分值


    public static TodayBest init(UserInfo userInfo, RecommendUser recommendUser) {
        TodayBest vo = new TodayBest();
        BeanUtils.copyProperties(userInfo,vo);
        if(userInfo.getTags() != null) {
            vo.setTags(userInfo.getTags().split(","));
        }
        vo.setFateValue(recommendUser.getScore().longValue());
        return vo;
    }
}
