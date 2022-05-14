package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mapper.LogMapper;
import com.itheima.pojo.Log;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

@DubboService
public class LogServiceImpl implements LogApi{

    @Autowired
    private LogMapper logMapper;
    @Override
    public Page<Log> findLogByPageAndType(Integer page, Integer pageSize, String sortProp, String sortOrder, String type, Long uid) {
        Page<Log> logPage = new Page<>(page,pageSize);
        QueryWrapper<Log> qw = new QueryWrapper<>();
        qw.eq("user_id",uid);
        qw.eq("type",type);
        if (!ObjectUtils.isEmpty(sortProp)){
            if ("ascending ".equals(sortOrder)){
                //升序
                qw.orderByAsc(sortProp);
            }else if ("descending ".equals(sortOrder)){
                //降序
                qw.orderByDesc(sortProp);
            }
        }else {
            qw.orderByDesc("created");
        }
        return logMapper.selectPage(logPage,qw);
    }
}
