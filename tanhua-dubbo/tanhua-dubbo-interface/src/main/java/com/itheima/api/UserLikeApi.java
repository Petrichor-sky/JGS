package com.itheima.api;

import com.itheima.mongo.UserLike;
import com.itheima.pojo.Count;
import com.itheima.pojo.UserInfo;

import java.util.List;

public interface UserLikeApi {
    boolean saveOrUpdate(Long userId,Long likeUserId,boolean isLike);

    List<UserLike> findList(String type, UserInfo userInfo, Long userId, Integer page, Integer pageSize);

    List<UserLike> findInfoByType3(String type, UserInfo userInfo, Long userId, Integer page, Integer pageSize);

    Boolean findByVisitorId(Long userId, Long visitorUserId);

    Count CountByUserId(Long userId);

    Boolean hasEachLove(Long userId, Long likeUserId);

    //List<UserLike> findByLikeUserId(Long userId, Integer page, Integer pageSize, boolean b);
}
