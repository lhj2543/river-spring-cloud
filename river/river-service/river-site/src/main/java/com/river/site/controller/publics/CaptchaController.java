package com.river.site.controller.publics;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.river.common.core.component.CaptchaServer;
import com.river.common.core.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.MimeType;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 * @author river
 */
@RestController
@Slf4j
@RefreshScope
@Api(value = "captcha",tags="验证码")
@RequestMapping("/public/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaServer captchaServer;

    @RequestMapping("/check")
    @ApiOperation(value = "export", httpMethod = "GET", response = Void.class, notes = "html转pdf")
    public Result check(String code,String randomStr){

        try {
            return captchaServer.checkCaptcha(code, randomStr);
        }catch (Exception e){
            log.error("验证验证码异常",e);
        }

        return Result.failed("验证验证码异常失败");

    }

    @GetMapping("/create")
    @ApiOperation(value = "export", httpMethod = "GET", response = Void.class, notes = "html转pdf")
    public void create(String randomStr,HttpServletResponse response){

        try {
            
            byte[] captchaImage = captchaServer.createCaptchaImage(randomStr);

            ServletOutputStream out = response.getOutputStream();

            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);

            out.write(captchaImage);
            out.flush();

        }catch (Exception e){
            log.error("验证验证码异常",e);
        }


    }


}
