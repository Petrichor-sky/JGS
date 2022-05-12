package com.itheima.api;

import com.itheima.chuanyin.SoulDimensions;

import java.util.List;

public interface SoulDimensionsApi {
    List<SoulDimensions> findByScore(String scoreStr);
}
