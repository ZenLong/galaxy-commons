package com.saysth.commons.web.session;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import com.saysth.commons.utils.spring.SpringContextHolder;

public class CustomHttpSessionWrapper extends HttpSessionWrapper {
	private static final String SESSION_MGR = "sessionManager";

	private String sessionId = "";
	private SessionManager sessionManager = null;

	public CustomHttpSessionWrapper(String sessionId, HttpSession session) {
		super(session);
		this.sessionId = sessionId;
		sessionManager = (SessionManager) SpringContextHolder.getBean(SESSION_MGR);
	}

	@Override
	public Object getAttribute(String name) {
		return sessionManager.getAttribute(sessionId, name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		sessionManager.setAttribute(sessionId, name, value);
	}

	@Override
	public void removeAttribute(String name) {
		sessionManager.removeAttribute(sessionId, name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return sessionManager.getAttributeNames(sessionId);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		sessionManager.removeSession(sessionId);
	}

}
