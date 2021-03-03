package com.river.auth.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  security 登录成功处理器
 * @author river
 */

@Component
public class RiverAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${river.security.defaultSuccessUrl:https://www.baidu.com}")
    private String defaultSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        response.setContentType("application/json;charset=utf-8");

        RequestCache cache = new HttpSessionRequestCache();
        SavedRequest savedRequest = cache.getRequest(request, response);
        //String redirectUrl = savedRequest.getRedirectUrl();
        String url = savedRequest == null ? defaultSuccessUrl : defaultSuccessUrl;

        response.sendRedirect(url);

    }

}
