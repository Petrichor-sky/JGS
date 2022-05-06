package com.itheima.api;

import com.itheima.mongo.Video;

import java.util.List;

public interface VideoApi {
    String save(Video video);

    List<Video> findByVids(List<Long> vids);

    List<Video> queryVideoList(int page, Integer pageSize);

    Video findById(String videoId);
}
