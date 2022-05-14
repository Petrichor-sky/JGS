package com.itheima.api;

import com.itheima.mongo.Movement;
import org.bson.types.ObjectId;

import java.util.List;

public interface MovementApi {
    Movement saveMovements(Movement movement);

    List<Movement> findByUserId(Long userId,Integer page,Integer pageSize);

    List<Movement> findByMoveIds(List<Object> movementIds);

    List<Movement> randomMovements(Integer pageSize);

    List<Movement> findMoveByPids(List<Long> pids);

    Movement findByMoveId(String movementId);

    Integer countByUserId(Long userId);

    List<Movement> findAllMovements(Long uid, Integer state, Integer page, Integer pageSize);

    void updateStateById(ObjectId objectId,String type);

    Integer countByState(Integer state);

    void updateTopState(String movementId);

    void downTopState(String movementId);

}
