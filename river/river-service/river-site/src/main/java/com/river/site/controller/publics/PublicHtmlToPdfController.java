package com.river.site.controller.publics;

import com.alibaba.csp.sentinel.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * HtmlToPdf
 * @author river
 */
@RestController
@Slf4j
@RefreshScope
@Api(value = "htmlToPdf",tags="html转pdf")
@RequestMapping("/public/htmlToPdf")
public class PublicHtmlToPdfController {


    @Value("${river.htmlToPdf.tempPath:C:/lhj/htmlToPdfTemp}")
    private String tempPath;

    @Value("${river.htmlToPdf.command:C:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe --encoding utf-8}")
    private String command;


    @RequestMapping("/export")
    @ApiOperation(value = "export", httpMethod = "POST", response = Void.class, notes = "html转pdf")
    public void export(String htmlConnect,String params,String remoteUrl, HttpServletResponse response){


        if (StringUtil.isNotBlank(htmlConnect) || StringUtils.isNotBlank(remoteUrl)) {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] btStr = decoder.decode(htmlConnect);

            BufferedWriter fileWritter = null;

            InputStream in = null;
            Process process = null;

            try {

                String strHtmlPath = "";
                String strFileName = String.valueOf(System.currentTimeMillis());
                File fHtmlFile = null;

                if(StringUtils.isNotBlank(htmlConnect)){
                    String strHtml = URLDecoder.decode( new String(btStr, "UTF-8"), "UTF-8");

                    File fileTempPathDir = new File(tempPath);
                    if (!fileTempPathDir.exists() || !fileTempPathDir.isDirectory()) {
                        fileTempPathDir.mkdirs();
                    }


                    strHtmlPath = fileTempPathDir + "/" + strFileName + ".html";

                    fHtmlFile = new File(strHtmlPath);
                    if (!fHtmlFile.exists() || !fHtmlFile.isFile()) {
                        fHtmlFile.createNewFile();
                    }

                    fileWritter = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(fHtmlFile, true), "UTF-8"));

                    fileWritter.write(strHtml);
                    fileWritter.flush();

                    fileWritter.close();
                }else {
                    strHtmlPath = remoteUrl;
                }

                String strPdfPath = tempPath + "/" + strFileName + ".pdf";

                StringBuffer cmds = new StringBuffer(command);
                if(StringUtils.isNotBlank(params)){
                    cmds.append(" ").append(params);
                }
                cmds.append(" ").append(strHtmlPath).append(" ").append(strPdfPath);
                log.info(cmds.toString());

                process = Runtime.getRuntime().exec(cmds.toString());
                process.waitFor();

                File pdfFile = new File(strPdfPath);
                in = new FileInputStream(pdfFile);
                String length = String.valueOf(in.available());
                response.setHeader("Content-Length", length);

                response.setHeader("Content-disposition", "attachment;filename=" + strFileName+".pdf");
                response.setContentType("application/x-download");

                OutputStream  out = response.getOutputStream();
                byte[] buf = new byte[1024];
                int len = -1;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                out.flush();

                File finalFHtmlFile = fHtmlFile;
                new Thread("htmltopdfthred"){
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(5L);
                            if(finalFHtmlFile != null ){
                                finalFHtmlFile.delete();
                            }
                            pdfFile.delete();
                        }catch (Exception e){

                        }
                    }
                }.start();



            } catch (Exception e) {
                e.printStackTrace();
                log.error("html转换pdf异常",e);
                sendScriptMessage("html转换pdf异常", response);
            } finally {

                if (fileWritter != null) {
                    IOUtils.closeQuietly(fileWritter);
                }
                if(in != null){
                    IOUtils.closeQuietly(in);
                }

                if(process!=null){
                    IOUtils.closeQuietly(process.getInputStream());
                    IOUtils.closeQuietly(process.getOutputStream());
                    IOUtils.closeQuietly(process.getErrorStream());
                }

            }
        }else {
            sendScriptMessage("转换内容为空",response);
        }

    }

    public void sendScriptMessage(String message, HttpServletResponse response){
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

}
