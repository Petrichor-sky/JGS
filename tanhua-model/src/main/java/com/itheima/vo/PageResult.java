package com.itheima.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult implements Serializable {
    private Integer counts;
    private Integer pagesize;
    private Integer pages;
    private Integer page = 0;
    private List<?> items = new ArrayList<>();

    public PageResult(Integer page,Integer pagesize,
                      Integer counts,List list) {
        this.page = page;
        this.pagesize = pagesize;
        this.items = list;
        this.counts = counts;
        this.pages =  counts % pagesize == 0 ? counts / pagesize : counts / pagesize + 1;
    }

}
