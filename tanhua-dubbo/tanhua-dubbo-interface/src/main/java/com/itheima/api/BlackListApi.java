package com.itheima.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.pojo.BlackList;

public interface BlackListApi {
    IPage<BlackList> findByUserId(Long id, Integer page, Integer pageSize);

    void deleteBlackUserById(Long id, Long uid);
}
