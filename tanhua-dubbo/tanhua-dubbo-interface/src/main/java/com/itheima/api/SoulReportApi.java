package com.itheima.api;

import com.itheima.chuanyin.SoulReport;

import java.util.List;

public interface SoulReportApi {
    List<SoulReport> findByUserId(Long userId);
}
