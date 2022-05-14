package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.api.*;
import com.itheima.entity.MockUserInfo;
import com.itheima.mongo.*;
import com.itheima.pojo.Log;
import com.itheima.pojo.MockPageResult;
import com.itheima.pojo.Totals;
import com.itheima.pojo.UserInfo;
import com.itheima.vo.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MockManageService {

    @DubboReference
    private MockUserInfoApi mockUserInfoApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @DubboReference
    private MockVideoApi mockVideoApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    /**
     * 用户基本资料
     * @param userID
     * @return
     */
    public MockUserInfo getUserInfo(Long userID) {
        MockUserInfo userInfo = mockUserInfoApi.findById(userID);
        String key = Constants.USER_FREEZE + userInfo.getId();
        if(redisTemplate.hasKey(key)) {
            userInfo.setUserStatus("2");
        }
        return userInfo;
    }

    public PageResult findAllUserInfo(Integer page, Integer pageSize, Long id, String nickname, String city) {
        IPage<MockUserInfo> iPage =mockUserInfoApi.findByPage(page,pageSize,id,nickname,city);
        List<MockUserInfo> records = iPage.getRecords();
        for (MockUserInfo mockUserInfo : records) {
            String key = Constants.USER_FREEZE + mockUserInfo.getId();
            if(redisTemplate.hasKey(key)) {
                mockUserInfo.setUserStatus("2");
            }
        }
        //创建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setCounts(Convert.toInt(iPage.getTotal()));
        result.setItems(records);
        result.setPages(Convert.toInt(iPage.getPages()));
        return result;

    }

    /**
     * 用户冻结
     * @param params
     * @return
     */
    public Map<String, String> freeze(Map<String, String> params) {
        //获取用户的id
        String userId = params.get("userId");
        //获取解冻的参数
        Integer freezingTime = Convert.toInt(params.get("freezingTime"));
        //定义冻结的天数
        int days = 0;
        if (freezingTime == 1){
            days = 3;
        }
        if (freezingTime == 2){
            days = 7;
        }
        if (freezingTime == 3){
            days = -1;
        }
        //将数据存入到redis中
        String value = JSON.toJSONString(params);
        redisTemplate.opsForValue().set(Constants.USER_FREEZE + userId,value, Duration.ofDays(days));
        Map<String,String> map = new HashMap<>();
        map.put("message","冻结成功");
        return map;
    }

    /**
     * 解冻操作
     * @param params
     * @return
     */
    public Map<String, String> unfreeze(Map<String, String> params) {
        String userId = params.get("userId");
        redisTemplate.delete(Constants.USER_FREEZE + userId);
        Map<String,String> map = new HashMap<>();
        map.put("message","解冻成功");
        return map;
    }

    /**
     * 视频翻页
     * @param uid
     * @param page
     * @param pageSize
     * @param sortProp
     * @param sortOrder
     * @return
     */
    public PageResult findAllVideos(Long uid, Integer page, Integer pageSize, String sortProp, String sortOrder) {
        //创建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        //拿到所有的视频集合
        List<MockVideo> videoList = mockVideoApi.findByIdAndPage(uid,page,pageSize,sortOrder,sortProp);
        if (ObjectUtils.isEmpty(videoList)){
            return result;
        }
        //获取素有的userId
        List<Long> userIds = CollUtil.getFieldValues(videoList, "userId", Long.class);
        //通过userIds获取对应的用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //创建集合
        List<MockVideoVo> vos = new ArrayList<>();
        for (MockVideo mockVideo : videoList) {
            UserInfo userInfo = map.get(mockVideo.getUserId());
            if (!ObjectUtils.isEmpty(userInfo)){
                vos.add(MockVideoVo.init(userInfo,mockVideo));
            }
        }

        result.setItems(vos);
        result.setPages(1);
        result.setCounts(vos.size());
        return result;
    }

    /**
     * 评论列表
     * @param messageID
     * @param page
     * @param pageSize
     * @param sortProp
     * @param sortOrder
     * @return
     */
    @DubboReference
    private CommentApi commentApi;
    public PageResult findAllCommemts(String messageID, Integer page, Integer pageSize, String sortProp, String sortOrder) {
        //创建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        //获取用户对应的评论列表
        List<Comment> commentList = commentApi.findCommentsByPage(messageID,page,pageSize,sortProp,sortOrder);
        if (ObjectUtils.isEmpty(commentList)){
            return result;
        }
        //获取对应的userId
        List<Long> userIds = CollUtil.getFieldValues(commentList, "userId", Long.class);
        //通过id获取用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //创建返回值对象
        List<MockCommentVo> vos = new ArrayList<>();
        //遍历集合
        for (Comment comment : commentList) {
            //获取对应的用户信息
            UserInfo userInfo = map.get(comment.getUserId());
            if (!ObjectUtils.isEmpty(userInfo)){
                vos.add(MockCommentVo.init(userInfo,comment));
            }
        }
        result.setItems(vos);
        result.setCounts(vos.size());
        result.setPages(1);
        return result;
    }

    /**
     * 日志翻页
     * @param page
     * @param pageSize
     * @param sortProp
     * @param sortOrder
     * @param type
     * @param uid
     * @return
     */
    @DubboReference
    private LogApi logApi;
    public PageResult getLogs(Integer page, Integer pageSize, String sortProp, String sortOrder, String type, Long uid) {
        //获取数据的分页
        Page<Log> logPage = logApi.findLogByPageAndType(page, pageSize, sortProp, sortOrder, type, uid);
        //获取集合数据
        List<Log> records = logPage.getRecords();
        List<LogVo> vos = new ArrayList<>();
        //遍历
        for (Log log : records) {
            LogVo vo = new LogVo();
            vo.setId(Convert.toInt(log.getId()));
            vo.setPlace(log.getPlace());
            try {
                vo.setLogTime(new SimpleDateFormat("yyyy-MM-dd").parse(log.getLogTime()).getTime());
            }catch (Exception e){
                e.printStackTrace();
            }
            vo.setEquipment(log.getEquipment());
            vos.add(vo);
        }
        //创建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        result.setCounts(Convert.toInt(logPage.getTotal()));
        return result;
    }

    /**
     * 消息通过
     * @param ids
     * @return
     */
    @DubboReference
    private MockMovementApi mockMovementApi;
    public Map<String, String> pass(String[] ids) {
        //将String类型的id转换为ObjectId类型的
        List<ObjectId> idList = Arrays.stream(ids).map(e -> new ObjectId(e))
                .collect(Collectors.toList());
        if (!idList.isEmpty()){
            for (ObjectId objectId : idList) {
                mockMovementApi.updateStateById(objectId,"5");
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("message","审核通过");
        return map;
    }

    /**
     * 消息拒绝
     * @param ids
     * @return
     */
    public Map<String, String> reject(String[] ids) {
        //将String类型的id转换为ObjectId类型的
        List<ObjectId> idList = Arrays.stream(ids).map(e -> new ObjectId(e))
                .collect(Collectors.toList());
        if (!idList.isEmpty()){
            for (ObjectId objectId : idList) {
                mockMovementApi.updateStateById(objectId,"4");
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("message","拒绝成功");
        return map;
    }
    /**
     * 消息撤销
     * @param ids
     * @return
     */
    public Map<String, String> revocation(String[] ids) {
        //将String类型的id转换为ObjectId类型的
        List<ObjectId> idList = Arrays.stream(ids).map(e -> new ObjectId(e))
                .collect(Collectors.toList());
        if (!idList.isEmpty()){
            for (ObjectId objectId : idList) {
                mockMovementApi.updateStateById(objectId,"3");
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("message","撤销成功");
        return map;
    }


    /**
     * 消息详情
     * @param movementId
     * @return
     */
    public MockMovement findMovement(String movementId) {
        //根据动态id，查询对应的动态
        return mockMovementApi.findByMoveId(movementId);
    }

    /**
     * 消息置顶
     * @param movementId
     * @return
     */
    public Map<String, String> top(String movementId) {
        mockMovementApi.updateTopState(movementId);
        Map<String,String> map = new HashMap<>();
        map.put("message","置顶成功");
        return map;
    }

    /**
     * 取消消息置顶
     * @param movementId
     * @return
     */
    public Map<String, String> untop(String movementId) {
        mockMovementApi.downTopState(movementId);
        Map<String,String> map = new HashMap<>();
        map.put("message","取消置顶成功");
        return map;
    }

    /**
     * 消息翻页
     * @param uid   用户id
     * @param state
     * @param page
     * @param pageSize
     * @param sortProp
     * @param sortOrder
     * @param sd
     * @param ed
     * @param id    动态Id
     * @return
     */
    public MockPageResult findAllMovements(Long uid, String state, Integer page, Integer pageSize, String sortProp, String sortOrder, Long sd, Long ed, String id) {
        List<MockMovement> movementList = mockMovementApi.findMovementByPage(uid,state,page,pageSize,sortProp,sortOrder,sd,ed,id);
        List<Totals> totalsList = mockMovementApi.Count();
        MockPageResult result = new MockPageResult();
        result.setPage(page);
        result.setItems(movementList);
        result.setPagesize(pageSize);
        result.setTotals(totalsList);
        result.setCounts(movementList.size());
        return result;
    }


}
