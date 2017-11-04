package op.config;

import op.services.UserService;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@EnableWebSecurity
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