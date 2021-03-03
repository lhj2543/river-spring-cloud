
package com.river.common.security.component;

import com.river.api.dto.system.SysUserRoleDto;
import lombok.Getter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

/**
 * @author river
 * @date 2019/2/1 扩展用户信息
 */
public class RiverUser extends User {

	/**
	 * 用户ID
	 */
	@Getter
	private String id;
	/**
	 * 用户称呼
	 */
	@Getter
	private String fullName;
	/**
	 * 用户角色
	 */
	@Getter
	private Set<SysUserRoleDto> userRoles;

	/**
	 * Construct the <code>User</code> with the details required by
	 * {@link DaoAuthenticationProvider}.
	 * @param id 用户ID
	 * @param fullName 用户名称
	 * @param userRoles 用户角色
	 * @param userCd 用户CD,登录名
	 * @param password 密码
	 * @param enabled  用户是否已启用
	 * @param accountNonExpired 帐户是否尚未过期
	 * @param credentialsNonExpired 是否有凭据
	 * @param accountNonLocked 帐户是否锁定
	 * @param authorities 账户权限
	 * @throws IllegalArgumentException if a <code>null</code> value was passed either as
	 * a parameter or as an element in the <code>GrantedAuthority</code> collection
	 */
	public RiverUser(String id, String fullName, Set<SysUserRoleDto> userRoles, String userCd, String password, boolean enabled,
					 boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
					 Collection<? extends GrantedAuthority> authorities) {

		super(userCd, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

		this.id = id;
		this.fullName = fullName;
		this.userRoles = userRoles;

	}

}
