
package com.river.common.core.constant;

/**
 * @author river
 * @date 2020年01月01日
 * <p>
 * 缓存的key 常量
 */
public interface CacheConstants {

	//===================================缓存前缀start============================

	/**
	 * 站点缓存前缀
	 */
	String PREFIX_SITE = "river-site:";
	/**
	 * 系统管理缓存前缀
	 */
	String PREFIX_SYSTEM = "river-system:";

	/**
	 * security oatuh 缓存前缀
	 */
	String PREFIX_AUTH = "river-auth:";



	/**
	 * 登录用户缓存
	 */
	String PREFIX_LOGIN_USER = PREFIX_AUTH + "login-user";

	/**
	 * 后台用户菜单缓存
	 */
	String PREFIX_USER_MENU = PREFIX_SITE + "user-menu";

	/**
	 * 笔记分类树缓存前缀
	 */
	String PREFIX_SITE_NOTEBOOKCATEGORY = PREFIX_SITE + "notebook";

	/**
	 * 验证码缓存
	 */
	String PREFIX_SITE_CAPTCHA = PREFIX_SITE + "captcha:";


	//===================================缓存前缀end============================




	//===================================缓存key start============================

	/**
	 * 站点菜单缓存
	 */
	String KEY_SITE_MENU = PREFIX_SYSTEM + "site-menu";

	/**
	 * 站点首页缓存
	 */
	String KEY_SITE_INDEX = "site-index";

	/**
	 * 字段缓存
	 */
	String KEY_SYS_ITEM = PREFIX_SYSTEM + "sys-item";

	/**
	 * 笔记分类树缓存
	 */
	String KEY_SITE_NOTEBOOKCATEGORY =  "notebook-category";


	//===================================缓存key end============================



	/**
	 * oauth 缓存前缀
	 */
	String PROJECT_OAUTH_ACCESS = "river-auth:access:";

	/**
	 * oauth 客户端信息
	 */
	String CLIENT_DETAILS_KEY = "river-auth:client:details";


}
