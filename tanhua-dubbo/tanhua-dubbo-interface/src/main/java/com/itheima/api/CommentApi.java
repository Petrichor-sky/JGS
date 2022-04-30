package com.itheima.api;

import com.itheima.mongo.Comment;

import java.util.List;

public interface CommentApi {
    //根据movementId查询评论列表
    List<Comment> findComments(String movementId, Integer page, Integer pageSize);
    //保存
    void save(Comment comment);

    Integer countByPublishId(String movementId);
}
