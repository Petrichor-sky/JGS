package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mapper.AnnouncementMapper;
import com.itheima.pojo.Announcement;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService
public class AnnouncemenServiceImpl implements AnnouncemenApi{
    @Autowired
    private AnnouncementMapper announcementMapper;
    @Override
    public IPage<Announcement> findAll(Integer page, Integer pageSize) {
        IPage<Announcement> iPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Announcement::getCreated);
        announcementMapper.selectPage(iPage,queryWrapper);
        return iPage;
    }
}
