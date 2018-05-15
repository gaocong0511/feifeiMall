package com.fMall.service.impl;

import com.fMall.service.IFileService;
import com.fMall.util.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by 高琮 on 2018/5/14.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger=LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 路径
     * @return 返回上传完成之后的url
     */
    @Override
    public String upload(MultipartFile file, String path) {
        //获取文件名
        String fileName=file.getOriginalFilename();
        //获取扩展名
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName=UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名:{},上传文件的路径:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir=new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile=new File(path,uploadFileName);

        try{
            file.transferTo(targetFile);

            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
        }
        catch (IOException e){
            logger.error("文件上传异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
