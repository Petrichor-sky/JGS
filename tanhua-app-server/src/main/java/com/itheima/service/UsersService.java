package com.itheima.service;

import cn.hutool.core.util.StrUtil;
import com.itheima.api.UserInfoApi;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @DubboReference(check = false)
    private UserInfoApi userInfoApi;

    //查询用户资料
    public UserInfoVo findById(Long userID) {

        UserInfo userInfo = userInfoApi.findById(userID);
        UserInfoVo userInfoVo = new UserInfoVo();
        //数据拷贝
        BeanUtils.copyProperties(userInfo,userInfoVo);
        //对年龄做字符串处理
        String strAge = StrUtil.toString(userInfo.getAge());
        userInfoVo.setAge(strAge);
        //返回结果
        return userInfoVo;

    }

    public void updateUserInfo(UserInfo userInfo) {
        //赋值id
        userInfo.setId(ThreadLocalUtils.get());
        //修改
        userInfoApi.saveOrUpdate(userInfo);
    }
}
