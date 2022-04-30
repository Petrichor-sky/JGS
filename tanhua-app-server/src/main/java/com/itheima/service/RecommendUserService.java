package com.itheima.service;

import com.itheima.api.RecommendUserApi;
import com.itheima.mongo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendUserService {
    @DubboReference
    private RecommendUserApi recommendUserApi;

    public RecommendUser findRecommendUserServiceByUserId(Long id) {
        return recommendUserApi.findRecommendUserServiceByUserId(id);
    }
}
