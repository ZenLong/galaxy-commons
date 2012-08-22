package com.saysth.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class RegexUtils {
	private static final Pattern MOBILE_PATTERN = Pattern.compile("^0{0,1}(13[4-9]|15[7-9]|15[0-2]|18[7-9])[0-9]{8}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
	private static final Pattern USRENAME_PATTERN = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");

	public static boolean isMobileId(String modileId) {
		Matcher m = MOBILE_PATTERN.matcher(modileId);
		boolean b = m.matches();
		if (!b) {
			return false;
		}
		return true;
	}

	public static boolean isEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		Matcher m = EMAIL_PATTERN.matcher(email);
		boolean b = m.matches();
		if (b) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isUsername(String username) {
		Matcher m = USRENAME_PATTERN.matcher(username);
		boolean b = m.matches();
		if (!b) {
			return false;
		}
		return true;
	}
}
