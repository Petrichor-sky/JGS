package com.itheima.chuanyin;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tb_soul_paper_question")
public class SoulPaperQuestion implements Serializable {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 问卷id
     */
    private Long paperId;
    /**
     * 问题id
     */
    private Long questionId;
}
