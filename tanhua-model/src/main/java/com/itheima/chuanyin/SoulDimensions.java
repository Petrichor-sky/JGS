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
@TableName("tb_soul_dimensions")
public class SoulDimensions implements Serializable {

    /**
     * 维度项（外向，判断，抽象，理性）
     */
    private String key;

    /**
     * 维度值
     */
    private String value;

}
