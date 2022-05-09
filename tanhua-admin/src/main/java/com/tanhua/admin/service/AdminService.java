package com.tanhua.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.pojo.Admin;
import com.tanhua.admin.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    public Admin findAdminByUserName(String username) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,username);
        return  adminMapper.selectOne(queryWrapper);
    }

    public Admin getAdminById(Long userId) {
        return adminMapper.selectById(userId);
    }
}
