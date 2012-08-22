package com.saysth.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 字符串中连续相同字符出现的最大次数
 * 
 * @author
 */
public class StringSubRepeatUtil {

	private static Map<Character, List<Integer>> charsMap = new HashMap<Character, List<Integer>>();

	private static String string;

	private static List<Store> stores = new ArrayList<Store>(0);

	private static int max = 1;

	public static int find(int number) {
		string = String.valueOf(number);
		countChar();
		Iterator<Entry<Character, List<Integer>>> it = charsMap.entrySet().iterator();
		for (; it.hasNext();) {
			List<Integer> value = it.next().getValue();
			// System.out.println(value);
			findIncrease(value);
		}
		charsMap.clear();
		max = 1;
		if (stores.iterator().hasNext()) {
			int count = stores.iterator().next().count;
			stores.clear();
			return count;
		} else {
			return 0;
		}
	}

	public static void countChar() {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			List<Integer> indexes = charsMap.get(c);
			if (indexes == null) {
				indexes = new ArrayList<Integer>();
				charsMap.put(c, indexes);
			}
			indexes.add(i);
		}
	}

	public static void findIncrease(List<Integer> list) {
		// int max = 1;
		// [3, 5, 6, 8, 11, 14, 17, 18, 20, 21]，假如一共出现了10个该字符
		for (int i = 0; i < list.size(); i++) {
			// 比如外层标志为第0个，这里代表的数字是3
			// 那么内层循环的从第i个，直到第(10-1 + i)/2 = 4(即内层循环小于等于4);
			for (int j = i + 1; j <= list.size() - 1 + i / 2; j++) {
				// 两者在字符串中的位置差
				int offsetInString = 1;// list.get(j) - list.get(i);
				// 两者在列表中的位置差
				int offset = j - i;
				// 本身算一次
				int count = 1;
				boolean same = true;
				while (same && i + offset * count < list.size()) {
					if (list.get(i + offset * count) == list.get(i) + count * offsetInString) {
						count++;
					} else {
						same = false;
					}
				}
				// 如果只有一次，则继续
				if (count == 1) {
					continue;
				}

				int result = check(list.get(i), offsetInString, count);
				if (result > max) {
					stores.clear();
					stores.add(new Store(list.get(i), offsetInString, result));
					max = result;
				} else if (result == max) {
					stores.add(new Store(list.get(i), offsetInString, result));
				}
			}
		}
	}

	public static int check(int start, int offset, int count) {
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < offset; j++) {
				if (start + j + offset * i >= string.length()
						|| string.charAt(start + j) != string.charAt(start + j + offset * i)) {
					count = i;
					break;
				}
			}
		}
		return count;
	}
}

class Store {
	int start;
	int offset;
	int count;

	public Store(int start, int offset, int count) {
		this.start = start;
		this.offset = offset;
		this.count = count;
	}
}
