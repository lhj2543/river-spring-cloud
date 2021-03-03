package com.river.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author helen
 * @since 2020/5/4
 */
@Data
@Component
//注意prefix要写到最后一个 "." 符号之前
@ConfigurationProperties(prefix="wweixin.open")
public class WeixinLoginProperties {

    private String appId;

    private String appSecret;

    private String redirectUri;

}