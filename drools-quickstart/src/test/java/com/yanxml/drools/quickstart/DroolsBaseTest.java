package com.yanxml.drools.quickstart;

import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

public abstract class DroolsBaseTest {	
	
	protected KieServices kieServices;
	protected KieContainer kieContainer;
	
	@Before
	public void setUp(){
		kieServices = KieServices.Factory.get();
		kieContainer = kieServices.getKieClasspathContainer();
	}

}
