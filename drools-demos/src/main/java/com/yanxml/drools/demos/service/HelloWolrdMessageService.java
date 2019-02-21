package com.yanxml.drools.demos.service;

import com.yanxml.drools.demos.pojo.Message;

public class HelloWolrdMessageService {
	
	public void changeMessageState(Message message,String msg, int status){
		message.setMessage(msg);
		message.setStatus(status);
	}

}
