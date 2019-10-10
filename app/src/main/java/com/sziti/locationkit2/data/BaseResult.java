package com.sziti.locationkit2.data;

public class BaseResult<T>{
	private String Code;
	private T data;

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
