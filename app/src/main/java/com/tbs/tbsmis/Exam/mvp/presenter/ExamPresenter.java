package com.tbs.tbsmis.Exam.mvp.presenter;

import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.tbsmis.Exam.bean.AnswerInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tbs.tbsmis.Exam.utils.DataUtils.createAllExamDatas;

/**
 * @author yiw
 * @ClassName: LivePresenter
 * @Description: 通知model请求服务器和通知view更新
 * @date 2015-12-28 下午4:06:03
 */
public class ExamPresenter implements com.tbs.tbsmis.Exam.mvp.contract.ExamContract.Presenter
{
    private com.tbs.tbsmis.Exam.mvp.contract.ExamContract.View view;

    //private static final String category = "0";//默认没有经纬度0
    private boolean isInitCache = false;

    public ExamPresenter(com.tbs.tbsmis.Exam.mvp.contract.ExamContract.View view) {
        this.view = view;
    }

    private String currentTime;

    public void loadData(String paperId) {
        Map params = new HashMap();
        params.put("paperId", paperId);
        OkGo.<String>get(com.tbs.tbsmis.Exam.utils.DataUtils.URL_EXAM_INFO)//
                .tag(this)
                .params(params, false)//由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.NO_CACHE)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new StringCallback()
                {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                JSONArray answerArray = jsonObject.getJSONArray("data");
                                JSONArray optionsArray = jsonObject.getJSONArray("options");
                                List<AnswerInfo> datas = createAllExamDatas(answerArray,optionsArray);
                                if (view != null) {
                                    view.updateData(datas);
                                }
                                view.hideLoading();
                            } else {
                                view.hideLoading();
                                Toast.makeText(view.getBaseContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            view.hideLoading();
                            Toast.makeText(view.getBaseContext(), "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        view.hideLoading();
                        Toast.makeText(view.getBaseContext(), "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public String getCurrentTime() {

        return currentTime;
    }


    /**
     * 清除对外部对象的引用，防止内存泄露。
     */
    public void recycle() {
        this.view = null;
        OkGo.getInstance().cancelTag(this);
    }
}
