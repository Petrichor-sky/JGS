package com.itheima.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (TbUseTimeLog)实体类
 *
 * @author makejava
 * @since 2022-05-14 11:06:35
 */
@Data
public class TbUseTimeLog implements Serializable {
    private static final long serialVersionUID = -85912323771270752L;
    
    private Long id;
    /**
     * 用户id
     */
    private Long userid;
    /**
     * 登录日期
     */
    private String logDate;
    /**
     * 登入时间
     */
    private Long logIn;
    /**
     * 登出时间
     */
    private Long logOut;
    /**
     * 使用时间
     */
    private Long useTime;
    /**
     * 创建时间
     */
    private Date created;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public Long getLogIn() {
        return logIn;
    }

    public void setLogIn(Long logIn) {
        this.logIn = logIn;
    }

    public Long getLogOut() {
        return logOut;
    }

    public void setLogOut(Long logOut) {
        this.logOut = logOut;
    }

    public Long getUseTime() {
        return useTime;
    }

    public void setUseTime(Long useTime) {
        this.useTime = useTime;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}

