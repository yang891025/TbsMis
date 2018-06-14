/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tbs.tbsmis.notification;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.tbs.tbsmis.update.CbsDownloadAsyncTask;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

/**
 * This class notifies the receiver of incoming notifcation packets
 * asynchronously.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationPacketListener implements PacketListener
{

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotificationPacketListener.class);

    private final XmppManager xmppManager;
//    private IniFile IniFile;
//    //private String appNewsFile;
//    private String webRoot;
//    //private CBSInterpret mInterpret;
//    private String WebIniFile;

    public NotificationPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
        //this.initPath();
    }
//   private void initPath() {
        // TODO Auto-generated method stub
//        this.webRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
//        if (this.webRoot.endsWith("/") == false) {
//            this.webRoot += "/";
//        }
//        this.webRoot += constants.SD_CARD_TBS_PATH;
//        if (this.webRoot.endsWith("/") == false) {
//            this.webRoot += "/";
//        }
//        this.IniFile = new IniFile();
//        String path = UIHelper.getShareperference(this.xmppManager.getContext(),
//                constants.SAVE_INFORMATION, "Path", "");
//        this.WebIniFile = path + constants.WEB_CONFIG_FILE_NAME;
//        this.appNewsFile = path
//                + this.IniFile.getIniString(this.WebIniFile, "TBSWeb", "IniName",
//                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
//    }

    @Override
    public void processPacket(Packet packet) {
        Log.d(NotificationPacketListener.LOGTAG, "NotificationPacketListener.processPacket()...");
        Log.d(NotificationPacketListener.LOGTAG, "packet.toXML()=" + packet.toXML());
        if (packet instanceof NotificationIQ) {
            NotificationIQ notification = (NotificationIQ) packet;
            if (notification.getChildElementXML().contains(
                    "androidpn:iq:notification")) {
                String notificationId = notification.getId();
                String notificationApiKey = notification.getApiKey();
                String notificationTitle = notification.getTitle();
                String notificationMessage = notification.getMessage();
                // String notificationTicker = notification.getTicker();
                //this.initPath();
                //String filePath = null;
                String msghandle = null;
                //String path = null;
                MediaPlayer mMediaPlayer = null;
                String notificationUri = notification.getUri();
                if (!StringUtils.isEmpty(notificationMessage)) {
                    if (notificationMessage.indexOf(":") > 0) {
                        if (notificationMessage.substring(0,
                                notificationMessage.indexOf(":") + 1)
                                .equalsIgnoreCase("tbs:")) {
                            if (UIHelper.getShareperference(
                                    this.xmppManager.getContext(),
                                    Constants.SHARED_PREFERENCE_NAME,
                                    Constants.SETTINGS_VIBRATE_ENABLED, false)) {
                                /*
                                 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
								 */
                                try {
                                    Vibrator vibrator = (Vibrator) this.xmppManager
                                            .getContext().getSystemService(
                                                    Context.VIBRATOR_SERVICE);
                                    long[] pattern = {800, 150, 400, 130}; // OFF/ON/OFF/ON...
                                    vibrator.vibrate(pattern, -1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            if (UIHelper.getShareperference(
                                    this.xmppManager.getContext(),
                                    Constants.SHARED_PREFERENCE_NAME,
                                    Constants.SETTINGS_SOUND_ENABLED, true)) {

                                try {
                                    Uri alert = RingtoneManager
                                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    mMediaPlayer = new MediaPlayer();
                                    mMediaPlayer.setDataSource(
                                            this.xmppManager.getContext(), alert);
                                    mMediaPlayer
                                            .setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                                    mMediaPlayer.setLooping(false);
                                    mMediaPlayer.prepare();
                                    mMediaPlayer.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            notificationMessage = notificationMessage
                                    .substring(notificationMessage.indexOf(":") + 1);
//                            int msgnum = Integer.parseInt(this.IniFile.getIniString(
//                                    this.appNewsFile, "MSG_HANDLE", "msgnum", "0",
//                                    (byte) 0));
                            if (notificationMessage.indexOf(":") > 0) {
                                msghandle = notificationMessage.substring(0,
                                        notificationMessage.indexOf(":"));
                                if (msghandle.equalsIgnoreCase("info")) {
                                    // System.out
                                    // .println("msghandle=" +
                                    // notificationMessage
                                    // .substring(notificationMessage
                                    // .indexOf(":") + 1));
                                    CbsDownloadAsyncTask Task = new CbsDownloadAsyncTask(
                                            this.xmppManager.getContext(),
                                            notificationMessage
                                                    .substring(notificationMessage
                                                            .indexOf(":") + 1),
                                            1);
                                    Task.execute();
                                    return;
                                } else if (msghandle.equalsIgnoreCase("wechat")) {
                                    if (UIHelper
                                            .getShareperference(
                                                    this.xmppManager.getContext(),
                                                    Constants.SHARED_PREFERENCE_NAME,
                                                    Constants.SETTINGS_SHOW_NOTIFICATION,
                                                    true)) {
                                        Intent intent = new Intent(
                                                Constants.ACTION_SHOW_NOTIFICATION);
                                        intent.putExtra(
                                                Constants.NOTIFICATION_ID,
                                                notificationId);
                                        intent.putExtra(
                                                Constants.NOTIFICATION_API_KEY,
                                                notificationApiKey);
                                        intent.putExtra(
                                                Constants.NOTIFICATION_TITLE,
                                                notificationTitle);
                                        intent.putExtra(
                                                Constants.NOTIFICATION_MESSAGE,
                                                notificationMessage);
                                        intent.putExtra(
                                                Constants.NOTIFICATION_URI,
                                                notificationUri);
                                        this.xmppManager.getContext().sendBroadcast(
                                                intent);
                                        return;
                                    }
                                }
//								for (int i = 0; i <= msgnum; i++) {
//									if (this.IniFile.getIniString(this.appNewsFile,
//											"MSG_HANDLE", "msgtype" + i,
//											"default", (byte) 0)
//											.equalsIgnoreCase(msghandle)) {
//										filePath = this.webRoot
//												+ this.IniFile.getIniString(
//                                                this.appNewsFile,
//														"MSG_HANDLE",
//														"savepath" + i,
//														"/msg/default/",
//														(byte) 0);
//										path = this.IniFile
//												.getIniString(
//                                                        this.appNewsFile,
//														"MSG_HANDLE",
//														"handleurl" + i,
//														"/tbsapp/message/ReceiveMsg.cbs",
//														(byte) 0);
//										break;
//									}
//									if (i >= msgnum) {
//										msghandle = "default";
//										filePath = this.webRoot
//												+ this.IniFile.getIniString(
//                                                this.appNewsFile,
//														"MSG_HANDLE",
//														"savepath0",
//														"/msg/default/",
//														(byte) 0);
//										path = this.IniFile
//												.getIniString(
//                                                        this.appNewsFile,
//														"MSG_HANDLE",
//														"handleurl0",
//														"/tbsapp/message/ReceiveMsg.cbs",
//														(byte) 0);
//									}
//								}
//								notificationMessage = notificationMessage
//										.substring(notificationMessage
//												.indexOf(":") + 1);
                            }
                            //else {
//								msghandle = "default";
//								filePath = this.webRoot
//										+ this.IniFile.getIniString(this.appNewsFile,
//												"MSG_HANDLE", "savepath0",
//												"/msg/default/", (byte) 0);
//								path = this.IniFile.getIniString(this.appNewsFile,
//										"MSG_HANDLE", "handleurl0",
//										"/tbsapp/message/ReceiveMsg.cbs",
//										(byte) 0);
//							}
//							FileIO.CreateTxt(filePath, notificationMessage,
//									notificationId + ".txt");
//							NotificationPacketListener.MyAsyncTask task = new NotificationPacketListener.MyAsyncTask
// (notificationId
//									+ ".txt", msghandle, path);
//							task.execute();
                            // CbsDownloadAsyncTask Task = new
                            // CbsDownloadAsyncTask(
                            // xmppManager.getContext(), msghandle);
                            // Task.execute();
                        }
                        //else {
//							if (UIHelper.getShareperference(
//                                    this.xmppManager.getContext(),
//									Constants.SHARED_PREFERENCE_NAME,
//									Constants.SETTINGS_SHOW_NOTIFICATION, true)) {
//								Intent intent = new Intent(
//										Constants.ACTION_SHOW_NOTIFICATION);
//								intent.putExtra(Constants.NOTIFICATION_ID,
//										notificationId);
//								intent.putExtra(Constants.NOTIFICATION_API_KEY,
//										notificationApiKey);
//								intent.putExtra(Constants.NOTIFICATION_TITLE,
//										notificationTitle);
//								intent.putExtra(Constants.NOTIFICATION_MESSAGE,
//										notificationMessage);
//								intent.putExtra(Constants.NOTIFICATION_URI,
//										notificationUri);
//                                this.xmppManager.getContext().sendBroadcast(intent);
//							}
//						}
                    }
                    //else{
                    if (UIHelper.getShareperference(
                            this.xmppManager.getContext(),
                            Constants.SHARED_PREFERENCE_NAME,
                            Constants.SETTINGS_SHOW_NOTIFICATION, true)) {
                        Intent intent = new Intent(
                                Constants.ACTION_SHOW_NOTIFICATION);
                        intent.putExtra(Constants.NOTIFICATION_ID,
                                notificationId);
                        intent.putExtra(Constants.NOTIFICATION_API_KEY,
                                notificationApiKey);
                        intent.putExtra(Constants.NOTIFICATION_TITLE,
                                notificationTitle);
                        intent.putExtra(Constants.NOTIFICATION_MESSAGE,
                                notificationMessage);
                        intent.putExtra(Constants.NOTIFICATION_URI,
                                notificationUri);
                        this.xmppManager.getContext().sendBroadcast(intent);
                    }
                    //}
                }
            }
        }

    }

//    private void doInterpret(String fileName, String msgType, String handleUrl) {
//        // TODO Auto-generated method stub
//        this.mInterpret = new CBSInterpret();
//        this.mInterpret.initGlobal(this.WebIniFile, UIHelper.getShareperference(
//                this.xmppManager.getContext(), constants.SAVE_INFORMATION, "Path",
//                ""));
//        if (handleUrl.indexOf("?") < 0) {
//            handleUrl += "?";
//        }
//        String interpretFile = this.mInterpret.Interpret(handleUrl + "fileName="
//                + fileName + "&msgType=" + msgType, "GET", "", null, 0);
//        FileUtils.deleteFileWithPath(interpretFile);
//    }
//
//    class MyAsyncTask extends AsyncTask<String, Integer, String>
//    {
//
//        private final String fileName;
//        private final String msgType;
//        private final String handleUrl;
//
//        public MyAsyncTask(String fileName, String msgType, String handleUrl) {
//            this.fileName = fileName;
//            this.msgType = msgType;
//            this.handleUrl = handleUrl;
//        }
//
//        // 运行在UI线程中，在调用doInBackground()之前执行
//        @Override
//        protected void onPreExecute() {
//            // StartTbsweb.Startapp(xmppManager.getContext(), 1);
//        }
//
//        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
//        @Override
//        protected String doInBackground(String... params) {
//            // HttpConnectionUtil connection = new HttpConnectionUtil();
//            if (!StringUtils.isEmpty(this.handleUrl)) {
//                NotificationPacketListener.this.doInterpret(this.fileName, this.msgType, this.handleUrl);
//            }
//            return null;
//        }
//
//        // 运行在ui线程中，在doInBackground()执行完毕后执行
//        @Override
//        protected void onPostExecute(String result) {
//            // StartTbsweb.Startapp(xmppManager.getContext(), 0);
//        }
//
//        // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//        }
//    }
}
