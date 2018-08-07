package com.yanxml.drools.quickstart;

import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class QuickStartTest extends DroolsBastTest{
	
	@Test
	public void test(){
		KieSession kSession = kieContainer.newKieSession("ksession-rules");
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);
		kSession.insert(message);
		kSession.fireAllRules();
	}

}
