package com.itheima.chuanyin;

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
public class SoulSimilarYou implements Serializable {

    /**
     * 用户编号
     */
    private Integer id;

    /**
     * 用户头像
     */
    private String avatar;
}
