package com.itheima.vo;

import cn.hutool.core.convert.Convert;
import com.itheima.mongo.Comment;
import com.itheima.pojo.UserInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MockCommentVo implements Serializable {
    private String id; //评论id
    private String nickname; //昵称
    private Integer userId;
    private String content; //评论
    private String createDate; //评论时间

    public static MockCommentVo init(UserInfo userInfo, Comment comment) {
        MockCommentVo vo = new MockCommentVo();
        vo.setId(comment.getId().toHexString());
        vo.setNickname(userInfo.getNickname());
        vo.setUserId(Convert.toInt(userInfo.getId()));
        vo.setContent(comment.getContent());
        vo.setCreateDate(comment.getCreated().toString());
        return vo;
    }
}
