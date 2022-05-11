package com.itheima.vo;

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
public class OptionsVo implements Serializable {
    /**
     * 主键id
     */
    private String id;
    /**
     * 选项内容
     */
    private String option;
}
