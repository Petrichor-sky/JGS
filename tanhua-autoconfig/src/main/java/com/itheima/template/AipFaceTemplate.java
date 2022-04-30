package com.itheima.template;

import cn.hutool.core.util.ObjectUtil;
import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class AipFaceTemplate {

    @Autowired
    private AipFace client;

    public  boolean checkFaceUrl(String image){
        String imageType = "URL";//表示网络图像验证
        boolean flag=false;
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");
        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        Object code = res.get("error_code");
        //错误码为0表示当前图片有人像
        if(ObjectUtil.equal(code,0)){
            flag=true;
        }
        return flag;
    }
}
