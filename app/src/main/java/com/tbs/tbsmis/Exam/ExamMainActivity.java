package com.tbs.tbsmis.Exam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.Exam.bean.AnswerInfo;
import com.tbs.tbsmis.Exam.mvp.contract.ExamContract;
import com.tbs.tbsmis.Exam.mvp.presenter.ExamPresenter;
import com.tbs.tbsmis.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class ExamMainActivity extends Activity implements ExamContract.View{

    private Button startBtn;

    private ImageView left;
    private TextView title;
    private String examId;
    private String paperId;
    private String e_name;
    private String e_time;
    private String e_passscore;
    private String e_totalscore;
    private ExamPresenter presenter;
    private ProgressDialog progressBar;
    private List<AnswerInfo> datas;
    /**
     * 考试信息
     *
     * @param savedInstanceState {
     *                           "examId":   ,
     *                           "paperId":   ,
     *                           "e_name":   ,
     *                           "e_time":   ,
     *                           "e_passscore":   ,
     *                           "e_totalscore":   ,
     *                           }
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exam_activity_main);
        presenter = new ExamPresenter(this);
        if (this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            String examInfo = bundle.getString("examInfo");
            try {
                JSONObject json = new JSONObject(examInfo);
                examId = json.getString("examId");
                paperId = json.getString("paperId");
                e_name = json.getString("e_name");
                e_time = json.getString("e_time");
                e_passscore = json.getString("e_passscore");
                e_totalscore = json.getString("e_totalscore");
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "获取考试信息失败", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(this, "获取考试信息失败", Toast.LENGTH_LONG).show();
            return;
        }

        left = (ImageView) findViewById(R.id.left);
        title = (TextView) findViewById(R.id.title);
        startBtn = (Button) findViewById(R.id.start);

        left.setVisibility(View.GONE);
        title.setText("考试");
        progressBar = new ProgressDialog(this);
        presenter.loadData(paperId);
        showLoading("配置考试信息");
    }

    @Override
    public void updateData(List<AnswerInfo> datas) {
          this.datas = datas;
        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("e_time",e_time);
                bundle.putSerializable("answerInfo", (Serializable) datas);
                intent.putExtras(bundle);
                intent.setClass(ExamMainActivity.this, AnalogyExaminationActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public void showLoading(String msg) {
      progressBar.setMessage(msg);
      progressBar.show();
    }

    @Override
    public void hideLoading() {
        progressBar.hide();
    }

    @Override
    public void LoadingProgress(int progress) {

    }

    @Override
    public void showError(String errorMsg) {

    }
}
