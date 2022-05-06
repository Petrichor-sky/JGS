package com.itheima.api;

import cn.hutool.core.collection.CollUtil;
import com.itheima.mongo.Comment;
import com.itheima.enums.CommentType;
import com.itheima.mongo.Movement;
import com.itheima.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;

import java.util.List;

@DubboService
public class CommentApiImpl implements CommentApi{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Comment> findComments(String movementId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(CommentType.COMMENT.getType())).with(pageable);
        return mongoTemplate.find(query,Comment.class);
    }

    /**
     * 发布评论
     * @param comment
     */
    @Override
    public Integer save(Comment comment) {
        //查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        //想comment对象设置被评论人属性
        if (!ObjectUtils.isEmpty(movement)){
             comment.setPublishUserId(movement.getUserId());
        }
        Video video = mongoTemplate.findById(comment.getPublishId(), Video.class);
        if (!ObjectUtils.isEmpty(video)){
            comment.setPublishUserId(video.getUserId());
        }
        //保存到数据库
        mongoTemplate.save(comment);
        //更新动态表中的对应字段
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()){
            update.inc("likeCount",1);
        }else if (comment.getCommentType() == CommentType.COMMENT.getType()){
            update.inc("commentCount",1);
        }else {
            update.inc("loveCount",1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        //获取更新后的最新数据
        options.returnNew(true);
        if (!ObjectUtils.isEmpty(movement)){
            Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
            return modify.statisCount(comment.getCommentType());
        }
        if (!ObjectUtils.isEmpty(video)){
            Video modify = mongoTemplate.findAndModify(query, update, options, Video.class);
            return modify.statisCount(comment.getCommentType());
        }
        return null;
    }

    /**
     * 统计数量
     * @param movementId
     * @return
     */
    @Override
    public Integer countByPublishId(String movementId) {
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(CommentType.COMMENT.getType()));
        return Math.toIntExact(mongoTemplate.count(query, Comment.class));
    }

    /**
     * 判断是否存在
     * @param movementId
     * @param userId
     * @param commentType
     * @return
     */
    @Override
    public boolean hasComment(String movementId, Long userId, CommentType commentType) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("publishId").is(new ObjectId(movementId))
                .and("commentType").is(commentType.getType()));
        return mongoTemplate.exists(query,Comment.class);
    }

    /**
     * 取消动态点赞
     * @param comment
     * @return
     */
    @Override
    public Integer delete(Comment comment) {
        //删除Comment表中的数据
        Query query = Query.query(Criteria.where("userId").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType()));
        //执行删除操作
        mongoTemplate.remove(query,Comment.class);
        //修改动态表中的总量
        Query movementQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()){
            update.inc("likeCount",-1);
        }else if (comment.getCommentType() == CommentType.COMMENT.getType()){
            update.inc("commentCount",-1);
        }else {
            update.inc("loveCount",-1);
        }
        //查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        //想comment对象设置被评论人属性
        if (!ObjectUtils.isEmpty(movement)){
            comment.setPublishUserId(movement.getUserId());
        }
        Video video = mongoTemplate.findById(comment.getPublishId(), Video.class);
        if (!ObjectUtils.isEmpty(video)){
            comment.setPublishUserId(video.getUserId());
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        //获取更新后的最新数据
        options.returnNew(true);
        if (!ObjectUtils.isEmpty(movement)){
            Movement modify = mongoTemplate.findAndModify(movementQuery, update, options, Movement.class);
            return modify.statisCount(comment.getCommentType());
        }
        if (!ObjectUtils.isEmpty(video)){
            Video modify = mongoTemplate.findAndModify(movementQuery, update, options, Video.class);
            return modify.statisCount(comment.getCommentType());
        }
        return null;
    }

    @Override
    public Comment findCommentById(String commentId) {
        return  mongoTemplate.findById(commentId,Comment.class);
    }

    /**
     * 评论点赞
     * @param comment
     * @param commentId
     * @return
     */
    @Override
    public Integer saveLikeComment(Comment comment,String commentId) {
        //执行保存操作
        mongoTemplate.save(comment);
        Query query = Query.query(Criteria.where("id").is(commentId));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()){
            update.inc("likeCount",1);
        }
        //构建更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Comment modify = mongoTemplate.findAndModify(query, update, options, Comment.class);
        return modify.statisCount(comment.getCommentType());
    }

    /**
     * 取消评论点赞
     * @param comment
     * @return
     */
    @Override
    public Integer deleteLikeComment(Comment comment) {
        //设置条件
        Query query = Query.query(Criteria.where("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType())
                .and("userId").is(comment.getUserId()));
        mongoTemplate.remove(query,Comment.class);
        Query commentQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()){
            update.inc("likeCount",-1);
        }
        //设置更新条件
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Comment modify = mongoTemplate.findAndModify(commentQuery, update, options, Comment.class);
        //返回输出结果
        return modify.statisCount(comment.getCommentType());
    }
}
