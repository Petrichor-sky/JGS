package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import com.itheima.api.CommentApi;
import com.itheima.api.MovementApi;
import com.itheima.api.UserInfoApi;
import com.itheima.api.VisitorsApi;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Comment;
import com.itheima.enums.CommentType;
import com.itheima.mongo.Constants;
import com.itheima.mongo.Visitors;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.CommentVo;
import com.itheima.vo.PageResult;
import com.itheima.vo.VisitorVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    @DubboReference
    private VisitorsApi visitorsApi;
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
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        if (CollUtil.isEmpty(commentList)){
            result.setItems(null);
            return result;
        }
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            Long userId = comment.getUserId();
            //通过userId来查询对应的信息
            UserInfo userInfo = userInfoApi.findById(userId);
            CommentVo vo = CommentVo.init(userInfo, comment);
            vo.setHasLiked(commentApi.hasComment(comment.getId().toHexString(),ThreadLocalUtils.get(),CommentType.LIKE) ? 1 : 0);
            commentVoList.add(vo);
        }
        Integer count = commentApi.countByPublishId(movementId);
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
    public Integer likeMovement(String movementId) {
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
    public Integer dislikeMovement(String movementId) {
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

    /**
     * 动态喜欢
     * @param movementId
     * @return
     */
    public Integer loveComment(String movementId) {
        //判断是否已喜欢
        boolean hasComment = commentApi.hasComment(movementId, ThreadLocalUtils.get(), CommentType.LOVE);
        if (hasComment){
            throw new BusinessException(ErrorResult.loveError());
        }
        //封装对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setCreated(System.currentTimeMillis());
        comment.setUserId(ThreadLocalUtils.get());
        //执行保存操作
        Integer count = commentApi.save(comment);
        //设置key
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String typeKey = Constants.MOVEMENT_LOVE_HASHKEY + ThreadLocalUtils.get();
        ///执行添加操作
        redisTemplate.opsForHash().put(key,typeKey,"1");
        return count;
    }

    /**
     * 动态取消喜欢
     * @param movementId
     * @return
     */
    public Integer unloveComment(String movementId) {
        //判断是否喜欢
        boolean hasComment = commentApi.hasComment(movementId, ThreadLocalUtils.get(), CommentType.LOVE);
        if (!hasComment){
            throw new BusinessException(ErrorResult.disloveError());
        }
        //创建对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setUserId(ThreadLocalUtils.get());
        comment.setCommentType(CommentType.LOVE.getType());
        //执行删除操作
        Integer count = commentApi.delete(comment);
        //设置key
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String typeKey = Constants.MOVEMENT_LOVE_HASHKEY + ThreadLocalUtils.get();
        //删除redis中的数据
        redisTemplate.opsForHash().delete(key,typeKey);
        //返回喜欢书
        return count;
    }

    /**
     * 谁看过我
     * @return
     */
    public List<VisitorVo> visitors() {
        //1.查询访问时间
        String key = Constants.VISITORS_USER;
        String hashKey = String.valueOf(ThreadLocalUtils.get());
        String value = (String) redisTemplate.opsForHash().get(key,hashKey);
        Long date = StringUtils.isEmpty(value) ? null : Long.valueOf(value);
        //2.调用API查询数据库表
        List<Visitors> list = visitorsApi.queryMyVisitors(date,ThreadLocalUtils.get());
        if (CollUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        //3.提取用户id
        List<Long> userIds = CollUtil.getFieldValues(list, "visitorUserId", Long.class);
        //4.查看用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //5.构造返回值
        List<VisitorVo> vos = new ArrayList<>();
        for (Visitors visitors : list) {
            UserInfo userInfo = map.get(visitors.getVisitorUserId());
            if (userInfo != null){
                vos.add(VisitorVo.init(userInfo,visitors));
            }
        }
        return vos;

   /*     //获取当前用户的id
        Long uid = ThreadLocalUtils.get();
        List<Visitors> visitorsList = visitorsApi.findVisitorsByUserId(uid);
        //遍历
        List<VisitorVo> visitorVoList = new ArrayList<>();
        for (Visitors visitor : visitorsList) {
            UserInfo userInfo = userInfoApi.findById(visitor.getUserId());
            visitorVoList.add(VisitorVo.init(userInfo, visitor));
        }*/
        //return visitorVoList;
    }

    /**
     * 评论点赞
     * @param commentId
     * @return
     */
    public Integer likeComment(String commentId) {
        //获取当前评论信息
        Comment com = commentApi.findCommentById(commentId);
        //判断当前评论是否已点赞
        boolean hasComment = commentApi.hasComment(commentId, ThreadLocalUtils.get(), CommentType.LIKE);
        if (hasComment){
            throw new BusinessException(ErrorResult.likeError());
        }
        //封装对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(commentId));
        comment.setUserId(ThreadLocalUtils.get());
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setCreated(System.currentTimeMillis());
        comment.setPublishUserId(com.getUserId());
        //执行保存操作
        Integer count = commentApi.saveLikeComment(comment,commentId);
        //设置key存储至redis中
        String key = Constants.COMMENT_INTERACT_KEY + commentId;
        String typeKey = Constants.COMMENT_LIKE_HASHKEY + ThreadLocalUtils.get();
        //将数据存入到redis中
        redisTemplate.opsForHash().put(key,typeKey,"1");
        return count;
    }

    public Integer dislikeComment(String commentId) {
        //判断当前评论是否已经点赞
        boolean hasComment = commentApi.hasComment(commentId, ThreadLocalUtils.get(), CommentType.LIKE);
        if (!hasComment){
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //封装对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(commentId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(ThreadLocalUtils.get());
        //调用删除方法，并返回修改后的点赞数值
        Integer count = commentApi.deleteLikeComment(comment);
        //删除redis中相关的数据
        String key = Constants.COMMENT_INTERACT_KEY + commentId;
        String typeKey = Constants.COMMENT_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().delete(key,typeKey);
        return count;
    }
}
