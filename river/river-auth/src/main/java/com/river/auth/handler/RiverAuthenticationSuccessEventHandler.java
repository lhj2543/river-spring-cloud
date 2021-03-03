package com.river.auth.handler;

import com.river.common.security.component.RiverUser;
import com.river.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author river
 * @date 2019/2/1
 */
@Slf4j
@Component
public class RiverAuthenticationSuccessEventHandler extends AbstractAuthenticationSuccessEventHandler {

	/**
	 * 处理登录成功方法
	 * <p>
	 * 获取到登录的authentication 对象
	 * @param authentication 登录对象
	 */
	@Override
	public void handle(Authentication authentication) {

		RiverUser user = SecurityUtils.getUser(authentication);

		log.info("用户：{} 登录成功", user.getUsername());
	}

}
