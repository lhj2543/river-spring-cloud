package com.river.auth.service;

import com.river.api.dto.system.SysUserDto;
import com.river.api.dto.system.SysUserRoleDto;
import com.river.api.entity.system.SysMenu;
import com.river.api.feign.RemoteAllService;
import com.river.common.core.constant.CacheConstants;
import com.river.common.core.constant.SecurityConstants;
import com.river.common.core.util.Result;
import com.river.common.security.component.RiverUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户详细信息
 *
 * @author river
 */
@Slf4j
@Service
/*@RequiredArgsConstructor*/
public class RiverUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RemoteAllService remoteAllService;

	/**
	 * 用户密码登录
	 * @param username 用户名
	 * @return
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String username) {
		String key = CacheConstants.PREFIX_LOGIN_USER + ":" + username;

		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		Boolean cacheHasUser = redisTemplate.hasKey(key);
		if (cacheHasUser) {
			UserDetails userDetails  = (UserDetails)redisTemplate.opsForValue().get(key);
			return userDetails;
		}

		SysUserDto sysUserDto = new SysUserDto();
		sysUserDto.setUserCd(username);
		Result<SysUserDto> loginUser = remoteAllService.getLoginUser(sysUserDto,SecurityConstants.FROM_IN);

		UserDetails userDetails = getUserDetails(loginUser);

		redisTemplate.opsForValue().set(key,userDetails,30L, TimeUnit.DAYS);

		return userDetails;
	}



	/**
	 * 构建userdetails
	 * @param result 用户信息
	 * @return
	 */
	private UserDetails getUserDetails(Result<SysUserDto> result) {

		if (result == null || !result.isSuccess()) {
			throw new RuntimeException(result.getMessage());
		}

        SysUserDto user = result.getData();

        if (user == null) {
			throw new UsernameNotFoundException("用户名或密码错误！");
		}

		Set<String> dbAuthsSet = new HashSet<>();

		Set<SysUserRoleDto> userRoles = user.getUserRoles();

		if (!userRoles.isEmpty()) {
			// 获取角色
			userRoles.forEach(role->dbAuthsSet.add(SecurityConstants.ROLE + role.getRoleCode()));
		}
		List<SysMenu> sysUserMenu = user.getSysUserMenu();
		if (sysUserMenu !=null ) {
			// 获取菜单资源
			sysUserMenu.forEach(menu->{
				String permission = menu.getPermission();
				if(StringUtils.isNotBlank(permission)){
					dbAuthsSet.add(permission);
				}
			});
		}

		String[] dbAuths = dbAuthsSet.toArray(new String[0]);
		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(dbAuths);

		// 构造security用户
		/*SecurityConstants.BCRYPT + "$2a$10$Om0Gj9Gdb5FbvOIsYgVOu.Fm25rX7UfeO7EKrzfBA5H2wFkEJo.3W"*/
		return new RiverUser(user.getSid(),user.getUserName(),user.getUserRoles(),user.getUserCd(),user.getPassword(),
				true, true, true, true, authorities);
	}

}
