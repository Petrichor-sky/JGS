package com.itheima.vo;

import com.itheima.mongo.Visitors;
import com.itheima.pojo.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
public class VisitorVo implements Serializable {
    private Long userId; //用户id
    private String avatar; //头像
    private String nickname; //昵称
    private String gender; //性别 man woman
    private Integer age; //年龄
    private String[] tags; //标签

    private Integer fateValue; //缘分值

    public static VisitorVo init(UserInfo userInfo, Visitors visitors) {
        VisitorVo vo = new VisitorVo();
        //设置动态数据
        BeanUtils.copyProperties(userInfo, vo);
        if(!StringUtils.isEmpty(userInfo.getTags())) {
            vo.setTags(userInfo.getTags().split(","));
        }
        vo.setUserId(userInfo.getId());
        vo.setFateValue(visitors.getScore().intValue());
        return vo;
    }
}
