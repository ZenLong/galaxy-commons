package com.saysth.commons.unit.utils;

import java.util.Date;

import org.junit.Test;

import com.saysth.commons.utils.DateUtils;

public class DateUtilsTest {
	@Test
	public void testDayDiff() {
		Date startDate = DateUtils.parseDate("2012-03-01", "yyyy-MM-dd");
		Date endDate = DateUtils.parseDate("2012-03-31", "yyyy-MM-dd");

		System.out.println(DateUtils.dayDiff(startDate, startDate));
		System.out.println(DateUtils.dayDiff(startDate, endDate));
		endDate = DateUtils.parseDate("2012-03-31 23:59:59", "yyyy-MM-dd hh:mm:ss");
		System.out.println(DateUtils.dayDiff(startDate, endDate));
	}

	@Test
	public void testGetDate() throws InterruptedException {
		Date date1 = new Date();
		Thread.sleep(1654);
		Date date2 = new Date();
		System.out.println(DateUtils.getStartTime(date1));
		System.out.println(DateUtils.getStartTime(date2));
		System.out.println(DateUtils.getStartTime(date1).getTime());
		System.out.println(DateUtils.getStartTime(date2).getTime());
		Date date3 = new Date();
		Thread.sleep(1654);
		Date date4 = new Date();
		System.out.println(DateUtils.getEndTime(date3));
		System.out.println(DateUtils.getEndTime(date4));
		System.out.println(DateUtils.getEndTime(date3).getTime());
		System.out.println(DateUtils.getEndTime(date4).getTime());
	}

	@Test
	public void testDateUtils() {
		Date date = new Date();
		System.out.println("当前小时数：" + DateUtils.getHours(date));
		System.out.println("当前分钟数：" + DateUtils.getMinutes(date));

	}
}
