package com.itheima.api;

import com.itheima.mongo.MovementTimeLine;
import org.bson.types.ObjectId;

import java.util.List;

public interface TimeLineApi {
    void saveTimeLine(Long userId, ObjectId id);

    List<MovementTimeLine> findMovByFid(Long uid, Integer page, Integer pageSize);

    List<MovementTimeLine> findByFid(Long uid, Integer page, Integer pageSize);
}
