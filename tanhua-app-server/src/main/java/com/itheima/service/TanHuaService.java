package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import com.itheima.api.RecommendUserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.dto.RecommendUserDto;
import com.itheima.mongo.RecommendUser;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.PageResult;
import com.itheima.vo.TodayBest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class TanHuaService {

    @Autowired
    private RecommendUserService recommendUserService;
    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private RecommendUserApi recommendUserApi;
    @Autowired
    private UserInfoService userInfoService;

    @Value("${recommend.uid}")
    private Long userId;

    /**
     * 查询今日最佳
     * @return
     */
    public TodayBest todayBest() {
        Long id = ThreadLocalUtils.get();

        //1.根据userId来获取当前用户缘分值最高的用户
        RecommendUser recommendUser = recommendUserService.findRecommendUserServiceByUserId(id);
        //如果没有分配今日佳人，则默认分配一个
        if (ObjectUtils.isEmpty(recommendUser)){
            recommendUser = new RecommendUser();
            recommendUser.setUserId(userId);
            recommendUser.setScore(88D);
        }
        //获取推荐用户的id
        Long userId = recommendUser.getUserId();
        //查询推荐用户的信息
        UserInfo userInfo = userInfoApi.findById(userId);
        //创建返回对象
        return TodayBest.init(userInfo, recommendUser);
    }

    /**
     * 推荐朋友
     * @param recommendUserDto
     * @return
     */
    public PageResult findByRecommendation(RecommendUserDto recommendUserDto) {
        //1.获取用户id
        Long id = ThreadLocalUtils.get();
        //2.调用分页查询数据列表
        List<RecommendUser> recommendUserList = recommendUserApi.findByUserId(id,recommendUserDto.getPage(),recommendUserDto.getPagesize());
        //3.创建集合
        List<TodayBest> list = new ArrayList<>();
        //遍历集合
        for (RecommendUser recommendUser : recommendUserList) {
            //获取推荐用户的id
            Long userId = recommendUser.getUserId();
            //根据推荐用户id查询对应用户信息
            UserInfo userInfo = userInfoService.findInfoById(userId,recommendUserDto);
            if (!ObjectUtils.isEmpty(userInfo)){
                TodayBest todayBest = TodayBest.init(userInfo, recommendUser);
                list.add(todayBest);
            }
        }
        //封装返回对象
        PageResult result = new PageResult();
        //设置当前页和每页显示条目数
        result.setPagesize(recommendUserDto.getPagesize());
        result.setPage(recommendUserDto.getPage());
        //设置推荐列表数据
        result.setItems(list);
        return result;
    }
}
