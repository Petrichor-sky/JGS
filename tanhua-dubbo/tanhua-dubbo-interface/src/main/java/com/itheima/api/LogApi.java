package com.itheima.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.pojo.Log;

import java.util.List;

public interface LogApi {
    Page<Log> findLogByPageAndType(Integer page, Integer pageSize, String sortProp, String sortOrder, String type, Long uid);

    void save(Log log);

    List<Long>  findLogByTimeAndType(String start, String end);

    void update(Log log);
}
