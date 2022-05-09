package com.tanhua.admin.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.itheima.mongo.Constants;
import com.itheima.pojo.Admin;
import com.itheima.utils.JwtUtils;
import com.itheima.vo.AdminVo;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private AdminService adminService;
    /**
     * 用户登录验证码图片
     * @param uuid
     */
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        //1.生成验证码对象
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(299, 97);
        //获取验证码
        String code = captcha.getCode();
        //将验证码存入到redis
        redisTemplate.opsForValue().set(Constants.CAP_CODE + uuid,code);
        //输出验证码图片
        captcha.write(response.getOutputStream());


    }


    /**
     * 用户登录
     */
    public Map<String, Object> login(Map<String, String> params) {
        //获取前端的参数
        String username = params.get("username");
        String password = params.get("password");

        String verificationCode = params.get("verificationCode");
        String uuid = params.get("uuid");
        String redisCode = redisTemplate.opsForValue().get(Constants.CAP_CODE + uuid);

        //根据用户名进行查询
        Admin admin = adminService.findAdminByUserName(username);
        //判断用户名或者密码是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            throw new BusinessException("用户名或密码为空");
        }
        //判断账号是否存在
        if (ObjectUtils.isEmpty(admin)){
            throw new BusinessException("账号不存在");
        }
        //判断密码是否正确
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!StringUtils.pathEquals(pwd,admin.getPassword())){
            throw new BusinessException("密码错误");
        }
        //判断验证码是否为空，或者不正确
        if (StringUtils.isEmpty(redisCode) || !StringUtils.pathEquals(verificationCode,redisCode)){
            throw new BusinessException("验证码错误");
        }
        //如果都不满足的话，说明登录成功
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("id",admin.getId());
        //生成token
        String token = JwtUtils.getToken(map);
        //构建返回值结果
        Map<String,Object> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        return tokenMap;
    }

    /**
     * 用户基本信息
     * @return
     */
    public AdminVo getProfile() {
        //获取当前用户的id
        Long userId = AdminHolder.getUserId();
        //根据id查询用户的基本信息
        Admin admin = adminService.getAdminById(userId);
        return AdminVo.init(admin);
    }

    /**
     * 用户登出
     * @param request
     */
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }
}
