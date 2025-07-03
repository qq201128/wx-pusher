package com.wang.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OssService {
    String uploadFile(MultipartFile multipartFile) throws IOException;
    List<String> uploadFiles(MultipartFile[] multipartFiles) throws IOException;
}
