package com.itheima.chuanyin;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.pojo.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soul_report")
public class SoulReport extends BasePojo implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     *用户id
     */
    private Long userId;
    /**
     * 问卷id
     */
    private Long paperId;
    /**
     * 分数
     */
    private Long score;
}