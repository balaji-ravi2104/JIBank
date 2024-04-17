package com.banking.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class APIFilter implements Filter {
	public void init(FilterConfig fConfig) throws ServletException {

	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {


		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String path = httpRequest.getPathInfo();

		System.out.println("In API Filter :" + path);
	
		chain.doFilter(request, response);
	}
}
