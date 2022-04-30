package com.itheima.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mapper.BlackListMapper;
import com.itheima.pojo.BlackList;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListServiceImpl implements BlackListApi{
    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    public IPage<BlackList> findByUserId(Long id, Integer page, Integer pageSize) {
        IPage<BlackList> iPage = new Page<>(page,pageSize);
        blackListMapper.selectPage(iPage,new LambdaQueryWrapper<BlackList>().eq(BlackList::getUserId,id));
        return iPage;
    }

    @Override
    public void deleteBlackUserById(Long id, Long uid) {
        LambdaQueryWrapper<BlackList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlackList::getUserId,id).eq(BlackList::getBlackUserId,uid);
        blackListMapper.delete(queryWrapper);
    }
}
