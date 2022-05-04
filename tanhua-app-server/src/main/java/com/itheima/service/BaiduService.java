package com.itheima.service;

import com.itheima.api.UserLocationApi;
import com.itheima.exception.BusinessException;
import com.itheima.pojo.ErrorResult;
import com.itheima.utils.ThreadLocalUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BaiduService {
    @DubboReference
    private UserLocationApi userLocationApi;
    /**
     * 更新地址
     * @param map
     */
    public void updateLocation(Map<String, String> map) {
        Double longitude = Double.valueOf(map.get("longitude"));
        Double latitude = Double.valueOf(map.get("latitude"));
        String address = map.get("addStr");
        Boolean aBoolean = userLocationApi.updateLocation(ThreadLocalUtils.get(), longitude, latitude, address);
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
    }
}
