package com.yanxml.drools.springboot.config;

import java.io.FileInputStream;
import java.io.IOException;

//import org.apache.naming.factory.ResourceFactory;
//import org.drools.io.ResourceFactory;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
public class DroolsConfig {
	private static final String RULES_PATH = "rules/";

	@Bean
	@ConditionalOnMissingBean(KieFileSystem.class)
	public KieFileSystem kieFileSystem() throws IOException {
		KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
		for (Resource file : getRuleFiles()) {
//			((org.kie.api.io.Resource) file).setResourceType(ResourceType.DRL);
			kieFileSystem.write(ResourceFactory.newClassPathResource(
					RULES_PATH + file.getFilename(), "UTF-8"));
//			FileInputStream fis = new FileInputStream( "src/main/resources/"+RULES_PATH + file.getFilename() );
//			kieFileSystem.write( "src/main/resources/"+RULES_PATH + file.getFilename(),
//					getKieServices().getResources().newInputStreamResource( fis ) );
		}
		return kieFileSystem;
	}

	private Resource[] getRuleFiles() throws IOException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		return resourcePatternResolver
				.getResources("classpath*:" + RULES_PATH + "**/*.*");
	}

	@Bean
	@ConditionalOnMissingBean(KieContainer.class)
	public KieContainer kieContainer() throws IOException {
        KieServices kieServices =  getKieServices();
		final KieRepository kieRepository = kieServices.getRepository();

		// 这边出了问题
		kieRepository.addKieModule(new KieModule() {
			public ReleaseId getReleaseId() {
				return kieRepository.getDefaultReleaseId();
			}
		});

		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem());
		 Results results = kieBuilder.getResults();
	        if (results.hasMessages(Message.Level.ERROR)) {
	            System.out.println(results.getMessages());
	            throw new IllegalStateException("### errors ###");
	        }
		kieBuilder.buildAll();

//		return getKieServices()
//				.newKieContainer(getKieServices().getRepository().getDefaultReleaseId());
        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());

		return kieContainer;
	}

	private KieServices getKieServices() {
		return KieServices.Factory.get();
	}

	@Bean
	@ConditionalOnMissingBean(KieBase.class)
	public KieBase kieBase() throws IOException {
		return kieContainer().getKieBase();
	}

	@Bean
	@ConditionalOnMissingBean(KieSession.class)
	public KieSession kieSession() throws IOException {
		return kieContainer().newKieSession();
	}

	@Bean
	@ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
	public KModuleBeanFactoryPostProcessor kiePostProcessor() {
		return new KModuleBeanFactoryPostProcessor();
	}
}
