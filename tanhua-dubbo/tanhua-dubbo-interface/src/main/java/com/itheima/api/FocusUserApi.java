package com.itheima.api;

public interface FocusUserApi {
    Boolean hasFocus(Long followUserId, Long aLong);

    void save(Long toLong, Long userId);

    void delete(Long toLong, Long userId);
}
