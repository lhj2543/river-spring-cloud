package com.river.common.core.exception;

public class BusinessServiceException extends RuntimeException
{
	private String errorCode;

	private String errorMessage;

	private Object sources;

	private static final long serialVersionUID = 5564417708050358351L;

	public BusinessServiceException() {}

	public BusinessServiceException(String message)
	{
		super(message);
		this.errorMessage = message;
	}

	public BusinessServiceException(Throwable cause)
	{
		super(cause);
	}

	public BusinessServiceException(String code, String message)
	{
		super(message);
		this.errorCode = code;
		this.errorMessage = message;
	}

	public BusinessServiceException(String message, Throwable cause)
	{
		super(message, cause);
		this.errorMessage = message;
	}

	public BusinessServiceException(String code, String message, Throwable cause)
	{
		super(message, cause);
		this.errorCode = code;
		this.errorMessage = message;
	}

	public BusinessServiceException(String code, String message, Object sources, Throwable cause)
	{
		super(message, cause);
		this.errorCode = code;
		this.errorMessage = message;
		this.sources = sources;
	}

	public String getErrorCode()
	{
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Object getSources() {
		return this.sources;
	}

	public void setSources(Object sources) {
		this.sources = sources;
	}
}

