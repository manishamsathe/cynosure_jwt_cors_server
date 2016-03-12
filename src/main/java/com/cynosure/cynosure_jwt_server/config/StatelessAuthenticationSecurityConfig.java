package com.cynosure.cynosure_jwt_server.config;

import com.cynosure.cynosure_jwt_server.filter.CorsFilter;
import com.cynosure.cynosure_jwt_server.filter.StatelessAuthenticationFilter;
import com.cynosure.cynosure_jwt_server.filter.StatelessLoginFilter;
import com.cynosure.cynosure_jwt_server.service.TokenAuthenticationService;
import com.cynosure.cynosure_jwt_server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;

@EnableWebSecurity
@Configuration
@Order(2)
public class StatelessAuthenticationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    CorsFilter corsFilter;

    public StatelessAuthenticationSecurityConfig() {
        super(true);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .and()
                .anonymous()
                .and()
                .servletApi()
                .and()
                .authorizeRequests()
                    .antMatchers("/", "/home").permitAll()
                    .antMatchers("/favicon.ico").permitAll()
                    .antMatchers("/resources/**").permitAll()
                    .antMatchers(HttpMethod.OPTIONS, "/*/**").permitAll()                
                    .antMatchers(HttpMethod.POST, "/api/login").permitAll()
                    .antMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                    //all other request need to be authenticated
                    .anyRequest().hasRole("USER").and()
                .addFilterBefore(corsFilter, HeaderWriterFilter.class)
                // custom JSON based authentication by POST of {"username":"<name>","password":"<password>"} which sets the token header upon authentication
                .addFilterBefore(new StatelessLoginFilter("/api/login", tokenAuthenticationService, userDetailsService, authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                // custom Token based authentication based on the header previously given to the client
                .addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class);//   .addFilterBefore(corsFilter, HeaderWriterFilter.class);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
