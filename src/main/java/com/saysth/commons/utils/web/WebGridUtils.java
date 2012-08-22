/**
 * 
 */
package com.saysth.commons.utils.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.saysth.commons.dao.Page;

/**
 * @author
 * 
 */
public class WebGridUtils {
	public static final String EC_TABLE_ID = "ec";
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final String PARAM_PAGE_SIZE = "ps";
	public static final String PARAM_PAGE_NO = "pn";

	/**
	 * 根据request传递过来的参数填充页面参数对象
	 * 
	 * @param request
	 * @param page
	 */
	public static void fillPage(HttpServletRequest request, Page<?> page) {
		String pageSize = request.getParameter(PARAM_PAGE_SIZE);
		String pageNo = request.getParameter(PARAM_PAGE_NO);

		if (StringUtils.isBlank(pageSize))
			pageSize = "10";

		if (StringUtils.isBlank(pageNo))
			pageNo = "1";

		page.setPageNo(Integer.parseInt(pageNo));
		page.setPageSize(Integer.parseInt(pageSize));

	}

	private WebGridUtils() {
	}
}
