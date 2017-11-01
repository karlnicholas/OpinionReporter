package op.config;


//@Configuration
// @ComponentScan(basePackageClasses = { Application.class }, includeFilters = @Filter(Controller.class), useDefaultFilters = false)
//@ComponentScan(basePackageClasses = { Application.class }, useDefaultFilters = false)
//@ComponentScan(basePackageClasses = { Application.class }, 
//	excludeFilters = { @Filter(type = FilterType.ANNOTATION, value = Configuration.class) })

public class WebMvcConfigSave {

}
/*
extends WebMvcConfigurationSupport implements ApplicationContextAware {

	private static final String VIEWS = "/WEB-INF/views/";
	private static final String MESSAGE_SOURCE = "/WEB-INF/i18n/messages";

	private static final String RESOURCES_HANDLER = "/resources/";
	private static final String RESOURCES_LOCATION = RESOURCES_HANDLER + "**";
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		super.setApplicationContext(applicationContext);
		RequestMappingHandlerMapping requestMappingHandlerMapping = super.requestMappingHandlerMapping();
		requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
		requestMappingHandlerMapping.setUseTrailingSlashMatch(false);
		return requestMappingHandlerMapping;
	}

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(MESSAGE_SOURCE);
		messageSource.setCacheSeconds(5);
		return messageSource;
	}

	@Bean
	public ViewResolver mvcViewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setEnableSpringELCompiler(true);
		templateEngine.setTemplateResolver(mvcTemplateResolver());
		templateEngine.addDialect(new SpringSecurityDialect());
		return templateEngine;
	}

	private ITemplateResolver mvcTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix(VIEWS);
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCacheable(false);
		return resolver;
	}

	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}

	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(RESOURCES_LOCATION);
	}
	

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Controller
	static class FaviconController {
		@RequestMapping("favicon.ico")
		String favicon() {
			return "forward:/resources/images/favicon.ico";
		}
	}
}
*/