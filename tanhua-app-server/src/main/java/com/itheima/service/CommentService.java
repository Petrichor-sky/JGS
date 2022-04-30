package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import com.itheima.api.CommentApi;
import com.itheima.api.MovementApi;
import com.itheima.api.UserInfoApi;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Comment;
import com.itheima.enums.CommentType;
import com.itheima.mongo.Constants;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.CommentVo;
import com.itheima.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @DubboReference
    private CommentApi commentApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private MovementApi movementApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    /**
     * 评论列表
     * @param movementId
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getCommemts(String movementId, Integer page, Integer pageSize) {
        //动态id
        List<Comment> commentList = commentApi.findComments(movementId,page,pageSize);
        if (CollUtil.isEmpty(commentList)){
            return new PageResult();
        }
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            Long userId = comment.getUserId();
            //通过userId来查询对应的信息
            UserInfo userInfo = userInfoApi.findById(userId);
            commentVoList.add(CommentVo.init(userInfo, comment));
        }
        Integer count = commentApi.countByPublishId(movementId);
        PageResult result = new PageResult();
        result.setPagesize(page);
        result.setPagesize(pageSize);
        result.setItems(commentVoList);
        result.setCounts(count);
        return result;
    }

    /**
     * 发布评论
     * @return
     */
    public void saveCommemts(Map<String, String> map) {
        //获取参数
        String publishId = map.get("movementId");
        String content = map.get("comment");
        //获取登录者的id
        Long uid = ThreadLocalUtils.get();
        //根据动态id来查询
        Long publishUserId = movementApi.findByMoveId(publishId).getUserId();
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setCreated(System.currentTimeMillis());
        comment.setUserId(uid);
        comment.setContent(content);
        comment.setPublishUserId(publishUserId);
        //执行保存操作
        commentApi.save(comment);

    }

    /**
     * 动态点赞
     * @param movementId
     * @return
     */
    public Integer likeComment(String movementId) {
        //查询是否已经点赞
        boolean hasComment = commentApi.hasComment(movementId,ThreadLocalUtils.get(),CommentType.LIKE);
        //如果已经点赞的话，则抛出异常
        if (hasComment){
            throw new BusinessException(ErrorResult.likeError());
        }
        //调用API保存数据到mongodb
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(ThreadLocalUtils.get());
        comment.setCreated(System.currentTimeMillis());
        //进行保存并返回对应的数量
        Integer count = commentApi.save(comment);
        //拼接redis的key，将用户的点赞状态存入redis中
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().put(key,hashKey,"1");
        //返回数量
        return count;
    }

    /**
     * 取消点赞
     * @param movementId
     * @return
     */
    public Integer dislikeComment(String movementId) {
        //判断是否已经点赞
        boolean hasComment = commentApi.hasComment(movementId, ThreadLocalUtils.get(), CommentType.LIKE);
        if (!hasComment){
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //调用API删除删除数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(ThreadLocalUtils.get());
        Integer count = commentApi.delete(comment);
        //拼接redis的key，删除点赞动态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.VIDEO_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().delete(key,hashKey);
        //返回点赞的数量
        return count;
    }
}
