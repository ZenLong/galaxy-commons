package com.saysth.commons.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.saysth.commons.utils.spring.SpringContextHolder;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private String sessionId = "";

	public CustomHttpServletRequestWrapper(String sessionId, HttpServletRequest request) {
		super(request);
		this.sessionId = sessionId;
	}

	public HttpSession getSession(boolean create) {
		if (SpringContextHolder.getBean("sessionManager") != null) {
			return new CustomHttpSessionWrapper(this.sessionId, super.getSession(create));
		} else {
			return super.getSession(create);
		}
	}

	public HttpSession getSession() {
		return new CustomHttpSessionWrapper(this.sessionId, super.getSession());
	}

}
