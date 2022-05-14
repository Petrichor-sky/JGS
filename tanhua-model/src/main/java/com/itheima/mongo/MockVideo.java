package com.itheima.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document("MockVideo")
public class MockVideo implements Serializable {
    private static final long serialVersionUID = -3136732836884933843L;

    private ObjectId id; //主键id
    private Long userId;
    private String videoUrl; //视频文件，URL
    private String picUrl; //视频封面文件，URL
    private Long createDate; //创建时间
    private Integer reportCount; //举报数
    private Integer likeCount;  //点赞数
    private Integer commentCount;   //评论数
    private Integer forwardingCount;   //转发数


}
