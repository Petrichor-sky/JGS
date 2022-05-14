package com.itheima.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_use_time_log")
public class UseTimeLog extends BasePojo implements Serializable {

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

}
