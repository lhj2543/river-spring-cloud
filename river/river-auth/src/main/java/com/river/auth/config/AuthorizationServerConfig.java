package com.river.auth.config;

import com.river.auth.service.RiverUserDetailsServiceImpl;
import com.river.common.core.constant.SecurityConstants;
import com.river.common.security.component.RiverUser;
import com.river.common.security.component.RiverWebResponseExceptionTranslator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author river
 * @date 2019/2/1 认证服务器配置
 */
@Configuration
//RequiredArgsConstructor 如果类里面没有 final 的成员变量，有没有它是一样的。但如果有的话，就会产生一个构造器，要求传入 final 对应的变量的值。然后无参构造器不可用？
//@RequiredArgsConstructor
//开启授权服务
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private  DataSource dataSource;

	@Autowired
	private  AuthenticationManager authenticationManager;

	@Autowired
	private  RedisConnectionFactory redisConnectionFactory;

	@Autowired
	private RiverUserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthorizationCodeServices authorizationCodeServices;

	/**
	 * 	注入 WebSecurityConfiguration 中配置的 BCryptPasswordEncoder
 	 */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	/**
	 * ClientDetailsServiceConfigurer
	 * 用来配置客户端详情服务（clientDetailsService） ，客户端详情信息在这里初始化，用来指定授权服务可以支持哪些客户端来请求
	 * @param clients
	 */
	@Override
	//@SneakyThrows 抛出所有异常注解
	@SneakyThrows
	public void configure(ClientDetailsServiceConfigurer clients) {

		/*//内存方式
		clients.inMemory()
				//客户端id
				.withClient("river_client")
				//客户端秘钥
				.secret(passwordEncoder.encode("client_secret"))
				//授权模式 password,refresh_token,authorization_code,client_credentials
				.authorizedGrantTypes("authorization_code")
				//范围
				.scopes("server")
				//回调地址
				.redirectUris("http://localhost:3000/oauth/authorize")
			 	.autoApprove(false);*/

		//JDBC方式
		/*RiverClientDetailsService clientDetailsService = new RiverClientDetailsService(dataSource);
		clientDetailsService.setSelectClientDetailsSql(SecurityConstants.DEFAULT_SELECT_STATEMENT);
		clientDetailsService.setFindClientDetailsSql(SecurityConstants.DEFAULT_FIND_STATEMENT);*/

		JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
		clients.withClientDetails(clientDetailsService);

	}

	/**
	 * AuthorizationServerEndpointsConfigurer
	 * 用来配置令牌端点,这个对象完成令牌服务及令牌端点endpoint配置
	 * @param endpoints
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
				// token存储方式
				.tokenStore(tokenStore())
				//token增强器（自定义token信息中携带的信息）
				.tokenEnhancer(tokenEnhancer())
				//密码授权类型时，需要注入AuthenticationManager
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService)
				//授权码模式
				.authorizationCodeServices(authorizationCodeServices)
				/*
				* 配置授权端点的URL（Endpoint URLs）
				*  pathMapping() 的方法用来配置端点URL链接，它有两个参数：
				* 第一个参数：String 类型的，这个端点URL的默认链接。
				* 第二个参数：String 类型的，你要进行替代的URL链接
				* /oauth/authorize：授权端点。
				* /oauth/token：令牌端点。
				* /oauth/confirm_access：用户确认授权提交端点。
				* /oauth/error：授权服务错误信息端点。
				* /oauth/check_token：用于资源服务访问的令牌解析端点。
				* /oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。
				*/
				.pathMapping("/oauth/confirm_access", "/rauth/confirm_access")

				//异常处理,重写oauth 默认实现
				.exceptionTranslator(new RiverWebResponseExceptionTranslator())

				//该字段设置设置refresh token是否重复使用,true:reuse;false:no reuse.
				.reuseRefreshTokens(false);
	}

	/**
	 * AuthorizationServerSecurityConfigurer
	 * 令牌访问端点安全约束配置
	 * @param oauthServer
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
		//允许表单提交，表单认证，申请令牌
		oauthServer.allowFormAuthenticationForClients()
				//oauth/check_token：用于资源服务访问的令牌解析端点。 公开
				.checkTokenAccess("permitAll()")
				//oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。 公开
				.tokenKeyAccess("permitAll()");
	}


	/**
	 * Token 令牌存储策略（InMemoryTokenStore-内存方式，JdbcTokenStore-数据库方式，JwtTokenStore-jwt方式，RedisTokenStore-Redis）
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		//基于 Redis 实现，令牌保存到数据
		/*RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
		tokenStore.setPrefix(CacheConstants.PROJECT_OAUTH_ACCESS);*/

		// 基于 JDBC 实现，令牌保存到数据
		JdbcTokenStore tokenStore = new JdbcTokenStore(dataSource);

		return tokenStore;
	}

	/**
	 * 设置授权码模式的授权码如何存取 jdbc方式
	 */
	@Bean
	public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	/**
	 * token增强器（自定义token信息中携带的信息）
	 * @return
	 */
	@Bean
	public TokenEnhancer tokenEnhancer() {
		return (accessToken, authentication) -> {
			final Map<String, Object> additionalInfo = new HashMap<>(4);

			RiverUser riverUser = (RiverUser) authentication.getUserAuthentication().getPrincipal();

			additionalInfo.put(SecurityConstants.DETAILS_LICENSE, SecurityConstants.PROJECT_LICENSE);
			additionalInfo.put(SecurityConstants.DETAILS_USER_ID, riverUser.getId());
			additionalInfo.put(SecurityConstants.DETAILS_USERNAME, riverUser.getFullName());
			additionalInfo.put(SecurityConstants.DETAILS_USER_CD, riverUser.getUsername());
			additionalInfo.put(SecurityConstants.DETAILS_USER_ROLES, riverUser.getUserRoles());

			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
			return accessToken;
		};
	}

}
