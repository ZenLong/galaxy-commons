package com.saysth.commons.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 * 
 */
public class URLUtils {
	private static Logger logger = LoggerFactory.getLogger(URLUtils.class);
	private static final Pattern ipPattern = Pattern
			.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
	private static final String domainPatternString1 = "aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel";
	private static final String domainPatternString2 = "ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw";
	private static final String domainPatternString = "[\\w-]+(\\.(" + domainPatternString1 + ")(\\.("
			+ domainPatternString2 + "))|\\.(" + domainPatternString1 + "|" + domainPatternString2 + "))$";
	private static final Pattern domainPattern = Pattern.compile(domainPatternString, Pattern.CASE_INSENSITIVE);
	private static final String HTTP_PROTOCAL = "http://";
	private static final String HTTPS_PROTOCAL = "https://";
	private static final String FTP_PROTOCAL = "ftp://";

	public static boolean isIpAddress(String str) {
		return ipPattern.matcher(str).matches();
	}

	public static boolean hasDomainName(String str) {
		Matcher matcher = domainPattern.matcher(str);
		return matcher.find();
	}

	public static String getDomainName(String str) {
		if (null == str || "".equals(str)) {
			return null;
		}
		String website = getWebsite(str);
		if (isIpAddress(website)) {
			return website;
		}
		Matcher matcher = domainPattern.matcher(website);
		return matcher.find() ? matcher.group() : str;
	}

	public static String getWebsite(String str) {
		String str1 = str.toLowerCase();
		int beginIndex = 0;
		if (str1.startsWith(HTTP_PROTOCAL) || str1.startsWith(HTTPS_PROTOCAL) || str1.startsWith(FTP_PROTOCAL)) {
			beginIndex = str1.startsWith(HTTP_PROTOCAL) ? HTTP_PROTOCAL.length()
					: str1.startsWith(HTTPS_PROTOCAL) ? HTTPS_PROTOCAL.length() : FTP_PROTOCAL.length();
		} else {
			beginIndex = str1.indexOf("://");
		}
		if (beginIndex < 0) {
			beginIndex = 0;
		}
		int endIndex = str1.indexOf("/", beginIndex);
		if (endIndex <= 0) {
			endIndex = str1.length();
		}
		String website = str1.substring(beginIndex, endIndex);
		website = website.split("@")[0];
		endIndex = website.indexOf(":");
		return endIndex > 0 ? website.substring(0, endIndex) : website;
	}

	public static String decode(String urlString) {
		String decodeCharset = "GBK";
		if (isUtf8Url(urlString)) {
			decodeCharset = "UTF-8";
		}
		try {
			return URLDecoder.decode(urlString, decodeCharset);
		} catch (UnsupportedEncodingException e) {
			logger.error("decodeCharset:" + decodeCharset + " urlString:" + urlString, e);
			return urlString;
		}
	}

	/**
	 * URL utf-8编码
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	/**
	 * 编码是否有效
	 * 
	 * @param text
	 * @return
	 */
	private static boolean isUtf8Charset(String text) {
		String sign = "";
		if (text.startsWith("%e"))
			for (int p = 0; p != -1;) {
				p = text.indexOf("%", p);
				if (p != -1)
					p++;
				sign += p;
			}
		return sign.equals("147-1");
	}

	/**
	 * 是否utf8Url编码
	 * 
	 * @param text
	 * @return
	 * @throws MalformedURLException
	 */
	public static boolean isUtf8Url(String text) {
		text = text.toLowerCase();
		int p = text.indexOf("%");
		if (p != -1 && text.length() - p >= 9) {
			text = text.substring(p, p + 9);
		}
		return isUtf8Charset(text);
	}

}
