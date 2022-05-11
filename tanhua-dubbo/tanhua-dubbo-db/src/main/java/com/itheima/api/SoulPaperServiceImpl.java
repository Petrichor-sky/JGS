package com.itheima.api;

import com.itheima.chuanyin.SoulPaper;
import com.itheima.mapper.SoulPaperMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SoulPaperServiceImpl implements SoulPaperAPi{
    @Autowired
    private SoulPaperMapper soulPaperMapper;

    /**
     * 根据id进行查询
     * @param id
     * @return
     */
    @Override
    public SoulPaper findById(int id) {
        return  soulPaperMapper.selectById(id);
    }
}
