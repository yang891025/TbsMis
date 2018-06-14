package com.tbs.tbsmis.util;

import java.io.Serializable;

public class DiskInfoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String length;
	private String createTime;
	private boolean isDirectory;
	private boolean isReadonly;
	private String path;
	private String parent;
	private String flag;

	public DiskInfoEntity() {

	}

	public DiskInfoEntity(String name, String length, String createTime,
			boolean isDirectory, boolean isReadonly, String path,
			String parent, String flag) {

        this.name = name;
		this.length = length;
		this.createTime = createTime;
		this.isDirectory = isDirectory;
		this.isReadonly = isReadonly;
		this.path = path;
		this.parent = parent;
		this.flag = flag;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLength() {
		return this.length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public boolean isDirectory() {
		return this.isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public boolean isReadonly() {
		return this.isReadonly;
	}

	public void setReadonly(boolean isReadonly) {
		this.isReadonly = isReadonly;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getParent() {
		return this.parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
