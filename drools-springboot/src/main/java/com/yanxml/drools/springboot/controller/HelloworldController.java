package com.yanxml.drools.springboot.controller;

import javax.annotation.Resource;

import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yanxml.drools.springboot.beans.pojo.Message;


@RequestMapping("/test")
@Controller
public class HelloworldController {
	@Resource
	private KieContainer kieContainer;
	
//	@Resource
//	private KieSession kieSession;
	
  @ResponseBody
  @RequestMapping("/helloworld")
  public void test(){
	  // newKieSession with name will be NullPointerException.(Bug)
//	  	KieSession kieSession = kieContainer.newKieSession("kSessionHelloWorld");
	  	KieSession kieSession = kieContainer.newKieSession();
		Message message = new Message();
		message.setMessage("STATUS_1");
		message.setStatus(Message.STATUS_1);
		
		System.out.println("Rule Before - Message: "+message.getMessage()+" status:"+message.getStatus());
		
		// hit rule
		kieSession.insert(message);
		kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("Transmit"));
		
		// 同步等待 - 等待rule执行完毕后 后面的语句才被执行。
		System.out.println("Rule After - Message: "+message.getMessage()+" status:"+message.getStatus());
	}

	
}