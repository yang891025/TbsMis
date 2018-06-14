package com.tbs.chat.entity;

import java.io.Serializable;

public class TreeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public TreeEntity() {

	}

	public TreeEntity(String label, String parent,int flag) {
		super();
		this.label = label;
		this.parent = parent;
		this.flag = flag;
	}

	private String label;
	private String parent;
	private int flag;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
