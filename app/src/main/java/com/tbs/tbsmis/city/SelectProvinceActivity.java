package com.tbs.tbsmis.city;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectProvinceActivity extends Activity implements View.OnClickListener {
	private CityAdapter mCityAdapter;
	private CityDB mCityDB;

	private TextView mTitleTextView;
	private ImageView mBackBtn;
	private ListView mCityListView;
	private List<String> mProvinceList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.biz_plugin_weather_select_city);
        this.initView();
        this.initData();
	}

	private void initView() {
        this.mTitleTextView = (TextView) this.findViewById(R.id.title_name);
        this.mBackBtn = (ImageView) this.findViewById(R.id.title_back);
        this.mBackBtn.setOnClickListener(this);
        this.mTitleTextView.setText("选择地区");
        this.mCityListView = (ListView) this.findViewById(R.id.citys_list);
		//mCityContainer = findViewById(R.id.city_content_container);
        this.mCityListView.setEmptyView(this.findViewById(R.id.citys_list_empty));
        this.mCityListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						// L.i(mCityAdapter.getItem(position).toString());
						Intent intent = new Intent();
						intent.putExtra("province", SelectProvinceActivity.this.mCityAdapter.getItem(position).toString());
						intent.setClass(SelectProvinceActivity.this, SelectCityActivity.class);
                        SelectProvinceActivity.this.startActivity(intent);
                        SelectProvinceActivity.this.finish();
					}
				});

	}

	private void initData() {
		SelectProvinceActivity.MyAsyncTask task = new SelectProvinceActivity.MyAsyncTask(this);// �����첽���ع���
		task.execute();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_back:
            this.finish();
			break;
		default:
			break;
		}
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {

		Context context;

		public MyAsyncTask(Context c) {
            context = c;
		}

		// ������UI�߳��У��ڵ���doInBackground()֮ǰִ��
		@Override
		protected void onPreExecute() {

		}
		// ��̨���еķ������������з�UI�̣߳�����ִ�к�ʱ�ķ���
		@Override
		protected Integer doInBackground(Void... params) {
			// ���confige�ļ��Ƿ����
            SelectProvinceActivity.this.mCityDB = SelectProvinceActivity.this.openCityDB();
            SelectProvinceActivity.this.initProvinceList();
			return null;
		}

		// ������ui�߳��У���doInBackground()ִ����Ϻ�ִ��
		@Override
		protected void onPostExecute(Integer integer) {
			// ��ת����
            SelectProvinceActivity.this.mCityAdapter = new CityAdapter(SelectProvinceActivity.this, SelectProvinceActivity.this.mProvinceList);
            SelectProvinceActivity.this.mCityListView.setAdapter(SelectProvinceActivity.this.mCityAdapter);
		}

		// ��publishProgress()�������Ժ�ִ�У�publishProgress()���ڸ��½��
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}
	
	//第一次需要拷贝数据库
	private CityDB openCityDB() {
		String path = "/data"
				+ Environment.getDataDirectory().getAbsolutePath()
				+ File.separator + getString(R.string.app_path) + File.separator
				+ CityDB.CITY_DB_NAME;
		File db = new File(path);
		if (!db.exists()) {
			// L.i("db is not exists");
			try {
				InputStream is = this.getAssets().open("city.db");
				FileOutputStream fos = new FileOutputStream(db);
				int len = -1;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					fos.flush();
				}
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		return new CityDB(this, path);
	}
	private void initProvinceList() {
        this.mProvinceList = new ArrayList<String>();
        this.prepareProvinceList();
	}

	private boolean prepareProvinceList() {
        this.mProvinceList = this.mCityDB.getAllProvince();// 获取数据库中所有城市
		return true;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		MyActivity.getInstance().finishActivity(this);
	}
}