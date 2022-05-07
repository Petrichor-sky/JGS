package com.itheima.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Count implements Serializable {
    private Integer eachLoveCount;
    private Integer loveCount;
    private Integer fanCount;
}
