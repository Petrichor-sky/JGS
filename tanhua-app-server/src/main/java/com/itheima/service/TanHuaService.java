package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.easemob.im.server.api.message.MessageApi;
import com.itheima.api.QuestionApi;
import com.itheima.api.RecommendUserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.api.UserLikeApi;
import com.itheima.dto.RecommendUserDto;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Constants;
import com.itheima.mongo.RecommendUser;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.Question;
import com.itheima.pojo.UserInfo;
import com.itheima.template.HuanXinTemplate;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.PageResult;
import com.itheima.vo.TodayBest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @DubboReference
    private QuestionApi questionApi;
    @Autowired
    private HuanXinTemplate template;
    @DubboReference
    private UserLikeApi userLikeApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private MessageService messageService;

    @Value("${recommend.uid}")
    private Long userId;
    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;


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

    /**
     * 佳人信息
     * @param id
     * @return
     */
    public TodayBest findPersonalInfoById(Long id) {

        RecommendUser recommendUser = recommendUserService.queryById(id,ThreadLocalUtils.get());
        //查询id用户的详细信息
        UserInfo userInfo = userInfoApi.findById(id);
        return TodayBest.init(userInfo,recommendUser);
    }

    /**
     * 查询陌生人问题
     * @param userId
     * @return
     */
    public String getStrangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        return question == null ? "你喜欢java吗" : question.getTxt();
    }

    /**
     * 回复陌生人消息
     * @param params
     */
    public void strangerQuestions(Map<String, String> params) {
        //获取参数信息
        String userId = params.get("userId");
        String reply = params.get("reply");
        Long uid = ThreadLocalUtils.get();
        UserInfo userInfo = userInfoApi.findById(uid);
        //创建map集合
        Map<String,Object> map = new HashMap<>();
        map .put("userId",uid);
        map.put("huanXinId", Constants.HX_USER_PREFIX + uid);
        map .put("nickname",userInfo.getNickname());
        map.put("strangerQuestion",getStrangerQuestions(Long.valueOf(userId)));
        map.put("reply",reply);
        //将map集合转换成json格式
        String message = JSON.toJSONString(map);
        //调用方法，发送信息给传进来的用户id
        Boolean aBoolean = template.sendMsg(Constants.HX_USER_PREFIX + userId, message);
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * 推荐用户列表
     * @return
     */
    public List<TodayBest> queryCardList() {
        //调用api，排除喜欢和不喜欢的用户
        List<RecommendUser> cardsList = recommendUserApi.queryCardsList(ThreadLocalUtils.get(), 10);
        //判断是否为空，如果为空的话，构建默认数据
        if (CollUtil.isEmpty(cardsList)){
            cardsList = new ArrayList<>();
            String[] userIds = recommendUser.split(",");
            for (String id : userIds) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(id));
                recommendUser.setToUserId(ThreadLocalUtils.get());
                recommendUser.setScore(RandomUtil.randomDouble(60,90));
                cardsList.add(recommendUser);
            }
        }
        //创建封装的好友
        List<TodayBest> bestList = new ArrayList<>();
        //遍历
        for (RecommendUser recommendUser : cardsList) {
            //获取userId
            Long userId = recommendUser.getUserId();
            //通过id查询用户的信息
            UserInfo userInfo = userInfoApi.findById(userId);
            //封装对象
            TodayBest todayBest = TodayBest.init(userInfo, recommendUser);
            bestList.add(todayBest);
        }
        //返回结果
        return bestList;
    }

    /**
     * 喜欢
     * @param likeUserId
     */
    public void love(Long likeUserId) {
        Long userId = ThreadLocalUtils.get();
        //1.执行保存数据
        boolean saveOrUpdate = userLikeApi.saveOrUpdate(userId, likeUserId, true);
        if (!saveOrUpdate){
            throw new BusinessException(ErrorResult.error());
        }
        //操作redis
        redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + userId,likeUserId.toString());
        redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + userId,likeUserId.toString());
        //判断是否双向喜欢
        boolean like = isLike(likeUserId, userId);
        if (like){
            //如果是的话，则添加好友
            messageService.contacts(likeUserId);
        }

    }

    /**
     * 是否双向喜欢
     */
    public Boolean isLike(Long userId,Long likeUserId){
        String key = Constants.USER_LIKE_KEY + userId;
        return redisTemplate.opsForSet().isMember(key,likeUserId.toString());
    }

    /**
     * 不喜欢
     * @param likeUserId
     */
    public void disLove(Long likeUserId) {
        //执行保存的操作
        Long userId = ThreadLocalUtils.get();
        boolean saveOrUpdate = userLikeApi.saveOrUpdate(userId, likeUserId, false);
        if (!saveOrUpdate){
            throw new BusinessException(ErrorResult.error());
        }
        //判断是否是双向好友
        Boolean like = isLike(userId, likeUserId);
        if (like){
            //如果是的话，则删除好友
            messageService.disContacts(likeUserId);
        }
        //操作redis
        redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + userId,likeUserId.toString());
        redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + userId,likeUserId.toString());

    }
}
