package com.tanhua.admin.controller;

import com.itheima.entity.MockUserInfo;
import com.itheima.mongo.MockMovement;
import com.itheima.pojo.MockPageResult;
import com.itheima.vo.PageResult;
import com.tanhua.admin.service.MockManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("manage")
public class MockManageController {

    @Autowired
    private MockManageService mockManageService;

    /**
     * 用户数据翻页
     */
    @GetMapping("users")
    public ResponseEntity<PageResult> findAllUserInfo(@RequestParam(defaultValue = "1")Integer page,
                                                          @RequestParam(defaultValue = "10")Integer pageSize,
                                                          Long id,String nickname,String city){
        PageResult list = mockManageService.findAllUserInfo(page,pageSize,id,nickname,city);
        return ResponseEntity.ok(list);
    }

    /**
     * 用户基本资料
     * @param userID
     * @return
     */
    @GetMapping("users/{userID}")
    public ResponseEntity<MockUserInfo> getUserInfo(@PathVariable("userID")Long userID){
        MockUserInfo userInfo = mockManageService.getUserInfo(userID);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 用户冻结
     */
    @PostMapping("users/freeze")
    public ResponseEntity<Map<String,String>> freeze(@RequestBody Map<String,String> params){
        Map<String,String> map = mockManageService.freeze(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 用户解冻
     */
    @PostMapping("users/unfreeze")
    public ResponseEntity<Map<String,String>> unfreeze(@RequestBody Map<String,String> params){
        Map<String,String> map = mockManageService.unfreeze(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 视频记录翻页
     */
    @GetMapping("videos")
    public ResponseEntity<PageResult> findAllVideos(@RequestParam(defaultValue = "1")Integer page,
                                                    @RequestParam(defaultValue = "10")Integer pageSize,
                                                    Long uid,String sortProp,String sortOrder){
        PageResult result = mockManageService.findAllVideos(uid,page,pageSize,sortProp,sortOrder);
        return ResponseEntity.ok(result);
    }

    /**
     * 评论列表翻页
     */
    @GetMapping("messages/comments")
    public ResponseEntity<PageResult> findAllComments(@RequestParam(defaultValue = "1")Integer page,
                                                      @RequestParam(defaultValue = "10")Integer pageSize,
                                                      String messageID,String sortProp,String sortOrder){
        PageResult result = mockManageService.findAllCommemts(messageID,page,pageSize,sortProp,sortOrder);
        return ResponseEntity.ok(result);
    }

    /**
     * 日志管理
     */
    @GetMapping("logs")
    public ResponseEntity<PageResult> getLogs(@RequestParam(defaultValue = "1")Integer page,
                                              @RequestParam(defaultValue = "10")Integer pageSize,
                                              String sortProp,String sortOrder,String type,Long uid){

        PageResult result = mockManageService.getLogs(page,pageSize,sortProp,sortOrder,type,uid);
        return ResponseEntity.ok(result);
    }

    /**
     * 消息通过
     */
    @PostMapping("messages/pass")
    public ResponseEntity<Map<String,String>> pass(@RequestBody String[] ids){
        Map<String,String> map = mockManageService.pass(ids);
        return ResponseEntity.ok(map);
    }
    /**
     * 消息撤销
     */
    @PostMapping("messages/revocation")
    public ResponseEntity<Map<String,String>> revocation(@RequestBody String[] ids){
        Map<String,String> map = mockManageService.revocation(ids);
        return ResponseEntity.ok(map);
    }

    /**
     * 消息拒绝
     */
    @PostMapping("messages/reject")
    public ResponseEntity<Map<String,String>> reject(@RequestBody String[] ids){
        Map<String,String> map = mockManageService.reject(ids);
        return ResponseEntity.ok(map);
    }


    /**
     * 消息详情
     */
    @GetMapping("messages/{id}")
    public ResponseEntity<MockMovement> findMovement(@PathVariable("id") String movementId){
        MockMovement vo = mockManageService.findMovement(movementId);
        return ResponseEntity.ok(vo);
    }
    /**
     * 消息置顶
     */
    @PostMapping("messages/{id}/top")
    public ResponseEntity<Map<String,String>> top(@PathVariable("id") String movementId){
        Map<String,String> map = mockManageService.top(movementId);
        return ResponseEntity.ok(map);
    }
    /**
     * 取消消息置顶
     */
    @PostMapping("messages/{id}/untop")
    public ResponseEntity<Map<String,String>> untop(@PathVariable("id") String movementId){
        Map<String,String> map = mockManageService.untop(movementId);
        return ResponseEntity.ok(map);
    }


    /**
     * 消息翻页
     */
    @GetMapping("messages")
    public ResponseEntity<MockPageResult> findAllMovements(@RequestParam(defaultValue = "1")Integer page,
                                                           @RequestParam(defaultValue = "10")Integer pageSize,
                                                           @RequestParam(required = false)  Long sd,
                                                           @RequestParam(required = false)Long ed,
                                                           @RequestParam(required = false) String id,
                                                           Long uid,
                                                           String state,
                                                           String sortProp,
                                                           String sortOrder){

        MockPageResult result = mockManageService.findAllMovements(uid,state,page,pageSize,sortProp,sortOrder,sd,ed,id);
        return ResponseEntity.ok(result);
    }




}
