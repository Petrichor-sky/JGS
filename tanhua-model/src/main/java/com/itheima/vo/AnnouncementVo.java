package com.itheima.vo;


import com.itheima.pojo.Announcement;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class AnnouncementVo implements Serializable {
    private String id;
    private String title;
    private String description;
    private String createDate;

    public static AnnouncementVo init(Announcement announcement) {
        AnnouncementVo vo = new AnnouncementVo();
        BeanUtils.copyProperties(announcement,vo);
        vo.setId(announcement.getId().toString());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(announcement.getCreated()));
        return vo;
    }

}
