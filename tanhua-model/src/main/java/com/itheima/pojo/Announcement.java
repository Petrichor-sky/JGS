package com.itheima.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Announcement extends BasePojo implements Serializable {
    private Long id;
    private String title;
    private String description;
}
