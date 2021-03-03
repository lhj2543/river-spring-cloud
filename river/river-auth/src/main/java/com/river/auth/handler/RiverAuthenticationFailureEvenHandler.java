package com.river.auth.handler;

import com.river.common.security.component.RiverUser;
import com.river.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author river
 * @date 2019/2/1
 */
@Slf4j
@Component
public class RiverAuthenticationFailureEvenHandler extends AbstractAuthenticationFailureEvenHandler {

	/**
	 * 处理登录失败方法
	 * <p>
	 * @param authenticationException 登录的authentication 对象
	 * @param authentication 登录的authenticationException 对象
	 */
	@Override
	public void handle(AuthenticationException authenticationException, Authentication authentication) {

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		RiverUser user = SecurityUtils.getUser(authentication);

		log.info("用户：{} 登录失败，异常：{}",authentication.getPrincipal() , authenticationException.getLocalizedMessage());
	}

}
