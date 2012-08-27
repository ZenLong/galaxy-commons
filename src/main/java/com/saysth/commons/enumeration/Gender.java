package com.saysth.commons.enumeration;

import java.util.Map;

import org.springframework.util.Assert;

/**
 * 性别枚举
 * 
 * @author KelvinZ
 * 
 */
public enum Gender {
	UNSPEC("未指定"), // 未指定
	MALE("男"), // 男
	FEMALE("女"); // 女

	@SuppressWarnings("rawtypes")
	private static Map<Enum, String> cache;
	private String label;

	private Gender(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * 把自身和标签放到缓存，方便用自定义标签处理
	 * @param cache
	 */
	@SuppressWarnings("rawtypes")
	public static void cache(Map<Enum, String> cache) {
		Assert.notNull(cache);
		Gender.cache = cache;
		Gender[] enums = values();
		for (Gender entry : enums)
			Gender.cache.put(entry, entry.getLabel());
	}

	// 获取存放缓存
	@SuppressWarnings("rawtypes")
	public static Map<Enum, String> getCache() {
		return Gender.cache;
	}

}
