package op.config;

import static org.springframework.context.annotation.ComponentScan.Filter;

import java.io.IOException;
import java.util.Properties;

import op.Application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import codesparser.FacetUtils;

@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan(basePackageClasses = {Application.class}, excludeFilters = @Filter({Controller.class, Configuration.class}))
class ApplicationConfig {
    private static final String MAILMESSAGE_SOURCE = "/emails/messages";
    private static final String MAIL = "emails/";
	
    @Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        String openshift = System.getenv("OPENSHIFT_APP_NAME");
        if ( openshift != null ) {
			ppc.setLocation(new ClassPathResource("/application.properties"));
        } else {
			ppc.setLocation(new ClassPathResource("/application-local.properties"));
        }
		return ppc;
	}

    @Bean
    public MessageSource mailMessageSource() {
        ResourceBundleMessageSource mailMessageSource = new ResourceBundleMessageSource();
        mailMessageSource.setBasename(MAILMESSAGE_SOURCE);
        return mailMessageSource;
    }

    @Bean
    public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
    	ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
    	classLoaderTemplateResolver.setPrefix(MAIL);
    	classLoaderTemplateResolver.setTemplateMode("HTML");
    	classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
    	return classLoaderTemplateResolver; 
    }
	
    @Bean
    public SpringTemplateEngine mailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(classLoaderTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(mailMessageSource());
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }
    
	
    @Bean
	public FacetUtils pathUtil() {
		return new FacetUtils(); 
	}

	@Bean
	public static Properties javaMailProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(ApplicationConfig.class.getResourceAsStream("/javamail.properties"));
		return properties;
	}

	@Value("${mail.server.host}")
	private String host;
	@Value("${mail.server.port}")
	private Integer port; 
	@Value("${mail.server.protocol}")
	private String protocol; 

	@Bean
	public JavaMailSender mailSender() throws IOException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setProtocol(protocol);

		mailSender.setUsername(System.getenv("mail_server_username"));
		mailSender.setPassword(System.getenv("mail_server_password"));
		mailSender.setJavaMailProperties(javaMailProperties());
		return mailSender;
	}

}