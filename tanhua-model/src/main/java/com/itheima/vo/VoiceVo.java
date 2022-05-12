package com.itheima.vo;

import com.itheima.mongo.Voice;
import com.itheima.pojo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceVo implements Serializable {

    private Integer id;                //用户id
    private String avatar;             //头像
    private String nickname;           //昵称
    private String gender;            //性别
    private String soundUrl;           //语音地址
    private Integer age;               //年龄
    private Integer remainingTimes;     //剩余次数

    public static VoiceVo init(UserInfo userInfo, Voice voice) {
        VoiceVo vo = new VoiceVo();
        BeanUtils.copyProperties(userInfo,vo);
        vo.setSoundUrl(voice.getSoundUrl());
        return vo;
    }

}