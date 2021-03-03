
package com.river.common.security.execption;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author river
 * @date 2019/2/1
 */
@JsonSerialize(using = RiverAuth2ExceptionSerializer.class)
public class InvalidException extends RiverAuth2Exception {

	public InvalidException(String msg, Throwable t) {
		super(msg);
	}

	@Override
	public String getOAuth2ErrorCode() {
		return "invalid_exception";
	}

	@Override
	public int getHttpErrorCode() {
		return 426;
	}

}
