package com.itheima.service;

import cn.hutool.core.thread.ThreadUtil;
import com.itheima.api.TimeLineApi;
import com.itheima.mongo.MovementTimeLine;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeLineService {
    @DubboReference
    private TimeLineApi timeLineApi;

    @Async
    public void saveTimeLine(Long userId, ObjectId id) {
        ThreadUtil.sleep(10000);
        timeLineApi.saveTimeLine(userId,id);
    }

    public List<MovementTimeLine> findMovByFid(Long uid, Integer page, Integer pageSize) {
        return timeLineApi.findMovByFid(uid,page,pageSize);
    }

    public List<MovementTimeLine> findByFid(Long uid, Integer page, Integer pageSize) {
        return timeLineApi.findByFid(uid,page,pageSize);
    }
}
