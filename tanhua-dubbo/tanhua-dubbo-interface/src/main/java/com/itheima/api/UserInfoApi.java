package com.itheima.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.dto.RecommendUserDto;
import com.itheima.pojo.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {
    void saveOrUpdate(UserInfo userInfo);

    UserInfo findById(Long id);

    List<UserInfo> findUserInfoByIds(List<Long> list);

    UserInfo findInfoById(Long userId, RecommendUserDto recommendUserDto);

    Map<Long, UserInfo> findByIds(List<Long> friendIds, UserInfo userInfo);

    List<UserInfo>  findByGender(String gender);

    IPage<UserInfo> findAll(Integer page, Integer pageSize);

}
