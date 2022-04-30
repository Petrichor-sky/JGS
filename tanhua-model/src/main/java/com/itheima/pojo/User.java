package com.itheima.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class User extends BasePojo {

    private Integer id;
    private String mobile;
    private String password;

    //环信用户信息
    private String hxUser;
    private String hxPassword;
}
