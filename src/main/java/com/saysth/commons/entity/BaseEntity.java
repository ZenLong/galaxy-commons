package com.saysth.commons.entity;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * 统一定义id的entity基类，同时加上了Morphia和JPA的注解以支持切换
 * 
 * 基类统一定义id的属性名称、数据类型、列名映射及生成策略. 子类可重载getId()函数重定义id的列名映射和生成策略.
 * 
 * @author
 */
@SuppressWarnings("serial")
@com.google.code.morphia.annotations.Polymorphic
@javax.persistence.MappedSuperclass
public abstract class BaseEntity implements IdEntity<String>, Serializable {
	@com.google.code.morphia.annotations.Id
	private String id;

	@javax.persistence.Id
	@javax.persistence.Column(length = 32, nullable = false)
	@javax.persistence.GeneratedValue(generator = "hibernate-uuid.hex")
	@org.hibernate.annotations.GenericGenerator(name = "hibernate-uuid.hex", strategy = "uuid.hex")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		// 如id值为空，则赋值为null
		this.id = StringUtils.isBlank(id) ? null : id;
	}

	@javax.persistence.Transient
	public boolean isNew() {
		return id == null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@");
		sb.append("id=").append(getId());
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BaseEntity))
			return false;
		final BaseEntity other = (BaseEntity) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
			else
				return super.equals(obj);
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		if (getId() == null) {
			return super.hashCode();
		} else {
			return this.getId().hashCode();
		}
	}

	/**
	 * 用于记录操作日志
	 * 
	 * @return
	 */
	public String toLogString() {
		return "id:" + getId();
	}

}
