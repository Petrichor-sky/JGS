package com.itheima.service;

import com.itheima.api.ReMovementApi;
import com.itheima.mongo.RecommendMovement;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReMovementService {
    @DubboReference
    private ReMovementApi reMovementApi;

    public List<RecommendMovement> findMoveByUserId(Long uid, Integer page, Integer pageSize) {
        return reMovementApi.findMoveByUserId(uid,page,pageSize);
    }
}
