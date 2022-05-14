package com.itheima.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.pojo.Log;

public interface LogApi {
    Page<Log> findLogByPageAndType(Integer page, Integer pageSize, String sortProp, String sortOrder, String type, Long uid);
}
