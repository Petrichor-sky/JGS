package com.itheima.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.entity.MockUserInfo;

import java.util.List;
import java.util.Map;

public interface MockUserInfoApi {
    MockUserInfo findById(Long userID);

    IPage<MockUserInfo> findByPage(Integer page, Integer pageSize, Long id, String nickname, String city);

    Map<String, List<Map<String, Object>>> countByTime(List<Long> userIds);
}
