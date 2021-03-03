
package com.river.gateway.filter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import com.river.common.core.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author river
 * @date 2019/2/1 密码解密工具类
 */
@Slf4j
@Component
public class PasswordDecoderFilter extends AbstractGatewayFilterFactory {

	private static final String PASSWORD = "password";

	private static final String KEY_ALGORITHM = "AES";

	/**
	 * 加解密秘钥，必须16位字符
	 */
	@Value("${security.encode.key:river_key354282p}")
	private  String encodeKey;

	/**
	 * AES解密
	 * @param data 密码
	 * @param pass 加解密秘钥
	 * @return
	 */
	private static String decryptAES(String data, String pass) {
		AES aes = new AES(Mode.CBC, Padding.NoPadding, new SecretKeySpec(pass.getBytes(), KEY_ALGORITHM),
				new IvParameterSpec(pass.getBytes()));
		byte[] result = aes.decrypt(Base64.decode(data.getBytes(StandardCharsets.UTF_8)));
		return new String(result, StandardCharsets.UTF_8);
	}

	/**
	 * AES加密
	 * @return
	 */
	public static String encryptAES(String data, String pass) {

		AES aes = new AES(Mode.CBC, Padding.ZeroPadding, new SecretKeySpec(pass.getBytes(), KEY_ALGORITHM),new IvParameterSpec(pass.getBytes()));
		byte[] result = aes.encrypt(data);

		String Base64Encode = Base64.encode(result);

		/*//加密为16进制表示
		String encryptHex = aes.encryptHex(result);
		//解密为字符串
		String decryptStr = aes.decryptStr(result, CharsetUtil.CHARSET_UTF_8);*/

		return Base64Encode;
	}

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			// 不是登录请求，直接向下执行
			if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath(), SecurityConstants.OAUTH_TOKEN_URL)) {
				return chain.filter(exchange);
			}

			URI uri = exchange.getRequest().getURI();
			String queryParam = uri.getRawQuery();
			Map<String, String> paramMap = HttpUtil.decodeParamMap(queryParam, CharsetUtil.CHARSET_UTF_8);

			String password = paramMap.get(PASSWORD);
			if (StrUtil.isNotBlank(password)) {
				try {
					password = decryptAES(password, encodeKey);
				}
				catch (Exception e) {
					log.error("AES 密码解密失败:{}", password);
					return Mono.error(e);
				}
				paramMap.put(PASSWORD, password.trim());
			}

			URI newUri = UriComponentsBuilder.fromUri(uri).replaceQuery(HttpUtil.toParams(paramMap)).build(true).toUri();

			ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri).build();
			return chain.filter(exchange.mutate().request(newRequest).build());
		};
	}

	public static void main(String[] args) {

		try {
			String pasw = PasswordDecoderFilter.encryptAES("123456", "river_key354282p");
			System.out.println(pasw);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
