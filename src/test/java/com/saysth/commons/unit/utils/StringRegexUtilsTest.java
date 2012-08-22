package com.saysth.commons.unit.utils;

import java.net.MalformedURLException;

import org.junit.Test;

import com.saysth.commons.utils.StringRegexUtils;

public class StringRegexUtilsTest {

	/**
	 * 测试邮件格式校验功能
	 */
	@Test
	public void testEmail() throws InterruptedException, MalformedURLException {
		String email = "zhq@xyz.com";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "zhq@xyz.comasd";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "@xyz.com";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "z.hq";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "z.hq@xyz";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "zhq@xyz";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "z_hq@xyz.com";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "z-hq@xyz.com";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "zhq@xyz-xyz.com";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "z-hq@xyz-xyz.com";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "z-hq@xyz_xyz.com";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
		email = "z-hq@xyz.com.cn";
		System.out.println(email + ": " + StringRegexUtils.isEmail(email));
	}

}
