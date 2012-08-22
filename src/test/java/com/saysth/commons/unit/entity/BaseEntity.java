package com.saysth.commons.unit.entity;

import org.springframework.data.annotation.Id;

import com.saysth.commons.entity.IdEntity;

/**
 * @author
 * 
 */
@SuppressWarnings("serial")
public class BaseEntity implements IdEntity<String> {
	@Id
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
