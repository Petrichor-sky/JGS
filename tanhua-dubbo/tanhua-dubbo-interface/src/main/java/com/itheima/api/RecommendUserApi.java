package com.itheima.api;

import com.itheima.mongo.RecommendUser;
import com.itheima.vo.PageResult;

import java.util.List;

public interface RecommendUserApi {
    RecommendUser findRecommendUserServiceByUserId(Long id);

    List<RecommendUser> findByUserId(Long id, Integer page, Integer pagesize);
}
