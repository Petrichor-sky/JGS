package com.itheima.api;

import com.itheima.mongo.Movement;

import java.util.List;

public interface MovementApi {
    Movement saveMovements(Movement movement);

    List<Movement> findByUserId(Long userId,Integer page,Integer pageSize);

    List<Movement> findByMoveIds(List<Object> movementIds);

    List<Movement> findByFriendId(Long uid, Integer page, Integer pageSize);

    List<Movement> randomMovements(Integer pageSize);

    List<Movement> findMoveByPids(List<Long> pids);

    Movement findByMoveId(String movementId);

    Integer countByUserId(Long userId);
}
