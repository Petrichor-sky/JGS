package com.itheima.api;

import com.itheima.enums.CommentType;
import com.itheima.mongo.Comment;

import java.util.List;

public interface CommentApi {
    //根据movementId查询评论列表
    List<Comment> findComments(String movementId, Integer page, Integer pageSize);
    //保存
    Integer save(Comment comment);

    Integer countByPublishId(String movementId);

    boolean hasComment(String movementId, Long userId, CommentType commentType);

    Integer delete(Comment comment);

    Comment findCommentById(String commentId);

    Integer saveLikeComment(Comment comment,String commentId);

    Integer deleteLikeComment(Comment comment);

    List<Comment> findCommentByUserId(Long userId,CommentType commentType,Integer page,Integer pageSize);

    List<Comment> findCommentsByPublishId(String messageID, Integer page, Integer pageSize);
}
