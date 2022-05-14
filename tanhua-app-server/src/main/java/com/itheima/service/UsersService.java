package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.itheima.api.UserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.api.UserLikeApi;
import com.itheima.api.VisitorsApi;
import com.itheima.mongo.UserLike;
import com.itheima.mongo.Visitors;
import com.itheima.pojo.Count;
import com.itheima.pojo.User;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.PageResult;
import com.itheima.vo.UserInfoAndLoveVo;
import com.itheima.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsersService {

    @DubboReference(check = false)
    private UserInfoApi userInfoApi;

    @DubboReference
    private UserLikeApi userLikeApi;

    @DubboReference
    private VisitorsApi visitorsApi;
    @DubboReference
    private UserApi userApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    //查询用户资料
    public UserInfoVo findById(Long userID) {

        UserInfo userInfo = userInfoApi.findById(userID);
        UserInfoVo userInfoVo = new UserInfoVo();
        //数据拷贝
        if (!ObjectUtils.isEmpty(userInfo)){
            BeanUtils.copyProperties(userInfo,userInfoVo);
            //对年龄做字符串处理
            String strAge = StrUtil.toString(userInfo.getAge());
            userInfoVo.setAge(strAge);
        }
        //返回结果
        return userInfoVo;

    }

    public void updateUserInfo(UserInfo userInfo) {
        //赋值id
        userInfo.setId(ThreadLocalUtils.get());
        //修改
        userInfoApi.saveOrUpdate(userInfo);
    }

    /**
     * 互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
     * @param type
     * @param nickname
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getList(String type, String nickname, Integer page, Integer pageSize) {
        //获取当前用户的id
        Long userId = ThreadLocalUtils.get();
        //创建条件对象
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(nickname);
        //构建返回对象
        List<UserInfoAndLoveVo> vos = new ArrayList<>();
        //创建空集合
        List<UserLike> userLikeList = new ArrayList<>();
        //构建返回对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);

        if ("1".equals(type) || "2".equals(type)){
            userLikeList = userLikeApi.findList(type,userInfo,userId,page,pageSize);
            if (userLikeList.isEmpty()){
                return result;
            }
            List<Long> userIds = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);
            //根据likeUserId获取对应的用户信息
            Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
            //遍历
            for (UserLike userLike : userLikeList) {
                UserInfo info = map.get(userLike.getLikeUserId());
                if (!ObjectUtils.isEmpty(info)){
                    vos.add(UserInfoAndLoveVo.init(info,userLike));
                }
            }
            result.setItems(vos);
            return result;
        }
        if ("3".equals(type)){
            userLikeList = userLikeApi.findInfoByType3(type,userInfo,userId,page,pageSize);
            if (userLikeList.isEmpty()){
                return result;
            }
            List<Long> userIds = CollUtil.getFieldValues(userLikeList, "userId", Long.class);
            //根据likeUserId获取对应的用户信息
            Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
            //遍历
            for (UserLike userLike : userLikeList) {
                UserInfo info = map.get(userLike.getUserId());
                if (!ObjectUtils.isEmpty(info)){
                    UserInfoAndLoveVo vo = UserInfoAndLoveVo.init(info, userLike);
                    Boolean aBoolean = userLikeApi.hasEachLove(userId,userLike.getUserId());
                    vo.setAlreadyLove(aBoolean);
                    vos.add(vo);
                }
            }
            result.setItems(vos);
            return result;
        }
        if ("4".equals(type)){
            List<Visitors> visitorsList = visitorsApi.findVisitorsByUserId(userId,page,pageSize);
            if (visitorsList.isEmpty()){
                return result;
            }
            List<Long> userIds = CollUtil.getFieldValues(visitorsList, "visitorUserId", Long.class);
            //根据likeUserId获取对应的用户信息
            Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
            for (Visitors visitors : visitorsList) {
                UserInfo info = map.get(visitors.getVisitorUserId());
                if (!ObjectUtils.isEmpty(info)){
                    UserInfoAndLoveVo vo = new UserInfoAndLoveVo();
                    BeanUtils.copyProperties(info,vo);
                    vo.setMatchRate(Convert.toInt(visitors.getScore()));
                    //判断是否喜欢
                    Boolean alreadyLove = userLikeApi.findByVisitorId(userId,visitors.getVisitorUserId());
                    vo.setAlreadyLove(alreadyLove);
                    vos.add(vo);
                }
            }

            result.setItems(vos);
            return result;
        }
        result.setItems(null);
        return result;

    }

    /**
     * 计算互相喜欢，喜欢和粉丝的数量
     * @return
     */
    public Count getCount() {
        return userLikeApi.CountByUserId(ThreadLocalUtils.get());
    }

    /**
     * 发送验证码
     */
    public void sendVerificationCode() {
        //获取当前用户的id
        Long userId = ThreadLocalUtils.get();
        User user = userApi.findById(userId);
        String codeKey = "CHECK_CODE_" + user.getMobile();
        String code = "123456";
        redisTemplate.opsForValue().set(codeKey,code, Duration.ofMinutes(5));
    }

    /**
     * 修改手机号-验证码校验
     * @param params
     * @return
     */
    public Map<String, Boolean> checkVerificationCode(Map<String, String> params) {
        Long userId = ThreadLocalUtils.get();
        User user = userApi.findById(userId);
        String verificationCode = params.get("verificationCode");
        String codeKey = "CHECK_CODE_" + user.getMobile();
        String codeValue = redisTemplate.opsForValue().get(codeKey);
        Map<String,Boolean> map = new HashMap<>();
        if (StringUtils.pathEquals(verificationCode,codeValue)){
            map.put("verification",true);
            return map;
        }
        map.put("verification",false);
        return map;
    }

    /**
     * 修改手机号-保存
     * @param map
     */
    public void savePhone(Map<String, String> map) {
        String phone = map.get("phone");
        //获取当前用户的信息
        Long userId = ThreadLocalUtils.get();
        User user = new User();
        user.setId(Convert.toInt(userId));
        user.setMobile(phone);
        //执行保存操作
        userApi.update(user);
    }

    /**
     * 取消-喜欢
     * @param likeUserId
     */
    public void deleteLove(Long likeUserId) {
        Long userId = ThreadLocalUtils.get();
        userLikeApi.saveOrUpdate(userId,likeUserId,false);
    }

    /**
     * 粉丝喜欢
     * @param likeUserId
     */
    public void fansLove(Long likeUserId) {
        Long userId = ThreadLocalUtils.get();
        userLikeApi.saveOrUpdate(userId,likeUserId,true);
    }
}
