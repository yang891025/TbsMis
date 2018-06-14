package com.tbs.tbsmis.update;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.tbs.tbsmis.util.ApiClient;
import com.tbs.tbsmis.util.HttpConnectionUtil;

/**
 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
 * 
 */
public class DownloadAsyncTask extends AsyncTask<Integer, Integer, Integer> {

	private final String url;
	private final String path;
	private final Context context;

	public DownloadAsyncTask(Context context, String url, String path) {
        this.url = url;
		this.path = path;
		this.context = context;
	}

	/**
	 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
	 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
	 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
	 */
	@Override
	protected Integer doInBackground(Integer... params) {
		ApiClient Client = new ApiClient();
        //return Client.http_get(this.url);
        HttpConnectionUtil connection = new HttpConnectionUtil();
        return connection.downFile(url, path);

	}

	/**
	 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
	 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
	 */
	@Override
	protected void onPostExecute(Integer result) {
		if (result == -1) {
			Toast.makeText(this.context, "文件写入失败！文件不存在或网络错误", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this.context, "文件写入成功", Toast.LENGTH_SHORT).show();
		}

	}

	// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
	@Override
	protected void onPreExecute() {

	}

	/**
	 * 这里的Intege参数对应AsyncTask中的第二个参数
	 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
	 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {

	}

}