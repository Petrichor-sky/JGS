package com.itheima.vo;

import com.itheima.pojo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//附近的人vo对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearUserVo implements Serializable {

    private Long userId;
    private String avatar;
    private String nickname;

    public static NearUserVo init(UserInfo userInfo) {
        NearUserVo vo = new NearUserVo();
        vo.setUserId(userInfo.getId());
        vo.setAvatar(userInfo.getAvatar());
        vo.setNickname(userInfo.getNickname());
        return vo;
    }
}