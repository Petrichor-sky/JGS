package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.api.CommentApi;
import com.itheima.api.FocusUserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.api.VideoApi;
import com.itheima.enums.CommentType;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Comment;
import com.itheima.mongo.Constants;
import com.itheima.mongo.FocusUser;
import com.itheima.mongo.Video;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.UserInfo;
import com.itheima.template.OssTemplate;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.PageResult;
import com.itheima.vo.VideoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.FacesWebRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {

    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer webServer;
    @Autowired
    private OssTemplate ossTemplate;
    @DubboReference
    private VideoApi videoApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserInfoApi userInfoApi;
    @DubboReference
    private CommentApi commentApi;
    @Autowired
    private CommentService commentService;
    @DubboReference
    private FocusUserApi focusUserApi;
    @Autowired
    private MessageService messageService;
    /**
     * 发布视频
     * @param videoThumbnail 视频
     * @param videoFile 图片
     */
    //@CacheEvict(value = "videoList",allEntries = true)
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        if (videoThumbnail.isEmpty() || videoFile.isEmpty()){
            throw new BusinessException(ErrorResult.error());
        }
        //1.将视频上传到FastDFS上，获取访问URL
        String filename = videoFile.getOriginalFilename();
        filename= filename.substring(filename.lastIndexOf(".") + 1);
        StorePath path = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        String videoUrl = webServer.getWebServerUrl() + path.getFullPath();

        //2.将图片上传到阿里云OSS上
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //3.构造Videos对象
        Video video = new Video();
        video.setUserId(ThreadLocalUtils.get());
        video.setVideoUrl(videoUrl);
        video.setPicUrl(imageUrl);
        video.setText("我是一个小石头");
        //4.调用API保存数据
        String videoId = videoApi.save(video);
        if (StringUtils.isEmpty(videoId)){
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * 小视频列表
     * @param page
     * @param pageSize
     * @return
     */
    //@Cacheable(value = "videoList",key = "T(com.itheima.utils.ThreadLocalUtils).get() + '_' + #page + '_' + #pageSize")
    public PageResult queryVideoList(Integer page, Integer pageSize) {
        //从redis里面获取对应的vids数据
        String redisKey = Constants.VIDEOS_RECOMMEND + ThreadLocalUtils.get();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //创建一个集合
        List<Video> list = new ArrayList<>();
        int redisPages = 0;
        if (!StringUtils.isEmpty(redisValue)){
            //对于redisValue进行切割处理
            String[] values = redisValue.split(",");
            //判断起始条数小于数据的总数
            if ((page -1 )*pageSize < values.length){
                List<Long> vids = Arrays.stream(values).skip((page - 1) * pageSize).limit(pageSize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());
                //调用相关的api来查询对应的video数据
                list = videoApi.findByVids(vids);
            }

            redisPages = PageUtil.totalPage(values.length,pageSize);
        }
        if (list.isEmpty()){
            //page的计算规则：传入的页码-redis查询的总页数
            list = videoApi.queryVideoList(page-redisPages,pageSize);
        }
        //获取所有的userId
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //通过userId来查询对应的用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //遍历
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo userInfo = map.get(video.getUserId());
            //如果不为空的话，则进行添加数据
            if (!ObjectUtils.isEmpty(userInfo)){
                VideoVo videoVo = VideoVo.init(userInfo, video);
                videoVo.setHasLiked(commentApi.hasComment(video.getId().toHexString(),ThreadLocalUtils.get(),CommentType.LIKE) ? 1 : 0);
                videoVo.setHasFocus(focusUserApi.hasFocus(video.getUserId(),ThreadLocalUtils.get()) ? 1 : 0);
                videoVo.setLikeCount(commentApi.countByPublishId(video.getId().toHexString()));
                vos.add(videoVo);
            }
        }
        //构建返回值
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        //返回结果
        return result;
    }

    /**
     * 视频点赞
     * @param videoId
     */
    public Integer likeVideo(String videoId) {
        //判断是否已点赞
        boolean hasComment = commentApi.hasComment(videoId, ThreadLocalUtils.get(), CommentType.LIKE);
        if (hasComment){
            throw new BusinessException(ErrorResult.error());
        }
        //封装对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(ThreadLocalUtils.get());
        comment.setCreated(System.currentTimeMillis());
        //执行保存操作
        Integer count = commentApi.save(comment);
        //设置key存储到redis中
        String key = Constants.VIDEOS_RECOMMEND + videoId;
        String typeKey = Constants.VIDEO_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().put(key,typeKey,"1");
        return count;
    }

    /**
     * 取消视频点赞
     * @param videoId
     */
    public Integer disLikeVideo(String videoId) {
        boolean hasComment = commentApi.hasComment(videoId, ThreadLocalUtils.get(), CommentType.LIKE);
        if (!hasComment){
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //构造对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setUserId(ThreadLocalUtils.get());
        comment.setCommentType(CommentType.LIKE.getType());
        //执行删除操作
        Integer count = commentApi.delete(comment);
        //删除redis中的数据
        String key = Constants.VISITORS_USER + videoId;
        String typeKey = Constants.COMMENT_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().delete(key,typeKey);
        return count;
    }
    /**
     * 获取视频评论列表
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getComments(String videoId, Integer page, Integer pageSize) {
        PageResult result = commentService.getCommemts(videoId, page, pageSize);
        return result;
    }

    /**
     * 评论发布
     * @param videoId
     * @param content
     */
    public void saveComment(String videoId, String content) {
        Video video = videoApi.findById(videoId);
        if (ObjectUtils.isEmpty(video)){
            throw new BusinessException(ErrorResult.error());
        }
        //封装对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setUserId(ThreadLocalUtils.get());
        comment.setContent(content);
        comment.setPublishUserId(video.getUserId());
        comment.setCreated(System.currentTimeMillis());
        //执行保存操作
        commentApi.save(comment);
        String key = Constants.VISITORS_USER + videoId;
        String typeKey = Constants.VIDEO_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().put(key,typeKey,"1");
    }

    /**
     * 视频用户关注
     * @param followUserId
     */
    public void saveUserFocus(Long followUserId) {
        Long userId = ThreadLocalUtils.get();
        Boolean aBoolean = focusUserApi.hasFocus(Convert.toLong(followUserId),userId);
        if (aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
        //执行保存操作
        focusUserApi.save(Convert.toLong(followUserId),userId);
        //设置redis的值
        redisTemplate.opsForSet().add(Constants.FOCUS_USER + userId,followUserId.toString());
        //判断是否为互相关注
        Boolean isFocus = isFocus(followUserId,userId);
        if (isFocus){
            //如果是双向关注的话，则执行保存操作
            messageService.contacts(Convert.toLong(followUserId));
        }
    }

    /**
     * 判断是否互相关注
     * @param followUserId
     * @param userId
     * @return
     */
    private Boolean isFocus(Long userId, Long followUserId) {
        String key = Constants.FOCUS_USER + userId;
        return redisTemplate.opsForSet().isMember(key,followUserId.toString());
    }

    /**
     * 视频用户关注-取消
     * @param followUserId
     */
    public void saveUserUnFocus(Long followUserId) {
        Long userId = ThreadLocalUtils.get();
        //判断是否关注
        Boolean aBoolean = focusUserApi.hasFocus(Convert.toLong(followUserId), userId);
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
        //执行删除操作
        focusUserApi.delete(Convert.toLong(followUserId),userId);
        //判断是否为双向关注用户
        Boolean isFocus = isFocus(followUserId,userId);
        if (isFocus){
            //如果为双向关注的话，则执行删除环信和好友操作
            messageService.disContacts(Convert.toLong(followUserId));
        }
        //删除redis的数据
        redisTemplate.opsForSet().remove(Constants.FOCUS_USER + userId,Constants.FOCUS_USER + followUserId);
    }

    /**
     * 评论点赞
     * @param commentId
     */
    public void likeComment(String commentId) {
        commentService.likeComment(commentId);
    }

    /**
     * 评论点赞取消
     * @param commentId
     */
    public void disLikeComment(String commentId) {
        commentService.dislikeComment(commentId);
    }
}
