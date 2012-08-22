package com.saysth.commons.entity;

import java.util.List;

/**
 * 树接口
 * <p>
 * 实现此接口的实体，当isFrozen()返回true时，不可修改实体属性。
 */
public interface Tree<T> {
	T getParent();

	List<T> getChildren();
}
