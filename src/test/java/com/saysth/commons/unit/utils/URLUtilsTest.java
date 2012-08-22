package com.saysth.commons.unit.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import org.junit.Test;

import com.saysth.commons.utils.URLUtils;

public class URLUtilsTest {

	/**
	 * 从传入的复杂URL中截取域名
	 */
	@Test
	public void testGetDoaminName() throws InterruptedException, MalformedURLException {
		String url = "http://192.168.0.123:8080/abcd/ef.action";
		System.out.println(URLUtils.getDomainName(url));
		url = "http://www.saysth.com:8080/abcd/ef.action";
		System.out.println(URLUtils.getDomainName(url));
		url = "www.baidu.com";
		System.out.println(URLUtils.getDomainName(url));
		url = "http://www.saysth.com:8080@user:abcd/efgh/ij.action";
		System.out.println(URLUtils.getDomainName(url));
		url = "http://localhost:8080@user:asdf/asdfa/dd.action";
		System.out.println(URLUtils.getDomainName(url));
		url = "www.baidu.comabc";
		System.out.println(URLUtils.getDomainName(url));
	}

	@Test
	public void testUrlDecode() throws UnsupportedEncodingException {
		String url;
		System.out.println(URLUtils.encode("中"));
		System.out.println(URLUtils.encode("华人民共和国"));
		System.out.println(URLUtils.encode("人民共和国"));
		System.out.println(URLUtils.encode("民共和国"));
		System.out.println(URLUtils.encode("共和国"));
		url = "http://www.abc.com/asdf.action?name=" + URLUtils.encode("中");
		if (URLUtils.isUtf8Url(url)) {
			System.out.println(URLDecoder.decode(url, "UTF-8"));
		} else {
			System.out.println(URLDecoder.decode(url, "GBK"));
		}
		url = "http://www.subeihm.com/price-search.asp?word=%D1%A9%CB%C9";

		if (URLUtils.isUtf8Url(url)) {
			System.out.println(URLDecoder.decode(url, "UTF-8"));
		} else {
			System.out.println(URLDecoder.decode(url, "GBK"));
		}
		url = "http://www.baidu.com/baidu?word=%D6%D0%B9%FA%B4%F3%B0%D9%BF%C6%D4%DA%CF%DF%C8%AB%CE%C4%BC%EC%CB%F7&tn=myie2dg";
		if (URLUtils.isUtf8Url(url)) {
			System.out.println(URLDecoder.decode(url, "UTF-8"));
		} else {
			System.out.println(URLDecoder.decode(url, "GBK"));
		}
		url = "http://www.subeihm.com/price-search.asp?word=%E7%BA%A2%E5%8F%B6%E7%9F%B3%E6%A5%A0";
		System.out.println(URLDecoder.decode(url, "UTF-8"));
		if (URLUtils.isUtf8Url(url)) {
			System.out.println(URLDecoder.decode(url, "UTF-8"));
		} else {
			System.out.println(URLDecoder.decode(url, "GBK"));
		}

	}
}
