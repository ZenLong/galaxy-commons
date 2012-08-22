package com.saysth.commons.unit.utils;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import com.saysth.commons.utils.AESCoder;

public class AESCoderTest {
	@Test
	public void test() throws Exception {
		AESCoder coder = new AESCoder();
		String data = "AES数据";
		System.out.println("加密前数据:" + data);
		String hexString = coder.encrypt(data);
		System.out.println("加密后数据:" + hexString);
		Assert.assertEquals(data, coder.decrypt(hexString));

		String hashCode = String.valueOf(UUID.randomUUID().toString().hashCode());
		System.out.println("加密前数据:" + hashCode);
		hexString = coder.encrypt(hashCode);
		System.out.println("加密后数据:" + hexString);
		Assert.assertEquals(hashCode, coder.decrypt(hexString));
	}
}
