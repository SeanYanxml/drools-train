package com.yanxml.drools.demos.test;

import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import com.yanxml.drools.demos.pojo.Message;
import com.yanxml.drools.demos.service.HelloWolrdMessageService;

public class HelloWorldTest extends DroolsBaseTest {
	
//	@Test
	public void testHelloWorld(){
		KieSession kieSession = kieContainer.newKieSession("ksession-helloworld");
		Message message = new Message();
		message.setMessage("STATUS_1");
		message.setStatus(Message.STATUS_1);
		
		System.out.println("Rule Before - Message: "+message.getMessage()+" status:"+message.getStatus());
		
		// hit rule
		kieSession.insert(message);
		kieSession.fireAllRules();
		
		// 同步等待 - 等待rule执行完毕后 后面的语句才被执行。
		System.out.println("Rule After - Message: "+message.getMessage()+" status:"+message.getStatus());
	}
	
	@Test
	public void testHelloWorldInvoke(){
		KieSession kieSession = kieContainer.newKieSession("ksession-helloworld");
		Message message = new Message();
		message.setMessage("STATUS_1");
		message.setStatus(Message.STATUS_1);
		
		HelloWolrdMessageService helloWolrdMessageService = new HelloWolrdMessageService();
		
		
		System.out.println("Rule Before - Message: "+message.getMessage()+" status:"+message.getStatus());
		
		// hit rule
		kieSession.insert(message);
		
		kieSession.insert("STATUS_3");
		kieSession.insert(Message.STATUS_3);
		kieSession.insert(helloWolrdMessageService);
		
//		kieSession.fireAllRules();

		kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("Invoke"));
		
		// 同步等待 - 等待rule执行完毕后 后面的语句才被执行。
		System.out.println("Rule After - Message: "+message.getMessage()+" status:"+message.getStatus());
	}

}
