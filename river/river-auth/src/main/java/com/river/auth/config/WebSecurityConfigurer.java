package com.river.auth.config;

import com.river.auth.handler.FormAuthenticationFailureHandler;
import com.river.auth.handler.RiverAuthenticationSuccessHandler;
import com.river.auth.service.RiverUserDetailsServiceImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * @author river
 * @date 2019/2/1 认证相关配置
 */
@Primary
@Order(90)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Autowired
	private RiverUserDetailsServiceImpl userDetailsService;

	@Autowired
	RiverAuthenticationSuccessHandler riverAuthenticationSuccessHandler;

	@Value("${river.security.defaultSuccessUrl:https://www.baidu.com}")
	private String defaultSuccessUrl;

	@Override
	@SneakyThrows
	protected void configure(HttpSecurity http) {
		http
			//对应表单认证相关的配置
			.formLogin()
				//登录页面
				.loginPage("/rauth/login")
				//登录表单 action = /rauth/form
				.loginProcessingUrl("/rauth/form")
				//登录失败处理器
				.failureHandler(authenticationFailureHandler())
				//默认登录成功跳转地址
				.defaultSuccessUrl(defaultSuccessUrl)
				//登录成功处理器
				.successHandler(riverAuthenticationSuccessHandler)
				.and()
			//配置路径拦截，表明路径访问所对应的权限，角色，认证信息
			.authorizeRequests()
				//不拦截的路径
				.antMatchers("/rauth/**", "/actuator/**", "/mobile/**").permitAll()
				//所有请求都需要通过认证
				.anyRequest().authenticated()
				.and()
			//basic登录 （弹出输入用户密码窗口）
			//.httpBasic().and()
			//关闭跨域保护
			.csrf().disable();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/css/**");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/*auth.inMemoryAuthentication()
				// 在内存中创建用户并为密码加密
				.withUser("user").password(passwordEncoder().encode("123")).roles("USER")
				.and()
				.withUser("admin").password(passwordEncoder().encode("123")).roles("ADMIN");*/

		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

	}

	@Bean
	@Override
	@SneakyThrows
	public AuthenticationManager authenticationManagerBean() {
		return super.authenticationManagerBean();
	}

	/**
	 * 表单登录失败处理器
	 * @return
	 */
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new FormAuthenticationFailureHandler();
	}

	/**
	 * 配置密码加密方式 BCrypt
	 * @return PasswordEncoder
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		// 设置默认的加密方式
		return new BCryptPasswordEncoder();
	}

	/*@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}*/

	
	public static void main(String[] args) {
		String client_secret = new BCryptPasswordEncoder().encode("123");
		System.err.println(client_secret);
	}

}
