package com.itheima.api;

import com.itheima.chuanyin.SoulDimensions;
import com.itheima.mapper.SoulDimensionsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class SoulDimensionsServiceImpl implements SoulDimensionsApi{
    @Autowired
    private SoulDimensionsMapper dimensionsMapper;
    @Override
    public List<SoulDimensions> findByScore(String scoreStr) {
        return dimensionsMapper.findByScore(scoreStr);
    }
}
