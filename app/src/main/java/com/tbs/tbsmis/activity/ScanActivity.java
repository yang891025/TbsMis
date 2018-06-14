package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.barcode.camera.CameraManager;
import com.barcode.decode.CaptureActivityHandler;
import com.barcode.decode.FinishListener;
import com.barcode.decode.InactivityTimer;
import com.barcode.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * 扫描二维码
 * 
 * @author 火蚁（http://my.oschina/LittleDY）
 * 
 */
@SuppressWarnings("deprecation")
public class ScanActivity extends Activity implements
		Callback, View.OnClickListener {

	private boolean hasSurface;
	private String characterSet;

	private ViewfinderView viewfinderView;

	private ImageView back;
	private ImageView flash;
	private ProgressDialog mProgress;

	/**
	 * 活动监控器，用于省电，如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
	 * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
	 */
	private InactivityTimer inactivityTimer;
	private CameraManager cameraManager;
	private Vector<BarcodeFormat> decodeFormats;// 编码格式
	private CaptureActivityHandler mHandler;// 解码线程
	private String userIni;
	private IniFile m_iniFileIO;

	private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
			.of(ResultMetadataType.ISSUE_NUMBER,
					ResultMetadataType.SUGGESTED_PRICE,
					ResultMetadataType.ERROR_CORRECTION_LEVEL,
					ResultMetadataType.POSSIBLE_COUNTRY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.initSetting();
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.capture);
        this.initView();
	}

	/**
	 * 初始化窗口设置
	 */
	private void initSetting() {
		Window window = this.getWindow();
		window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
		// window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this);
        this.cameraManager = new CameraManager(this.getApplication());
        this.viewfinderView = (ViewfinderView) this.findViewById(R.id.viewfinder_view);
        this.viewfinderView.setCameraManager(this.cameraManager);
        this.back = (ImageView) this.findViewById(R.id.capture_back);
        this.flash = (ImageView) this.findViewById(R.id.capture_flash);
        this.back.setOnClickListener(this);
        this.flash.setOnClickListener(this);
	}

	/**
	 * 主要对相机进行初始化工作
	 */
	@Override
	protected void onResume() {

        this.inactivityTimer.onActivity();
		SurfaceView surfaceView = (SurfaceView) this.findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (this.hasSurface) {
            this.initCamera(surfaceHolder);
		} else {
			// 如果SurfaceView已经渲染完毕，会回调surfaceCreated，在surfaceCreated中调用initCamera()
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		// 恢复活动监控器
        this.inactivityTimer.onResume();
		super.onResume();
	}

	public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
	}

	/**
	 * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (this.cameraManager.isOpen()) {
			return;
		}
		try {
            this.cameraManager.openDriver(surfaceHolder);
			// Creating the mHandler starts the preview, which can also throw a
			// RuntimeException.
			if (this.mHandler == null) {
                this.mHandler = new CaptureActivityHandler(this, this.decodeFormats,
                        this.characterSet, this.cameraManager);
			}
		} catch (IOException ioe) {
            this.displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
            this.displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * 初始化照相机失败显示窗口
	 */
	private void displayFrameworkBugMessageAndExit() {
		Builder builder = new Builder(this);
		builder.setTitle(this.getString(R.string.app_name));
		builder.setMessage(this.getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton("确定", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	/**
	 * 暂停活动监控器,关闭摄像头
	 */
	@Override
	protected void onPause() {
		if (this.mHandler != null) {
            this.mHandler.quitSynchronously();
            this.mHandler = null;
		}
		// 暂停活动监控器
        this.inactivityTimer.onPause();
		// 关闭摄像头
        this.cameraManager.closeDriver();
		if (!this.hasSurface) {
			SurfaceView surfaceView = (SurfaceView) this.findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		if (this.mProgress != null && this.mProgress.isShowing()) {
            this.mProgress.dismiss();
		}
		super.onPause();
	}

	/**
	 * 停止活动监控器,保存最后选中的扫描类型
	 */
	@Override
	protected void onDestroy() {
		// 停止活动监控器
        this.inactivityTimer.shutdown();
		if (this.mProgress != null) {
            this.mProgress.dismiss();
		}
		super.onDestroy();
	}

	/**
	 * 获取扫描结果
	 * 
	 * @param rawResult
	 * @param barcode
	 * @param scaleFactor
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		// inactivityTimer.onActivity();
		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {

			// Then not from history, so beep/vibrate and we have an image to
			// draw on
		}
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT);
		Map<ResultMetadataType, Object> metadata = rawResult
				.getResultMetadata();
		StringBuilder metadataText = new StringBuilder(20);
		if (metadata != null) {
			for (Entry<ResultMetadataType, Object> entry : metadata
					.entrySet()) {
				if (ScanActivity.DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
					metadataText.append(entry.getValue()).append('\n');
				}
			}
			if (metadataText.length() > 0) {
				metadataText.setLength(metadataText.length() - 1);
			}
		}
        this.parseBarCode(rawResult.getText());
	}

	// 解析二维码
	private void parseBarCode(String msg) {
		// 手机震动
		Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);
        this.mProgress = ProgressDialog.show(this, null,
				"已扫描，正在处理···", true, true);
        this.mProgress.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
                ScanActivity.this.restartPreviewAfterDelay(1l);
			}
		});
        this.iniPath();
		String scanUrl = this.m_iniFileIO.getIniString(this.userIni, "subscribe",
				"ScanPath", "", (byte) 0);
		if (StringUtils.isEmpty(scanUrl)) {
			if (msg.toLowerCase().startsWith("http://")
					|| msg.toLowerCase().startsWith("www.")) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(UIHelper.encodeGB(msg));
				intent.setData(content_url);
                this.startActivity(intent);
                this.finish();
			} else {
                this.showDialog(msg);
			}

		} else {
			String Url;
			String portUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
					"currentPort", constants.DefaultServerPort, (byte) 0);
			String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
					"currentAddress", constants.DefaultServerIp, (byte) 0);
			Url = "http://" + ipUrl + ":" + portUrl;
			scanUrl = StringUtils.isUrl(scanUrl, Url, null);
			if (scanUrl.indexOf("?") != -1) {
				scanUrl = scanUrl + "&scanContent=" + msg;
			} else {
				scanUrl = scanUrl + "?scanContent=" + msg;
			}
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse(scanUrl);
			intent.setData(content_url);
            this.startActivity(intent);
            this.finish();
		}

	}

	private void iniPath() {
		// TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
		String webRoot = UIHelper.getSoftPath(this);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
	}

	// /**
	// * 启动签到界面
	// *
	// * @param barcode
	// */
	// private void signin(Barcode barcode) {
	// Intent intent = new Intent(ScanActivity.this, Signin.class);
	// Bundle bundle = new Bundle();
	// bundle.putSerializable("barcode", barcode);
	// intent.putExtras(bundle);
	// startActivity(intent);
	// }

	/**
	 * 扫描结果对话框
	 * 
	 * @param msg
	 */
	private void showDialog(final String msg) {
		new Builder(this)
				.setTitle("扫描结果")
				.setMessage("内容：" + msg)
				.setPositiveButton("复制", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
                        ScanActivity.this.mProgress.dismiss();
						dialog.dismiss();
						// 获取剪贴板管理服务
						ClipboardManager cm = (ClipboardManager) ScanActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
						// 将文本数据复制到剪贴板
						cm.setText(msg);
						Toast.makeText(ScanActivity.this, "复制成功",
								Toast.LENGTH_SHORT).show();// UIHelper.ToastMessage(,
															// );
					}
				})
				.setNegativeButton("返回", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
                        ScanActivity.this.mProgress.dismiss();
						dialog.dismiss();
                        ScanActivity.this.restartPreviewAfterDelay(0L);
					}
				}).show();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {

		}
		if (!this.hasSurface) {
            this.hasSurface = true;
            this.initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
	}

	/**
	 * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
	 */
	public ViewfinderView getViewfinderView() {
		return this.viewfinderView;
	}

	public Handler getHandler() {
		return this.mHandler;
	}

	public CameraManager getCameraManager() {
		return this.cameraManager;
	}

	/**
	 * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
	 * 
	 * @param delayMS
	 */
	public void restartPreviewAfterDelay(long delayMS) {
		if (this.mHandler != null) {
            this.mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
            this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setFlash() {
		if (this.flash.getTag() != null) {
            this.cameraManager.setTorch(true);
            this.flash.setTag(null);
            this.flash.setBackgroundResource(R.drawable.flash_open);
		} else {
            this.cameraManager.setTorch(false);
            this.flash.setTag("1");
            this.flash.setBackgroundResource(R.drawable.flash_default);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.capture_back:
            this.finish();
			break;
		case R.id.capture_flash:
            this.setFlash();
			break;
		default:
			break;
		}
	}
}
