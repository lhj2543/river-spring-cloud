package com.river.common.core.component;

import cn.hutool.core.util.StrUtil;
import com.google.code.kaptcha.Producer;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.util.Result;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *  验证码
 * @author river
 */
@Component
public class CaptchaServer {

    @Autowired
    private  Producer producer;

    @Autowired
    private  RedisTemplate redisTemplate;

    /**
     * 验证码图片生成
     * @param randomStr
     * @return
     * @throws IOException
     */
    public byte[] createCaptchaImage(String randomStr) throws IOException {
        // 生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        // 保存验证码信息
        redisTemplate.opsForValue().set(CacheConstants.PREFIX_SITE_CAPTCHA + randomStr, text, 60, TimeUnit.SECONDS);

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpeg", os);
        return os.toByteArray();
    }


    /**
     * 验证验证码
     * @param code
     * @param randomStr
     * @return
     */
    public Result checkCaptcha(String code, String randomStr) {

        if (StrUtil.isBlank(code)) {
            return  Result.failed("验证码不能为空");
        }

        String key = CacheConstants.PREFIX_SITE_CAPTCHA  + randomStr;
        if (StrUtil.isBlank(randomStr) || !redisTemplate.hasKey(key)) {
            return  Result.failed("验证码不合法");
        }

        Object codeObj = redisTemplate.opsForValue().get(key);

        if (codeObj == null) {
            return  Result.failed("验证码不合法");
        }

        String saveCode = codeObj.toString();
        if (StrUtil.isBlank(saveCode)) {
            redisTemplate.delete(key);
            return  Result.failed("验证码不合法");
        }

        if (!StrUtil.equals(saveCode, code)) {
            redisTemplate.delete(key);
            return  Result.failed("验证码不合法");
        }

        redisTemplate.delete(key);
        return  Result.ok(null,"验证成功");

    }


}
