package com.saysth.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.saysth.commons.utils.reflection.ReflectionUtils;

/**
 * 集合操作方法集合.
 * 
 * 
 * @author
 */
public class CollectionUtils {

	private CollectionUtils() {
	}

	/**
	 * 提取集合中的对象的属性(通过Getter函数), 组合成List.
	 * 
	 * @param collection
	 *            来源集合.
	 * @param propertyName
	 *            要提取的属性名.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List extractElementPropertyToList(final Collection collection, final String propertyName) {
		List list = new ArrayList<Object>();

		try {
			for (Object obj : collection) {
				list.add(PropertyUtils.getProperty(obj, propertyName));
			}
		} catch (Exception e) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(e);
		}

		return list;
	}

	/**
	 * 提取集合中的对象的属性(通过Getter函数), 组合成由分割符分隔的字符串.
	 * 
	 * @param collection
	 *            来源集合.
	 * @param propertyName
	 *            要提取的属性名.
	 * @param separator
	 *            分隔符.
	 */
	public static String extractElementPropertyToString(final Collection<?> collection, final String propertyName,
			final String separator) {
		List<?> list = extractElementPropertyToList(collection, propertyName);
		return StringUtils.join(list, separator);
	}

	/**
	 * 判断集合是否为空
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.size() == 0 ? true : false;
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.size() == 0 ? true : false;
	}

	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	public static String toString(Map<?, ?> map) {
		Assert.notNull(map);
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) it.next();
			toString(e.getKey(), e.getValue(), sb);
			if (it.hasNext()) {
				sb.append(',');
			}
		}
		return sb.toString();
	}

	protected static void toString(Object name, Object value, StringBuilder sb) {
		sb.append('"');
		sb.append(String.valueOf(name));
		sb.append('"');
		sb.append(':');
		if (value == null) {
			sb.append("null");
		} else if (value instanceof Boolean) {
			sb.append(String.valueOf(value));
		} else if (value instanceof Number) {
			sb.append(String.valueOf(value));
		} else if (value instanceof Date) {
			sb.append(DateUtils.format((Date) value));
		} else if (value instanceof Calendar) {
			sb.append(DateUtils.format((Calendar) value));
		} else if (value instanceof Character) {
			sb.append("'");
			sb.append(String.valueOf(value));
			sb.append("'");
		} else {
			if (HibernateUtils.isEntity(value)) {
				Class<?> clazz = AopUtils.getTargetClass(value);
				Method method = HibernateUtils.getIdMethod(clazz);
				sb.append('{');
				if (method != null) {
					String idName = StringUtils.uncapitalize(method.getName().substring(3));
					Object idValue = ReflectionUtils.invokeGetterMethod(value, method);
					toString(idName, idValue, sb);
				} else {
					Field field = HibernateUtils.getIdFiled(clazz);
					if (field != null) {
						String idName = field.getName();
						Object idValue = ReflectionUtils.getFieldValue(value, field);
						toString(idName, idValue, sb);
					}
				}
				sb.append('}');
			} else {
				sb.append('"').append(StringEscapeUtils.escapeJava(String.valueOf(value))).append('"');
			}
		}
	}

	public static <T> Map<String, T> getSubMapStartingWith(Map<String, T> map, String prefix) {
		Assert.notNull(map, "map must not be null");
		if (prefix == null) {
			prefix = "";
		}
		Map<String, T> subMap = new TreeMap<String, T>();
		for (Map.Entry<String, T> entry : map.entrySet()) {
			String key = entry.getKey();
			if ("".equals(prefix) || key.startsWith(prefix)) {
				String unprefixed = key.substring(prefix.length());
				T value = entry.getValue();
				if (value != null) {
					subMap.put(unprefixed, value);
				}
			}
		}
		return subMap;
	}
}
