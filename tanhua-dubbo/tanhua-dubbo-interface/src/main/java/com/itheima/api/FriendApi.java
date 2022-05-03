package com.itheima.api;

import com.itheima.mongo.Friend;

import java.util.List;

public interface FriendApi {
    //添加好友关系
    void save(Long userId, String friendId);
    //根据登陆者id查询对应的好友数据
    List<Friend> findByUserId(Long userId,Integer page, Integer pageSize);

}
