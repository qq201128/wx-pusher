package com.wang.controller;

import com.wang.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * oss文件管理
 *
 */
@RestController
@RequiredArgsConstructor
public class OssController {
    private final OssService ossService;
    @PostMapping("/uploadFile")
    public String uploadFile(MultipartFile multipartFile) throws Exception{
        return ossService.uploadFile(multipartFile);
    }
}
