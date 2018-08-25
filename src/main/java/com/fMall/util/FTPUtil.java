package com.fMall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by 高琮 on 2018/5/14.
 */
public class FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
        logger.info("开始连接FTP服务器");
        boolean result = ftpUtil.uploadFile("image", fileList);
        logger.info("结束上传，上传结果为{}",result);
        return result;
    }


    public boolean uploadFile(String remotePath, List<File> fileList) throws IOException{
        boolean uploaded = true;
        FileInputStream fis = null;
        if(connectServer(this.ip,this.user,this.pwd)){
            try{
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem:fileList){
                    fis=new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            }catch (IOException e){
                logger.error("上传文件异常",e);
                uploaded=false;
            }
            finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }


    /**
     * 连接到FTP服务器
     *
     * @param ip   ip地址
     * @param user 用户名
     * @param pwd  密码
     * @return 返回是不是连接成功
     */
    private boolean connectServer(String ip, String user, String pwd) {
        boolean succeed = true;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            succeed = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常", e);
        }
        return succeed;
    }


    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public static Logger getLogger() {
        return logger;
    }

    public static String getFtpIp() {
        return ftpIp;
    }

    public static void setFtpIp(String ftpIp) {
        FTPUtil.ftpIp = ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    public static String getFtpPass() {
        return ftpPass;
    }

    public static void setFtpPass(String ftpPass) {
        FTPUtil.ftpPass = ftpPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
