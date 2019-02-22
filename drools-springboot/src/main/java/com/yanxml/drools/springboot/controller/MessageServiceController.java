package com.yanxml.drools.springboot.controller;

import javax.annotation.Resource;

import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yanxml.drools.springboot.beans.pojo.Message;
import com.yanxml.drools.springboot.service.MessageServiceService;

@Controller
@RequestMapping("/message")
public class MessageServiceController {
	@Resource 
	KieContainer kieContainer;
	
	@Autowired
	MessageServiceService messageServiceService;
	
	@ResponseBody
	@RequestMapping("/insert")
	public void insert(){
		KieSession kieSession = kieContainer.newKieSession();
		
		// insert Facts
		Message message = new Message();
		message.setMessage("Insert-0-Message");
		message.setStatus(Message.STATUS_1);
		kieSession.insert(message);
		
		// insert Service
		kieSession.insert(messageServiceService);
		
		kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("Invoke"));

		
	}

}
