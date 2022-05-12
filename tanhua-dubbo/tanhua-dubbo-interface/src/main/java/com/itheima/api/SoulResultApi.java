package com.itheima.api;

import com.itheima.vo.ConclusionVo;

public interface SoulResultApi {
    ConclusionVo findByPaperIdAndScore(Long paperId, String scoreStr);
}
