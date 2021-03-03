package com.river.site.controller.publics;

import com.river.common.core.component.FileBreakpoint;
import com.river.common.core.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文件断点续传
 * @author river
 */
@RestController
@Slf4j
@RefreshScope
@Api(value = "fileBreakPoint",tags="文件断点续传")
@RequestMapping("/public/fileBreakPoint")
public class FileBreakPointController {

    @Value("${river.filebreakpoint:D:/test-file/upload/}")
    private String fileDir;

    @Autowired
    private FileBreakpoint fileBreakpoint;

    @PostMapping("/upload")
    @ApiOperation(value = "export", httpMethod = "POST", response = Void.class, notes = "文件断点续传")
    public Result upload(MultipartHttpServletRequest request){

        try {

            Map<String, String[]> parameterMap = request.getParameterMap();
            String fileTitle = request.getParameter("fileName");
            long fileSize = Long.valueOf(request.getParameter("fileSize"));

            Iterator<String> fileNames = request.getFileNames();

            while (fileNames.hasNext()) {
                String fileName = fileNames.next();

                List<MultipartFile> files = request.getFiles(fileName);
                for (int i = 0; i < files.size(); i++) {
                    MultipartFile file = files.get(i);
                    if (file.getSize() > 0L) {

                        long attachSize = file.getSize();
                        //log.warn(String.valueOf(attachSize));

                        String filePath = fileDir + fileTitle;


                        /*
                         * r:以只读方式打开
                         * rw:读写
                         * rws:对于rw要求对文件的内容或元数据的每个更新都同步写入到底层存储设备
                         * rwd:对于rw要求对文件的内容的每个更新都同步写入到底层存储设备
                         */
                        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath,"rw");

                        long length = randomAccessFile.length();

                        if(length>=fileSize){
                            randomAccessFile.close();
                            return  Result.restResult(null,4000,true,"文件已上传");
                        }

                        randomAccessFile.seek(length);
                        randomAccessFile.write(file.getBytes());

                        randomAccessFile.close();

                    }
                }
            }


        }catch (Exception e){
            log.error("文件断点续传异常",e);
        }

        return  Result.ok();

    }


    @RequestMapping("/download")
    @ApiOperation(value = "export", httpMethod = "POST", response = Void.class, notes = "文件下载")
    public void download(HttpServletRequest request,HttpServletResponse response){

        try {

            Map<String, String[]> parameterMap = request.getParameterMap();
            String fileName = request.getParameter("fileName");
            if(StringUtils.isBlank(fileName)){
                fileName = "test.mp4";
            }

            String filePath = fileDir + fileName;
            File file = new File(filePath);

            fileBreakpoint.downLoad(file,request,response);

        } catch (Throwable e) {
            log.info("下载文件失败....");
            e.printStackTrace();
        }

    }


    @DeleteMapping("/delete")
    @ApiOperation(value = "export", httpMethod = "POST", response = Void.class, notes = "文件刪除")
    public Result delete(String fileName){

        try {
            if(StringUtils.isBlank(fileName)){
                return  Result.failed("文件名为空");
            }

            String filePath = fileDir + fileName;
            File file = new File(filePath);
            if(file.exists()){
                boolean b = file.delete();
                return Result.result(b);
            }

        }catch (Exception e){
            log.error("文件刪除异常",e);
        }

        return  Result.failed("文件刪除失败");

    }

    @RequestMapping("/download2")
    @ApiOperation(value = "export", httpMethod = "POST", response = Void.class, notes = "文件下载")
    public void download2(HttpServletRequest request,HttpServletResponse response) {

        OutputStream out = null;
        InputStream in = null;
        try {

            String fileName = request.getParameter("fileName");
            if(StringUtils.isBlank(fileName)){
                fileName = "test.mp4";
            }

            String filePath = fileDir + fileName;
            File file = new File(filePath);

            response.setCharacterEncoding("utf-8");
            ServletContext context = request.getServletContext();
            // get MIME type of the file
            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }

            // set content attributes for the response
            response.setContentType(mimeType);
            //response.setContentType("application/x-download");

            String requestHeaderUserAgent = request.getHeader("user-agent");
            try {
                if (requestHeaderUserAgent.contains("MSIE")) {
                    fileName = URLEncoder.encode(fileName, "utf-8");
                } else if (requestHeaderUserAgent.contains("Firefox")) {
                    fileName = "=?UTF-8?B?" + new String(new BASE64Encoder().encode(fileName.getBytes("UTF-8"))).replaceAll("[\\t\\n\\r]", "") + "?=";
                } else {
                    fileName = URLEncoder.encode(fileName, "utf-8");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            String headerValue = String.format("attachment; filename=\"%s\"", fileName);
            response.setHeader("Content-Disposition", headerValue);

            in = new FileInputStream(file);
            String length = String.valueOf(in.available());
            response.setHeader("Content-Length", length);

            // 不支持断点续传 它被用来禁用或删除下载管理器中的暂停按钮。
            response.setHeader("Accept-Ranges", "none");

            out = response.getOutputStream();
            // 缓冲区大小
            int bufLen = (int) (file.length() < 2048 ? file.length() : 2048);
            byte[] buffer = new byte[bufLen];

            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != out) {
                IOUtils.closeQuietly(out);
            }
            if (null != in) {
                IOUtils.closeQuietly(in);
            }
        }
    }

}
