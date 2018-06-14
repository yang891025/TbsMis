package com.tbs.tbsmis.util;

import android.webkit.JavascriptInterface;

public class JavaScriptInterface
{
    /**
     * 添加网页js接口
     */
    @JavascriptInterface
    public void Hyperlinks_Move(String source, String sourceUrl) {
    }

    @JavascriptInterface
    public void Loading_NoMove(String sourceUrl) {
    }

    @JavascriptInterface
    public void loadsearch(String sourceUrl, String word) {
    }
    @JavascriptInterface
    public void Collect(String url, String Title,String type,String content,String pic) {
    }
    @JavascriptInterface
    public void Login() {
    }

    @JavascriptInterface
    public void SynLanding(String LoginId, String account) {

    }

    @JavascriptInterface
    public int showLoginDialog(int action) {
        return action;

    }

    @JavascriptInterface
    public String getLoginInfo() {
        return null;
    }
    @JavascriptInterface
    public String getDeviceInfo() {
        return null;
    }
    @JavascriptInterface
    public void Regist() {
    }

    @JavascriptInterface
    public void GoDetail(int num, int count, String source, String sourceUrl) {
    }

    @JavascriptInterface
    public void NoLoading_Move(String source, String sourceUrl) {
    }

    @JavascriptInterface
    public void Loading_Move(String source, String sourceUrl) {
    }

    @JavascriptInterface
    public void OriginalLink(String sourceUrl) {
    }

    @JavascriptInterface
    public void NewOriginalLink(String sourceUrl) {
    }

    @JavascriptInterface
    public void createDeskShortCut(String source, String sourceID,
                                   String iniPATH) {
    }

    @JavascriptInterface
    public void startTbsweb() {
    }

    @JavascriptInterface
    public void showDetail(int num, int count, String source, String sourceUrl) {
    }

    @JavascriptInterface
    public void showNewDetail(int num, int count, String source,
                              String sourceUrl) {
    }

    @JavascriptInterface
    public void getHtmlSource(String category, String title, String urlPath) {

    }

    @JavascriptInterface
    public boolean isHtmlSaven(String category, String urlPath) {
        return false;
    }

    @JavascriptInterface
    public void showToast(String content) {

    }

    @JavascriptInterface
    public void showMyApplication() {
    }

    @JavascriptInterface
    public void showMySkyDrive() {
    }

    @JavascriptInterface
    public void showMyCloud() {
    }

    @JavascriptInterface
    public void showMyEmail() {
    }

    @JavascriptInterface
    public void showMyResource() {
    }

    @JavascriptInterface
    public void showSearchPage() {
    }

    @JavascriptInterface
    public void showSettingPage() {
    }

    @JavascriptInterface
    public void showDisplaySettingPage() {
    }

    @JavascriptInterface
    public void showAppearanceSettingPage() {
    }
    @JavascriptInterface
    public void showMobileNetworkSettingPage() {
    }

    @JavascriptInterface
    public void showNetworkSettingPage() {
    }

    @JavascriptInterface
    public void showApplicationSettingPage() {
    }

    @JavascriptInterface
    public void showApplicationManagerPage() {
    }

    @JavascriptInterface
    public void showSubscribePage() {
    }

    @JavascriptInterface
    public void showInfoPage() {
    }

    @JavascriptInterface
    public void showInformationPushPage() {
    }

    @JavascriptInterface
    public void showOfflineDownloadPage() {
    }

    @JavascriptInterface
    public void showLogManagerPage() {
    }

    @JavascriptInterface
    public void showCommentManagerPage() {
    }

    @JavascriptInterface
    public void showDeviceManagerPage() {
    }

    @JavascriptInterface
    public void showFileManagerPage() {
    }

    @JavascriptInterface
    public void showFeedBackPage() {
    }

    @JavascriptInterface
    public void showAboutPage() {
    }

    @JavascriptInterface
    public void showQuitDialog() {

    }

    @JavascriptInterface
    public void showQuitAllDialog() {

    }
    @JavascriptInterface
    public void showLive(String info) {

    }
    @JavascriptInterface
    public void showVideoPublish() {
    }
    @JavascriptInterface
    public void showVideo(String code) {

    }
    @JavascriptInterface
    public void showVideoORAudio(int type,String code) {

    }
    @JavascriptInterface
    public void showOnlineVideo(String path, String title, String chapter) {

    }
    @JavascriptInterface
    public void showWebVideo(String category,String path, String title, String chapter) {
    }

    @JavascriptInterface
    public void showOverview(String source, String sourceUrl) {
    }

    @JavascriptInterface
    public void showPopWindow(String sourceUrl) {
    }

    @JavascriptInterface
    public void showLocalSettingpage() {
    }

    @JavascriptInterface
    public void showLeftWindow(int action) {

    }

    @JavascriptInterface
    public void showRightWindow(int action) {

    }

    @JavascriptInterface
    public void showMainMenu() {
    }

//    @JavascriptInterface
//    public void showMainSetMenu() {
//    }

    @JavascriptInterface
    public void getFavouriteFlag(int num) {
    }

    @JavascriptInterface
    public void getDeleteFlag(int num) {
    }

    @JavascriptInterface
    public void getTitle(String name) {
    }

    @JavascriptInterface
    public void share(String name, String url) {
    }

    @JavascriptInterface
    public void setActionName(String action, String name) {
    }

    @JavascriptInterface
    public void setMainTitle(String title) {
    }

    @JavascriptInterface
    public void setLeftTitle(String title) {
    }

    @JavascriptInterface
    public void setRightTitle(String title) {
    }

    @JavascriptInterface
    public void setDefLib(String source, String sourceID) {
    }

    @JavascriptInterface
    public void CloseDetail() {
    }

    @JavascriptInterface
    public void ClosePop() {
    }

    @JavascriptInterface
    public void downloadFile(String url, String savePath, boolean ishowProgress) {

    }
    @JavascriptInterface
    public void backgroundDownFile(String url, String savePath) {

    }
    @JavascriptInterface
    public void openFile(String Type, String Path) {

    }

    @JavascriptInterface
    public void updateData(String category) {
    }

    @JavascriptInterface
    public void uploadFile(String category) {
    }

    @JavascriptInterface
    public int CheckVersion(String fileName, String timer) {
        return 0;
    }

    @JavascriptInterface
    public int CheckFileVersion(String fileName, String timer) {
        return 0;
    }

    @JavascriptInterface
    public void FileStoreInfo(String dirPath, String fileinfo) {

    }

    @JavascriptInterface
    public void appUpdate() {

    }

    @JavascriptInterface
    public void StartApp(String fileName) {
    }
    @JavascriptInterface
    public void OpenWebFile(String fileInfo, String path) {
    }
    @JavascriptInterface
    public void openApp(String appName, String sourceID) {
    }

    @JavascriptInterface
    public void wxPay(String orderId, String money) {
    }

    @JavascriptInterface
    public void aliPay(String orderId, String money) {
    }
}