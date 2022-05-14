package com.itheima.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document("mock_movement")
public class MockMovement implements Serializable {
    private ObjectId id; //主键id
    private String nickname;
    private Long userId;
    private String userLogo;
    private Long createDate; //发布时间
    private String text; //文字
    private String state = "3";//审核状态，1为待审核，2为自动审核通过，3为待人工审核，4为人工审核拒绝，5为人工审核通过，6为自动审核拒绝
    private Integer reportCount; //举报数
    private Integer likeCount;  //点赞数
    private Integer commentCount;   //评论数
    private Integer forwardingCount;   //转发数
    private List<String> medias; //媒体数据，图片或小视频 url
    private Integer topState = 1;//1表示未置顶
}
