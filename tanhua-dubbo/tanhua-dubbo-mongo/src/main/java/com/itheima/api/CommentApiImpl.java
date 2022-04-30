package com.itheima.api;

import com.itheima.mongo.Comment;
import com.itheima.enums.CommentType;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

    @Override
    public void save(Comment comment) {
        mongoTemplate.save(comment);
    }

    @Override
    public Integer countByPublishId(String movementId) {
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(movementId)));
        return Math.toIntExact(mongoTemplate.count(query, Comment.class));
    }
}
