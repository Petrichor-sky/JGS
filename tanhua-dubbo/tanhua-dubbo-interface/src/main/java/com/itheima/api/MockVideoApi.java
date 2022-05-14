package com.itheima.api;

import com.itheima.mongo.MockVideo;

import java.util.List;

public interface MockVideoApi {
    void save(MockVideo vo);

    List<MockVideo> findByIdAndPage(Long uid, Integer page, Integer pageSize, String sortOrder, String sortProp);
}
