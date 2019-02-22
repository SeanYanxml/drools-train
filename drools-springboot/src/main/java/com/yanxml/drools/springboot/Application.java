package com.yanxml.drools.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * 主程序的入口
 *
 */
@ComponentScan(basePackages={
		"com.yanxml.drools.springboot"
})
@SpringBootApplication

public class Application {
	
    public static Logger log = LogManager.getLogger(Application.class);

    public static void main(String[] args) {	
		try {
			SpringApplication app = new SpringApplication(Application.class);
			app.setWebEnvironment(true);
	        app.run(args);
	    	log.info("application started successfully");
		} catch (Exception e) {
			log.error("application main exception", e);
		}
	}
	
}
