package com.saysth.commons.web.support;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class RealIPRequestWrapper extends HttpServletRequestWrapper {
	private String remoteAddrParamName;

	public RealIPRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public RealIPRequestWrapper(HttpServletRequest request, String remoteAddrParamName) {
		super(request);
		this.remoteAddrParamName = remoteAddrParamName;
	}

	@Override
	public String getRemoteAddr() {
		if (remoteAddrParamName != null) {
			String realIP = super.getHeader(remoteAddrParamName);
			return realIP != null ? realIP.split(",")[0] : super.getRemoteAddr();
		} else {
			return super.getRemoteAddr();
		}
	}

	@Override
	public String getRemoteHost() {
		try {
			return InetAddress.getByName(getRemoteAddr()).getHostName();
		} catch (UnknownHostException e) {
			return getRemoteAddr();
		}
	}
}