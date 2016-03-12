package com.cynosure.cynosure_jwt_server.filter;

import java.io.IOException;
import javax.servlet.Filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

 
public class CorsFilter implements Filter {
        private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        //TODO add if - if this request is HTTP
        System.out.println("Filtering on...........................................................");
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8383");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
   
        // Access-Control-Allow-Headers
	response.setHeader("Access-Control-Allow-Headers",
		"Origin, X-Requested-With, Content-Type, Accept, " + AUTH_HEADER_NAME);
        response.setHeader("Access-Control-Expose-Headers", "content-length, " + AUTH_HEADER_NAME);
        chain.doFilter(req, res);
    }
 
    public void init(FilterConfig filterConfig) {}
 
    public void destroy() {}

}
