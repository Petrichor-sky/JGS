package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mapper.SettingsMapper;
import com.itheima.pojo.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SettingsServiceImpl implements SettingsApi{
    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public Settings findByUserId(Long id) {
        LambdaQueryWrapper<Settings> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Settings::getUserId,id);
        return settingsMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(Settings settings) {
        settingsMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        settingsMapper.updateById(settings);
    }
}
