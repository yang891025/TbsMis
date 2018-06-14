package com.tbs.tbsmis.entity;

import java.io.Serializable;

public class WXGroupEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int id;
	private int count;

	public WXGroupEntity() {

	}

	public WXGroupEntity(int id, String name, int count) {
        this.id = id;
		this.name = name;
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
