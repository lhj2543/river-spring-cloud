package com.river.system.component;

import com.river.common.core.exception.BusinessServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ftp 附件
 * @author river
 */
@Component
@Slf4j
@RefreshScope
public class FtpService {

    @Value("${ftp.server.ip:127.0.0.1}")
    private String ip;

    @Value("${ftp.server.port:21}")
    private int port;

    @Value("${ftp.server.username:admin}")
    private String username;

    @Value("${ftp.server.password:admin}")
    private String password;

    @Value("${ftp.server.systype:WINDOWS}")
    private String systype;

    @Value("${ftp.server.charset:UTF-8}")
    private String charset;

    private FTPClient ftpClient = null;

    public boolean connect() {
        
        this.ftpClient = new FTPClient();
        FTPClientConfig ftpConfig = new FTPClientConfig(this.systype);
        ftpConfig.setServerLanguageCode(this.charset);
        this.ftpClient.configure(ftpConfig);
        this.ftpClient.setDefaultPort(this.port);
        this.ftpClient.setCharset(Charset.forName(this.charset));
        this.ftpClient.setControlEncoding(this.charset);
        try
        {
            this.ftpClient.connect(this.ip);
            this.ftpClient.login(this.username, this.password);
            log.info("Ftp Reply String >>>" + this.ftpClient.getReplyString());
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                log.error("FTP server refused connection.");
                return false;
            }

            this.ftpClient.setFileType(2);
            return true;
        } catch (Exception e) {
            System.err.println("登录ftp服务器【" + this.ip + "】失败");
            e.printStackTrace();
        }
        
        throw new BusinessServiceException("登录ftp服务器【" + this.ip + "】失败");
        
    }

    public boolean download(String remote, String local){
        try
        {
            connect();
            boolean flag = this.ftpClient.retrieveFile(remote, new FileOutputStream(new File(local)));
            log.info("download file [" + remote + "] result >>>" + flag);
            return flag;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("文件下载失败：" + e.getMessage());
        }
    }

    public InputStream download(String remote){
        try
        {
            connect();
            return this.ftpClient.retrieveFileStream(remote);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("文件下载失败：" + e.getMessage());
        }
    }

    public boolean upload(String loacal, String remote) {
        InputStream in = null;
        try {
            in = new FileInputStream(loacal);
            if (remote.lastIndexOf("/") > 0) {
                String workdir = remote.substring(0, remote.lastIndexOf("/"));
                makeDirs(workdir);
                this.ftpClient.changeWorkingDirectory(workdir);
            }
            boolean flag = this.ftpClient.storeFile(remote, in);
            if (remote.lastIndexOf("/") > 0) {
                this.ftpClient.changeWorkingDirectory("/");
            }
            log.info("upload file [" + remote + "] result >>> " + flag);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessServiceException("文件上传失败：" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    public boolean upload(InputStream in, String remote){
        try
        {
            if (remote.lastIndexOf("/") > 0) {
                String workdir = remote.substring(0, remote.lastIndexOf("/"));
                makeDirs(workdir);
                this.ftpClient.changeWorkingDirectory(workdir);
            }

            boolean flag = this.ftpClient.storeFile(remote, in);
            if (remote.lastIndexOf("/") > 0) {
                this.ftpClient.changeWorkingDirectory("/");
            }
            log.info("upload file [" + remote + "] result >>> " + flag);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessServiceException("文件上传失败：" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            }catch (Exception e){
            }
        }
    }

    public OutputStream upload(String remote){
        try
        {
            if (remote.lastIndexOf("/") > 0) {
                String workdir = remote.substring(0, remote.lastIndexOf("/"));
                makeDirs(workdir);
                this.ftpClient.changeWorkingDirectory(workdir);
            }
            return this.ftpClient.storeFileStream(remote);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("文件上传失败：" + e.getMessage());
        }
    }

    public boolean delete(String remote){
        try
        {
            connect();
            boolean flag = this.ftpClient.deleteFile(remote);
            log.info("delete file [" + remote + "] result >>> " + flag);
            return flag;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("文件删除失败：" + e.getMessage());
        }
    }

    public boolean reName(String remotedir, String oldname, String newname) {
        try
        {
            this.ftpClient.changeWorkingDirectory(remotedir);
            boolean flag = this.ftpClient.rename(oldname, newname);
            this.ftpClient.changeWorkingDirectory("/");
            log.info("reName file [" + remotedir + "/" + oldname + " to " + newname + "] result >>> " + flag);
            return flag;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("重命名失败：" + e.getMessage());
        }
    }

    public boolean makeDirs(String dirpath){
        try
        {
            String[] temp = dirpath.substring(1, dirpath.length()).split("/");
            boolean flag = false;
            for (int index = 0; index < temp.length; index++) {
                String pdir = "";
                for (int i = 0; i <= index; i++) {
                    pdir = pdir + "/" + temp[i];
                }
                flag = this.ftpClient.makeDirectory(pdir);
            }
            log.info("makeDirs file [" + dirpath + "] result >>> " + flag);
            return flag;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("创建文件夹失败：" + e.getMessage());
        }
    }

    public boolean deleteDir(String dirpath){
        try
        {
            String dir = dirpath;
            if (dir.lastIndexOf("/") > 0) {
                String workdir = dir.substring(0, dir.lastIndexOf("/"));
                this.ftpClient.changeWorkingDirectory(workdir);
                dir = dirpath.substring(dir.lastIndexOf("/"), dirpath.length());
            }
            boolean flag = this.ftpClient.removeDirectory(dir);
            if (dir.lastIndexOf("/") > 0) {
                this.ftpClient.changeWorkingDirectory("/");
            }
            log.info("deleteDir file [" + dirpath + "] result >>> " + flag);
            return flag;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("删除文件夹失败：" + e.getMessage());
        }
    }

    public synchronized String createFname(String type) {
        StringBuffer filename = new StringBuffer();
        filename.append(System.currentTimeMillis()).append((int)(Math.random() * 1000.0D)).append(".").append(type);
        return filename.toString();
    }

    public synchronized String createDateDir(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date)+"/";
    }

    public void disconnect() {
        try
        {
            if (this.ftpClient != null) {
                this.ftpClient.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new BusinessServiceException("文件上传失败：" + e.getMessage());
        }
    }

}
