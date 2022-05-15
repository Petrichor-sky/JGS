package com.itheima.api;

import com.itheima.pojo.UseTimeLog;

public interface UseTimeLogApi {
    void save(UseTimeLog log);

    Long CountUseTime(String today, String beforeWeek);
}
