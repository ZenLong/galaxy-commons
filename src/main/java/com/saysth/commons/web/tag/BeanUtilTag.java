package com.saysth.commons.web.tag;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import net.sf.cglib.proxy.Enhancer;

public class BeanUtilTag {

	public static boolean instanceOf(Object o, String className) {
		if (o == null || className == null) {
			return false;
		}
		try {
			Class<?> clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
			if (isProxy(o.getClass())) {
				String objClassName = o.toString().split("@")[0];
				Class<?> objClass = Class.forName(objClassName, false, Thread.currentThread().getContextClassLoader());
				return clazz.isAssignableFrom(objClass);
			} else {
				return clazz.isInstance(o);
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean hasProperty(Object o, String propertyName) {
		if (o == null || propertyName == null) {
			return false;
		}
		BeanInfo beanInfo;
		try {
			beanInfo = java.beans.Introspector.getBeanInfo(o.getClass());
		} catch (IntrospectionException e) {
			return false;
		}
		for (final PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {

			if (propertyName.equals(pd.getName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isProxy(Class<?> beanClass) {
		return Enhancer.isEnhanced(beanClass);
	}

	public static Class<?> getBeanClass(Object bean) {
		Class<?> beanClass = bean.getClass();
		if (isProxy(beanClass)) {
			// CGLIB specific
			return beanClass.getSuperclass();
		}
		return beanClass;
	}
}
