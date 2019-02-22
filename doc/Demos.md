### Drools Demos
> Author: Sean / Date: 2019-02-22 

本文主要讲解Drools的在开发过程中的使用样例. 

基础配置: JDK 1.7 + Eclipse Mar + Maven

依赖: Drools插件(非必须)

--- 
## [Drools Demos] 其 (一) : Quick Start

### 前言

近来总结下Drools的基本内容,将使用的样例从浅到深一一拎出,作为模块参考. 本章主要对应项目内的`drools-quickstart`子模块.

### Quick Start

Quick Start的基本操作步骤如下:

1 - `pom.xml`文件内导入所需依赖包:

```
	<!-- drools jars -->
	<properties>
		<drools-version>6.5.0.Final</drools-version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-core</artifactId>
			<version>${drools-version}</version>
		</dependency>

		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-compiler</artifactId>
			<version>${drools-version}</version>
		</dependency>

		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>knowledge-api</artifactId>
			<version>${drools-version}</version>
		</dependency>

		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-decisiontables</artifactId>
			<version>${drools-version}</version>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-templates</artifactId>
			<!--templates 模板 -->
			<version>${drools-version}</version>
		</dependency>
		<dependency>
			<groupId>org.kie</groupId>
			<artifactId>kie-api</artifactId>
			<version>${drools-version}</version>
		</dependency>

		<!-- other jars -->
		
		<!-- test -->
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.7</version>
			<scope>test</scope>
		</dependency>
  </dependencies>
```

 2 - 编写Message.java类(用于规则文件内传递数据)
 
```
package com.yanxml.drools.quickstart;

/**
 * The pojo for drl.
 * @author Sean
 * */

public class Message {
	public static final int HELLO = 0;
	public static final int GOODBYE = 1;
	
	private String message;
	private int status;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
```

3 - 编写kmodule.xml文件和QuickStart.drl规则文件

>kmodule.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
    <kbase name="rules" packages="com.yanxml.drools.quickstart">
    <!-- 特别注意这边的packages 千万不能写成package 不然会报错 -->
    <!-- name 是kbase的名字 packages 是drl文件的路径 也就是HelloWorld.drl文件的物理路径 是在src/main/test 
		目录下的 -->
	<!-- ksession 是ksession的名字 但是此处ksession的名字和 .drl规则文件的名称不是对应的 -->
	<!-- 生成之后和.class文件放在一起 找的是resources下的物理地址 -->	        <ksession name="ksession-rules"/>
    </kbase>
    <kbase name="dtables" packages="dtables">
        <ksession name="ksession-dtables"/>
    </kbase>
</kmodule>
``` 

>QuickStart.drl

```
package com.yanxml.drools.quickstart

import com.yanxml.drools.quickstart.Message;

rule "Hello World"
    when
        m : Message( status == Message.HELLO, myMessage : message )
    then
        System.out.println( myMessage );
        m.setMessage( "Goodbye cruel world" );
        m.setStatus( Message.GOODBYE );
        update( m );
end

rule "GoodBye"
    when
        Message( status == Message.GOODBYE, myMessage : message )
    then
        System.out.println( myMessage );
end
```

4 - 编写测试类DroolsBaseTest和QuickStartTest

>DroolsBaseTest(基础测试类)

```
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

```

>QuickStartTest(QuickStart测试类)

```
package com.yanxml.drools.quickstart;

import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class QuickStartTest extends DroolsBaseTest{
	
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

```

--- 

## [Drools Demos] 其 (二) : 数据传递

### 前言
本章主要对应项目内的`drools-demos`内的HelloWorld样例.主要讲解Drools内是如何进行数据传递的.

### 详细内容

通过上节我们可以看到,通过`kSession.insert(message);`方法,将message对象传递给了DRL规则文件内部.

* Q1:
那么,有一个问题,DRL内改变对象后,规则文件外的对象`message`是否改变了?我们带着这个问题进行今天的测试

> Message.java(POJO类)

```
package com.yanxml.drools.demos.pojo;

public class Message {
	// status 4 种状态
	public static final int STATUS_1 = 1;
	public static final int STATUS_2 = 2;
	public static final int STATUS_3 = 3;
	public static final int STATUS_4 = 4;
	
	private String message;
	private int status;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}

```

> kmodule.xml(kmodule配置文件)

```
<?xml version="1.0" encoding="utf-8" ?>
<kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
	<!-- demo helloworld -->
	<kbase name="kbase-helloworld" packages="com.yanxml.drools.demos.helloworld">
		<ksession name="ksession-helloworld"/>
	</kbase>
	
</kmodule>
```

> Helloworld.drl

```

import com.yanxml.drools.demos.pojo.Message;

no-loop true

rule "TransmitHelloworldRule"
	when 
		$m : Message( status == Message.STATUS_1, myMessage:message)
	then
		System.out.println("Hit Transmit HelloWorld Rule.");
		$m.setMessage("STATUS_2");
		$m.setStatus(Message.STATUS_2);
//		update(m);
end
```

>DroolsBaseTest.java(基础测试类)

```
package com.yanxml.drools.demos.test;

import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

public abstract class DroolsBaseTest {
	
	protected KieServices kieServices;
	protected KieContainer kieContainer;
	
	@Before
	public void init(){
		kieServices =  KieServices.Factory.get();
		kieContainer = kieServices.getKieClasspathContainer(); 
	}

}
```

>HelloWorldTest.java(HelloWorld测试类)

```
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
	
}

```

> 测试结果:

```
Rule Before - Message: STATUS_1 status:1
Hit Transmit HelloWorld Rule.
Rule After - Message: STATUS_2 status:2
```

结论: 规则内和规则外使用的是同一个对象.规则内对象改变了,规则外也会跟随改变.
 
* Q2:
我们在规则内部调用规则外的工具类等,应当进行如何调用呢?

> HelloWorldService(待回调的Service方法)

```
package com.yanxml.drools.demos.service;

import com.yanxml.drools.demos.pojo.Message;

public class HelloWolrdMessageService {
	
	// 更改业务
	public void changeMessageState(Message message,String msg, int status){
		message.setMessage(msg);
		message.setStatus(status);
	}
	
	public void insertMessage(Message message){
		// 插入message业务(暂略)
		// messageDao.insert();
	}

}

```

> Helloworld.drl(规则补充)

```
rule "InvokeHelloWorldRule"
	when
		$m : Message()
		$msg : String()
		$status : Integer()
		$helloWolrdMessageService : HelloWolrdMessageService()
	then
		System.out.println("Hit Invoke HelloWorld Rule.");
		$helloWolrdMessageService.changeMessageState($m,$msg,$status);
end
```

> HelloWorldTest(测试类补充)

```
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
```

> 测试结果:

```
Rule Before - Message: STATUS_1 status:1
Hit Invoke HelloWorld Rule.
Rule After - Message: STATUS_3 status:3
```

结论: 通过`kieSession.insert(service)`方法一并传入即可.

---
## [Drools Demos] 其 (三) : 动态加载规则文件

### 前言

本章主要探讨Drools的动态加载.有很多时候,规则文件是存储在数据库或其他地方,这就要求我们对于`Drools规则`文件的装配过程和装配过程中关键的几个对象非常熟悉,例如:`KieFileSystem / KieModule/ KieBase / KieContainer / KieSession...`.

本章主要对应项目内的`drools-filesystem`内容.

### 动态加载方式

* Method1 - 通过KieModuleModel装载

```
package com.yanxml.drools.filesystem;

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
		
		kieContainer.getKieBase("FileSystemKBase");
		
		KieSession kieSession = kieContainer.newKieSession("FileSystemKSession");
		kieSession.fireAllRules();
		
	}

}

```

> 1. 先创建KieModuleModel；
2. 再创建KieBaseModel；
3. 然后创建 KieSessionModel；
4. 创建完成之后可以生产一个xml文件，就是kmodule.xml文件了；
5. 将这个xml文件写入到KieFileSystem中；
6. 然后将规则文件等写入到KieFileSystem中；
7. 最后通过KieBuilder进行构建就将该kmodule加入到KieRepository中了。这样就将自定义的kmodule加入到引擎中了，就可以按照之前的方法进行使用了。

* Method2 - 直接装载

```
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

```

* Method 3(与Method 2 比较相似)

```
  KieServices ks = KieServices.Factory.get();
    KieFileSystem kfs = ks.newKieFileSystem();
    FileInputStream fis = new FileInputStream( "sale/sale.drl" );
    kfs.write( "src/main/resources/sale.drl",
                   ks.getResources().newInputStreamResource( fis ) );
    KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
    Results results = kieBuilder.getResults();
    if( results.hasMessages( Message.Level.ERROR ) ){
        System.out.println( results.getMessages() );
        throw new IllegalStateException( "### errors ###" );
    }
    KieContainer kieContainer =
        ks.newKieContainer( ks.getRepository().getDefaultReleaseId() );

    // CEP - get the KIE related configuration container and set the EventProcessing (from default cloud) to Stream
    KieBaseConfiguration config = ks.newKieBaseConfiguration();
    config.setOption( EventProcessingOption.STREAM );
    KieBase kieBase = kieContainer.newKieBase( config );
    //      KieSession kieSession = kieContainer.newKieSession();
    KieSession kieSession = kieBase.newKieSession();
```
[(StackOverflow) drools dynamic ](https://stackoverflow.com/questions/27488034/with-drools-6-x-how-do-i-avoid-maven-and-the-compiler)

> 

---
## [Drools Demos] 其 (四) : SpringBoot-Drools模板

### 前言

前几节我们说了Drools的快速启动、数据传递和动态加载.本章我们提供一个简单的 Drools-SpringBoot实例模板.

本章主要对应项目内的`drools-springboot-dynamic`内容.

### 详细内容(Demos-1-模板基础)

> pom.xml(导入相关Jar包)

```
	<!-- drools jars -->
	<properties>
		<drools-version>6.5.0.Final</drools-version>
		<springboot.version>1.5.13.RELEASE</springboot.version>
	</properties>
	
	<dependencies>
	
		<!-- spring boot -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
			<version>${springboot.version}</version>
			<exclusions>
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-logging</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		
		<!-- spring boot log4j2 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-log4j2</artifactId>
			<version>${springboot.version}</version>
		</dependency>
		
				<!--spring-boot -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<version>${springboot.version}</version>
			<exclusions>
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-logging</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		
	<!-- 	<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-core</artifactId>
			<version>${drools-version}</version>
		</dependency> -->

		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-compiler</artifactId>
			<version>${drools-version}</version>
		</dependency>

		<!-- <dependency>
			<groupId>org.drools</groupId>
			<artifactId>knowledge-api</artifactId>
			<version>${drools-version}</version>
		</dependency>

		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-decisiontables</artifactId>
			<version>${drools-version}</version>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-templates</artifactId>
			templates 模板
			<version>${drools-version}</version>
		</dependency>
		<dependency>
			<groupId>org.kie</groupId>
			<artifactId>kie-api</artifactId>
			<version>${drools-version}</version>
		</dependency> -->

		<dependency>
			<groupId>org.kie</groupId>
			<artifactId>kie-spring</artifactId>
			<version>${drools-version}</version>
			<exclusions>
				<exclusion>
					<groupId>org.springframework</groupId>
					<artifactId>spring-tx</artifactId>
				</exclusion>
				<exclusion>
					<groupId>org.springframework</groupId>
					<artifactId>spring-beans</artifactId>
				</exclusion>
				<exclusion>
					<groupId>org.springframework</groupId>
					<artifactId>spring-core</artifactId>
				</exclusion>
				<exclusion>
					<groupId>org.springframework</groupId>
					<artifactId>spring-context</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<!-- other jars -->
		
		<!-- test -->
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.7</version>
			<scope>test</scope>
		</dependency>

		<dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.6</version>
            <scope>compile</scope>
        </dependency>
        
        <!--log4j-->
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
        </dependency>
        	
	</dependencies>
```

> DroolsConfig.java(Drools动态加载配置类)

```
package com.yanxml.drools.springboot.config;

import java.io.IOException;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
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

```

> Application(SpringBoot启动类)

```
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

```

> Message(POJO)

```
package com.yanxml.drools.springboot.beans.pojo;

public class Message {
	// status 4 种状态
	public static final int STATUS_1 = 1;
	public static final int STATUS_2 = 2;
	public static final int STATUS_3 = 3;
	public static final int STATUS_4 = 4;

	private String message;
	private int status;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
```

> HelloWorld.drl

```
package com.yanxml.drools.springboot

import com.yanxml.drools.springboot.beans.pojo.Message;
import com.yanxml.drools.springboot.service.MessageServiceService;

no-loop true

rule "TransmitHelloworldRule"
	when 
		$m : Message( status == Message.STATUS_1, myMessage:message)
	then
		System.out.println("Hit Transmit HelloWorld Rule.");
		$m.setMessage("STATUS_2");
		$m.setStatus(Message.STATUS_2);
//		update($m);
//		update(m);
end

rule "InvokeHelloWorldRule"
	when
		$m : Message()
		$messageServiceService : MessageServiceService()
	then
		System.out.println("Hit Invoke HelloWorld Rule.");
		$messageServiceService.insert($m);
end
```

> HelloworldController

```
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
```

> 测试结果:

```

[1] localhost:8080/test/helloworld

# output

Rule Before - Message: STATUS_1 status:1
Hit Transmit HelloWorld Rule.
Rule After - Message: STATUS_2 status:2

RELATING-CLASS:HelloworldController
```

### 详细内容(Demos-2-方法回调)

> MessageServiceService

```
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
```

> HelloWorld.drl 规则补充

```
rule "InvokeHelloWorldRule"
	when
		$m : Message()
		$messageServiceService : MessageServiceService()
	then
		System.out.println("Hit Invoke HelloWorld Rule.");
		$messageServiceService.insert($m);
end
```

> MessageServiceController(新Controller)

```
package com.yanxml.drools.springboot.controller;

import javax.annotation.Resource;

import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
```

> 测试结果:

```

[2] http://localhost:8080/message/insert

# output

Hit Invoke HelloWorld Rule.
Message Service Insert SQL.

RELATING-CLASS:MessageServiceController
```

### Reference

[1] [《Drools7.0.0.Final规则引擎教程》Springboot+规则重新加载](https://hello.blog.csdn.net/article/details/76566016)

[2] [Drools入门系列](https://blog.csdn.net/u010416101/article/details/53619704#comments)