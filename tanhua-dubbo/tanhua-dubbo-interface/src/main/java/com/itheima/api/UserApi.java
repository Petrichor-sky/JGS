package com.itheima.api;

import com.itheima.pojo.User;

public interface UserApi {
    /**
     * 根据手机号查询相关用户
     */
    User findByPhone(String phone);

    /**
     * 保存用户
     */
    int save(User user);

    User findById(Long id);
}
