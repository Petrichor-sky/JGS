package com.itheima.chuanyin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.pojo.BasePojo;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_soul_result")
public class SoulResult extends BasePojo implements Serializable {
    private String id;
    /**
     * 问卷id
     */
    private Long paperId;
    /**
     * 分数
     */
    private Long score;
    /**
     * 封面
     */
    private String cover;
    /**
     * 结果文字描述
     */
    private String conclusion;
}
