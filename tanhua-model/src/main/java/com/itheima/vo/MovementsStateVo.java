package com.itheima.vo;

import com.itheima.mongo.Movement;
import com.itheima.pojo.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class MovementsStateVo implements Serializable {
    private String id; //动态id
    private String nickname; //昵称
    private Long userId; //用户id
    private String avatar; //头像
    private String createDate; //发布时间 如: 10分钟前
    private String textContent; //文字动态
    private Integer state = 0;//状态 0：未审（默认），1：通过，2：驳回
    private Integer likeCount; //点赞数
    private Integer commentCount; //评论数
    private String[] imageContent; //图片动态

    public static MovementsStateVo init(UserInfo userInfo, Movement item) {
        MovementsStateVo vo = new MovementsStateVo();
        //设置动态数据
        BeanUtils.copyProperties(item, vo);
        vo.setId(item.getId().toHexString());
        //设置用户数据
        BeanUtils.copyProperties(userInfo, vo);
        //图片列表
        vo.setImageContent(item.getMedias().toArray(new String[]{}));
        Date date = new Date(item.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
        vo.setId(item.getId().toHexString());
        return vo;
    }


}
