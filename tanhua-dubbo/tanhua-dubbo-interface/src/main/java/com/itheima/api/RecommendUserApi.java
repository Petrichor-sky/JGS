package com.itheima.api;

import com.itheima.mongo.RecommendUser;
import com.itheima.vo.PageResult;

import java.util.List;

public interface RecommendUserApi {
    RecommendUser findRecommendUserServiceByUserId(Long id);

    List<RecommendUser> findByUserId(Long id, Integer page, Integer pagesize);

    RecommendUser queryById(Long userId, Long toUserId);

    //查询探花列表，需要排除喜欢和不喜欢的用户
    List<RecommendUser> queryCardsList(Long userId,int count);
}
