package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.entity.MockUserInfo;
import com.itheima.mapper.MockUserInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@DubboService
public class MockUserInfoServiceImpl implements MockUserInfoApi{
    @Autowired
    private MockUserInfoMapper userInfoMapper;

    @Override
    public MockUserInfo findById(Long userID) {
        return userInfoMapper.selectById(userID);
    }

    /**
     * 用户数据
     * @param page
     * @param pageSize
     * @param id
     * @param nickname
     * @param city
     * @return
     */
    @Override
    public IPage<MockUserInfo> findByPage(Integer page, Integer pageSize, Long id, String nickname, String city) {
        IPage<MockUserInfo> iPage = new Page<>(page,pageSize);
        QueryWrapper<MockUserInfo> qw = new QueryWrapper<>();
        qw.eq(!ObjectUtils.isEmpty(id),"id",id);
        qw.like(!StringUtils.isEmpty(nickname),"nickname",nickname);
        qw.like(!StringUtils.isEmpty(city),"city",city);
        qw.orderByDesc("created");
        return userInfoMapper.selectPage(iPage,qw);
    }
}
