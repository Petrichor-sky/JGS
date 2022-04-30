package com.itheima.service;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.api.*;
import com.itheima.pojo.*;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.PageResult;
import com.itheima.vo.SettingsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SettingsService {
    @DubboReference
    private SettingsApi settingsApi;
    @DubboReference
    private QuestionApi questionApi;
    @DubboReference
    private UserApi userApi;
    @DubboReference
    private BlackListApi blackListApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    public ResponseEntity getSettingsVo() {
        //创建返回对象
        SettingsVo vo = new SettingsVo();
        //获取用户id
        Long id = ThreadLocalUtils.get();
        //获取当前用户的信息
        User user = userApi.findById(id);
        vo.setId(ThreadLocalUtils.get());
        vo.setPhone(user.getMobile());
        //获取陌生人问题
        Question question = questionApi.findByUserId(id);
        String txt = question == null ? "你喜欢java吗" : question.getTxt();
        vo.setStrangerQuestion(txt);
        //获取通知信息
        Settings settings = settingsApi.findByUserId(id);
        if (!ObjectUtils.isEmpty(settings)){
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
            vo.setLikeNotification(settings.getLikeNotification());
        }
        return ResponseEntity.ok(vo);
    }

    public PageResult blacklist(Integer page, Integer pageSize) {
        //获取当前用户id
        Long id = ThreadLocalUtils.get();
        //获取id对应的黑名单
        IPage<BlackList> iPage = blackListApi.findByUserId(id,page,pageSize);
        //3.获取分页数据的集合
        List<BlackList> records = iPage.getRecords();
        List<Long> list = new ArrayList<>();
        for (BlackList blackList : records) {
            list.add(blackList.getBlackUserId());
        }
        if (ObjectUtils.isEmpty(list)){
            throw new RuntimeException("暂无黑名单数据");
        }
        //根据黑名单的id来查询对应的用户信息
        List<UserInfo> userInfoList = userInfoApi.findUserInfoByIds(list);
        //封装响应对象
        PageResult result = new PageResult();
        result.setCounts(Convert.toInt(iPage.getTotal()));
        result.setPagesize(Convert.toInt(iPage.getSize()));
        result.setPages(Convert.toInt(iPage.getPages()));
        result.setPage(Convert.toInt(iPage.getCurrent()));
        result.setItems(userInfoList);
        return result;
    }

    /**
     * 根据id进行删除
     * @param uid
     */
    public void deleteBlackUserById(Long uid) {
        //获取当前用户的id
        Long id = ThreadLocalUtils.get();
        blackListApi.deleteBlackUserById(id,uid);
    }

    /**
     * 保存通用设置
     * @param map
     */
    public void settings(Map map) {
        //获取相关数据
        boolean likeNotification = (boolean) map.get("likeNotification");
        boolean pinglunNotification = (boolean) map.get("pinglunNotification");
        boolean gonggaoNotification = (boolean) map.get("gonggaoNotification");
        //获取当前用户id
        Long id = ThreadLocalUtils.get();
        Settings settings = settingsApi.findByUserId(id);
        //判断是否存在
        if (ObjectUtils.isEmpty(settings)){
            //封装数据
            settings = new Settings();
            settings.setUserId(id);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);
            //如果不存在执行添加操作
            settingsApi.save(settings);
        }else {
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);
            //如果已经存在执行修改操作
            settingsApi.update(settings);
        }


    }
}
