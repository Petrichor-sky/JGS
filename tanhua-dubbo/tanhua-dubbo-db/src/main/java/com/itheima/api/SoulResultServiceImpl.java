package com.itheima.api;

import com.itheima.mapper.SoulResultMapper;
import com.itheima.vo.ConclusionVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SoulResultServiceImpl implements SoulResultApi{
    @Autowired
    private SoulResultMapper resultMapper;
    @Override
    public ConclusionVo findByPaperIdAndScore(Long paperId, String scoreStr) {
        return resultMapper.findByPaperIdAndScore(paperId,scoreStr);
    }
}
