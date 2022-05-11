package com.itheima.chuanyin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.pojo.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ZJWzxy
 * 问卷列表之选项
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soul_question_option")
public class  SoulOptions  extends BasePojo implements Serializable {
    /**
     * 主键id
     */
    private String id;
    /**
     * 选项
     */
    private String option;
    /**
     * 所属于的问题id
     */
    private String questionId;
    /**
     * 分数
     */
    private Long score;
}
