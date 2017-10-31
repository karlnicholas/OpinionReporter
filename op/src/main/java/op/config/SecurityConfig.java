package op.config;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import op.services.UserService;

import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@ImportResource(value = "classpath:spring-security-context.xml")
class SecurityConfig {
	
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
    /**
     * Copy of default request matcher from
     * CsrfFilter$DefaultRequiresCsrfMatcher
     */
    return new RequestMatcher() {
      private Pattern allowedMethods = Pattern
        .compile("^(GET|HEAD|TRACE|OPTIONS)$");

      /*
       * (non-Javadoc)
       *
       * @see
       * org.springframework.security.web.util.matcher.RequestMatcher#
       * matches(javax.servlet.http.HttpServletRequest)
       */
      public boolean matches(HttpServletRequest request) {
        return !allowedMethods.matcher(request.getMethod()).matches();
      }
    };
  }
}