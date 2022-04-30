package com.itheima.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document("recommend_movement")
public class RecommendMovement implements Serializable {
    private ObjectId id;    //id
    private Long created;   //创建时间
    private Long userId;    //登陆这id
    private Long pid;       //pid
    private ObjectId publishId; //推荐id
    private Double score;   //分数
}
