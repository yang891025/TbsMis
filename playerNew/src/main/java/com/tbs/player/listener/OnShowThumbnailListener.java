package com.tbs.player.listener;

import android.widget.ImageView;

/**
 * ========================================
 * 版 本：1.0
 * <p>
 * 创建日期：2016/8/10 15:28
 * <p>
 * 描 述：显示视频缩列图监听
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public interface OnShowThumbnailListener {

    /**回传封面的view，让用户自主设置*/
    void onShowThumbnail(ImageView ivThumbnail);
}