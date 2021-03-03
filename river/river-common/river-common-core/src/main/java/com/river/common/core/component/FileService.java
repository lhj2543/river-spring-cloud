package com.river.common.core.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * File 上传
 * @author river
 */
@Component
@Slf4j
@RefreshScope
public class FileService {

    @Value("${file.service.homedir:./filestore}")
    private String homedir;

    public boolean save(File file, String path){
        log.debug("进入->保存文件方法,from:" + file.getAbsolutePath() + ",to:" + path);
        try {
            mkdirs(this.homedir + path);
            FileUtils.copyFile(file, new File(this.homedir + path));
        } catch (Exception e) {
            log.error("保存文件异常", e);
            return false;
        }
        log.debug("退出->保存文件方法,from:" + file.getAbsolutePath() + ",to:" + path);
        return true;
    }

    public boolean save(InputStream fileIn, String path)
    {
        log.debug("进入->保存文件方法 to:" + path);
        try {
            mkdirs(this.homedir + path);
            FileUtils.copyInputStreamToFile(fileIn, new File(this.homedir + path));
        } catch (Exception e) {
            log.error("保存文件异常", e);
            return false;
        }

        log.debug("退出->保存文件方法to:" + path);
        return true;
    }

    public boolean delete(String path)
    {
        log.debug("进入->删除文件方法，path:" + path);
        try {
            File file = new File(this.homedir + path);
            file.delete();
        } catch (Exception e) {
            log.error("删除文件异常", e);
            return false;
        }
        log.debug("退出->删除文件方法，path:" + path);
        return true;
    }

    public boolean replace(File file, String path)
    {
        log.debug("进入->替换文件方法,源文件path:" + path + ",新文件path:" + file.getAbsolutePath());

        boolean flag = delete(path);

        flag = flag ? save(file, path) : flag;

        log.debug("退出->替换文件方法,源文件path:" + path + ",新文件path:" + file.getAbsolutePath());
        return flag;
    }

    public InputStream open(String path)
    {
        log.debug("进入->打开文件方法，path:" + path);
        InputStream in = null;
        try
        {
            in = new FileInputStream(new File(this.homedir + path));
        } catch (Exception e) {
            log.error("打开文件异常", e);
        }

        log.debug("退出->打开文件方法，path:" + path);
        return in;
    }

    private void mkdirs(String path)
    {
        String[] arr = path.split("/");
        File dir = new File(arr[0]);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (int index = 1; index < arr.length - 1; index++) {
            dir = new File(dir, arr[index]);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

}
