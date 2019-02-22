package com.yanxml.drools.demos.service;

import com.yanxml.drools.demos.pojo.Message;

public class HelloWolrdMessageService {
	
	// 更改业务
	public void changeMessageState(Message message,String msg, int status){
		message.setMessage(msg);
		message.setStatus(status);
	}
	
	public void insertMessage(Message message){
		// 插入message业务(暂略)
		// messageDao.insert();
	}

}
