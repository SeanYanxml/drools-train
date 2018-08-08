package com.yanxml.drools.filesystem;

import javax.management.RuntimeErrorException;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class KieFileSystemTest {
	
	@Test
	public void test(){
		KieServices kieServices = KieServices.Factory.get();
		KieResources resources = kieServices.getResources();
		
		// 构建 kmodule.xml配置文件
		KieModuleModel kieModuleModel = kieServices.newKieModuleModel(); //1
		KieBaseModel baseModel = kieModuleModel.newKieBaseModel("FileSystemKBase").addPackage("rules");//2
		baseModel.newKieSessionModel("FileSystemKSession"); //3
		
		// 加载 kmodule.xml配置文件 和 .drl规则文件
		KieFileSystem fileSystem = kieServices.newKieFileSystem();
		String xml = kieModuleModel.toXML();
		System.out.println(xml); //4
		fileSystem.writeKModuleXML(xml);
		// 自动加载drl 规则文件
//		fileSystem.write(resources
//              .newClassPathResource("kiefilesystem/KieFileSystemTest.drl"));
		fileSystem.write("src/main/resources/rules/rule.drl",resources
                .newClassPathResource("kiefilesystem/KieFileSystemTest.drl"));//6 加载了两个drl配置文件
		
		// 根据 kmodule.xml配置文件 和 .drl规则文件 加载KieContainer模型
		KieBuilder  kieBuilder = kieServices.newKieBuilder(fileSystem);
		kieBuilder.buildAll(); //7
		
		// 获取构建的结果 如果存在异常的情况 作相应的处理
		if(kieBuilder.getResults().hasMessages(Level.ERROR)){
			throw new RuntimeException("Build Errors:\n"
                    + kieBuilder.getResults().toString());
		}
		
		KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
		
//		assertNotNull();
		kieContainer.getKieBase("FileSystemKBase");
		
		KieSession kieSession = kieContainer.newKieSession("FileSystemKSession");
		kieSession.fireAllRules();
		
	}

}
