package com.river.common.security.util;

import cn.hutool.core.util.StrUtil;
import com.river.api.dto.system.SysUserRoleDto;
import com.river.common.core.constant.SecurityConstants;
import com.river.common.security.component.RiverUser;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * Security安全工具类
 *
 * @author river
 */
@UtilityClass
public class SecurityUtils {

	/**
	 * 获取Authentication
	 */
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * 获取用户
	 */
	public RiverUser getUser(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof RiverUser) {
			return (RiverUser) principal;
		}
		return null;
	}

	/**
	 * 获取用户
	 */
	public RiverUser getUser() {
		Authentication authentication = getAuthentication();
		if (authentication == null) {
			return null;
		}
		return getUser(authentication);
	}

	/**
	 * 获取用户cd
	 */
	public String  getUserCd() {
		RiverUser user = getUser();
		return user == null ? "" : user.getUsername();
	}

	/**
	 * 获取用户名称
	 */
	public String  getUserName() {
		RiverUser user = getUser();
		return user == null ? "" : user.getFullName();
	}

	/**
	 * 获取用户id
	 */
	public String  getUserId() {
		RiverUser user = getUser();
		return user == null ? "" : user.getId();
	}

	/**
	 * 是否超级管理员
	 */
	public boolean isAdministrator(){

		return (getUser() != null && StringUtils.equals(getUser().getUsername(), "administrator")) || getUserRolesCode().contains("administrator");

	}

	/**
	 * 获取用户角色集合
	 * @return 角色集合
	 */
	public Set<SysUserRoleDto> getUserRoles() {
		RiverUser user = getUser();
		if(user == null){
			return  new HashSet<>();
		}
		return user.getUserRoles();
	}

	/**
	 * 获取用户角色code集合
	 * @return 角色集合
	 */
	public List<String> getUserRolesCode() {
		Authentication authentication = getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		List<String> roles = new ArrayList<>();
		authorities.stream().filter(granted -> StrUtil.startWith(granted.getAuthority(), SecurityConstants.ROLE))
				.forEach(granted -> {
					String roleCode = StrUtil.removePrefix(granted.getAuthority(), SecurityConstants.ROLE);
					roles.add(roleCode);
				});
		return roles;
	}

}
