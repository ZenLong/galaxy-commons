package com.saysth.commons.util;

import org.apache.commons.codec.digest.DigestUtils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 加密工具
 * 
 * @author
 * 
 */
@SuppressWarnings("restriction")
public class EncryptUtil {

	/**
	 * encrypt the input type by MD5
	 * 
	 * @param input
	 *            input string
	 * @return md5 encrypted string
	 */
	public static String md5(String input) {
		return DigestUtils.md5Hex(input.getBytes());
	}

	/**
	 * encrypt the input type by SHA
	 * 
	 * @param input
	 *            input string
	 * @return SHA encrypted string
	 */
	public static String sha(String input) {
		return DigestUtils.shaHex(input.getBytes());
	}

	/**
	 * encrypt the input type by SHA256
	 * 
	 * @param input
	 *            input string
	 * @return SHA256 encrypted string
	 */
	public static String sha256(String input) {
		return DigestUtils.sha256Hex(input.getBytes());
	}

	/**
	 * encrypt the input type by SHA384
	 * 
	 * @param input
	 *            input string
	 * @return SHA384 encrypted string
	 */
	public static String sha384(String input) {
		return DigestUtils.sha384Hex(input.getBytes());
	}

	/**
	 * encrypt the input type by SHA512
	 * 
	 * @param input
	 *            input string
	 * @return SHA512 encrypted string
	 */
	public static String sha512(String input) {
		return DigestUtils.sha512Hex(input.getBytes());
	}

	/**
	 * base64 encode the input string
	 * 
	 * @param input
	 *            input string
	 * @return encoded string
	 */
	public static String base64Encode(String input) {
		return Base64.encode(input.getBytes()).trim();
	}

	/**
	 * base64 decode the input string
	 * 
	 * @param input
	 *            input string
	 * @return decoded string
	 */
	public static String base64Decode(String input) {
		return new String(Base64.decode(input));
	}

	public static void main(String[] args) {
		System.out.println(md5("111111"));
		System.out.println(sha("111"));
		System.out.println(md5("111111"));
		System.out.print(base64Encode("96e79218965eb72c92a549dd5a330112"));
	}
}
