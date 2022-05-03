package com.itheima;


import com.itheima.properties.AipFaceProperties;
import com.itheima.properties.HuanXinProperties;
import com.itheima.properties.OssProperties;
import com.itheima.template.AipFaceTemplate;
import com.itheima.template.HuanXinTemplate;
import com.itheima.template.OssTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        OssProperties.class,
        AipFaceProperties.class,
        HuanXinProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean
    public OssTemplate ossTemplate(OssProperties properties) {
        return new OssTemplate(properties);
    }
    @Bean
    public AipFaceTemplate aipFaceTemplate() {
        return new AipFaceTemplate();
    }
    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties){
        return new HuanXinTemplate(properties);
    }
}
