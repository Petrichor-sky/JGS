package com.itheima.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class MockUserInfoVo {

    private Long id;
    /**
     * 头像
     */
    private String logo;

    /**
     * 头像状态 ,1通过，2拒绝
     */
    @TableField(exist = false)
    private String logoStatus = "1";
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号，即用户账号
     */
    private String mobile;
    /**
     * 性别
     */
    private String sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 职业,暂无该字段
     */
    private String occupation;
    /**
     * 用户状态,1为正常，2为冻结
     */
    @TableField(exist = false)
    private String userStatus = "1";
    /**
     * 最近活跃时间
     */
    private Long lastActiveTime;
    /**
     * 注册地区
     */
    private String city;
    /**
     * 注册时间
     */
    private Date created;


}
