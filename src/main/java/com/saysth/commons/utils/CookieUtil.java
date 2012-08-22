package com.saysth.commons.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

/**
 * Cookie工具
 * 
 * @author
 * 
 */
public class CookieUtil {

	HttpServletRequest request;
	HttpServletResponse response;

	/**
	 * construtor with arguments
	 * 
	 * @param req HttpServletRequest object
	 * @param res HttpServletResponse object
	 */
	public CookieUtil(HttpServletRequest req, HttpServletResponse res) {
		request = req;
		response = res;
	}

	/**
	 * construtor without argument
	 */
	public CookieUtil() {
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
	}

	/**
	 * get cookie value by cookie name
	 * 
	 * @param name cookie name
	 * @return cookie value, if the cookie name does not exists, return null
	 */
	public String getCookieValue(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0)
			return null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name))
				return cookie.getValue();
		}
		return null;
	}

	/**
	 * get cookie value by cookie name
	 * 
	 * @param name cookie name
	 * @return cookie value, if the cookie name does not exists, return null
	 */
	public Cookie getCookie(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0)
			return null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name))
				return cookie;
		}
		return null;
	}

	/**
	 * add a session cookie to browser, the cookie will disappear after the browser close
	 * 
	 * @param name cookie name
	 * @param value cookie value
	 */
	public void addCookie(String name, String value) {
		addCookie(name, value, null, null, 0);
	}

	/**
	 * add a session cookie to browser, the cookie will disappear after the browser close
	 * 
	 * @param name cookie name
	 * @param value cookie value
	 * @param domain cookie domain
	 * @param path cookie path
	 * @param maxAge cookie max time to live
	 */
	public void addCookie(String name, String value, String domain, String path, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		if (domain != null)
			cookie.setDomain(domain);
		if (path != null)
			cookie.setPath(path);
		if (maxAge > 0)
			cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	/**
	 * delete specified cookie by cookie name
	 * 
	 * @param name cooke name
	 */
	public void removeCookie(String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	/**
	 * delete specified cookie by cookie name
	 * 
	 * @param name cooke name
	 */
	public void removeCookie(String name, String path) {
		Cookie cookie = new Cookie(name, "");
		if (path != null)
			cookie.setPath(path);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

}
