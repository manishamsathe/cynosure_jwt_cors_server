package com.cynosure.cynosure_jwt_server.filter;

import com.cynosure.cynosure_jwt_server.entity.User;
import com.cynosure.cynosure_jwt_server.entity.UserAuthentication;
import com.cynosure.cynosure_jwt_server.service.TokenAuthenticationService;
import com.cynosure.cynosure_jwt_server.service.UserDetailsService;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final TokenAuthenticationService tokenAuthenticationService;
    private final UserDetailsService userDetailsService;

    public StatelessLoginFilter(String urlMapping, TokenAuthenticationService tokenAuthenticationService,
            UserDetailsService userDetailsService, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(urlMapping));
        this.userDetailsService = userDetailsService;
        this.tokenAuthenticationService = tokenAuthenticationService;
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        final User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
        final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());
        return getAuthenticationManager().authenticate(loginToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // Lookup the complete User1 object from the database and create an Authentication for it
        final User authenticatedUser = userDetailsService.loadUserByUsername(authentication.getName());
        final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

        // Add the custom token as HTTP header to the response
        tokenAuthenticationService.addAuthentication(response, userAuthentication);

        // Add the authentication to the Security context
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //There are total 4 exceptions related to 4 flags and InternalAuthenticationServiceException is thrown with different message
        //Order of exception is as follows
        //Locked - Disabled - account expired - credentials expired
        //Also one more exception which can be handled BadCredentialsException & UsernameNotFoundException
        //below one example is given incase different status code needs to be sent for exception
        String str = exception.getMessage(); // this gives different msg for each exception
        if (exception instanceof InternalAuthenticationServiceException) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
