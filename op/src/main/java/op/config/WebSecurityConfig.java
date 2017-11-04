package op.config;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import op.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
//@ImportResource(value = "classpath:spring-security-context.xml")
/*
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:security="http://www.springframework.org/schema/security"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
http://www.springframework.org/schema/security
http://www.springframework.org/schema/security/spring-security-4.1.xsd">
	
	<security:http auto-config="true" disable-url-rewriting="true" use-expressions="true">
		<security:csrf request-matcher-ref="csrfMatcher" />
        <security:form-login login-page="/signin" authentication-failure-url="/signin?error=1"/>
		<security:logout logout-url="/logout" />
		<security:remember-me services-ref="rememberMeServices" key="remember-me-key"/>
		<security:intercept-url pattern="/" access="permitAll" />
        <security:intercept-url pattern="/favicon.ico" access="permitAll" />
        <security:intercept-url pattern="/resources/**" access="permitAll" />
		<security:intercept-url pattern="/signin" access="permitAll" />
		<security:intercept-url pattern="/signup" access="permitAll" />
		<security:intercept-url pattern="/about" access="permitAll" />
		<security:intercept-url pattern="/resetsend" access="permitAll" />
		<security:intercept-url pattern="/resetpassword" access="permitAll" />
		<security:intercept-url pattern="/opinions" access="permitAll" />
		<security:intercept-url pattern="/verify" access="permitAll" />
		<security:intercept-url pattern="/**" access="isAuthenticated()" />
	</security:http>
	
	<security:authentication-manager erase-credentials="true" >
		<security:authentication-provider user-service-ref="userService">
			<security:password-encoder ref="passwordEncoder" />
		</security:authentication-provider>
	</security:authentication-manager>

</beans> 
*/
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String[] allAccessUrls = {
			"/", 
			"/resources/**", 
			"/signin", 
			"/signup", 
			"/about", 
			"/resetsend", 
			"/resetpassword", 
			"/opinions", 
			"/verify", 
			"/favicon.ico"			
	};
/*
    @Override
    public void configure(WebSecurity web) throws Exception {
            web.ignoring()
            // Spring Security should completely ignore URLs starting with /resources/
            .antMatchers("/resources/**")
            .antMatchers("/signin")
            .antMatchers("/signup")
            .antMatchers("/about")
            .antMatchers("/resetsend")
            .antMatchers("/resetpassword")
            .antMatchers("/opinions")
            .antMatchers("/verify")
            .antMatchers("/favicon.ico");
    }
*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    		http
	    		.logout()                                                                
				.logoutUrl("/logout")                                                 
				.logoutSuccessUrl("/opinions")                                           
			.and()
			.authorizeRequests()                                                                
				.antMatchers(allAccessUrls)
					.permitAll()                  
				.antMatchers("/**")
					.hasRole("USER")                                      
					.anyRequest()
					.authenticated()                                                   
			.and()
    		.formLogin()
    			.loginPage("/signin") 
    				.permitAll();        
    }

    @Bean
	public UserDetailsService userDetailsService() {
		return new UserService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices("op-remember-me-key", userDetailsService());
	}
	
	/*    
	@Bean
	public UserService userService() {
		return new UserService();
	}

	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices("remember-me-key", userService());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

  @Profile("test")
  @Bean(name = "csrfMatcher")
  public RequestMatcher testCsrfMatcher() {
    return new RequestMatcher() {

      @Override
      public boolean matches(HttpServletRequest request) {
        return false;
      }
    };
  }

  @Profile("!test")
  @Bean(name = "csrfMatcher")
  public RequestMatcher csrfMatcher() {
    return new RequestMatcher() {
      private Pattern allowedMethods = Pattern
        .compile("^(GET|HEAD|TRACE|OPTIONS)$");

      public boolean matches(HttpServletRequest request) {
        return !allowedMethods.matcher(request.getMethod()).matches();
      }
    };
  }
*/
  
}