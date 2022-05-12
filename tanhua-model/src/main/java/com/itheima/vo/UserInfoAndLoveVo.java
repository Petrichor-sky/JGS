package com.itheima.vo;

import cn.hutool.core.util.RandomUtil;
import com.itheima.mongo.UserLike;
import com.itheima.pojo.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserInfoAndLoveVo {

    private Long id; //用户id
    private String avatar; //用户头像
    private String nickname; //昵称
    private String gender; //性别
    private Integer age; //年龄
    private String city; //城市
    private String education; //学历
    private Integer marriage; //婚姻状态

    private Integer matchRate;  //匹配度
    private Boolean alreadyLove;  //是否喜欢ta

    public static UserInfoAndLoveVo init(UserInfo userInfo, UserLike userLike) {
        UserInfoAndLoveVo vo = new UserInfoAndLoveVo();
        BeanUtils.copyProperties(userInfo,vo);
        vo.setMatchRate(RandomUtil.randomInt(60,90));
        vo.setAlreadyLove(userLike.getIsLike());
        return vo;
    }


}
