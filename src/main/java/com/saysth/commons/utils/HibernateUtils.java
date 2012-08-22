package com.saysth.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.beanutils.PropertyUtils;

public class HibernateUtils {
	/**
	 * 根据对象ID集合,整理合并集合.
	 * 
	 * http请求发送变更后的子对象id列表时，hibernate不适合删除原来子对象集合再创建一个全新的集合 需采用以下整合的算法：
	 * 在源集合中删除不在ID集合中的元素,创建在ID集合中的元素并对其ID属性赋值并添加到源集合中. 默认对象的id属性名为"id".
	 * 
	 * @param collection
	 *            源对象集合
	 * @param retainIds
	 *            目标集合
	 * @param clazz
	 *            集合中对象的类型
	 */
	public static <T, ID> void mergeByCheckedIds(Collection<T> collection, Collection<ID> checkedIds, Class<T> clazz)
			throws Exception {
		mergeByCheckedIds(collection, checkedIds, "id", clazz);
	}

	/**
	 * 根据对象ID集合,整理合并集合.
	 * 
	 * http请求发送变更后的子对象id列表时，hibernate不适合删除原来子对象集合再创建一个全新的集合 需采用以下整合的算法：
	 * 在源集合中删除不在ID集合中的元素,创建在ID集合中的元素并对其ID属性赋值并添加到源集合中.
	 * 
	 * @param collection
	 *            源对象集合
	 * @param retainIds
	 *            目标集合
	 * @param idName
	 *            对象中ID的属性名
	 * @param clazz
	 *            集合中对象的类型
	 */
	public static <T, ID> void mergeByCheckedIds(Collection<T> collection, Collection<ID> checkedIds, String idName,
			Class<T> clazz) throws Exception {

		if (checkedIds == null) {
			collection.clear();
			return;
		}

		Iterator<T> it = collection.iterator();

		while (it.hasNext()) {
			T obj = it.next();
			if (checkedIds.contains(PropertyUtils.getProperty(obj, idName))) {
				checkedIds.remove(PropertyUtils.getProperty(obj, idName));
			} else {
				it.remove();
			}
		}

		for (ID id : checkedIds) {
			T obj = clazz.newInstance();
			PropertyUtils.setProperty(obj, idName, id);
			collection.add(obj);
		}
	}

	/**
	 * 是否实体对象
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isEntity(Object object) {
		Class<?> clazz = AopUtils.getTargetClass(object);
		return isEntityClass(clazz);
	}

	/**
	 * 是否实体类
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isEntityClass(Class<?> clazz) {
		return clazz.isAnnotationPresent(javax.persistence.Entity.class)
				|| clazz.isAnnotationPresent(org.hibernate.annotations.Entity.class) ? true : false;
	}

	public static Field getIdFiled(Class<?> clazz) {
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(javax.persistence.Id.class)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * 取实体类的id方法
	 * 
	 * @param object
	 * @return
	 */
	public static Method getIdMethod(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(javax.persistence.Id.class)) {
				return method;
			}
		}

		return null;
	}

	private HibernateUtils() {

	}
}
