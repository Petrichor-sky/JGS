package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.api.CommentApi;
import com.itheima.api.MovementApi;
import com.itheima.api.UserInfoApi;
import com.itheima.api.VideoApi;
import com.itheima.mongo.Comment;
import com.itheima.mongo.Constants;
import com.itheima.mongo.Movement;
import com.itheima.mongo.Video;
import com.itheima.pojo.UserInfo;
import com.itheima.vo.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private VideoApi videoApi;
    @DubboReference
    private CommentApi commentApi;
    @DubboReference
    private MovementApi movementApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    /**
     * 查询所有的用户信息
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult findAllUserInfo(Integer page, Integer pageSize) {
        //查找分页用户数据
        IPage<UserInfo> iPage = userInfoApi.findAll(page,pageSize);
        List<UserInfo> list = iPage.getRecords();
        for (UserInfo userInfo : list) {
            String key = Constants.USER_FREEZE + userInfo.getId();
            if(redisTemplate.hasKey(key)) {
                userInfo.setUserStatus("2");
            }
        }
        //构建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setCounts(Convert.toInt(iPage.getTotal()));
        result.setItems(iPage.getRecords());
        return result;
    }

    /**
     * 根据id获取用户的用户
     * @param userID
     * @return
     */
    public UserInfo findUserInfo(Long userID) {
        UserInfo userInfo = userInfoApi.findById(userID);
        String key = Constants.USER_FREEZE + userInfo.getId();
        if(redisTemplate.hasKey(key)) {
            userInfo.setUserStatus("2");
        }
        return userInfo;
    }

    /**
     * 获取所有的视频
     * @param uid
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult findAllVideos(Long uid, Integer page, Integer pageSize) {
        //获取对应用户的所有视频
        List<Video> videoList = videoApi.findByUserIdAndPage(uid,page,pageSize);
        //获取所有的用户id
        List<Long> userIds = CollUtil.getFieldValues(videoList, "userId", Long.class);
        if (userIds.isEmpty()){
            return new PageResult();
        }
        //调用方法
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //创建封装集合
        List<VideoVo> vos = new ArrayList<>();
        //遍历集合
        for (Video video : videoList) {
            UserInfo userInfo = map.get(video.getUserId());
            if (!ObjectUtils.isEmpty(userInfo)){
                vos.add(VideoVo.init(userInfo,video));
            }
        }
        //创建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        return result;
    }

    /**
     * 评论列表翻页
     * @param messageID
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult findAllCommemts(String messageID, Integer page, Integer pageSize) {
        //获取该用户对应的所有评论
        List<Comment> commentList = commentApi.findCommentsByPublishId(messageID,page,pageSize);
        //获取评论对应的userIds
        List<Long> userIds = CollUtil.getFieldValues(commentList, "userId", Long.class);
        if (userIds.isEmpty()){
            return new PageResult();
        }
        //获取ids对应的所有用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //遍历
        List<CommentPageVo> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            UserInfo userInfo = map.get(comment.getUserId());
            vos.add(CommentPageVo.init(userInfo,comment));
        }
        //构建返回对象
        PageResult result = new PageResult();
        result.setPagesize(pageSize);
        result.setPage(page);
        result.setItems(vos);
        result.setCounts(commentApi.countByPublishId(messageID));
        return result;

    }

    /**
     * 动态分页
     * @param uid
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult findAllMovements(Long uid, Integer state, Integer page, Integer pageSize) {
        //查找符合条件的动态
        List<Movement> movementList = movementApi.findAllMovements(uid,state,page,pageSize);
        //获取对应的userId
        List<Long> userIds = CollUtil.getFieldValues(movementList, "userId", Long.class);
        if (userIds.isEmpty()){
            return new PageResult();
        }
        //根据userIds查找对应的用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //创建封装对象
        List<MovementsStateVo> vos = new ArrayList<>();
        //遍历
        for (Movement movement : movementList) {
            UserInfo userInfo = map.get(movement.getUserId());
            if (!ObjectUtils.isEmpty(movement)){
                vos.add(MovementsStateVo.init(userInfo,movement));
            }
        }
        Integer count = movementApi.countByState(state);
        //创建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        result.setCounts(count);
        return result;
    }

    /**
     * 冻结用户
     * @param params
     * @return
     */
    public Map<String, String> freeze(Map<String,String>  params) {
        String userId = params.get("userId");
        //冻结时间，1为冻结3天，2为冻结7天，3为永久冻结
        int freezingTime =  Convert.toInt(params.get("freezingTime"));
        //设置冻结的天数
        int days = 0;
        if (freezingTime ==1){
            days = 1;
        }
        if (freezingTime == 2){
            days = 7;
        }
        if (freezingTime == 3){
            days = -1;
        }
        String value = JSON.toJSONString(params);
        //将结果存入redis中
        redisTemplate.opsForValue().set(Constants.USER_FREEZE + userId,value, Duration.ofDays(days));
        //创建集合
        Map<String,String> map = new HashMap<>();
        map.put("message","冻结成功");
        return map;
    }

    /**
     * 用户解冻
     * @param params
     * @return
     */
    public Map<String, String> unfreeze(Map<String, String> params) {
        String userId = params.get("userId");
        //删除redis中的值
        redisTemplate.delete(Constants.USER_FREEZE + userId);
        Map<String,String> map = new HashMap<>();
        map.put("message","解冻成功");
        return map;
    }

    /**
     * 动态详情
     * @param movementId
     * @return
     */
    public MovementsVo findMovement(String movementId) {
        //根据动态id，查询对应的动态
        Movement movement = movementApi.findByMoveId(movementId);
        if (!ObjectUtils.isEmpty(movement)){
            //获取用户对应的信息
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            return  MovementsVo.init(userInfo,movement);
        }
        return null;
    }

    /**
     * 动态通过
     * @param ids
     * @return
     */
    public Map<String, String> pass(String[] ids) {
        //将String类型的id转换为ObjectId类型的
        List<ObjectId> idList = Arrays.stream(ids).map(e -> new ObjectId(e))
                .collect(Collectors.toList());
        if (!idList.isEmpty()){
            for (ObjectId objectId : idList) {
                movementApi.updateStateById(objectId,"1");
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("message","审核通过");
        return map;
    }

    public Map<String, String> reject(String[] ids) {
        //将String类型的id转换为ObjectId类型的
        List<ObjectId> idList = Arrays.stream(ids).map(e -> new ObjectId(e))
                .collect(Collectors.toList());
        if (!idList.isEmpty()){
            for (ObjectId objectId : idList) {
                movementApi.updateStateById(objectId,"2");
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("message","拒绝成功");
        return map;
    }
}
