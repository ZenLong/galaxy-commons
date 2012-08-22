package com.saysth.commons.unit.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseURLKeyword {
	public static void main(String[] args) {
		String url = "http://www.google.co.kr/search?hl=en&q=%ED%95%9C%EA%B5%AD%EC%96%B4+&btnG=Google+Search&aq=f&oq=";
		System.out.println(ParseURLKeyword.getKeyword(url));
		System.out.println("");
		url = "http://www.google.cn/search?q=%E6%8F%90%E5%8F%96+%E6%90%9C%E7%B4%A2%E5%BC%95%E6%93%8E+%E5%85%B3%E9%94%AE%E5%AD%97&hl=zh-CN&newwindow=1&sa=2";
		System.out.println(ParseURLKeyword.getKeyword(url));
		System.out.println("");
		url = "http://www.google.com.tw/search?hl=zh-CN&q=%E6%B9%98%E9%8B%BC%E4%B8%AD%E5%9C%8B%E9%A6%99%E7%85%99&btnG=Google+%E6%90%9C%E7%B4%A2&aq=f&oq=";
		System.out.println(ParseURLKeyword.getKeyword(url));
		System.out.println("");
		url = "http://www.baidu.com/s?wd=%D6%D0%87%F8%D3%D0%BE%80%D8%9F%C8%CE%B9%AB%CB%BE";
		System.out.println(ParseURLKeyword.getKeyword(url));
		System.out.println("");
		url = "http://www.baidu.com/s?wd=%C6%F3%D2%B5%CD%C6%B9%E3";
		System.out.println(ParseURLKeyword.getKeyword(url));
		System.out.println("");
	}

	public static String getKeyword(String url) {
		String keywordReg = "(?:yahoo.+?[\\?|&]p=|openfind.+?query=|google.+?q=|lycos.+?query=|onseek.+?keyword=|search\\.tom.+?word=|search\\.qq\\.com.+?word=|zhongsou\\.com.+?word=|search\\.msn\\.com.+?q=|yisou\\.com.+?p=|sina.+?word=|sina.+?query=|sina.+?_searchkey=|sohu.+?word=|sohu.+?key_word=|sohu.+?query=|163.+?q=|baidu.+?wd=|soso.+?w=|3721\\.com.+?p=|Alltheweb.+?q=)([^&]*)";
		String encodeReg = "^(?:[\\x00-\\x7f]|[\\xfc-\\xff][\\x80-\\xbf]{5}|[\\xf8-\\xfb][\\x80-\\xbf]{4}|[\\xf0-\\xf7][\\x80-\\xbf]{3}|[\\xe0-\\xef][\\x80-\\xbf]{2}|[\\xc0-\\xdf][\\x80-\\xbf])+$";
		Pattern keywordPatt = Pattern.compile(keywordReg);
		StringBuffer keyword = new StringBuffer(20);
		Matcher keywordMat = keywordPatt.matcher(url);
		while (keywordMat.find()) {
			keywordMat.appendReplacement(keyword, "$1");
		}
		if (!keyword.toString().equals("")) {
			String keywordsTmp = keyword.toString().replace("http://www.", "");
			Pattern encodePatt = Pattern.compile(encodeReg);
			String unescapeString = ParseURLKeyword.unescape(keywordsTmp);
			Matcher encodeMat = encodePatt.matcher(unescapeString);
			String encodeString = "gbk";
			if (encodeMat.matches())
				encodeString = "utf-8";
			try {
				return URLDecoder.decode(keywordsTmp, encodeString);
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}
		return "";
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
}
