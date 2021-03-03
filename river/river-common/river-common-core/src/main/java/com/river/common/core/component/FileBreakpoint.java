package com.river.common.core.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * 文件断点续传
 * @author river
 */
@Component
@Slf4j
public class FileBreakpoint {

    /**
     * 文件下载
     * @param request
     * @param response
     */
    public void downLoad(File file,HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {

        String fileName = file.getName();
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        this.downLoad(in,fileName,request,response);

    }
    /**
     * 文件下载
     * @param request
     * @param response
     */
    public void downLoad(InputStream in,String fileName,HttpServletRequest request, HttpServletResponse response){

        OutputStream out = null;

        try {

            long fSize = in.available();

            // 设置响应报头
            response.setCharacterEncoding("utf-8");
            ServletContext context = request.getServletContext();
            String mimeType = context.getMimeType(fileName);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
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

            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setHeader("Accept-Ranges", "bytes");

            //pos开始读取位置;  last最后读取位置;  sum记录总共已经读取了多少字节
            long pos = 0, last = fSize - 1, sum = 0;
            if (null != request.getHeader("Range")) {
                // 断点续传
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                try {
                    // 情景一：RANGE: bytes=2000070- 情景二：RANGE: bytes=2000070-2000970
                    String numRang = request.getHeader("Range").replaceAll("bytes=", "");
                    String[] strRange = numRang.split("-");
                    if (strRange.length == 2) {
                        pos = Long.parseLong(strRange[0].trim());
                        last = Long.parseLong(strRange[1].trim());
                    } else {
                        pos = Long.parseLong(numRang.replaceAll("-", "").trim());
                    }
                } catch (NumberFormatException e) {
                    log.error(request.getHeader("Range") + " is not Number!");
                    pos = 0;
                }
            }

            // 总共需要读取的字节
            long rangLength = last - pos + 1;
            // Content-Range: bytes 10-1033/304974592
            String contentRange = new StringBuffer("bytes ").append(pos).append("-").append(last).append("/").append(fSize).toString();
            response.setHeader("Content-Range", contentRange);
            // Content-Length: 1024
            response.addHeader("Content-Length", String.valueOf(rangLength));

            // 跳过已经下载的部分，进行后续下载
            out = new BufferedOutputStream(response.getOutputStream());

            in = new BufferedInputStream(in);
            in.skip(pos);

            byte[] buffer = new byte[1024];
            int length = 0;
            while (sum < rangLength) {
                length = in.read(buffer, 0, ((rangLength - sum) <= buffer.length ? ((int) (rangLength - sum)) : buffer.length));
                sum = sum + length;
                out.write(buffer, 0, length);
            }

        } catch (Throwable e) {
            log.error("下载文件失败....",e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
