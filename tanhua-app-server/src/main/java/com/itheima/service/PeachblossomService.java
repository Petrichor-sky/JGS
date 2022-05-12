package com.itheima.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.api.UserInfoApi;
import com.itheima.api.VideoApi;
import com.itheima.api.VoiceApi;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Constants;
import com.itheima.mongo.Voice;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.UserInfo;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.VoiceVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @DubboReference
    private VoiceApi voiceApi;
    @Autowired
    private FileStorageService fileStorageService;

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
       //上传
        String soundUrl = fileStorageService.upload(soundFile);
        //获取当前用户的信息
        UserInfo userInfo = userInfoApi.findById(ThreadLocalUtils.get());
        //创建Voice对象
        Voice voice = Voice.init(userInfo);
        voice.setState(0);
        voice.setSoundUrl(soundUrl);
        voice.setCreated(new Date());
        voice.setUpdated(new Date());
        //执行保存操作
        String voiceId = voiceApi.save(voice);
        if (StringUtils.isEmpty(voiceId)) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * 获取语音
     * @return
     */
    public VoiceVo getPeachblossom() {
        //获取当前用户的id
        Long userId = ThreadLocalUtils.get();
        VoiceVo voiceVo = new VoiceVo();
        //设置redisKey
        String key = Constants.SOUND_VOICE + userId;
        if (!redisTemplate.hasKey(key)){
            redisTemplate.opsForValue().set(key,"10", Duration.ofDays(1));
        }
        //获取redis中的value值
        String value = redisTemplate.opsForValue().get(key);
        if (Integer.parseInt(value) <= 0){
            return voiceVo;
        }
        List<VoiceVo> vos = new ArrayList<>();
        //查询当前用户的信息
        UserInfo userInfo = userInfoApi.findById(userId);
        String gender = userInfo.getGender().equalsIgnoreCase("man") ? "woman":"man";
        List<Voice> voiceList = voiceApi.findByGenderAndCreated(gender);
        //获取对应的userIds
        List<Long> userIds = CollUtil.getFieldValues(voiceList, "userId", Long.class);
        if (voiceList.isEmpty()){
            return voiceVo;
        }
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        for (Voice voice : voiceList) {
            UserInfo info = map.get(voice.getUserId());
            if (!ObjectUtils.isEmpty(info)){
                vos.add(VoiceVo.init(info,voice));
            }
        }
        //将redis的记录-1
        VoiceVo vo = vos.get(RandomUtil.randomInt(0,vos.size()));
        vo.setRemainingTimes(redisTemplate.opsForValue().decrement(key).intValue());
        return vo;
    }
}
