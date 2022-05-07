package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.api.*;
import com.itheima.enums.CommentType;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Comment;
import com.itheima.mongo.Constants;
import com.itheima.mongo.Friend;
import com.itheima.mongo.UserLike;
import com.itheima.pojo.Announcement;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.User;
import com.itheima.pojo.UserInfo;
import com.itheima.template.HuanXinTemplate;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private UserApi userApi;
    @Autowired
    private HuanXinTemplate template;
    @DubboReference
    private FriendApi friendApi;
    @DubboReference
    private CommentApi commentApi;
    @DubboReference
    private AnnouncemenApi announcemenApi;

    public UserInfoVo findUserInfoByHuanxin(String huanxinId) {
        //根据环信id来查询用户
        User user = userApi.findByHuanXinId(huanxinId);
        //根据用户id来查询用户详情
        UserInfo userInfo = userInfoApi.findById(user.getId().longValue());
        //创建封装对象
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,vo);
        if (userInfo.getAge() != null){
            vo.setAge(userInfo.getAge().toString());
        }
        //返回结果
        return vo;
    }

    /**
     * 添加好友
     * @param friendId
     */
    public void contacts(Long friendId) {
        //获取当前登陆者id
        Long userId = ThreadLocalUtils.get();
        //将好友关系添加到环信
        Boolean aBoolean = template.addContact(Constants.HX_USER_PREFIX + userId, Constants.HX_USER_PREFIX + friendId);
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
        //如果注册成功，将好友关系存入friend表中
        friendApi.save(userId,friendId);
    }

    /**
     * 联系人列表
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult findContactsList(String keyword, Integer page, Integer pageSize) {
        //创建返回值对象
        PageResult result = new PageResult();
        result.setPagesize(pageSize);
        result.setPage(page);
        //根本当前用户来查询对应的好友
        Long userId = ThreadLocalUtils.get();
        List<Friend> friendList = friendApi.findByUserId(userId,page,pageSize);
        if (CollUtil.isEmpty(friendList)){
            return result;
        }
        //拿到所有的用户有id
        List<Long> friendIds = CollUtil.getFieldValues(friendList, "friendId",Long.class);
        //创建对象
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(keyword);
        Map<Long,UserInfo> map = userInfoApi.findByIds(friendIds,userInfo);
        //遍历然后获取对应的用户id
        //创建封装的ContactVo集合
        List<ContactVo> contactVoList = new ArrayList<>();
        for (Friend friend : friendList) {
            //通过id获取对应的集合
            UserInfo info = map.get(friend.getFriendId());
            if (info != null){
                //添加到集合中
                contactVoList.add(ContactVo.init(info));
            }
        }
        result.setItems(contactVoList);
        result.setCounts(contactVoList.size());
        return result;
    }

    /**
     * 删除好友
     * @param friendId
     */
    public void disContacts(Long friendId) {
        //获取当前用户id
        Long userId = ThreadLocalUtils.get();
        //删除环信中的好友关系
        Boolean aBoolean = template.deleteContact(Constants.HX_USER_PREFIX + userId, Constants.HX_USER_PREFIX + friendId);
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
        //执行删除表中好友的操作
        friendApi.delete(userId,friendId);

    }

    /**
     * 评论列表
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getComments(Integer page, Integer pageSize) {
        //获取当前用户的id
        Long userId = ThreadLocalUtils.get();
        //根据userId进行查询并分页
        List<Comment> commentList = commentApi.findCommentByUserId(userId, CommentType.COMMENT,page,pageSize);
        if (CollUtil.isEmpty(commentList)){
            return new PageResult();
        }
        //通过list集合获取到所有的用户id
        List<Long> userIds = CollUtil.getFieldValues(commentList, "userId", Long.class);
        //创建返回值对象
        List<UserInfoAndLikeVo> vos = new ArrayList<>();
        //调用用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        for (Comment comment : commentList) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (!ObjectUtils.isEmpty(userInfo)){
                vos.add(UserInfoAndLikeVo.init(userInfo,comment));
            }
        }
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        result.setCounts(vos.size());
        return result;
    }

    /**
     * 点赞列表
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getLikes(Integer page, Integer pageSize) {
        //获取当前登陆者的Id
        Long userId = ThreadLocalUtils.get();
        List<Comment> commentList = commentApi.findCommentByUserId(userId, CommentType.LIKE, page, pageSize);
        if (CollUtil.isEmpty(commentList)){
            return new PageResult();
        }
        //获取到对应的userId
        List<Long> userIds = CollUtil.getFieldValues(commentList, "userId", Long.class);
        //创建封装集合
        List<UserInfoAndLikeVo> vos = new ArrayList<>();
        //根据ids获取对应的用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //遍历
        for (Comment comment : commentList) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (!ObjectUtils.isEmpty(userInfo)){
                vos.add(UserInfoAndLikeVo.init(userInfo,comment));
            }
        }
        //创建返回结果对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        result.setCounts(vos.size());
        return result;
    }

    /**
     * 获取喜欢列表
     * @param page
     * @param pageSize
     * @return
     */
    @DubboReference
    private UserLikeApi userLikeApi;
    public PageResult getLoves(Integer page, Integer pageSize) {
        //获取当前登陆者的Id
        Long userId = ThreadLocalUtils.get();
        List<UserLike> userLikeList = userLikeApi.findInfoByType3("3",null,userId,page,pageSize);
        if (CollUtil.isEmpty(userLikeList)){
            return new PageResult();
        }
        //获取到对应的userId
        List<Long> userIds = CollUtil.getFieldValues(userLikeList, "userId", Long.class);
        //创建封装集合
        List<UserInfoAndLikeVo> vos = new ArrayList<>();
        //根据ids获取对应的用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //遍历
        for (UserLike userLike : userLikeList) {
            UserInfo userInfo = map.get(userLike.getUserId());
            if (!ObjectUtils.isEmpty(userInfo)){
                UserInfoAndLikeVo vo = UserInfoAndLikeVo.init(userInfo, userLike);
                vo.setId(userInfo.getId().toString());
                vos.add(vo);
            }
        }
        //创建返回结果对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        result.setCounts(vos.size());
        return result;
    }

    /**
     * 公告列表
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getAnnouncements(Integer page, Integer pageSize) {
        IPage<Announcement> iPage = announcemenApi.findAll(page,pageSize);
        //获取对应的数据
        List<Announcement> list = iPage.getRecords();
        //创建封装对象
        List<AnnouncementVo> vos = new ArrayList<>();
        for (Announcement announcement : list) {
            vos.add(AnnouncementVo.init(announcement));
        }
        //构建返回值对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        result.setCounts(list.size());
        return result;
    }
}
