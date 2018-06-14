package com.tbs.tbsmis.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.ChildrenItem;
import com.tbs.tbsmis.util.GroupItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewAdapter extends BaseExpandableListAdapter {
	private final List<GroupItem> dataList;
	private final LayoutInflater inflater;
	// ��ѡ�е����б���
	private final List<String> checkedChildren = new ArrayList<String>();
	// ���б����ѡ��״̬��valueֵΪ1��ѡ�У���2������ѡ�У���3��δѡ�У�
	private final Map<String, Integer> groupCheckedStateMap = new HashMap<String, Integer>();

	@SuppressWarnings("null")
	public ListViewAdapter(Context context, List<GroupItem> dataList) {
		this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);

		// Ĭ���������еĸ��б�������б��Ϊѡ��״̬
		int groupCount = this.getGroupCount();
		for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
			try {
				GroupItem groupItem = dataList.get(groupPosition);
				if (groupItem == null) {
                    this.groupCheckedStateMap.put(groupItem.getId(), 3);
					continue;
				}
                this.groupCheckedStateMap.put(groupItem.getId(), 1);
				List<ChildrenItem> childrenItems = groupItem.getChildrenItems();
				for (ChildrenItem childrenItem : childrenItems) {
                    this.checkedChildren.add(childrenItem.getId());
				}
				if (groupItem.getChildrenItems() == null
						|| groupItem.getChildrenItems().isEmpty()) {
                    this.checkedChildren.add(groupItem.getName());
				}
			} catch (Exception e) {

			}
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		GroupItem groupItem = this.dataList.get(groupPosition);
		if (groupItem == null) {
			return null;
		}
		return groupItem.getChildrenItems().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildrenItem childrenItem = (ChildrenItem) this.getChild(groupPosition,
				childPosition);
		ListViewAdapter.ChildViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ListViewAdapter.ChildViewHolder();
			convertView = this.inflater.inflate(R.layout.children_item, null);
			viewHolder.childrenNameTV = (TextView) convertView
					.findViewById(R.id.tvPath);
			viewHolder.childrenCB = (CheckBox) convertView
					.findViewById(R.id.children_cb);
			viewHolder.childCTImage = (ImageView) convertView
					.findViewById(R.id.tvImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewAdapter.ChildViewHolder) convertView.getTag();
		}
		if (childrenItem.getName().contains(".")) {
			viewHolder.childCTImage.setImageResource(R.drawable.format_text);
		} else {
			viewHolder.childCTImage.setImageResource(R.drawable.format_folder);
		}
		viewHolder.childrenNameTV.setText(childrenItem.getName());
		final String childrenId = childrenItem.getId();
		viewHolder.childrenCB
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							if (!ListViewAdapter.this.checkedChildren.contains(childrenId)) {
                                ListViewAdapter.this.checkedChildren.add(childrenId);
							}
						} else {
                            ListViewAdapter.this.checkedChildren.remove(childrenId);
						}
                        ListViewAdapter.this.setGroupItemCheckedState(ListViewAdapter.this.dataList.get(groupPosition));
                        notifyDataSetChanged();
					}
				});

		if (this.checkedChildren.contains(childrenId)) {
			viewHolder.childrenCB.setChecked(true);
		} else {
			viewHolder.childrenCB.setChecked(false);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		GroupItem groupItem = this.dataList.get(groupPosition);
		if (groupItem == null || groupItem.getChildrenItems() == null
				|| groupItem.getChildrenItems().isEmpty()) {
			return 0;
		}
		return groupItem.getChildrenItems().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (this.dataList == null) {
			return null;
		}
		return this.dataList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		if (this.dataList == null) {
			return 0;
		}
		return this.dataList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		try {
			GroupItem groupItem = this.dataList.get(groupPosition);

			ListViewAdapter.GroupViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ListViewAdapter.GroupViewHolder();
				convertView = this.inflater.inflate(R.layout.group_item, null);
				viewHolder.groupNameTV = (TextView) convertView
						.findViewById(R.id.group_name);
				viewHolder.groupCBImg = (ImageView) convertView
						.findViewById(R.id.group_cb_img);
				viewHolder.groupCTImage = (ImageView) convertView
						.findViewById(R.id.group_Image);
				viewHolder.groupCBLayout = (LinearLayout) convertView
						.findViewById(R.id.cb_layout);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ListViewAdapter.GroupViewHolder) convertView.getTag();
			}

			viewHolder.groupCBLayout
					.setOnClickListener(new GroupCBLayoutOnClickListener(
							groupItem));
			File groupFile = new File(groupItem.getId());
			if (groupFile.isFile()) {
				viewHolder.groupCTImage
						.setImageResource(R.drawable.format_text);
			} else {
				viewHolder.groupCTImage
						.setImageResource(R.drawable.format_folder);
			}
			viewHolder.groupNameTV.setText(groupItem.getName());
			int state = this.groupCheckedStateMap.get(groupItem.getId());
			switch (state) {
			case 1:
				viewHolder.groupCBImg.setImageResource(R.drawable.ck_checked);
				break;
			case 2:
				viewHolder.groupCBImg
						.setImageResource(R.drawable.ck_partial_checked);
				break;
			case 3:
				viewHolder.groupCBImg.setImageResource(R.drawable.ck_unchecked);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private void setGroupItemCheckedState(GroupItem groupItem) {
		List<ChildrenItem> childrenItems = groupItem.getChildrenItems();
		if (childrenItems == null || childrenItems.isEmpty()) {
			if (this.checkedChildren.contains(groupItem.getName())) {
                this.groupCheckedStateMap.put(groupItem.getId(), 1);
			} else {
                this.groupCheckedStateMap.put(groupItem.getId(), 3);
			}
			return;
		}

		int checkedCount = 0;
		for (ChildrenItem childrenItem : childrenItems) {
			if (this.checkedChildren.contains(childrenItem.getId())) {
				checkedCount++;
			}
		}
		int state = 1;
		if (checkedCount == 0) {
			state = 3;
		} else if (checkedCount == childrenItems.size()) {
			state = 1;
		} else {
			state = 2;
		}
        this.groupCheckedStateMap.put(groupItem.getId(), state);
	}

	static final class GroupViewHolder {
		TextView groupNameTV;
		ImageView groupCBImg;
		ImageView groupCTImage;
		LinearLayout groupCBLayout;
	}

	static final class ChildViewHolder {
		TextView childrenNameTV;
		ImageView childCTImage;
		CheckBox childrenCB;
	}

	public class GroupCBLayoutOnClickListener implements View.OnClickListener {

		private final GroupItem groupItem;

		public GroupCBLayoutOnClickListener(GroupItem groupItem) {
			this.groupItem = groupItem;
		}

		@Override
		public void onClick(View v) {
			List<ChildrenItem> childrenItems = this.groupItem.getChildrenItems();
			if (childrenItems == null || childrenItems.isEmpty()) {
				if (ListViewAdapter.this.checkedChildren.contains(this.groupItem.getName())) {
                    ListViewAdapter.this.groupCheckedStateMap.put(this.groupItem.getId(), 3);
                    ListViewAdapter.this.checkedChildren.remove(this.groupItem.getName());

				} else {
                    ListViewAdapter.this.groupCheckedStateMap.put(this.groupItem.getId(), 1);
                    ListViewAdapter.this.checkedChildren.add(this.groupItem.getName());
				}
                notifyDataSetChanged();
				return;
			}
			int checkedCount = 0;
			for (ChildrenItem childrenItem : childrenItems) {
				if (ListViewAdapter.this.checkedChildren.contains(childrenItem.getId())) {
					checkedCount++;
				}
			}

			boolean checked = false;
			if (checkedCount == childrenItems.size()) {
				checked = false;
                ListViewAdapter.this.groupCheckedStateMap.put(this.groupItem.getId(), 3);
			} else {
				checked = true;
                ListViewAdapter.this.groupCheckedStateMap.put(this.groupItem.getId(), 1);
			}

			for (ChildrenItem childrenItem : childrenItems) {
				String holderKey = childrenItem.getId();
				if (checked) {
					if (!ListViewAdapter.this.checkedChildren.contains(holderKey)) {
                        ListViewAdapter.this.checkedChildren.add(holderKey);
					}
				} else {
                    ListViewAdapter.this.checkedChildren.remove(holderKey);
				}
			}

            notifyDataSetChanged();
		}
	}

	public List<String> getCheckedRecords() {
		return this.checkedChildren;
	}

	public List<String> getCheckedChildren() {
		return this.checkedChildren;
	}

}
