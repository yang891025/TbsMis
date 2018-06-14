/**   
* @Title: ItemBean.java 
* @Package com.example.test 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @date 2014-6-25 ����9:45:29    
*/
package com.tbs.tbsmis.util;

import java.util.List;

public class GroupItem {

	private String id;
	private String name;
	private List<ChildrenItem> childrenItems;
	
	
	public GroupItem() {
	}

	
	public GroupItem(String id,String name,List<ChildrenItem> childrenItems) {
		this.id = id;
		this.name = name;
		this.childrenItems = childrenItems;
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


	public List<ChildrenItem> getChildrenItems() {
		return this.childrenItems;
	}


	public void setChildrenItems(List<ChildrenItem> childrenItems) {
		this.childrenItems = childrenItems;
	}

	
	
	
}
