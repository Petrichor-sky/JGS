package com.itheima.controller;

import com.itheima.service.PeachblossomService;
import com.itheima.vo.VoiceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/peachblossom")
public class PeachblossomController {

    @Autowired
    private PeachblossomService peachblossomService;

    /**
     * 发送语音
     * @param soundFile
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity savePeachblossom(MultipartFile soundFile) throws IOException {
        peachblossomService.savePeachblossom(soundFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 接收语音
     * @return
     * @throws IOException
     */
    @GetMapping
    public ResponseEntity<VoiceVo> getPeachblossom() throws IOException {
        VoiceVo voiceVo = peachblossomService.getPeachblossom();
        return ResponseEntity.ok(voiceVo);
    }
}
