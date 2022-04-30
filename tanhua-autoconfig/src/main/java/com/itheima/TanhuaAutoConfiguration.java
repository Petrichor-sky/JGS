package com.itheima;

import com.itheima.properties.AipFaceProperties;
import com.itheima.properties.OssProperties;
import com.itheima.template.AipFaceTemplate;
import com.itheima.template.OssTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(  {
        OssProperties.class,
        AipFaceProperties.class
})
public class TanhuaAutoConfiguration {
    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        return new OssTemplate(ossProperties);
    }

    @Bean
    private AipFaceTemplate aipFaceTemplate(AipFaceProperties aipFaceProperties){
        return new AipFaceTemplate();
    }
}
