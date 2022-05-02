package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import com.itheima.api.CommentApi;
import com.itheima.api.MovementApi;
import com.itheima.api.UserInfoApi;
import com.itheima.enums.CommentType;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Movement;
import com.itheima.mongo.MovementTimeLine;
import com.itheima.mongo.RecommendMovement;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.UserInfo;
import com.itheima.template.OssTemplate;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.MovementsVo;
import com.itheima.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovementService {

    @Autowired
    private OssTemplate ossTemplate;
    @DubboReference
    private MovementApi movementApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private TimeLineService timeLineService;
    @Autowired
    private ReMovementService reMovementService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @DubboReference
    private CommentApi commentApi;
    /**
     * 发布动态
     * @param movement
     * @param imageContent
     */
    public void saveMovements(Movement movement, MultipartFile[] imageContent) {
        if (StringUtils.isEmpty(movement.getTextContent())){
            throw new BusinessException(ErrorResult.contentError());
        }
        //获取当前登录者id
        Long userId = ThreadLocalUtils.get();
        //将文件上传到阿里云oss，获取地址
        List<String> medias = new ArrayList<>();
        try {
            for (MultipartFile multipartFile : imageContent) {
                String upload = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
                //将获取到的每一个路径添加到medias中
                medias.add(upload);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //封装数据对象
        movement.setUserId(userId);
        movement.setMedias(medias);
        //调用movementApi实现发布动态功能
        Movement mvResult = movementApi.saveMovements(movement);
        timeLineService.saveTimeLine(mvResult.getUserId(),mvResult.getId());
    }

    /**
     * 我的动态
     * @param page
     * @param pageSize
     * @param userId
     * @return
     */
    public PageResult all(Integer page, Integer pageSize, Long userId) {
        if (ObjectUtils.isEmpty(userId)){
            userId = ThreadLocalUtils.get();
        }
        UserInfo userInfo = userInfoApi.findById(userId);
        //根据id查询发布的动态
        List<Movement> movementList = movementApi.findByUserId(userId,page,pageSize);
        //创建一个集合
        List<MovementsVo> list = new ArrayList<>();
        //遍历
        for (Movement movement : movementList) {
            MovementsVo movementsVo = MovementsVo.init(userInfo, movement);
            list.add(movementsVo);
        }
        Integer count = movementApi.countByUserId(userId);
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(list);
        result.setCounts(count);
        //返回结果
        return result;
    }

    /**
     * 获取好友的动态
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getFriendMovements(Integer page, Integer pageSize) {
        //获取当前用户的id
        Long uid = ThreadLocalUtils.get();
        //根据friendId查询数据
        List<MovementTimeLine> movementTimeLines = timeLineService.findByFid(uid,page,pageSize);
        //获取到数据中movementId
        List<Object> movementIds = CollUtil.getFieldValues(movementTimeLines, "movementId");
        //根据ids查询所有的动态信息
        List<Movement> movementList = movementApi.findByMoveIds(movementIds);
        //创建封装集合对象
        List<MovementsVo> movementsVoList = new ArrayList<>();
        //遍历
        for (Movement movement : movementList) {
            //获取当前用户id
            Long userId = movement.getUserId();
            //查询当前用户对应的id
            UserInfo userInfo = userInfoApi.findById(userId);
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            vo.setHasLiked(commentApi.hasComment(movement.getId().toHexString(),ThreadLocalUtils.get(), CommentType.LIKE) ? 1 : 0);
            vo.setCommentCount(commentApi.countByPublishId(movement.getId().toHexString()));
            movementsVoList.add(vo);
        }
        //创建返回数据对象
        PageResult result = new PageResult();
        result.setCounts(movementsVoList.size());
        result.setPages(1);
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(movementsVoList);
        return result;
    }

    /**
     * 推荐动态
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult recommend(Integer page, Integer pageSize) {
        List<MovementsVo> movementsVoList = new ArrayList<>();
        //创建key和获取对应的value
        String redisKey = "MOVEMENTS_RECOMMEND_" + ThreadLocalUtils.get();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //创建集合
        List<Movement> list = Collections.EMPTY_LIST;
        //判断数据是否为空
        if (StringUtils.isEmpty(redisValue)){
            //如果为空的话，则进行随机生成
            list = movementApi.randomMovements(pageSize);
        }else {
            //将获取到的value进行切割处理
            String[] values = redisValue.split(",");
            //判断起始条数是否小于数组的长度
            if ((page-1)*pageSize < values.length){
                List<Long> pids = Arrays.stream(values).skip((page - 1) * pageSize).limit(pageSize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());
                //根据ids来查询对应的动态信息
                list = movementApi.findMoveByPids(pids);
            }
        }
        //遍历
        for (Movement movement : list) {
            //获取对应的userId
            Long userId = movement.getUserId();
            UserInfo userInfo = userInfoApi.findById(userId);
            MovementsVo movementsVo = MovementsVo.init(userInfo, movement);
            movementsVo.setHasLiked(commentApi.hasComment(movement.getId().toHexString(),ThreadLocalUtils.get(), CommentType.LIKE) ? 1 : 0);
            movementsVo.setCommentCount(commentApi.countByPublishId(movement.getId().toHexString()));
            movementsVoList.add(movementsVo);
        }
        //创建返回数据的对象
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(movementsVoList);
        return result;
    }

    public MovementsVo findMovementById(String movementId) {
        //获取动态信息
        Movement movement = movementApi.findByMoveId(movementId);
        if (ObjectUtils.isEmpty(movement)){
            return null;
        }
        //根据动态信息获取对应的用户信息
        UserInfo userInfo = userInfoApi.findById(movement.getUserId());
        //创建返回对象
        return MovementsVo.init(userInfo, movement);
    }
}
