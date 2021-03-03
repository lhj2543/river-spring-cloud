
package com.river.api.feign.factory;

import com.river.api.dto.system.SysUserDto;
import com.river.api.entity.system.SysMenu;
import com.river.api.feign.RemoteAllService;
import com.river.common.core.util.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author river
 * @date 2019/2/1
 */
@Component
@Slf4j
public class RemoteSysUserServiceFallbackFactory implements FallbackFactory<RemoteAllService> {

	@Override
	public RemoteAllService create(Throwable throwable) {

		return new RemoteAllService(){
			@Override
			public Result<SysUserDto> getLoginUser(SysUserDto param,String from) {
				log.error("feign 查询用户信息失败:{}", param.getUserCd(), throwable);
				return Result.failed(param,"feign 查询用户信息失败:"+throwable.getMessage());
			}

			@Override
			public Result loadTopMenus(SysMenu param) {
				log.error("feign 查询站点菜单失败", throwable);
				return Result.failed(param,"feign 查询站点菜单失败:"+throwable.getMessage());
			}

			@Override
			public Result deleteByTargetIds(List<String> param) {
				log.error("feign 根据targetIds删除附件失败", throwable);
				return Result.failed(param,"feign 根据targetIds删除附件失败:"+throwable.getMessage());
			}

		};

	}

}
