package com.river.auth.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.river.common.core.util.WebUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author river
 * @date 2019-08-20
 * <p>
 * 表单登录失败处理逻辑
 */
@Slf4j
public class FormAuthenticationFailureHandler implements AuthenticationFailureHandler {

	/**
	 * Called when an authentication attempt fails.
	 * @param request the request during which the authentication attempt occurred.
	 * @param response the response.
	 * @param exception the exception which was thrown to reject the authentication
	 */
	@Override
	@SneakyThrows
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,AuthenticationException exception) {
		String errMessage = exception.getMessage();
		log.error("表单登录失败:{}", errMessage);
		if(StringUtils.equals(errMessage,"Bad credentials")){
			errMessage = "密码错误！";
		}
		String url = HttpUtil.encodeParams(String.format("/rauth/login?error=%s", errMessage),CharsetUtil.CHARSET_UTF_8);
		WebUtils.getResponse().sendRedirect(url);
	}

}
