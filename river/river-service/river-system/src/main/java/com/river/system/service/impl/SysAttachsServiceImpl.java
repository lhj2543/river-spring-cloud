package com.river.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.entity.system.SysAttachs;
import com.river.common.core.component.FileService;
import com.river.system.component.FtpService;
import com.river.system.mapper.SysAttachsMapper;
import com.river.system.service.ISysAttachsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * 系统附件表 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-09-20
 */
@Service
@Data
@Slf4j
@RefreshScope
public class SysAttachsServiceImpl extends ServiceImpl<SysAttachsMapper, SysAttachs> implements ISysAttachsService {

    @Value("${file.service.type:ftp}")
    private String fileServerType;

    @Autowired
    private FtpService ftpService;

    @Autowired
    private FileService fileService;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${ftp.server.rootDir:}")
    private String rootDir;


    public SysAttachs saveAttach(SysAttachs attach, File file){
        log.debug("进入->保存附件方法，targetId:{}",attach.getTargetId());

        StringBuffer remoteDir = new StringBuffer();
        /*if (dir != null) {
            remoteDir.append(dir);
        }*/
        remoteDir.append("/").append(this.ftpService.createDateDir(new Date()));
        try{

            long attachSize = file.length();
            String attachTitle = file.getName();
            String attachType = attachTitle.substring(attachTitle.lastIndexOf(".") + 1, attachTitle.length());

            attach.setName(attachTitle);
            attach.setSize(Long.valueOf(attachSize));
            attach.setType(attachType);

            StringBuffer remote = new StringBuffer();
            remote.append(remoteDir);
            remote.append(this.ftpService.createFname(attachType));
            attach.setPath(remote.toString());

            boolean flag = false;
            if ("ftp".equals(this.fileServerType)) {
                if (this.ftpService.connect()) {
                    flag = this.ftpService.upload(new FileInputStream(file), attach.getPath());
                }
            }else {
                flag = this.fileService.save(file, attach.getPath());
            }

            log.debug("附件  title:" + attach.getName() + "上传结果：" + flag);
            if (flag) {
                this.save(attach);
            } else {
                //attach.setMessage("附件上传失败");
            }
        } catch (Exception e) {
            log.error("保存附件异常", e);
        } finally {
            this.ftpService.disconnect();
            FileUtils.deleteQuietly(file);
        }

        log.debug("退出->保存附件方法");
        return attach;
    }

    public SysAttachs replaceAttach(String sid, File file) {
        log.debug("进入-> 替换附件，根据原有附件sid进行替换方法，sid:" + sid);
        SysAttachs attach = new SysAttachs();
        try
        {
            SysAttachs temp = this.getById(sid);
            if (temp == null) {
                //attach.setMessage("原附件记录未找到");
                return attach;
            }
            attach = replaceAttach(temp, file);
        } catch (Exception e) {
            log.error("替换附件，根据原有附件sid进行替换异常", e);
        }
        log.debug("进入-> 替换附件，根据原有附件sid进行替换方法，sid:" + sid + ",结果：");
        return attach;
    }

    public SysAttachs replaceAttach(SysAttachs attach, File file){
        log.debug("进入->替换附件，attach sid:" + attach.getSid());
        try
        {
            if ("ftp".equals(this.fileServerType)) {
                if (this.ftpService.connect()){
                    this.ftpService.delete(attach.getPath());
                }
            }else {
                this.fileService.delete(attach.getPath());
            };

            long attachSize = file.length();
            String attachTitle = file.getName();
            String attachType = attachTitle.substring(attachTitle.lastIndexOf(".") + 1, attachTitle.length());

            attach.setName(attachTitle);
            attach.setSize(Long.valueOf(attachSize));
            attach.setType(attachType);

            boolean flag = false;
            if ("ftp".equals(this.fileServerType)) {
                if (this.ftpService.connect()) {
                    flag = this.ftpService.upload(new FileInputStream(file), attach.getPath());
                }
            }else {
                flag = this.fileService.save(file, attach.getPath());
            }

            log.debug("附件  title:" + attach.getName() + "上传结果：" + flag);
            if (flag) {
                this.updateById(attach);
            } else {
                //attach.setMessage("附件上传失败");
            }
        } catch (Exception e) {
            log.error("替换附件异常", e);
        } finally {
            this.ftpService.disconnect();
            FileUtils.deleteQuietly(file);
        }

        log.debug("退出->替换附件方法，attach sid:" + attach.getSid() + "，结果：" );
        return attach;
    }


    public SysAttachs saveAttach(SysAttachs attach, MultipartHttpServletRequest request){
        log.debug("进入->保存附件方法，targetId:" + attach.getTargetId());
        SysAttachs result = new SysAttachs();

        StringBuffer remoteDir = new StringBuffer();
        if (rootDir != null) {
            remoteDir.append(rootDir);
        }
        remoteDir.append("/").append(appName);
        remoteDir.append("/").append(this.ftpService.createDateDir(new Date()));

        Iterator<String> fileNames = request.getFileNames();

        try{

            while (fileNames.hasNext()){
                String fileName = fileNames.next();

                List<MultipartFile> files = request.getFiles(fileName);
                for (int i=0;i<files.size();i++) {
                    MultipartFile file = files.get(i);
                    if (file.getSize() > 0L){
                        long attachSize = file.getSize();
                        String attachTitle = file.getOriginalFilename();
                        String attachType = attachTitle.substring(attachTitle.lastIndexOf(".") + 1, attachTitle.length());

                        attach.setName(attachTitle);
                        attach.setDisplayName(attachTitle);
                        attach.setSize(Long.valueOf(attachSize));
                        attach.setType(attachType);
                        attach.setSortKey(BigDecimal.valueOf(i));

                        StringBuffer remote = new StringBuffer();
                        remote.append(remoteDir);
                        remote.append(this.ftpService.createFname(attachType));
                        attach.setPath(remote.toString().replace(rootDir,""));

                        boolean flag = false;
                        if ("ftp".equals(this.fileServerType)) {
                            if (this.ftpService.connect()) {
                                flag = this.ftpService.upload(file.getInputStream(), remote.toString());
                            }
                        }else {
                            flag = this.fileService.save(file.getInputStream(), remote.toString());
                        }

                        log.debug("附件  title:" + attach.getName() + "上传结果：" + flag);
                        if (flag) {
                            boolean save = this.save(attach);

                            SysAttachs att = new SysAttachs();
                            BeanUtils.copyProperties(attach, att);
                            if(result.getRows()==null){
                                result.setRows(new ArrayList<SysAttachs>());
                            }
                            result.getRows().add(att);
                        }
                    }
                }

            }


        } catch (Exception e) {
            log.error("保存附件异常", e);
        } finally {
            this.ftpService.disconnect();
        }

        log.debug("退出->保存附件方法");
        return result;
    }

    public void download(String sid, HttpServletRequest request, HttpServletResponse response){
        log.debug("进入->下载附件方法，sid:" + sid);
        SysAttachs attach = this.getById(sid);
        
        if (attach != null) {
            download(attach, request, response);
        } else {
            sendScriptMessage("附件不存在", response);
        }
        log.debug("退出->下载附件方法，sid:" + sid);
    }

    public void download(SysAttachs attach, HttpServletRequest request, HttpServletResponse response){
        log.debug("进入->下载附件方法，sid:" + attach.getSid());

        OutputStream out = null;
        InputStream in = null;
        try {
            String attachTitle = attach.getName();

            String requestHeaderUserAgent = request.getHeader("user-agent");
            try {
                if (requestHeaderUserAgent.contains("MSIE")) {
                    attachTitle = URLEncoder.encode(attachTitle, "utf-8");
                } else if (requestHeaderUserAgent.contains("Firefox")) {
                    attachTitle = "=?UTF-8?B?" + new String(new BASE64Encoder().encode(attachTitle.getBytes("UTF-8"))).replaceAll("[\\t\\n\\r]", "") + "?=";
                } else {
                    attachTitle = URLEncoder.encode(attachTitle, "utf-8");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            response.setHeader("Content-disposition", "attachment;filename=" + attachTitle);
            response.setContentType("application/x-download");

            if ("ftp".equals(this.fileServerType)) {
                if (this.ftpService.connect()) {
                    in = this.ftpService.download(attach.getPath());
                }
            }else {
                in = this.fileService.open(attach.getPath());
            }

            if (in != null) {
                String length = String.valueOf(in.available());
                response.setHeader("Content-Length", length);
                out = response.getOutputStream();
                byte[] buf = new byte[1024];
                int len = -1;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                out.flush();
            } else {
                sendScriptMessage("附件下载失败", response);
            }
        }catch (Exception e) {
            log.error("下载附件异常", e);
            sendScriptMessage("下载附件异常", response);
        } finally {
            IOUtils.closeQuietly(in);
            this.ftpService.disconnect();
        }

        log.debug("退出->下载附件方法，sid:" + attach.getSid());
    }

    public void preview(String sid, HttpServletRequest request, HttpServletResponse response){
        log.debug("进入->根据附件id预览方法，sid:" + sid);
        SysAttachs attach  = (SysAttachs)this.getById(sid);
        if (attach != null) {
            preview(attach, request, response);
        } else {
            sendHtmlMessage("附件不存在", response);
        }
        log.debug("退出->根据附件id预览方法，sid:" + sid);
    }

    public void preview(SysAttachs attach, HttpServletRequest request, HttpServletResponse response)
    {
        log.debug("进入->根据附件id预览方法，sid:" + attach.getSid());

        OutputStream out = null;
        InputStream in = null;
        try
        {
            if ("ftp".equals(this.fileServerType)) {
                if (this.ftpService.connect()) {
                    in = this.ftpService.download(attach.getPath());
                }
            }else {
                in = this.fileService.open(attach.getPath());
            }

            if (in != null) {
                out = response.getOutputStream();
                byte[] buf = new byte[1024];
                int len = -1;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                out.flush();
            } else {
                sendHtmlMessage("附件下载失败", response);
            }
        }catch (Exception e) {
            log.error("下载附件异常", e);
            sendHtmlMessage("下载附件异常", response);
        } finally {
            IOUtils.closeQuietly(in);
            this.ftpService.disconnect();
        }

        log.debug("退出->根据附件id预览方法，sid:" + attach.getSid());
    }

    public SysAttachs deleteAttachByOwner(String targetId){
        log.debug("进入->根据拥有者ID删除附件方法，targetId:" + targetId);
        SysAttachs attach = new SysAttachs();
        try {
            SysAttachs temp = new SysAttachs();
            temp.setTargetId(targetId);
            List<SysAttachs> rows = this.list(Wrappers.<SysAttachs>lambdaQuery().eq(SysAttachs::getTargetId, targetId));
            for (SysAttachs att : rows) {
                deleteAttach(att);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        log.debug("退出->根据拥有者ID删除附件方法，targetId:" + targetId);
        return attach;
    }

    public boolean deleteAttach(String sid) {
        log.debug("进入->根据附件ID删除附件方法，sid:" + sid);
        boolean result = false;
        try {

            SysAttachs temp = this.getById(sid);
            if (temp != null) {
                result = deleteAttach(temp);
            }
        }catch (Exception e) {
            log.error("根据附件ID删除附件异常", e);
        }

        log.debug("退出->根据附件ID删除附件方法，sid:" + sid);
        return result;
    }

    public boolean deleteAttachByTarget(String targetId) {
        log.debug("进入->根据附件ID删除附件方法，targetId:" + targetId);
        boolean result = false;
        try {

            List<SysAttachs> rows = this.list(Wrappers.<SysAttachs>lambdaQuery().eq(SysAttachs::getTargetId,targetId));
            if (rows != null && rows.size()>0) {
                for(SysAttachs temp:rows){
                    result = deleteAttach(temp);
                }
            }
        }catch (Exception e) {
            log.error("根据附件ID删除附件异常", e);
        }

        log.debug("退出->根据附件ID删除附件方法，targetId:" + targetId);
        return result;
    }

    public boolean deleteAttach(SysAttachs attach){
        log.debug("进入->删除附件方法");
        boolean result = false;
        try {
            boolean flag = false;
            if ("ftp".equals(this.fileServerType)) {
                if (this.ftpService.connect()) {
                    flag = this.ftpService.delete(rootDir + attach.getPath());
                    log.debug("删除附件 flag:" + flag);
                }
            } else {
                flag = this.fileService.delete(rootDir + attach.getPath());
                log.debug("删除附件 flag:" + flag);
            }
            if(flag){
                result = this.removeById(attach.getSid());
            }

        } catch (Exception e) {
            log.error("删除附件异常", e);
        } finally {
            this.ftpService.disconnect();
        }
        log.debug("退出->删除附件方法");
        return result;
    }

    public void sendScriptMessage(String message, HttpServletResponse response)
    {
        response.setContentType("text/html;charset=utf-8");
        String str = "<script type='text/javascript'>alert('" + message + "');</script>";
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("pragma", "no-cache");
            response.setHeader("cache-control", "no-cache");

            PrintWriter pw = response.getWriter();
            pw.write(str);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            log.error("输出页面脚本异常", e);
        }
    }

    public void sendHtmlMessage(String message, HttpServletResponse response)
    {
        response.setContentType("text/html;charset=utf-8");
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("pragma", "no-cache");
            response.setHeader("cache-control", "no-cache");

            PrintWriter pw = response.getWriter();
            pw.write(message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            log.error("输出页面脚本异常", e);
        }
    }

}
