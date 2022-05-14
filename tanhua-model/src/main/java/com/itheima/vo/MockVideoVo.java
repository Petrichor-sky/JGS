package com.itheima.vo;

import com.itheima.mongo.MockVideo;
import com.itheima.pojo.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
public class MockVideoVo implements Serializable {
    private String id;
    private Long userId;
    private String nickname;
    private String videoUrl; //视频文件，URL
    private String picUrl; //视频封面文件，URL
    private Long createDate; //创建时间
    private Integer reportCount; //举报数
    private Integer likeCount;  //点赞数
    private Integer commentCount;   //评论数
    private Integer forwardingCount;   //转发数

    public static MockVideoVo init(UserInfo userInfo, MockVideo mockVideo) {
        MockVideoVo vo = new MockVideoVo();
        //copy用户属性
        BeanUtils.copyProperties(userInfo,vo);  //source,target
        //copy视频属性
        BeanUtils.copyProperties(mockVideo,vo);
        vo.setCreateDate(mockVideo.getCreateDate());
        vo.setId(mockVideo.getId().toHexString());
        return vo;
    }
}
