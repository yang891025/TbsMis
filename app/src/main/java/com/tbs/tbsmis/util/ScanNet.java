package com.tbs.tbsmis.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.tbs.tbsmis.R;

import java.io.IOException;

public class ScanNet {

	private String locAddress;// 存储本机ip，例：本地ip ：192.168.1.

	private final Runtime run = Runtime.getRuntime();// 获取当前运行环境，来执行ping，相当于windows的cmd

	private Process proc;

	private final String ping = "ping -c 1 -w 0.5 ";// 其中 -c 1为发送的次数，-w 表示发送后等待响应的时间

	private int j, i, n;// 存放ip最后一位地址 0-255

	private final Context ctx;// 上下文

	public ScanNet(Context ctx) {
		this.ctx = ctx;
        j = 1;
        i = 88;
        n = 175;
	}

	@SuppressLint("HandlerLeak")
	private final Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 222:// 服务器消息
                ScanNet.this.j = ScanNet.this.j + 1;
				if (ScanNet.this.j < 88) {
                    ScanNet.this.scan();
				}
				System.out.println("j=" + ScanNet.this.j);
				break;
			case 333:// 扫描完毕消息
                ScanNet.this.n = ScanNet.this.n + 1;
				if (ScanNet.this.n < 255) {
                    ScanNet.this.scanN();
				}
				System.out.println("n=" + ScanNet.this.n);
				break;
			case 444:// 扫描失败
                ScanNet.this.i = ScanNet.this.i + 1;
				if (ScanNet.this.i < 175) {
                    ScanNet.this.scanI();
				}
				System.out.println("i=" + ScanNet.this.i);
				break;
			}
		}

	};

	private ProgressDialog mProDialog;

	/**
	 * 显示进度条对话框
	 */
	@SuppressWarnings("deprecation")
	private void showScanDialog() {
		if (this.mProDialog == null) {
			// mProDialog = ProgressDialog.show(mContext, "版本检测",
			// "正在检测，请稍候...",
			// false, true);
			// //设置ProgressDialog 的一个Button
			// mProDialog.setButton("确定", new SureButtonListener());
			// mProDialog.setCanceledOnTouchOutside(false);
			// 创建ProgressDialog对象
            this.mProDialog = new ProgressDialog(this.ctx);
			// 设置进度条风格，风格为圆形，旋转的
            this.mProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置ProgressDialog 标题
            this.mProDialog.setTitle("载入中...");
			// 设置ProgressDialog 提示信息
            this.mProDialog.setMessage("正在扫描，请稍候...");
			// 设置ProgressDialog 的进度条是否不明确
            this.mProDialog.setIndeterminate(false);
			// 设置ProgressDialog 是否可以按退回按键取消
            this.mProDialog.setCancelable(true);
			// 设置ProgressDialog 的一个Button
            this.mProDialog.setButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                    ScanNet.this.j = 88;
                    ScanNet.this.i = 175;
                    ScanNet.this.n = 255;
					dialog.dismiss();
				}
			});
			// 让ProgressDialog显示
            this.mProDialog.show();
		}
	}

	/**
	 * 扫描局域网内ip，找到对应服务器
	 */
	public void scan() {
        this.locAddress = this.getLocAddrIndex();// 获取本地ip前缀
		if (this.locAddress == null) {
			Toast.makeText(this.ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
			return;
		}
        this.showScanDialog();
		if (this.j == 1) {
            this.scanI();
            this.scanN();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				String p = ping + ScanNet.this.locAddress + j;
				String current_ip = ScanNet.this.locAddress + j;
				try {
                    ScanNet.this.proc = ScanNet.this.run.exec(p);
					int result = ScanNet.this.proc.waitFor();
					if (result == 0) {
						//System.out.println("连接成功" + current_ip);
						Intent intent = new Intent();
						intent.setAction("Device"
								+ ScanNet.this.ctx.getString(R.string.about_title));
						intent.putExtra("flag", 1);
						intent.putExtra("DeviceName", current_ip);
                        ScanNet.this.ctx.sendBroadcast(intent);
					}
					Message msg = new Message();
					msg.what = 222;
                    ScanNet.this.handler.sendMessage(msg);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("e1=" + e1);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
					System.out.println("e2=" + e2);
				} finally {
                    ScanNet.this.proc.destroy();
				}
			}
		}).start();

	}

	/**
	 * 扫描局域网内ip，找到对应服务器
	 */
	public void scanN() {

        this.locAddress = this.getLocAddrIndex();// 获取本地ip前缀

		if (this.locAddress == null) {
			Toast.makeText(this.ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				String p = ping + ScanNet.this.locAddress + n;
				String current_ip = ScanNet.this.locAddress + n;
				try {
                    ScanNet.this.proc = ScanNet.this.run.exec(p);
					int result = ScanNet.this.proc.waitFor();
					if (result == 0) {
						System.out.println("连接成功" + current_ip);
						Intent intent = new Intent();
						intent.setAction("Device"
								+ ScanNet.this.ctx.getString(R.string.about_title));
						intent.putExtra("flag", 1);
						intent.putExtra("DeviceName", current_ip);
                        ScanNet.this.ctx.sendBroadcast(intent);
					}
					Message msg = new Message();
					msg.what = 333;
                    ScanNet.this.handler.sendMessage(msg);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("e1=" + e1);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
					System.out.println("e2=" + e2);
				} finally {
                    ScanNet.this.proc.destroy();
				}
			}
		}).start();

	}

	/**
	 * 扫描局域网内ip，找到对应服务器
	 */
	public void scanI() {

        this.locAddress = this.getLocAddrIndex();// 获取本地ip前缀

		if (this.locAddress == null) {
			Toast.makeText(this.ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				String p = ping + ScanNet.this.locAddress + i;
				String current_ip = ScanNet.this.locAddress + i;
				try {
                    ScanNet.this.proc = ScanNet.this.run.exec(p);
					int result = ScanNet.this.proc.waitFor();
					if (result == 0) {
						System.out.println("连接成功" + current_ip);
						Intent intent = new Intent();
						intent.setAction("Device"
								+ ScanNet.this.ctx.getString(R.string.about_title));
						intent.putExtra("flag", 1);
						intent.putExtra("DeviceName", current_ip);
                        ScanNet.this.ctx.sendBroadcast(intent);
					}
					Message msg = new Message();
					msg.what = 444;
                    ScanNet.this.handler.sendMessage(msg);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("e1=" + e1);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
					System.out.println("e2=" + e2);
				} finally {
                    ScanNet.this.proc.destroy();
				}
			}
		}).start();

	}

	// 获取本地ip地址
	public String getLocAddress() {
		WifiManager wm = (WifiManager) this.ctx
				.getSystemService(Context.WIFI_SERVICE);
		// 检查Wifi状态
		if (wm.isWifiEnabled()) {
			WifiInfo wi = wm.getConnectionInfo();
			// 获取32位整型IP地址
			int ipAdd = wi.getIpAddress();
			// 把整型地址转换成“*.*.*.*”地址
			return this.intToIp(ipAdd);
		} else {
			return "";
		}
	}

	public String intToIp(int i) {
		return (i & 0xFF) + "." + (i >> 8 & 0xFF) + "." + (i >> 16 & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	// // 获取本地ip地址
	// public String getLocAddress() {
	//
	// String ipaddress = "";
	//
	// try {
	// Enumeration<NetworkInterface> en = NetworkInterface
	// .getNetworkInterfaces();
	// // 遍历所用的网络接口
	// while (en.hasMoreElements()) {
	// NetworkInterface networks = en.nextElement();
	// // 得到每一个网络接口绑定的所有ip
	// Enumeration<InetAddress> address = networks.getInetAddresses();
	// // 遍历每一个接口绑定的所有ip
	// while (address.hasMoreElements()) {
	// InetAddress ip = address.nextElement();
	// if (!ip.isLoopbackAddress()
	// && InetAddressUtils.isIPv4Address(ip
	// .getHostAddress())) {
	// ipaddress = ip.getHostAddress();
	// }
	// }
	// }
	// } catch (SocketException e) {
	// Log.e("", "获取本地ip地址失败");
	// e.printStackTrace();
	// }
	//
	// System.out.println("本机IP:" + ipaddress);
	//
	// return ipaddress;
	//
	// }

	// 获取IP前缀
	public String getLocAddrIndex() {

		String str = this.getLocAddress();

		if (!str.equals("")) {
			return str.substring(0, str.lastIndexOf(".") + 1);
		}

		return null;
	}

	// // 获取本机设备名称
	// public String getLocDeviceName() {
	//
	// return android.os.Build.MODEL;
	//
	// }

}