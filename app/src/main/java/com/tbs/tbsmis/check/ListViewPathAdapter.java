package com.tbs.tbsmis.check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.FileUtils;

import java.util.List;

/**
 * 
 * @author yeguozhong@yeah.net
 * 
 */
public class ListViewPathAdapter extends BaseAdapter {

	private final List<String> listItems;// 数据集合
	private final LayoutInflater listContainer;// 视图容器
	private final int itemViewResource;// 自定义项视图源

	private final ListViewPathAdapter.OnPathOperateListener listener;
    private int arg0;

    public interface OnPathOperateListener {

		int DEL = 0;
		int RENAME = 1;

		void onPathOperate(int type, int position, TextView pathName);
	}

	/**
	 * 实例化Adapter
	 *
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewPathAdapter(Context context, List<String> data,
			int resource, ListViewPathAdapter.OnPathOperateListener listener) {
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        itemViewResource = resource;
        listItems = data;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return this.listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}



	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.listContainer.inflate(this.itemViewResource, null);
		}

		LinearLayout llOp = (LinearLayout) convertView.findViewById(R.id.ll_op);
		llOp.setVisibility(View.GONE);

		final TextView tvPath = (TextView) convertView
				.findViewById(R.id.tvPath);
		tvPath.setText(FileUtils.getPathName(this.listItems.get(position)));
		Button btnDel = (Button) convertView.findViewById(R.id.btn_del);
		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                ListViewPathAdapter.this.listener.onPathOperate(ListViewPathAdapter.OnPathOperateListener.DEL, position,
						tvPath);
			}
		});
		Button btnRename = (Button) convertView.findViewById(R.id.btn_rename);
		btnRename.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                ListViewPathAdapter.this.listener.onPathOperate(ListViewPathAdapter.OnPathOperateListener.RENAME, position,
						tvPath);
			}
		});
		return convertView;
	}

}
