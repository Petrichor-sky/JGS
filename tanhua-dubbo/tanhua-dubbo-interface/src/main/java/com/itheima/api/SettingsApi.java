package com.itheima.api;

import com.itheima.pojo.Settings;

public interface SettingsApi {
    Settings findByUserId(Long id);

    void save(Settings settings);

    void update(Settings settings);
}
