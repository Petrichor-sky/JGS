package com.itheima.chuanyin;

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
@TableName("tb_soul_question")
public class SoulQuestion extends BasePojo implements Serializable {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 问题
     */
    private String question;
    /**
     * 级别---初级,中级,高级
     */
    private String paperId;
}