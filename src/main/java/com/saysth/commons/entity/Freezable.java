package com.saysth.commons.entity;

/**
 * 可冻结接口
 * <p>
 * 实现此接口的实体，当isFrozen()返回true时，不可修改实体属性。
 * 
 * @author
 * 
 */
public interface Freezable {
	boolean isFreezed();
}
