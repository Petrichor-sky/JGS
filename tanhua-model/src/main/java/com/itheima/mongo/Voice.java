package com.itheima.mongo;

import com.itheima.pojo.BasePojo;
import com.itheima.pojo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "voice")
public class Voice extends BasePojo implements Serializable {
    private ObjectId id;
    private Long userId; //我的id
    private String nickname; //昵称
    private String avatar; //用户头像
    private String birthday; //生日
    private String gender; //性别
    private Integer age; //年龄
    private Integer state = 0;//状态 0：未审（默认），1：通过，2：驳回
    private String SoundUrl;

    public static Voice init(UserInfo userInfo){
        Voice voice = new Voice();
        voice.setId(new ObjectId());
        voice.setUserId(userInfo.getId());
        BeanUtils.copyProperties(userInfo,voice);
        return voice;
    }
}
