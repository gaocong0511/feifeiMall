package com.fMall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 高琮 on 2018/5/14.
 */
public interface IFileService {
    String upload(MultipartFile file,String path);
}
