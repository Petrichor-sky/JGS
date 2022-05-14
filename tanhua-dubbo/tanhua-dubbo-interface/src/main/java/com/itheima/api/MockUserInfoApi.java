package com.itheima.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.entity.MockUserInfo;

public interface MockUserInfoApi {
    MockUserInfo findById(Long userID);

    IPage<MockUserInfo> findByPage(Integer page, Integer pageSize, Long id, String nickname, String city);
}
