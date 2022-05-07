package com.itheima.service;

import cn.hutool.core.util.RandomUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.api.UserApi;
import com.itheima.api.UserInfoApi;
import com.itheima.api.VideoApi;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Constants;
import com.itheima.mongo.Video;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.SoundVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class PeachblossomService {

    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer webServer;
    @DubboReference
    private VideoApi videoApi;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 发送语音
     *
     * @param soundFile
     */
    public void savePeachblossom(MultipartFile soundFile) throws IOException {
        //判断是否为空，如果为空的话，抛出异常
        if (soundFile.isEmpty()) {
            throw new BusinessException(ErrorResult.error());
        }
        //讲语音上传到FastDFS上，获取到对应的url
        String filename = soundFile.getOriginalFilename();
        filename = filename.substring(filename.lastIndexOf(".") + 1);
        StorePath path = client.uploadFile(soundFile.getInputStream(), soundFile.getSize(), filename, null);
        String soundUrl = webServer.getWebServerUrl() + path.getFullPath();
        //构建对象
        Video video = new Video();
        video.setUserId(ThreadLocalUtils.get());
        video.setVideoUrl(soundUrl);
        video.setText("这是一个语音");
        //执行保存操作
        String videoId = videoApi.save(video);
        if (StringUtils.isEmpty(videoId)) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * 获取语音
     * @return
     */
    public SoundVo getPeachblossom() {
        //获取当前用户的id
        Long userId = ThreadLocalUtils.get();
        SoundVo soundVo = new SoundVo();
        //设置redisKey
        String key = Constants.SOUND_VIDEOS + userId;
        if (!redisTemplate.hasKey(key)){
            redisTemplate.opsForValue().set(key,"10", Duration.ofDays(1));
        }
        //获取redis中的value值
        String value = redisTemplate.opsForValue().get(key);
        if (Integer.parseInt(value) <= 0){
            return soundVo;
        }
        List<SoundVo> voList = new ArrayList<>();
        //查询当前用户的信息
        UserInfo userInfo = userInfoApi.findById(userId);
        String gender = userInfo.getGender().equalsIgnoreCase("man") ? "woman":"man";
        List<UserInfo> userInfoList = userInfoApi.findByGender(gender);
        if (userInfoList.isEmpty()){
            return soundVo;
        }
        for (UserInfo info : userInfoList) {
            List<Video> videoList = videoApi.findByUserId(info.getId());
            for (Video video : videoList) {
                SoundVo vo = SoundVo.init(info, video);
                vo.setUserId(info.getId());
                voList.add(vo);
            }
        }
        if (voList.isEmpty()){
            return soundVo;
        }
        //将redis的记录-1
        SoundVo vo = voList.get(RandomUtil.randomInt(0,voList.size()));
        vo.setRemainingTimes(redisTemplate.opsForValue().decrement(key).intValue());
        return vo;
    }
}
