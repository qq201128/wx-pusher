package com.wang.service.impl;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.wang.config.OssConfig;
import com.wang.domain.SysOss;
import com.wang.mapper.SysOssMapper;
import com.wang.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {
    private final OssConfig ossConfig;
    private final SysOssMapper ossMapper;
    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        //获取文件输入流
        InputStream inputStream = multipartFile.getInputStream();
        //创建文件名
        String fileName = UUID.randomUUID() + multipartFile.getOriginalFilename();
        //上传文件到OSS
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
        ossClient.putObject(ossConfig.getBucketName(), fileName, inputStream);
        //文件访问路径
        String url = "https://" + ossConfig.getBucketName()
                + "." + ossConfig.getEndpoint()
                + "/" + fileName;
        //关闭OSSClient
        ossClient.shutdown();
        //保存数据到本地数据库
        SysOss sysOss = new SysOss();
        sysOss.setUrl(url);
        sysOss.setFileName(fileName);
        ossMapper.insert(sysOss);

        //返回上传路径
        return url;
    }

    @Override
    public List<String> uploadFiles(MultipartFile[] multipartFiles) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            urls.add(uploadFile(file));
        }
        return urls;
    }
}
