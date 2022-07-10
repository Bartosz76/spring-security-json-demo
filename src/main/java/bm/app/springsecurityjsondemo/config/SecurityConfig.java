package bm.app.springsecurityjsondemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Configure (AuthenticationManagerBuilder) method configures the list of users in memory
 * (allows for the configuration of any number of users).
 */
@Configuration
@EnableWebSecurity(debug = true) //'"debug = true" for the development stage.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private RestAuthenticationSuccessHandler authenticationSuccessHandler;
    private RestAuthenticationFailureHandler authenticationFailureHandler;

    public SecurityConfig(RestAuthenticationSuccessHandler authenticationSuccessHandler, RestAuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public SecurityConfig(boolean disableDefaults, RestAuthenticationSuccessHandler authenticationSuccessHandler, RestAuthenticationFailureHandler authenticationFailureHandler) {
        super(disableDefaults);
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    //    @Override
//    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
//        builder.inMemoryAuthentication()
//                .withUser("user")
//                .password("password")
//                .roles("USER");
//    }

    /**
     * The instance of the filter needs to be configured so that it utilizes the default authentication manager
     * and I need to set my own implementation of successHandler and failureHandler to allow the disposal of
     * the redirection upon the log in.
     */
    @Bean
    public JsonObjectAuthenticationFilter authenticationFilter() throws Exception {
        JsonObjectAuthenticationFilter filter = new JsonObjectAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    /**
     * By default upon reaching any endpoint, I will be automatically redirected to the
     * logging form. I want to log in using a JSON sent by POST and receive an 401
     * unauthorized error instead of the redirection. Thus, the below method is
     * implemented to cancel the redirection and allow me to reach root context (/)
     * without having to log in.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); //Disabling the csrf token.
        http
                .authorizeRequests()
                .antMatchers("/").permitAll() //Allowing the access to root context (/) without logging.
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll() //Allowing the access the form without logging.
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                //The above two lines mean that upon reaching the secured endpoint, I will
                //get the 401 unauthorized error instead of being redirected to the logging form.
    }
}
