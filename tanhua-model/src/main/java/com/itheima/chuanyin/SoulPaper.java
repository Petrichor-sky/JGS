package com.itheima.chuanyin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.pojo.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ZJWzxy
 * 问卷列表之问卷
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_soul_paper")
public class SoulPaper extends BasePojo implements Serializable {
    /**
     * 问卷编号
     */
    private String id;
    /**
     * 级别---初级,中级,高级
     */
    private String level;
    /**
     * 问卷名字
     */
    private String name;
    /**
     * 封面
     */
    private String cover;
    /**
     * 星别-----（例如：2颗星，3颗星，5颗星）
     */
    private Integer star;

}
