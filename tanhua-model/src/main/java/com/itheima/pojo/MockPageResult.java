package com.itheima.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MockPageResult {
    private Integer counts;
    private Integer pagesize;
    private Integer pages;
    private Integer page = 0;
    private List<?> items = new ArrayList<>();
    private List<?> totals = new ArrayList<>();
}
