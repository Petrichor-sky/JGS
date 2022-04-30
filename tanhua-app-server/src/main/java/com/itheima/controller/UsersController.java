package com.itheima.controller;

import com.itheima.pojo.Question;
import com.itheima.pojo.UserInfo;
import com.itheima.service.QuestionService;
import com.itheima.service.SettingsService;
import com.itheima.service.UserService;
import com.itheima.service.UsersService;
import com.itheima.utils.ThreadLocalUtils;
import com.itheima.vo.PageResult;
import com.itheima.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("users")
public class UsersController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SettingsService settingsService;

    /**
     * 查询用户资料
     * @param userID
     * @return
     */
    @GetMapping
    public ResponseEntity FindById(Long userID){
        if (ObjectUtils.isEmpty(userID)){
            userID = ThreadLocalUtils.get();
        }
        UserInfoVo userInfoVo = usersService.findById(userID);
        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 保存头像
     */
    @PostMapping("header")
    public ResponseEntity updateHeader(MultipartFile headPhoto){
        userService.uploadHead(headPhoto);
        return ResponseEntity.ok(null);
    }

    /**
     * 修改资料
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo){
        usersService.updateUserInfo(userInfo);
        return ResponseEntity.ok(null);
    }

    /**
     * 设置陌生人问题
     * @param map
     * @return
     */
    @PostMapping("questions")
    public ResponseEntity setQuestions(@RequestBody Map<String,Object> map){
        String content = (String) map.get("content");
        return questionService.save(content);
    }

    /**
     * 通用设置
     * @return
     */
    @GetMapping("settings")
    public ResponseEntity getSettings(){
        return settingsService.getSettingsVo();
    }

    /**
     * 获取所有黑名单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("blacklist")
    public ResponseEntity<PageResult> blacklist(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        PageResult result =  settingsService.blacklist(page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 移除黑名单
     * @param uid
     * @return
     */
    @DeleteMapping("blacklist/{uid}")
    public ResponseEntity deleteBlackUserById(@PathVariable("uid")Long uid){
        settingsService.deleteBlackUserById(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 保存通用设置
     *
     */
    @PostMapping("notifications/setting")
    public ResponseEntity settings(@RequestBody Map map){
        settingsService.settings(map);
        return ResponseEntity.ok(null);
    }
}
