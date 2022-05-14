package com.itheima.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Totals implements Serializable {
    private String title;
    private String code;
    private Integer value;

    public Totals(String title, String code, Integer value) {
        this.title = title;
        this.code = code;
        this.value = value;
    }
}
