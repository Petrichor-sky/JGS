package com.itheima.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (TbMockUserInfo)实体类
 *
 * @author makejava
 * @since 2022-05-13 14:22:17
 */
@Data
@TableName("tb_mock_user_info")
public class MockUserInfo implements Serializable {
    private static final long serialVersionUID = -89302493535637676L;
    
    private Long id;
    /**
     * 头像
     */
    private String logo;
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
    /**
     * 修改时间
     */
    private Date updated;
    /**
     * 个性签名
     */
    private String personalSignature;
    /**
     * 被喜欢人数
     */
    private Long countBeLiked;
    /**
     * 喜欢人数
     */
    private Long countLiked;
    /**
     * 配对人数
     */
    private Long countMatching;
    /**
     * 收入
     */
    private Long income;
    /**
     * 最近登陆地
     */
    private String lastLoginLocation;
    /**
     * 用户标签
     */
    private String tags;

    /**
     * 用户状态,1为正常，2为冻结
     */
    @TableField(exist = false)
    private String userStatus = "1";

    /**
     * 头像状态 ,1通过，2拒绝
     */
    @TableField(exist = false)
    private String logoStatus = "1";
}

