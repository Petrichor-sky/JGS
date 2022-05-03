package com.itheima.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("tanhua.huanxin")
@Data
public class HuanXinProperties {
    private String appkey;
    private String clientId;
    private String clientSecret;
}