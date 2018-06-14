package com.tbs.circle.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.Utils;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.tbs.circle.R;
import com.tbs.circle.adapter.PostArticleImgAdapter;
import com.tbs.circle.callback.ImgMoveCallBack;
import com.tbs.circle.listener.OnRecyclerItemClickListener;
import com.tbs.circle.mvp.contract.ImageContract;
import com.tbs.circle.mvp.presenter.ImagePresenter;
import com.tbs.circle.utils.GlideImageLoader;
import com.tbs.circle.widgets.dialog.UpLoadDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.lzy.imagepicker.ImagePicker.REQUEST_CODE_PREVIEW;

/**
 * Created by TBS on 2017/9/14.
 */

public class ImgDynamicActivity extends YWActivity implements ImageContract.View
{
    private View parentView;
    public static final int IMAGE_SIZE = 9;
    private List<ImageItem> originImages;
    private Context mContext;
    private RecyclerView rcvImg;
    private TextView tv;
    private TextView tv_des;
    private PostArticleImgAdapter postArticleImgAdapter;
    private ItemTouchHelper itemTouchHelper;
    private Button mBtnOk;       //确定按钮
    private ImageView mBtnBack;       //确定按钮
    private UpLoadDialog uploadDialog;
    private ImagePresenter presenter;
    private EditText et_content;
    private View topBar;

    public static void startPostActivity(Context context, List<ImageItem> images) {
        Intent intent = new Intent(context, ImgDynamicActivity.class);
        intent.putExtra("img", (Serializable) images);
        context.startActivity(intent);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
        setContentView(parentView);
        presenter = new ImagePresenter(this);
        initData();
    }

    private void initData() {
        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.include);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        originImages = (List<ImageItem>) getIntent().getSerializableExtra("img");
        mContext = getApplicationContext();
        rcvImg = (RecyclerView) findViewById(R.id.noScrollgridview);
        tv = (TextView) findViewById(R.id.tv);
        tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText("动态");
        et_content = (EditText) findViewById(R.id.et_content);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setText("发送");
        mBtnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                initUploadDialog();
                showLoading("");
                presenter.setPathList(originImages);
                presenter.setContent(et_content.getText().toString());
                presenter.upLoadImage();

            }
        });
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                backDialog();
            }
        });
        initRcv();
    }

    private void initUploadDialog() {
        uploadDialog = new UpLoadDialog(this);
    }

    private void initRcv() {
        postArticleImgAdapter = new PostArticleImgAdapter(mContext, originImages);
        rcvImg.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rcvImg.setAdapter(postArticleImgAdapter);
        ImgMoveCallBack imgMoveCallBack = new ImgMoveCallBack(postArticleImgAdapter, originImages);
        itemTouchHelper = new ItemTouchHelper(imgMoveCallBack);
        itemTouchHelper.attachToRecyclerView(rcvImg);//绑定RecyclerView

        //事件监听
        rcvImg.addOnItemTouchListener(new OnRecyclerItemClickListener(rcvImg)
        {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if (originImages.size() >= 9) {
                    //打开预览
                    Intent intentPreview = new Intent(ImgDynamicActivity.this, ImagePreviewDelActivity.class);
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (Serializable) originImages);
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, vh.getAdapterPosition());
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                } else {
                    if (vh.getAdapterPosition() == originImages.size()) {
                        ImagePicker imagePicker = ImagePicker.getInstance();
                        imagePicker.setImageLoader(new GlideImageLoader());
                        imagePicker.setShowCamera(false);
                        imagePicker.setSelectLimit(IMAGE_SIZE - originImages.size());
                        imagePicker.setCrop(false);
                        Intent intent = new Intent(getApplicationContext(), ImageGridActivity.class);
                        startActivityForResult(intent, 100);
                    } else {
                        //打开预览
                        Intent intentPreview = new Intent(ImgDynamicActivity.this, ImagePreviewDelActivity.class);
                        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (Serializable) originImages);
                        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, vh.getAdapterPosition());
                        startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                    }
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                //如果item不是最后一个，则执行拖拽
                if (vh.getLayoutPosition() != originImages.size()) {
                    itemTouchHelper.startDrag(vh);
                }
            }
        });

        imgMoveCallBack.setDragListener(new ImgMoveCallBack.DragListener()
        {
            @Override
            public void deleteState(boolean delete) {
                if (delete) {
                    tv.setBackgroundResource(R.color.holo_red_dark);
                    tv.setText(getResources().getString(R.string.post_delete_tv_s));
                } else {
                    tv.setText(getResources().getString(R.string.post_delete_tv_d));
                    tv.setBackgroundResource(R.color.holo_red_light);
                }
            }

            @Override
            public void dragState(boolean start) {
                if (start) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.GONE);
                }
            }
        });
    }

    ArrayList<ImageItem> images = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {//从相册选择完图片
                List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                originImages.addAll(images);
                postArticleImgAdapter.notifyDataSetChanged();
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    originImages.clear();
                    originImages.addAll(images);
                    postArticleImgAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        backDialog();
        return;
    }

    private void backDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("退出此次编辑？").setPositiveButton("退出", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNeutralButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void showLoading(String msg) {
        uploadDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.recycle();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void hideLoading() {
        uploadDialog.dismiss();
    }

    @Override
    public void LoadingProgress(int progress) {
        //System.out.println("progress="+progress);
       uploadDialog.setPercentsProgress(progress);
    }

    @Override
    public void showError(String errorMsg) {

    }

}
