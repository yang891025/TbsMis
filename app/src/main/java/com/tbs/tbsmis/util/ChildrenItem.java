/**   
* @Title: ItemBean.java 
* @Package com.example.test 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @date 2014-6-25 ����9:45:29    
*/
package com.tbs.tbsmis.util;

public class ChildrenItem {

	private String id;
	private String name;
	
	public ChildrenItem() {
	}

	
	public ChildrenItem(String id,String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
