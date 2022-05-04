package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import com.itheima.api.FriendApi;
import com.itheima.api.UserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Constants;
import com.itheima.mongo.Friend;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.User;
import com.itheima.pojo.UserInfo;
import com.itheima.template.HuanXinTemplate;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.ContactVo;
import com.itheima.vo.PageResult;
import com.itheima.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
