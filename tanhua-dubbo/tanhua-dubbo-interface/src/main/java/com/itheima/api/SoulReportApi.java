package com.itheima.api;

import com.itheima.chuanyin.SoulReport;

import java.util.List;

public interface SoulReportApi {
    List<SoulReport> findByUserId(Long userId);

    SoulReport findByIdAndUserId(String paperId, Long userId);

    void update(SoulReport soulReport);

    String save(SoulReport soulReport);
}
