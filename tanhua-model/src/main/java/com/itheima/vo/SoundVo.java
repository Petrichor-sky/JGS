package com.itheima.vo;

import com.itheima.mongo.RecommendUser;
import com.itheima.mongo.Video;
import com.itheima.pojo.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
public class SoundVo implements Serializable {
    private Long userId; //用户id
    private String avatar; //头像
    private String nickname; //昵称
    private String gender; //性别 man woman
    private Integer age; //年龄

    private String soundUrl;
    private Integer remainingTimes;

    public static SoundVo init(UserInfo userInfo, Video video) {
        SoundVo vo = new SoundVo();
        BeanUtils.copyProperties(userInfo,vo);
        vo.setSoundUrl(video.getVideoUrl());
        return vo;
    }
}
