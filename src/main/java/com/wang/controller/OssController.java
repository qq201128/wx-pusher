package com.wang.controller;

import com.wang.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * oss文件管理
 *
 */
@RestController
@RequiredArgsConstructor
public class OssController {
    private final OssService ossService;
    @PostMapping("/uploadFile")
    @ResponseBody
    public List<String> uploadFile(@RequestParam("multipartFile") MultipartFile[] multipartFiles) throws Exception{
        return ossService.uploadFiles(multipartFiles);
    }
}
