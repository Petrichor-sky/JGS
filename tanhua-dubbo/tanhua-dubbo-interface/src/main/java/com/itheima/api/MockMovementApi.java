package com.itheima.api;

import com.itheima.mongo.MockMovement;
import com.itheima.pojo.Totals;
import org.bson.types.ObjectId;

import java.util.List;

public interface MockMovementApi {
    void save(MockMovement movement);

    List<MockMovement> findMovementByPage(Long uid, String state, Integer page, Integer pageSize, String sortProp, String sortOrder, Long sd, Long ed, String id);

    List<Totals> Count();

    void updateStateById(ObjectId objectId, String state);

    void updateTopState(String movementId);

    void downTopState(String movementId);

    MockMovement findByMoveId(String movementId);
}
