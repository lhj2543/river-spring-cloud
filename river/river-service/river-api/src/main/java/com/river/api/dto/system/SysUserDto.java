package com.river.api.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.river.api.entity.system.SysAccount;
import com.river.api.entity.system.SysMenu;
import com.river.api.entity.system.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author river
 * @date 2020-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserDto extends SysUser {

    /**
     * 是否超级管理员
     */
    private boolean isAdministrator;

    public boolean isAdministrator() {
        return StringUtils.equals("administrator",this.getUserCd());
    }

    /**
     * 密码
     */
    private String password;

    /**
     * colNameCn 	许可证
     */
    private String licence;
    /**
     * colNameCn 	账户类型:（01=普通用户，02=管理员）
     */
    private String type;
    /**
     * colNameCn 	有效开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    /**
     * colNameCn 	有效结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    /**
     * colNameCn 	锁住时间
     */
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date lockDate;
    /**
     * colNameCn 	登录失败次数
     */
    private BigDecimal loginFailureCount;

    /**
     * 用户token
     */
    private String token;

    /*
     *	是否强制登录
     **/
    private String isForceLogin;

    /**
     *  账户
     */
    private SysAccount sysAccount = new SysAccount();


    /**
     * 角色状态
     */
    private String roleStatus;

    /**
     * 账户状态
     */
    private String accountStatus;

    /**
     *  关联账户角色表
     */
    private Set<SysUserRoleDto> userRoles = new HashSet<SysUserRoleDto>(0);

    /**
     * 用户菜单资源
     */
    private List<SysMenu> sysUserMenu;

    private String category;

}
