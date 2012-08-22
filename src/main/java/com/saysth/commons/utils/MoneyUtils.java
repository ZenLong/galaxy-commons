package com.saysth.commons.utils;

public class MoneyUtils {
	/**
	 * 四舍五入
	 * 
	 * @param money
	 * @return
	 */
	public static double round(double money) {
		return Math.round(money * 100) / 100D;
	}

	public static final void main(String[] args) {
		System.out.println(round(10.94888888));
		System.out.println(round(10.945));
		System.out.println(round(10.944));
		System.out.println(round(10.943));
		System.out.println(round(10.942));
		System.out.println(round(10.941));
	}
}
