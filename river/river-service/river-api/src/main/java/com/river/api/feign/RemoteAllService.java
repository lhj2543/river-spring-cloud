package com.river.api.feign;

import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.system.SysMenu;
import com.river.api.feign.factory.RemoteSysUserServiceFallbackFactory;
import com.river.common.core.constant.SecurityConstants;
import com.river.common.core.constant.ServiceNameConstants;
import com.river.common.core.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @author river
 * @date 2019/2/1
 */
@FeignClient(contextId = "remoteSysUserService", value = ServiceNameConstants.SYSTEM_SERVICE,
		fallbackFactory = RemoteSysUserServiceFallbackFactory.class)
public interface RemoteAllService {

	/**
	 * 获取登录用户信息
	 * @param param 参数
	 * @return Result
	 */
	@PostMapping("/sysUser/getLoginUser")
	Result<SysUserDto> getLoginUser(@RequestBody SysUserDto param, @RequestHeader(SecurityConstants.FROM)String form);

	/**
	 * 加载站点首页菜单
	 * @param param
	 * @return
	 */
	@PostMapping("/public/loadTopMenus")
	public Result loadTopMenus(@RequestBody SysMenu param);

	/**
	 * 根据targetIds删除附件
	 * @param param
	 * @return
	 */
	@DeleteMapping("/sysAttachs/deleteByTargetIds")
	public Result deleteByTargetIds(@RequestBody List<String> param);

}
