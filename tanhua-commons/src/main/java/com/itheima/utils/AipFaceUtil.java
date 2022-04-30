package com.itheima.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baidu.aip.face.AipFace;
import org.json.JSONObject;

import java.util.HashMap;

/*
* 百度人脸识别
* */
public class AipFaceUtil {

    public static boolean checkFaceUrl(String image){
        boolean flag=false;
        // 初始化一个AipFace
        AipFace client = new AipFace("26037961", "RcLUvA7a62rUE11U3diiwZWY", "phW7xvU7b16GwArf2l6pRejpSmBQfqeV");
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");
        // 调用接口
        //String image = "取决于image_type参数，传入BASE64字符串或URL字符串或FACE_TOKEN字符串";
        String imageType = "URL";//表示网络图像验证
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
