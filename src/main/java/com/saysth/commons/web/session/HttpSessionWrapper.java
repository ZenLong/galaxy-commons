package com.saysth.commons.web.session;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class HttpSessionWrapper implements HttpSession {
	public static final int SESSION_EXPIRED_SECEND = 24 * 60 * 60;// 24小时

	private HttpSession session;

	public HttpSessionWrapper(HttpSession session) {
		this.session = session;
	}

	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}

	public Enumeration<?> getAttributeNames() {
		return session.getAttributeNames();
	}

	public long getCreationTime() {
		return session.getCreationTime();
	}

	public String getId() {
		return session.getId();
	}

	public long getLastAccessedTime() {
		return session.getLastAccessedTime();
	}

	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	public ServletContext getServletContext() {
		return session.getServletContext();
	}

	public HttpSessionContext getSessionContext() {
		return session.getSessionContext();
	}

	public Object getValue(String name) {
		return session.getValue(name);
	}

	public String[] getValueNames() {
		return session.getValueNames();
	}

	public void invalidate() {
		session.invalidate();
	}

	public boolean isNew() {
		return session.isNew();
	}

	public void putValue(String name, Object value) {
		session.putValue(name, value);
	}

	public void removeAttribute(String name) {
		session.removeAttribute(name);
	}

	public void removeValue(String name) {
		session.removeValue(name);
	}

	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

	public void setMaxInactiveInterval(int interval) {
		session.setMaxInactiveInterval(interval);
	}

}
