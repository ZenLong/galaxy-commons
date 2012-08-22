package com.saysth.commons.utils.reflection;

import org.apache.commons.beanutils.Converter;

public class EnumConverter implements Converter {

	public boolean isSupported(Class<?> c) {
		return c.isEnum();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object decode(Class type, Object value) {
		if (value == null)
			return null;
		return Enum.valueOf(type, value.toString());
	}

	@SuppressWarnings("rawtypes")
	protected Object encode(Object value) {
		if (value == null)
			return null;

		return getName((Enum) value);
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Enum> String getName(T value) {
		return value.name();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object convert(Class type, Object value) {
		return decode(type, value);
	}
}