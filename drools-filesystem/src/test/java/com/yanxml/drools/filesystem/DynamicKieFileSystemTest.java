package com.yanxml.drools.filesystem;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;

/**
 * 生产环境所用的KieFileSystem。都是用于动态加载。与KieFileSyetmTest略有差别。
 * 
 * */
public class DynamicKieFileSystemTest {
	
	
	@Test
	public void test(){
		String ruleFilePath = "package com.yanxml.drools.files \n rule \"Test KieFileSystem\" \n when \n then \n System.out.println( \"hello, KieFileSystem\" ); \n end";
		try {
            // 设置时间格式
            System.setProperty("drools.dateformat", "yyyy-MM-dd");

            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kfs = kieServices.newKieFileSystem();
            Resource resource = kieServices.getResources().newFileSystemResource(ruleFilePath);
            System.out.println(resource.toString());
            resource.setResourceType(ResourceType.DRL);
            kfs.write(resource);// 将XML配置文件写入kieFileSystem内
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
            if (kieBuilder.getResults().getMessages(Message.Level.ERROR).size() > 0) {
                throw new RuntimeException(kieBuilder.getResults().getMessages().toString());
            }
            KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
            KieBase kBase = kieContainer.getKieBase();
            
            kBase.newKieSession().fireAllRules();
            //放入缓存
        } catch (Exception e) {
        }
	}
	

}
