package com.itheima.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.dto.RecommendUserDto;
import com.itheima.mapper.UserInfoMapper;
import com.itheima.pojo.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@DubboService
public class UserInfoServiceImpl implements UserInfoApi{
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public void saveOrUpdate(UserInfo userInfo) {
        //何时新增？何时修改？
        UserInfo uif = userInfoMapper.selectById(userInfo.getId());
        if(ObjectUtil.isEmpty(uif)){//userinfo中不存在则新增
            userInfoMapper.insert(userInfo);
        }else {
            userInfoMapper.updateById(userInfo);
        }
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public List<UserInfo> findUserInfoByIds(List<Long> list) {
        return userInfoMapper.selectBatchIds(list);
    }

    @Override
    public UserInfo findInfoById(Long userId, RecommendUserDto recommendUserDto) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getId,userId);
        queryWrapper.eq(StrUtil.isNotBlank(recommendUserDto.getGender()),
                UserInfo::getGender,recommendUserDto.getGender());
        return userInfoMapper.selectOne(queryWrapper);
    }

    /**
     * 根据ids和info来进行条件查询
     * @param friendIds
     * @param userInfo
     * @return
     */
    @Override
    public Map<Long, UserInfo> findByIds(List<Long> friendIds, UserInfo userInfo) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",friendIds);
        //设置条件
        if (userInfo != null){
            if (userInfo.getAge() != null){
                queryWrapper.lt("age",userInfo.getAge());
            }
            if (!StringUtils.isEmpty(userInfo.getGender())){
                queryWrapper.eq("gender",userInfo.getGender());
            }
            if (!StringUtils.isEmpty(userInfo.getNickname())){
                queryWrapper.like("nickname",userInfo.getNickname());
            }
        }
        List<UserInfo> list = userInfoMapper.selectList(queryWrapper);
        return CollUtil.fieldValueMap(list,"id");
    }

    /**
     * 查询异性信息
     * @param gender
     * @return
     */
    @Override
    public List<UserInfo> findByGender(String gender) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getGender,gender);
        return userInfoMapper.selectList(queryWrapper);
    }

    /**
     * 查询所有
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public IPage<UserInfo> findAll(Integer page, Integer pageSize) {
        IPage<UserInfo> iPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UserInfo::getCreated);
        return userInfoMapper.selectPage(iPage,queryWrapper);
    }
}
