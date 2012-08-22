package com.saysth.commons.unit.orm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.saysth.commons.dao.Order;
import com.saysth.commons.dao.Page;

public class PageTest {
	private Page<Object> page;

	@Before
	public void setUp() {
		page = new Page<Object>();
	}

	/**
	 * 检测Page的默认值契约.
	 */
	@Test
	public void defaultParameter() {
		assertEquals(1, page.getPageNo());
		assertEquals(-1, page.getPageSize());
		assertEquals(-1, page.getTotalCount());
		assertEquals(-1, page.getTotalPages());
		assertEquals(true, page.isAutoCount());

		page.setPageNo(-1);
		assertEquals(1, page.getPageNo());

		assertNull(page.getOrders());

		assertEquals(false, page.isOrderBySetted());
		page.addOrder(Order.asc("Id"));
		assertEquals(false, page.isOrderBySetted());
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOrderParameter() {
		page.addOrder(Order.asc("abcd"));
	}

	@Test
	public void getFirst() {
		page.setPageSize(10);

		page.setPageNo(1);
		assertEquals(1, page.getFirst());
		page.setPageNo(2);
		assertEquals(11, page.getFirst());

	}

	@Test
	public void getTotalPages() {
		page.setPageSize(10);

		page.setTotalCount(1);
		assertEquals(1, page.getTotalPages());

		page.setTotalCount(10);
		assertEquals(1, page.getTotalPages());

		page.setTotalCount(11);
		assertEquals(2, page.getTotalPages());
	}

	@Test
	public void hasNextOrPre() {
		page.setPageSize(10);
		page.setPageNo(1);

		page.setTotalCount(9);
		assertEquals(false, page.isHasNext());

		page.setTotalCount(11);
		assertEquals(true, page.isHasNext());

		page.setPageNo(1);
		assertEquals(false, page.isHasPre());

		page.setPageNo(2);
		assertEquals(true, page.isHasPre());
	}

	@Test
	public void getNextOrPrePage() {
		page.setPageNo(1);
		assertEquals(1, page.getPrePage());

		page.setPageNo(2);
		assertEquals(1, page.getPrePage());

		page.setPageSize(10);
		page.setTotalCount(11);
		page.setPageNo(1);
		assertEquals(2, page.getNextPage());

		page.setPageNo(2);
		assertEquals(2, page.getNextPage());
	}
}
