package com.itheima.api;

import com.itheima.mapper.UseTimeLogMapper;
import com.itheima.pojo.UseTimeLog;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UseTimeLogServiceImpl implements UseTimeLogApi {

    @Autowired
    private UseTimeLogMapper useTimeLogMapper;
    /**
     * 记录登入信息
     * @param log
     */
    @Override
    public void save(UseTimeLog log) {
        useTimeLogMapper.insert(log);
    }
}
