package com.itheima.service;

import com.itheima.api.UserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.pojo.User;
import com.itheima.pojo.UserInfo;
import com.itheima.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private UserApi userApi;

    public UserInfoVo findUserInfoByHuanxin(String huanxinId) {
        //根据环信id来查询用户
        User user = userApi.findByHuanXinId(huanxinId);
        //根据用户id来查询用户详情
        UserInfo userInfo = userInfoApi.findById(user.getId().longValue());
        //创建封装对象
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,vo);
        if (userInfo.getAge() != null){
            vo.setAge(userInfo.getAge().toString());
        }
        //返回结果
        return vo;
    }
}
