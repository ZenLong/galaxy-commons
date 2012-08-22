package com.saysth.commons.html;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class HtmlDocumentHelper {
	public static final int DEFAULT_REPEAT_CONNECT = 2;// 重复连接次数
	public static final int MAX_REPEAT_CONNECT = 5;// 最大重复连接次数
	public static final int CONNECT_SLEEP_SECOND = 5;// 连接暂停秒数

	public static final String USER_AGENT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	public static final String ACCEPT_LANGUAGE = "zh-cn,zh;q=0.5";
	public static final String ACCEPT_ENCODING = "gzip,deflate";
	public static final String CONTENT_CHARSET = "UTF-8";
	public static final String CONNECTION = "Keep-Alive";

	private static final Logger logger = LoggerFactory.getLogger(HtmlDocumentHelper.class);

	public static String getAttribute(Node node, String attributeName) {
		NamedNodeMap attributes = node.getAttributes();
		return attributes.getNamedItem(attributeName).getNodeValue();
	}

	public static boolean hasAttribute(Node node, String attributeName) {
		return node.getAttributes().getNamedItem(attributeName) == null ? false : true;
	}

	public static String getValue(Node node) {
		return getAttribute(node, HtmlTagConstants.VALUE);
	}

	public static String getText(Node node) {
		return node.getTextContent();
	}

	public static String getInnerHtml(Node node) {
		return getInnerHtml(node, null);
	}

	public static String getInnerHtml(Node node, String encoding) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
			DOMSource source = new DOMSource(node);
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			Properties props = new Properties();
			if (encoding != null) {
				props.setProperty("encoding", encoding);
			}
			props.setProperty("method", "xml");
			props.setProperty("omit-xml-declaration", "yes");
			transformer.setOutputProperties(props);
			transformer.transform(source, result);
			sw.flush();
			// 去除第一层即自身的标签
			String html = sw.toString();
			int beginIndex = html.indexOf(">");
			int endIndex = html.lastIndexOf("<");
			if (beginIndex > -1 && endIndex > -1 && endIndex >= beginIndex + 1) {
				return html.substring(beginIndex + 1, endIndex);
			}
			return html;
		} catch (TransformerConfigurationException e) {
			logger.error("发生异常！", e);
		} catch (TransformerException e) {
			logger.error("发生异常！", e);
		} finally {
		}
		return null;
	}

	public static NodeList getNodeList(Node parentNode, String xPath) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		// xpath.setNamespaceContext(new HtmlNamespaceContext());
		NodeList result = null;
		try {
			XPathExpression expr = xpath.compile(xPath);
			result = (NodeList) expr.evaluate(parentNode, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			logger.error("发生异常！", e);
		}
		return result;
	}

	public static Node getNode(Node parentNode, String xPath) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node result = null;
		try {
			XPathExpression expr = xpath.compile(xPath);
			result = (Node) expr.evaluate(parentNode, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			logger.error("发生异常！", e);
		}
		return result;
	}

	public static String captureHtmlContent(HttpClient httpClient, String url) {
		return captureHtmlContent(httpClient, null, url, null, HtmlDocumentHelper.DEFAULT_REPEAT_CONNECT);
	}

	public static String captureHtmlContent(HttpClient httpClient, String url, String charset) {
		return captureHtmlContent(httpClient, null, url, charset, HtmlDocumentHelper.DEFAULT_REPEAT_CONNECT);
	}

	public static String captureHtmlContent(HttpClient httpClient, HttpContext context, String url) {
		return captureHtmlContent(httpClient, context, url, null, HtmlDocumentHelper.DEFAULT_REPEAT_CONNECT);
	}

	public static String captureHtmlContent(HttpClient httpClient, HttpContext context, String url, String charset) {
		return captureHtmlContent(httpClient, context, url, charset, HtmlDocumentHelper.DEFAULT_REPEAT_CONNECT);
	}

	public static HttpClient getHttpClient() {
		// 创建HttpClinet对象并设置参数
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setMaxTotal(100);
		HttpParams params = new BasicHttpParams();
		params.setParameter("http.useragent", USER_AGENT);
		// 设置Http连接超时为30秒
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// 设置字符集
		HttpProtocolParams.setContentCharset(params, CONTENT_CHARSET);
		HttpProtocolParams.setHttpElementCharset(params, CONTENT_CHARSET);
		// 设置cookie策略
		HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置自动重定向
		// HttpClientParams.setRedirecting(params, true);
		// 创建Http Client
		DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);
		// 支持压缩
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {

			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
			}

		});
		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(final HttpResponse response, final HttpContext context) throws HttpException,
					IOException {
				HttpEntity entity = response.getEntity();
				Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					HeaderElement[] codecs = ceheader.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new GzipDecompressingEntity(response.getEntity()));
							return;
						}
					}
				}
			}
		});
		return httpClient;
	}

	public static String captureHtmlContent(HttpContext context, String url, String charset, int repeatConnect) {
		return captureHtmlContent(getHttpClient(), context, url, charset, repeatConnect);
	}

	public static String captureHtmlContent(HttpEntityEnclosingRequestBase request, String charset) {
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			HttpClient httpClient = getHttpClient();
			response = httpClient.execute(request);
			// 判断页面返回状态判断是否进行转向抓取新链接
			int statusCode = response.getStatusLine().getStatusCode();
			// 判断访问的状态码
			if (statusCode == HttpStatus.SC_OK) {// 正常状态
				// 获取html页面内容
				entity = response.getEntity();
				// 处理响应的消息实体，如果有
				if (entity != null) {
					return EntityUtils.toString(entity, charset);
				}
			} else {
				logger.error("返回的HTTP状态:{}不正确。", statusCode);
			}
		} catch (IOException e) {
			logger.error("发生网络异常{},无法获取相关内容。", e, request.getURI());
		} finally {
			// release connection gracefully
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static String captureHtmlContent(HttpClient httpClient, HttpContext context, String url, String charset,
			int repeatConnect) {
		assert (repeatConnect <= HtmlDocumentHelper.MAX_REPEAT_CONNECT) : "重复连接次数不能超过"
				+ HtmlDocumentHelper.MAX_REPEAT_CONNECT + "次";
		HttpGet method = new HttpGet(url);
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpClient.execute(method, context);
			// 判断页面返回状态判断是否进行转向抓取新链接
			int statusCode = response.getStatusLine().getStatusCode();
			// 判断访问的状态码
			if (statusCode == HttpStatus.SC_OK) {// 正常状态
				// 获取html页面内容
				entity = response.getEntity();
				// 处理响应的消息实体，如果有
				if (entity != null) {
					String html = EntityUtils.toString(entity, charset);
					String redirectUrl = getRedirectUrlByHtmlContent(html);
					if (redirectUrl != null) {
						return captureHtmlContent(httpClient, context, redirectUrl, charset, repeatConnect);
					} else {
						return html;
					}
				}
			} else if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_SEE_OTHER)
					|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) { // 重定向
				// 获取重定向url
				Header locationHeader = response.getLastHeader("location");
				if (locationHeader != null) {
					// 根据重定向url获取重新html内容
					String redirectUrl = locationHeader.getValue();
					if (StringUtils.isNotBlank(redirectUrl)) {
						return captureHtmlContent(httpClient, context, redirectUrl, charset, repeatConnect);
					}
				}
			} else {
				logger.error("返回的HTTP状态:{}不正确。", statusCode);
			}
		} catch (IOException e) {
			if (repeatConnect > 0) {
				logger.error("发生网络异常{},无法获取相关内容,{}秒后程序会自动再重新抓取一次。", new Object[] { e,
						HtmlDocumentHelper.CONNECT_SLEEP_SECOND });
				try {
					Thread.sleep(HtmlDocumentHelper.CONNECT_SLEEP_SECOND * 1000);
				} catch (InterruptedException e1) {
				}
				return captureHtmlContent(httpClient, context, url, charset, repeatConnect - 1);
			} else {
				logger.error("发生网络异常{},无法获取相关内容。", e, url);
			}
		} finally {
			// release connection gracefully
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
			}
		}
		return null;
	}

	/**
	 * 从输入源获取字符串
	 * 
	 * @param is
	 *            输入源
	 * @param bufferSize
	 *            缓冲大小
	 * @param charset
	 *            字符集
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream is, int bufferSize, String charset) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[bufferSize];
		int i = -1;
		while ((i = is.read(buf)) != -1) {
			out.write(buf, 0, i);
		}
		return charset != null ? new String(out.toByteArray(), charset) : new String(out.toByteArray());
	}

	public static Document getDocument(String html, String charset) {
		if (StringUtils.isBlank(html)) {
			return null;
		}
		Document document = null;
		try {
			// 生成html parser
			DOMParser parser = new DOMParser();

			// 设置网页的默认编码
			if (StringUtils.isNotBlank(charset)) {
				parser.setProperty("http://cyberneko.org/html/properties/default-encoding", charset);
			}
			if (StringUtils.isNotBlank(html)) {
				// 去除xhtml的命名空间
				html = StringUtils.replace(html, "<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>");
				html = html.replaceAll("\t|\r|\n", "");// //去除字符串中的回车/换行符/制表符
				BufferedReader in = new BufferedReader(new StringReader(html));
				parser.parse(new InputSource(in));
				document = parser.getDocument();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return document;
	}

	public static String processHtml(String html) {
		// 替换&nbsp;为空格,因为DOMParser会把&nbsp;替换为?
		String result = StringUtils.replace(html, "&nbsp;", " ");
		// 去除xhtml的命名空间
		result = StringUtils.replace(html, "<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>");
		return result;
	}

	public static void write(Node node, File file, String encoding) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		FileOutputStream fos = null;
		try {
			transformer = tf.newTransformer();
			DOMSource source = new DOMSource(node);
			fos = new FileOutputStream(file);
			StreamResult result = new StreamResult(fos);
			Properties props = new Properties();
			props.setProperty("encoding", encoding);
			props.setProperty("method", "xml");
			props.setProperty("omit-xml-declaration", "yes");
			transformer.setOutputProperties(props);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void write(Node node, String file, String encoding) {
		write(node, new File(file), encoding);
	}

	public static String getParameter(String url, String paramName) {
		String queryString = getQueryString(url);
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		int beginIndex = queryString.indexOf(paramName);
		if (beginIndex > -1) {
			beginIndex = queryString.indexOf("=", beginIndex);
			if (beginIndex > -1) {
				beginIndex++;
				int endIndex = queryString.indexOf("&", beginIndex);
				endIndex = endIndex == -1 ? url.length() : endIndex;
				if (endIndex > beginIndex) {
					return queryString.substring(beginIndex, endIndex);
				}
			}
		}
		return null;
	}

	public static String getQueryString(String url) {
		int beginIndex = url.indexOf("?");
		return beginIndex > -1 ? url.substring(beginIndex + 1) : null;
	}

	public static String getRedirectUrlByHtmlContent(String html) {
		// <meta http-equiv=\"refresh\"
		// content=\"0;URL=http://ok.wo99.com/blistall.php?type=1&area=1\">"
		String regex = "^[\\s]*<meta\\s{1,}(http-equiv)[\\s]*=[\\s]*['|\"]?(refresh)['|\"]?[\\s]*(content)[\\s]*=[\\s]*['|\"]?([\\d])[\\s]*;[\\s]*(URL)[\\s]*=[\\s]*['|\"]?([^>]*)\\b[\\s]*['|\"]?[\\s]*(>)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(html);
		if (match.find() && match.groupCount() == 7) {
			return match.group(6);
		}
		return null;
	}

}
