package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.api.UserApi;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DubboService
public class UserServiceImpl implements UserApi {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMobile,phone);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public int save(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
}
