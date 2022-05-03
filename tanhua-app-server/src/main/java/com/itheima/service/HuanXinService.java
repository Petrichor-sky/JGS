package com.itheima.service;

import com.itheima.api.UserApi;
import com.itheima.pojo.User;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.HuanXinUserVo;
import io.netty.util.internal.ObjectUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class HuanXinService {
    @DubboReference
    private UserApi userApi;


    public HuanXinUserVo findHuanXinUserVo() {
        //查询对应的用户信息
        User user = userApi.findById(ThreadLocalUtils.get());
        if (ObjectUtils.isEmpty(user)){
            return null;
        }
        //返回结果
        return new HuanXinUserVo(user.getHxUser(),user.getHxPassword());
    }
}
