package com.saysth.commons.entity;

import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Polymorphic;

/**
 * 不可删实体
 * 
 * @author
 * 
 */
@SuppressWarnings("serial")
@Polymorphic
public abstract class UnDeletedEntity extends BaseEntity implements UnDeletable {

	@Indexed
	private boolean deleted = false;

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * 用于记录操作日志
	 */
	public String toLogString() {
		return super.toLogString() + ",deleted:" + isDeleted();
	}

}
