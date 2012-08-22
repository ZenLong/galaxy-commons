package com.saysth.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StringRegexUtils {
	public static boolean isEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		Pattern p = Pattern
				.compile("^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,4}");
		Matcher m = p.matcher(email);
		boolean b = m.matches();
		if (b) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isUsername(String username) {
		if (StringUtils.isNumericSpace(username)) {
			return false;
		}
		Pattern p = Pattern.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w\\-\\.]+$");
		Matcher m = p.matcher(username);
		boolean b = m.matches();
		if (!b) {
			return false;
		}
		return true;
	}
}
