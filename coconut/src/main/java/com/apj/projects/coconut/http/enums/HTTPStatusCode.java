package com.apj.projects.coconut.http.enums;

public enum HTTPStatusCode {

	OK(200, "OK"), CREATED(201, "CREATED"), NOT_FOUND(404, "NOT FOUND"), BAD_REQUEST(400, "BAD REQUEST"),
	INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"), UNAUTHORIZED(401, "UNAUTHORIZED");

	private int statusCode;
	private String msg;

	HTTPStatusCode(int statusCode, String msg) {
		this.statusCode = statusCode;
		this.msg = msg;
	}

	public int getCode() {
		return statusCode;
	}

	public String getMsg() {
		return msg;
	}

}
