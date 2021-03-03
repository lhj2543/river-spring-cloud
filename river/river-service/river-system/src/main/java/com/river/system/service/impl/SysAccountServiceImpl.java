package com.river.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.entity.system.SysAccount;
import com.river.system.mapper.SysAccountMapper;
import com.river.system.service.ISysAccountService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统账户表 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-09-01
 */
@Service
public class SysAccountServiceImpl extends ServiceImpl<SysAccountMapper, SysAccount> implements ISysAccountService {

}
