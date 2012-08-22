package com.saysth.commons.web.support;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class RealIPFilter implements Filter {
	private String remoteAddrParamName = null;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (request instanceof HttpServletRequest) {
			chain.doFilter(new RealIPRequestWrapper((HttpServletRequest) request, remoteAddrParamName), response);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		String str1 = arg0.getInitParameter("remote_addr_param_name");
		if (StringUtils.isNotBlank(str1)) {
			remoteAddrParamName = str1;
		}
	}
}
