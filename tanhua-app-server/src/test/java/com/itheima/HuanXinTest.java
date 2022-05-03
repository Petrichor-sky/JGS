package com.itheima;

import com.itheima.api.UserApi;
import com.itheima.mongo.Constants;
import com.itheima.pojo.User;
import com.itheima.template.HuanXinTemplate;
import org.apache.dubbo.config.annotation.DubboReference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HuanXinTest {

    @Autowired
    private HuanXinTemplate template;
    @DubboReference
    private UserApi userApi;


    @Test
    public void testRegister() {
        template.createUser("user05","123456");
    }


    @Test
    public void register() {
        for (int i = 1; i <= 113; i++) {
            User user = userApi.findById(Long.valueOf(i));
            if(user != null) {
                Boolean create = template.createUser("hx" + user.getId(), Constants.INIT_PASSWORD);
                if (create){
                    user.setHxUser("hx" + user.getId());
                    user.setHxPassword(Constants.INIT_PASSWORD);
                    userApi.update(user);
                }
            }
        }
    }

}
