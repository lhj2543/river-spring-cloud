package com.river.common.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.river.common.core.exception.BusinessServiceException;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class JsonSupport
{
	public JsonSupport() {}

	public <T> T toList(TypeReference<T> typeReference, String json)
	{
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, typeReference);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessServiceException("JSON 转换异常");
		}
	}


	public <T> T toBean(Class<T> c, String json)
	{
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, c);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessServiceException("JSON 转换异常");
		}
	}
	

	public String toJson(Object pojo)
	{
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(pojo);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessServiceException("JSON 转换异常");
		}
	}
}

