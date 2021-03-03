
package com.river.common.security.execption;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.river.common.core.constant.CommonConstants;
import lombok.SneakyThrows;

/**
 * @author river
 * @date 2019/2/1
 * <p>
 * OAuth2 异常格式化
 */
public class RiverAuth2ExceptionSerializer extends StdSerializer<RiverAuth2Exception> {

	public RiverAuth2ExceptionSerializer() {
		super(RiverAuth2Exception.class);
	}

	@Override
	@SneakyThrows
	public void serialize(RiverAuth2Exception value, JsonGenerator gen, SerializerProvider provider) {
		gen.writeStartObject();
		gen.writeObjectField("code", CommonConstants.FAIL);
		gen.writeStringField("message", value.getMessage());
		gen.writeStringField("data", value.getErrorCode());
		gen.writeStringField("success", "false");
		gen.writeEndObject();
	}

}
