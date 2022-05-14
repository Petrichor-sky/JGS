package com.tanhua.admin.controller;

import com.itheima.pojo.UserInfo;
import com.tanhua.admin.service.ManagerService;
import com.itheima.vo.MovementsStateVo;
import com.itheima.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@RequestMapping("manage")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    /***
     * 用户数据翻页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("users")
    public ResponseEntity<PageResult> findAllUserInfo(@RequestParam(defaultValue = "1")Integer page,
                                                      @RequestParam(defaultValue = "10")Integer pageSize){
        PageResult result = managerService.findAllUserInfo(page,pageSize);
        return ResponseEntity.ok(result);
    }
    /**
     * 用户基本信息
     */
    @GetMapping("users/{userID}")
    public ResponseEntity<UserInfo> UserInfo(@PathVariable("userID") Long userID){
        UserInfo userInfo = managerService.findUserInfo(userID);
        return ResponseEntity.ok(userInfo);
    }
    /**
     * 视频记录翻页
     */
    @GetMapping("videos")
    public ResponseEntity<PageResult> findAllVideos(@RequestParam(defaultValue = "1")Integer page,
                                                    @RequestParam(defaultValue = "10")Integer pageSize,
                                                    Long uid){
        PageResult result = managerService.findAllVideos(uid,page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 评论列表翻页
     */
    @GetMapping("messages/comments")
    public ResponseEntity<PageResult> findAllComments(@RequestParam(defaultValue = "1")Integer page,
                                                      @RequestParam(defaultValue = "10")Integer pageSize,
                                                      String messageID){
        PageResult result = managerService.findAllCommemts(messageID,page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 动态分页
     */
    @GetMapping("messages")
    public ResponseEntity<PageResult> findAllMovements(@RequestParam(defaultValue = "1")Integer page,
                                                       @RequestParam(defaultValue = "10")Integer pageSize,
                                                       Long uid,Integer state){

        PageResult result = managerService.findAllMovements(uid,state,page,pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 用户冻结
     */
    @PostMapping("users/freeze")
    public ResponseEntity<Map<String,String>> freeze(@RequestBody Map<String,String> params){
        Map<String,String> map = managerService.freeze(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 用户解冻
     */
    @PostMapping("users/unfreeze")
    public ResponseEntity<Map<String,String>> unfreeze(@RequestBody Map<String,String> params){
        Map<String,String> map = managerService.unfreeze(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 动态详情
     */
    @GetMapping("messages/{id}")
    public ResponseEntity<MovementsStateVo> findMovement(@PathVariable("id") String movementId){
        MovementsStateVo vo = managerService.findMovement(movementId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 动态通过
     */
    @PostMapping("messages/pass")
    public ResponseEntity<Map<String,String>> pass(@RequestBody String[] ids){
        Map<String,String> map = managerService.pass(ids);
        return ResponseEntity.ok(map);
    }

    /**
     * 动态拒绝
     */
    @PostMapping("messages/reject")
    public ResponseEntity<Map<String,String>> reject(@RequestBody String[] ids){
        Map<String,String> map = managerService.reject(ids);
        return ResponseEntity.ok(map);
    }

    /**
     * 动态置顶
     */
    @PostMapping("messages/{id}/top")
    public ResponseEntity<Map<String,String>> top(@PathVariable("id") String movementId){
        Map<String,String> map = managerService.top(movementId);
        return ResponseEntity.ok(map);
    }
    /**
     * 取消动态置顶
     */
    @PostMapping("messages/{id}/untop")
    public ResponseEntity<Map<String,String>> untop(@PathVariable("id") String movementId){
        Map<String,String> map = managerService.untop(movementId);
        return ResponseEntity.ok(map);
    }

    /**
     * 日志管理
     */
    @GetMapping("logs")
    public ResponseEntity<PageResult> getLogs(@RequestParam(defaultValue = "1")Integer page,
                                              @RequestParam(defaultValue = "10")Integer pageSize,
                                              String sortProp,String sortOrder,String type,Long uid){

        PageResult result = managerService.getLogs(page,pageSize,sortProp,sortOrder,type,uid);
        return ResponseEntity.ok(result);
    }

}
