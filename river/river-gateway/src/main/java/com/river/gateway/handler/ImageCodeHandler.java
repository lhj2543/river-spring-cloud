package com.river.gateway.handler;

import com.river.common.core.component.CaptchaServer;
import com.river.common.core.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author river
 * @date 2019/2/1 验证码生成逻辑处理类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCodeHandler implements HandlerFunction<ServerResponse> {

	@Autowired
	private CaptchaServer captchaServer;

	@Override
	public Mono<ServerResponse> handle(ServerRequest serverRequest) {

		MultiValueMap<String, String> param = serverRequest.queryParams();

		if(param==null || param.size()<=0 || StringUtils.isBlank(param.getFirst("randomStr"))){
			return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).bodyValue(Result.failed("randomStr 参数为空！"));
		}

		final String randomStr = param.getFirst("randomStr");

		return ServerResponse.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG)
				.body(
					BodyInserters.fromDataBuffers(Mono.create(monoSink -> {
						try {
							byte[] bytes = captchaServer.createCaptchaImage(randomStr);
							DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);

							monoSink.success(dataBuffer);
						}
						catch (IOException e) {
							log.error("ImageIO write err", e);
							monoSink.error(e);
						}
					}))
				);
	}

}
