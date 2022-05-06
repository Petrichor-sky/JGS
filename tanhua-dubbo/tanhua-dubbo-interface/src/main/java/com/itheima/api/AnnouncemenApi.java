package com.itheima.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.pojo.Announcement;

import java.util.List;

public interface AnnouncemenApi {
    IPage<Announcement> findAll(Integer page, Integer pageSize);
}
