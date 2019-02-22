package com.yanxml.drools.springboot.beans.pojo;

public class Message {
	// status 4 种状态
	public static final int STATUS_1 = 1;
	public static final int STATUS_2 = 2;
	public static final int STATUS_3 = 3;
	public static final int STATUS_4 = 4;

	private String message;
	private int status;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
