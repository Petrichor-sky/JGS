package com.itheima.api;

import com.itheima.mongo.RecommendMovement;

import java.util.List;

public interface ReMovementApi {
    List<RecommendMovement> findMoveByUserId(Long uid, Integer page, Integer pageSize);
}
