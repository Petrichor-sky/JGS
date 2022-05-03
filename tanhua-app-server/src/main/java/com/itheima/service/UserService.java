package com.itheima.service;

import com.itheima.api.UserApi;
import com.itheima.exception.BusinessException;
import com.itheima.mongo.Constants;
import com.itheima.pojo.ErrorResult;
import com.itheima.pojo.User;
import com.itheima.pojo.UserInfo;
import com.itheima.template.AipFaceTemplate;
import com.itheima.template.HuanXinTemplate;
import com.itheima.template.OssTemplate;
import com.itheima.utils.JwtUtils;
import com.itheima.utils.ThreadLocalUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @DubboReference
    private UserApi userApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    /**
     * 发送验证码功能
     * @param map
     */
    public void login(Map<String, String> map) {
        //设置key
        String codeKey = "CHECK_CODE_" + map.get("phone");
        //设置验证码为固定的123456
        String code = "123456";
        redisTemplate.opsForValue().set(codeKey,code, Duration.ofMinutes(5));
    }

    /**
     * 登录验证码校验的过程
     * @param map
     */
    public ResponseEntity loginVerification(Map<String, String> map) {
        //1.获取手机号和验证码
        String phone = map.get("phone");
        String code = map.get("verificationCode");
        //
        String codeKey = "CHECK_CODE_" + map.get("phone");
        String redisCode = redisTemplate.opsForValue().get(codeKey);
        //2.判断验证码是否为空或者不正确
        if (StringUtils.isEmpty(code) || !code.equals(redisCode)){
            throw new BusinessException(ErrorResult.loginError());
        }
        //3.如果验证码存在并且匹配正确的话，将验证码从redis中删除
        redisTemplate.delete(codeKey);
        //设置一个标记，判断是否为新用户
        boolean isNew = false;
        //4.查询数据库中是否存在该用户
        User user = userApi.findByPhone(phone);
        //5.判断是否为空，如果为空的话，则进行封装并添加
        if (ObjectUtils.isEmpty(user)){
            user=new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
            //保存数据
            int id = userApi.save(user);
            user.setId(id);
            isNew = true;

            //注册环信用户
            String hxUSer = "hx" + user.getId();
            Boolean create = huanXinTemplate.createUser(hxUSer, Constants.INIT_PASSWORD);
            if (create){
                user.setHxUser(hxUSer);
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }
        }
        //6.获取该用户的token
        Map<String,Object> params = new HashMap<>();
        params.put("id",user.getId());
        params.put("mobile",phone);
        String token = JwtUtils.getToken(params);
        //7.封装返回结果
        Map<String,Object> maps = new HashMap<>();
        maps.put("token",token);
        maps.put("isNew",isNew);
        return ResponseEntity.ok(maps);
    }

    /**
     * 添加
     * @param userInfo
     * @return
     */
    public void loginReginfo(UserInfo userInfo) {
        //设置id
        userInfo.setId(ThreadLocalUtils.get());
        //设置默认年龄
        userInfo.setAge(18);
        //将数据保存到userInfo表中
        userInfoService.save(userInfo);
    }

    public ResponseEntity uploadHead(MultipartFile headPhoto) {
        try {
            //文件上传
            String url = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
            //调用获取人像的方法
            boolean checkFaceUrl = aipFaceTemplate.checkFaceUrl(url);
            if (!checkFaceUrl){
                throw new BusinessException(ErrorResult.faceError());
            }
            userInfoService.saveHead(ThreadLocalUtils.get(),url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(null);
    }


}

