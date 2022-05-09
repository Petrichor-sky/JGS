package com.itheima.vo;

import com.itheima.pojo.Admin;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
public class AdminVo implements Serializable {
    private String id;
    private String username;
    private String avatar;

    public static AdminVo init(Admin admin){
        AdminVo vo = new AdminVo();
        BeanUtils.copyProperties(admin,vo);
        vo.setId(admin.getId().toString());
        return vo;
    }
}
