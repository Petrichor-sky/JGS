package com.itheima.service;

import com.alibaba.fastjson.JSON;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Constants;
import com.itheima.pojo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class UserFreezeService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public void checkUserStatus(Integer state,Long userId) {
        String value = redisTemplate.opsForValue().get(Constants.USER_FREEZE+ userId);
        if(!StringUtils.isEmpty(value)) {
            Map map = JSON.parseObject(value, Map.class);
            Integer freezingRange = (Integer) map.get("freezingRange");
            if(freezingRange == state) {
                throw new BusinessException(ErrorResult.builder().errMessage("您的账号被冻结！").build());
            }
        }
    }
}