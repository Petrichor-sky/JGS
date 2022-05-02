package com.itheima.mongo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Data
@Document("visitors")
public class Visitors implements Serializable {
    private ObjectId id;
    private String date;
    private Long userId;
    private Long visitorUserId;
    private Double score;
    private String from;
    private String visitDate;
}
