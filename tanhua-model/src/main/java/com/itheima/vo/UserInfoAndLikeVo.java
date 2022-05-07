package com.itheima.vo;

import cn.hutool.core.util.RandomUtil;
import com.itheima.mongo.Comment;
import com.itheima.mongo.UserLike;
import com.itheima.pojo.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class UserInfoAndLikeVo {
    private String id;
    private String avatar;
    private String nickname;
    private String createDate;

    public static UserInfoAndLikeVo init(UserInfo userInfo, UserLike userLike) {
        UserInfoAndLikeVo vo = new UserInfoAndLikeVo();
        BeanUtils.copyProperties(userInfo,vo);
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userLike.getCreated()));
        return vo;
    }
    public static UserInfoAndLikeVo init(UserInfo userInfo, Comment item) {
        UserInfoAndLikeVo vo = new UserInfoAndLikeVo();
        BeanUtils.copyProperties(userInfo, vo);
        BeanUtils.copyProperties(item, vo);
        Date date = new Date(item.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        vo.setId(item.getId().toHexString());
        return vo;
    }


}
