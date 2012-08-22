package com.saysth.commons.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装分页和排序查询的结果.
 * 
 * 
 * @param <T> - Page中的记录类型.
 * 
 * @author
 */
@SuppressWarnings("serial")
public class Page<T> extends QueryParameter {

	private List<T> result = new ArrayList<T>(0);

	private long totalCount = 0;

	public Page() {
	}

	public Page(int pageSize) {
		setPageSize(pageSize);
	}

	public Page(int pageSize, boolean autoCount) {
		setPageSize(pageSize);
		setAutoCount(autoCount);
	}

	/**
	 * 页内的数据列表.
	 */
	public List<T> getResult() {
		return result;
	}

	@SuppressWarnings("unchecked")
	public <M extends T> void setResult(List<M> result) {
		this.result = (List<T>) result;
	}

	/**
	 * 总记录数.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 计算总页数.
	 */
	public int getTotalPages() {
		if (totalCount == 0)
			return 0;

		int count = (int) (totalCount / pageSize);
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNext() {
		return (pageNo + 1 <= getTotalPages());
	}

	/**
	 * 返回下页的页号,序号从1开始.
	 */
	public int getNextPage() {
		if (isHasNext())
			return pageNo + 1;
		else
			return pageNo;
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPre() {
		return (pageNo - 1 >= 1);
	}

	/**
	 * 返回上页的页号,序号从1开始.
	 */
	public int getPrePage() {
		if (isHasPre())
			return pageNo - 1;
		else
			return pageNo;
	}

}
