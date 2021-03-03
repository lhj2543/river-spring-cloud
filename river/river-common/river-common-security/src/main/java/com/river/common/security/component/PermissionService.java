
package com.river.common.security.component;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.river.common.core.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author river
 * @date 2019/2/1 接口权限判断工具
 */
@Slf4j
@Component("pms")
public class PermissionService {

	/**
	 * 判断接口是否有xxx:xxx权限
	 * @param permission 权限
	 * @return {boolean}
	 */
	public boolean hasPermission(String... permission) {
		String[] permissions = permission;

		if (ArrayUtil.isEmpty(permissions)) {
			return false;
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.stream().map(GrantedAuthority::getAuthority).filter(StringUtils::hasText)
				//anyMatch表示，判断的条件里，任意一个元素成功，返回true
				.anyMatch(x -> PatternMatchUtils.simpleMatch(permissions, x)
						|| org.apache.commons.lang3.StringUtils.equals(x.replace(SecurityConstants.ROLE,StrUtil.EMPTY),"administrator"));
	}

}
