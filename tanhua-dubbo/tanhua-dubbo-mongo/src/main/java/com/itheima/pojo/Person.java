package com.itheima.pojo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "Person")
public class Person {
    private ObjectId id;
    private String name;
    private int age;
    private String address;
}
