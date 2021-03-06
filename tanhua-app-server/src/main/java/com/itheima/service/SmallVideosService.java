package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.api.CommentApi;
import com.itheima.api.FocusUserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.api.VideoApi;
import com.itheima.enums.CommentType;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Comment;
import com.itheima.mongo.Constants;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
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
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private FileStorageService fileStorageService;
    /**
     * ????????????
     * @param videoThumbnail ??????
     * @param videoFile ??????
     */
    //@CacheEvict(value = "videoList",allEntries = true)
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        if (videoThumbnail.isEmpty() || videoFile.isEmpty()){
            throw new BusinessException(ErrorResult.error());
        }
        //1.??????????????????FastDFS??????????????????URL
        /*String filename = videoFile.getOriginalFilename();
        filename= filename.substring(filename.lastIndexOf(".") + 1);
        StorePath path = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        String videoUrl = webServer.getWebServerUrl() + path.getFullPath();*/
        String videoUrl = fileStorageService.upload(videoFile);

        //2.???????????????????????????OSS???
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //3.??????Videos??????
        Video video = new Video();
        video.setUserId(ThreadLocalUtils.get());
        video.setVideoUrl(videoUrl);
        video.setPicUrl(imageUrl);
        video.setText("?????????????????????");
        //4.??????API????????????
        String videoId = videoApi.save(video);
        //???MQ???????????????
        mqMessageService.sendLogMessage(ThreadLocalUtils.get(),"0301","video",videoId);
        if (StringUtils.isEmpty(videoId)){
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * ???????????????
     * @param page
     * @param pageSize
     * @return
     */
    //@Cacheable(value = "videoList",key = "T(com.itheima.utils.ThreadLocalUtils).get() + '_' + #page + '_' + #pageSize")
    public PageResult queryVideoList(Integer page, Integer pageSize) {
        //???redis?????????????????????vids??????
        String redisKey = Constants.VIDEOS_RECOMMEND + ThreadLocalUtils.get();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //??????????????????
        List<Video> list = new ArrayList<>();
        int redisPages = 0;
        if (!StringUtils.isEmpty(redisValue)){
            //??????redisValue??????????????????
            String[] values = redisValue.split(",");
            //???????????????????????????????????????
            if ((page -1 )*pageSize < values.length){
                List<Long> vids = Arrays.stream(values).skip((page - 1) * pageSize).limit(pageSize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());
                //???????????????api??????????????????video??????
                list = videoApi.findByVids(vids);
            }

            redisPages = PageUtil.totalPage(values.length,pageSize);
        }
        if (list.isEmpty()){
            //page?????????????????????????????????-redis??????????????????
            list = videoApi.queryVideoList(page-redisPages,pageSize);
        }
        //???????????????userId
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //??????userId??????????????????????????????
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //??????
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo userInfo = map.get(video.getUserId());
            //?????????????????????????????????????????????
            if (!ObjectUtils.isEmpty(userInfo)){
                VideoVo videoVo = VideoVo.init(userInfo, video);
                videoVo.setHasLiked(commentApi.hasComment(video.getId().toHexString(),ThreadLocalUtils.get(),CommentType.LIKE) ? 1 : 0);
                videoVo.setHasFocus(focusUserApi.hasFocus(video.getUserId(),ThreadLocalUtils.get()) ? 1 : 0);
                videoVo.setLikeCount(commentApi.countLikeCount(video.getId(),CommentType.LIKE));
                vos.add(videoVo);
            }
        }
        //???????????????
        PageResult result = new PageResult();
        result.setPage(page);
        result.setPagesize(pageSize);
        result.setItems(vos);
        //????????????
        return result;
    }

    /**
     * ????????????
     * @param videoId
     */
    public Integer likeVideo(String videoId) {
        //?????????????????????
        boolean hasComment = commentApi.hasComment(videoId, ThreadLocalUtils.get(), CommentType.LIKE);
        if (hasComment){
            throw new BusinessException(ErrorResult.likeError());
        }
        //????????????
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(ThreadLocalUtils.get());
        comment.setCreated(System.currentTimeMillis());
        //??????????????????
        Integer count = commentApi.save(comment);
        //??????key?????????redis???
        String key = Constants.VIDEOS_RECOMMEND + videoId;
        String typeKey = Constants.VIDEO_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().put(key,typeKey,"1");
        //???MQ???????????????
        mqMessageService.sendLogMessage(ThreadLocalUtils.get(),"0302","video",videoId);
        return count;
    }

    /**
     * ??????????????????
     * @param videoId
     */
    public Integer disLikeVideo(String videoId) {
        boolean hasComment = commentApi.hasComment(videoId, ThreadLocalUtils.get(), CommentType.LIKE);
        if (!hasComment){
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //????????????
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setUserId(ThreadLocalUtils.get());
        comment.setCommentType(CommentType.LIKE.getType());
        //??????????????????
        Integer count = commentApi.delete(comment);
        //??????redis????????????
        String key = Constants.VISITORS_USER + videoId;
        String typeKey = Constants.COMMENT_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().delete(key,typeKey);
        //???MQ???????????????
        mqMessageService.sendLogMessage(ThreadLocalUtils.get(),"0303","video",videoId);
        return count;
    }
    /**
     * ????????????????????????
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult getComments(String videoId, Integer page, Integer pageSize) {
        return commentService.getCommemts(videoId, page, pageSize);
    }

    /**
     * ????????????
     * @param videoId
     * @param content
     */
    public void saveComment(String videoId, String content) {
        Video video = videoApi.findById(videoId);
        if (ObjectUtils.isEmpty(video)){
            throw new BusinessException(ErrorResult.error());
        }
        //????????????
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setUserId(ThreadLocalUtils.get());
        comment.setContent(content);
        comment.setPublishUserId(video.getUserId());
        comment.setCreated(System.currentTimeMillis());
        //??????????????????
        commentApi.save(comment);
        String key = Constants.VISITORS_USER + videoId;
        String typeKey = Constants.VIDEO_LIKE_HASHKEY + ThreadLocalUtils.get();
        redisTemplate.opsForHash().put(key,typeKey,"1");
        //???MQ???????????????
        mqMessageService.sendLogMessage(ThreadLocalUtils.get(),"0304","video",videoId);
    }

    /**
     * ??????????????????
     * @param followUserId
     */
    public void saveUserFocus(Long followUserId) {
        Long userId = ThreadLocalUtils.get();
        Boolean aBoolean = focusUserApi.hasFocus(Convert.toLong(followUserId),userId);
        if (aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
        //??????????????????
        focusUserApi.save(Convert.toLong(followUserId),userId);
        //??????redis??????
        redisTemplate.opsForSet().add(Constants.FOCUS_USER + userId,followUserId.toString());
        //???????????????????????????
        Boolean isFocus = isFocus(followUserId,userId);
        if (isFocus){
            //???????????????????????????????????????????????????
            messageService.contacts(Convert.toLong(followUserId));
        }
    }

    /**
     * ????????????????????????
     * @param followUserId
     * @param userId
     * @return
     */
    private Boolean isFocus(Long userId, Long followUserId) {
        String key = Constants.FOCUS_USER + userId;
        return redisTemplate.opsForSet().isMember(key,followUserId.toString());
    }

    /**
     * ??????????????????-??????
     * @param followUserId
     */
    public void saveUserUnFocus(Long followUserId) {
        Long userId = ThreadLocalUtils.get();
        //??????????????????
        Boolean aBoolean = focusUserApi.hasFocus(Convert.toLong(followUserId), userId);
        if (!aBoolean){
            throw new BusinessException(ErrorResult.error());
        }
        //??????????????????
        focusUserApi.delete(Convert.toLong(followUserId),userId);
        //?????????????????????????????????
        Boolean isFocus = isFocus(followUserId,userId);
        if (isFocus){
            //??????????????????????????????????????????????????????????????????
            messageService.disContacts(Convert.toLong(followUserId));
        }
        //??????redis?????????
        redisTemplate.opsForSet().remove(Constants.FOCUS_USER + userId,Constants.FOCUS_USER + followUserId);
    }

    /**
     * ????????????
     * @param commentId
     */
    public void likeComment(String commentId) {
        commentService.likeComment(commentId);
    }

    /**
     * ??????????????????
     * @param commentId
     */
    public void disLikeComment(String commentId) {
        commentService.dislikeComment(commentId);
    }
}
