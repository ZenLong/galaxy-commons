package com.saysth.commons.utils;

import java.util.Random;

/**
 * 随机产生一个邮箱验证码
 * 
 * @author
 * 
 */
public class CreateVerifyCodeUtil {
	private static Random rnd = new Random(System.currentTimeMillis());

	/**
	 * 创建一个随机验证码
	 * 
	 * @return 验证码串
	 */
	public static String createVerifyCode() {
		return Long.toHexString(12300000000L + rnd.nextInt(100000000));
	}

	public static void main(String[] args) {
		System.out.println(createVerifyCode());
	}

}