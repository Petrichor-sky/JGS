package com.itheima.service;

import com.itheima.api.UserInfoApi;
import com.itheima.dto.RecommendUserDto;
import com.itheima.pojo.UserInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
@Service
public class UserInfoService {

    @DubboReference(check = false)
    private UserInfoApi userInfoApi;


    /**
     * 保存用户个人资料
     * @param userInfo
     */
    public void save(UserInfo userInfo) {
        userInfoApi.saveOrUpdate(userInfo);
    }

    /**
     * 保存头像
     * @param id
     * @param url 头像存储阿里云oss的位置
     */
    public void saveHead(Long id, String url) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAvatar(url);
        userInfoApi.saveOrUpdate(userInfo);
    }

    public UserInfo findInfoById(Long userId, RecommendUserDto recommendUserDto) {
        return userInfoApi.findInfoById(userId,recommendUserDto);
    }
}
