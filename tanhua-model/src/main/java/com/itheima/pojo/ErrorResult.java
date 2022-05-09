package com.itheima.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResult {
    //错误状态码
    private String errCode;
    //错误信息
    private String errMessage;

    public static ErrorResult error(){
        return ErrorResult.builder().errCode("99999").errMessage("系统未知错误").build();
    }
    public static ErrorResult fail() {
        return ErrorResult.builder().errCode("000001").errMessage("发送验证码失败").build();
    }

    public static ErrorResult loginError() {
        return ErrorResult.builder().errCode("000002").errMessage("验证码失效").build();
    }

    public static ErrorResult faceError() {
        return ErrorResult.builder().errCode("000003").errMessage("图片非人像，请重新上传!").build();
    }

    public static ErrorResult mobileError() {
        return ErrorResult.builder().errCode("000004").errMessage("手机号码已注册").build();
    }

    public static ErrorResult contentError() {
        return ErrorResult.builder().errCode("000005").errMessage("动态内容为空").build();
    }

    public static ErrorResult likeError() {
        return ErrorResult.builder().errCode("000006").errMessage("用户已点赞").build();
    }

    public static ErrorResult disLikeError() {
        return ErrorResult.builder().errCode("000007").errMessage("用户未点赞").build();
    }

    public static ErrorResult loveError() {
        return ErrorResult.builder().errCode("000008").errMessage("用户已喜欢").build();
    }

    public static ErrorResult disloveError() {
        return ErrorResult.builder().errCode("000009").errMessage("用户未喜欢").build();
    }
    public static ErrorResult freezeError1() {
        return ErrorResult.builder().errCode("000010").errMessage("您的账号已被冻结登录").build();
    }
    public static ErrorResult freezeError2() {
        return ErrorResult.builder().errCode("000011").errMessage("您的账号已被冻结发言").build();
    }
    public static ErrorResult freezeError3() {
        return ErrorResult.builder().errCode("000012").errMessage("您的账号已被冻结发布动态").build();
    }

}
