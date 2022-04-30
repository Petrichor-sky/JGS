package com.itheima;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OSSTest {
    @Test
    public void test() throws FileNotFoundException {
        //配置图片的路径
        String path = "D:\\资料\\图片\\1.jpg";

        FileInputStream inputStream = new FileInputStream(new File(path));
        //构造图片的路径
        String filename = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                 + "/" + UUID.randomUUID().toString() + path.substring(path.lastIndexOf("."));

        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5t5s6pJdWd9tkXoZqAr8";
        String accessKeySecret = "5VEyyDnmMg6pMwsBIwFNcDWdEouVK8";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        ossClient.putObject("tanhua001",filename,inputStream);
        ossClient.shutdown();
    }
}
