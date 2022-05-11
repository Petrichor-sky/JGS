package com.itheima.chuanyin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.pojo.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soul_question_options")
public class Answers extends BasePojo {

    /**
     * 试题id
     */
    private String questionId;
    /**
     * 选项id
     */
    private String optionId;

}