package com.yanxml.drools.springboot.service;

import org.springframework.stereotype.Service;
import com.yanxml.drools.springboot.beans.pojo.Message;

@Service
public class MessageServiceService {
	
	public void insert(Message message){
		// 数据库 Insert语句
		System.out.println("Message Service Insert SQL.");
	}
}
