package com.itheima.vo;

import cn.hutool.core.convert.Convert;
import com.itheima.mongo.Comment;
import com.itheima.pojo.UserInfo;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class CommentPageVo implements Serializable {
    private String id;
    private String nickname;
    private Integer userId;
    private String content;
    private String createDate;

    public static CommentPageVo init(UserInfo userInfo, Comment item) {
        CommentPageVo vo = new CommentPageVo();
        vo.setNickname(userInfo.getNickname());
        vo.setContent(item.getContent());
        vo.setUserId(Convert.toInt(userInfo.getId()));
        Date date = new Date(item.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        vo.setId(item.getId().toHexString());
        return vo;
    }
}
