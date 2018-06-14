/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.widget.ImageView;

/**
 * Asynchronously loads file icons and thumbnail, mostly single-threaded.
 */
public class FileIconLoader implements Handler.Callback {

    private static final String LOADER_THREAD_NAME = "FileIconLoader";

    /**
     * Type of message sent by the UI thread to itself to indicate that some
     * photos need to be loaded.
     */
    private static final int MESSAGE_REQUEST_LOADING = 1;

    /**
     * Type of message sent by the loader thread to indicate that some photos
     * have been loaded.
     */
    private static final int MESSAGE_ICON_LOADED = 2;

    private abstract static class ImageHolder {
        public static final int NEEDED = 0;

        public static final int LOADING = 1;

        public static final int LOADED = 2;

        int state;

        public static FileIconLoader.ImageHolder create(FileCategoryHelper.FileCategory cate) {
            switch (cate) {
                case Apk:
                    return new FileIconLoader.DrawableHolder();
                case Picture:
                case Video:
                    return new FileIconLoader.BitmapHolder();
            }

            return null;
        }

        public abstract boolean setImageView(ImageView v);

        public abstract boolean isNull();

        public abstract void setImage(Object image);
    }

    private static class BitmapHolder extends FileIconLoader.ImageHolder {
        SoftReference<Bitmap> bitmapRef;

        @Override
        public boolean setImageView(ImageView v) {
            if (this.bitmapRef.get() == null)
                return false;
            v.setImageBitmap(this.bitmapRef.get());
            return true;
        }

        @Override
        public boolean isNull() {
            return this.bitmapRef == null;
        }

        @Override
        public void setImage(Object image) {
            this.bitmapRef = image == null ? null : new SoftReference<Bitmap>((Bitmap) image);
        }
    }

    private static class DrawableHolder extends FileIconLoader.ImageHolder {
        SoftReference<Drawable> drawableRef;

        @Override
        public boolean setImageView(ImageView v) {
            if (this.drawableRef.get() == null)
                return false;

            v.setImageDrawable(this.drawableRef.get());
            return true;
        }

        @Override
        public boolean isNull() {
            return this.drawableRef == null;
        }

        @Override
        public void setImage(Object image) {
            this.drawableRef = image == null ? null : new SoftReference<Drawable>((Drawable) image);
        }
    }

    /**
     * A soft cache for image thumbnails. the key is file path
     */
    private static final ConcurrentHashMap<String, FileIconLoader.ImageHolder> mImageCache = new ConcurrentHashMap<String, FileIconLoader.ImageHolder>();

    /**
     * A map from ImageView to the corresponding photo ID. Please note that this
     * photo ID may change before the photo loading request is started.
     */
    private final ConcurrentHashMap<ImageView, FileIconLoader.FileId> mPendingRequests = new ConcurrentHashMap<ImageView, FileIconLoader.FileId>();

    /**
     * Handler for messages sent to the UI thread.
     */
    private final Handler mMainThreadHandler = new Handler(this);

    /**
     * Thread responsible for loading photos from the database. Created upon the
     * first request.
     */
    private FileIconLoader.LoaderThread mLoaderThread;

    /**
     * A gate to make sure we only send one instance of MESSAGE_PHOTOS_NEEDED at
     * a time.
     */
    private boolean mLoadingRequested;

    /**
     * Flag indicating if the image loading is paused.
     */
    private boolean mPaused;

    private final Context mContext;

    private final FileIconLoader.IconLoadFinishListener iconLoadListener;

    /**
     * Constructor.
     *
     * @param context content context
     */
    public FileIconLoader(Context context, FileIconLoader.IconLoadFinishListener l) {
        this.mContext = context;
        this.iconLoadListener = l;
    }

    public static class FileId {
        public String mPath;

        public long mId; // database id

        public FileCategoryHelper.FileCategory mCategory;

        public FileId(String path, long id, FileCategoryHelper.FileCategory cate) {
            this.mPath = path;
            this.mId = id;
            this.mCategory = cate;
        }
    }

    public interface IconLoadFinishListener {
        void onIconLoadFinished(ImageView view);
    }

    /**
     * Load photo into the supplied image view. If the photo is already cached,
     * it is displayed immediately. Otherwise a request is sent to load the
     * photo from the database.
     *
     * @param id, database id
     */
    public boolean loadIcon(ImageView view, String path, long id, FileCategoryHelper.FileCategory cate) {
        boolean loaded = this.loadCachedIcon(view, path, cate);
        if (loaded) {
            this.mPendingRequests.remove(view);
        } else {
            FileIconLoader.FileId p = new FileIconLoader.FileId(path, id, cate);
            this.mPendingRequests.put(view, p);
            if (!this.mPaused) {
                // Send a request to start loading photos
                this.requestLoading();
            }
        }
        return loaded;
    }

    public void cancelRequest(ImageView view) {
        this.mPendingRequests.remove(view);
    }

    /**
     * Checks if the photo is present in cache. If so, sets the photo on the
     * view, otherwise sets the state of the photo to
     * {@link FileIconLoader.BitmapHolder#NEEDED}
     */
    private boolean loadCachedIcon(ImageView view, String path, FileCategoryHelper.FileCategory cate) {
        FileIconLoader.ImageHolder holder = FileIconLoader.mImageCache.get(path);

        if (holder == null) {
            holder = FileIconLoader.ImageHolder.create(cate);
            if (holder == null)
                return false;

            FileIconLoader.mImageCache.put(path, holder);
        } else if (holder.state == FileIconLoader.ImageHolder.LOADED) {
            if (holder.isNull()) {
                return true;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if (holder.setImageView(view)) {
                return true;
            }
        }

        holder.state = FileIconLoader.ImageHolder.NEEDED;
        return false;
    }

    public long getDbId(String path, boolean isVideo) {
        String volumeName = "external";
        Uri uri = isVideo ? MediaStore.Video.Media.getContentUri(volumeName) : Media.getContentUri(volumeName);
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = {
            path
        };

        String[] columns = {
                BaseColumns._ID, MediaStore.MediaColumns.DATA
        };

        Cursor c = this.mContext.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return 0;
        }
        long id = 0;
        if (c.moveToNext()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }

    /**
     * Stops loading images, kills the image loader thread and clears all
     * caches.
     */
    public void stop() {
        this.pause();

        if (this.mLoaderThread != null) {
            this.mLoaderThread.quit();
            this.mLoaderThread = null;
        }

        this.clear();
    }

    public void clear() {
        this.mPendingRequests.clear();
        FileIconLoader.mImageCache.clear();
    }

    /**
     * Temporarily stops loading
     */
    public void pause() {
        this.mPaused = true;
    }

    /**
     * Resumes loading
     */
    public void resume() {
        this.mPaused = false;
        if (!this.mPendingRequests.isEmpty()) {
            this.requestLoading();
        }
    }

    /**
     * Sends a message to this thread itself to start loading images. If the
     * current view contains multiple image views, all of those image views will
     * get a chance to request their respective photos before any of those
     * requests are executed. This allows us to load images in bulk.
     */
    private void requestLoading() {
        if (!this.mLoadingRequested) {
            this.mLoadingRequested = true;
            this.mMainThreadHandler.sendEmptyMessage(FileIconLoader.MESSAGE_REQUEST_LOADING);
        }
    }

    /**
     * Processes requests on the main thread.
     */
    @Override
	public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case FileIconLoader.MESSAGE_REQUEST_LOADING:
                this.mLoadingRequested = false;
                if (!this.mPaused) {
                    if (this.mLoaderThread == null) {
                        this.mLoaderThread = new FileIconLoader.LoaderThread();
                        this.mLoaderThread.start();
                    }

                    this.mLoaderThread.requestLoading();
                }
                return true;

            case FileIconLoader.MESSAGE_ICON_LOADED:
                if (!this.mPaused) {
                    this.processLoadedIcons();
                }
                return true;
        }
        return false;
    }

    /**
     * Goes over pending loading requests and displays loaded photos. If some of
     * the photos still haven't been loaded, sends another request for image
     * loading.
     */
    private void processLoadedIcons() {
        Iterator<ImageView> iterator = this.mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            ImageView view = iterator.next();
            FileIconLoader.FileId fileId = this.mPendingRequests.get(view);
            boolean loaded = this.loadCachedIcon(view, fileId.mPath, fileId.mCategory);
            if (loaded) {
                iterator.remove();
                this.iconLoadListener.onIconLoadFinished(view);
            }
        }

        if (!this.mPendingRequests.isEmpty()) {
            this.requestLoading();
        }
    }

    /**
     * The thread that performs loading of photos from the database.
     */
    private class LoaderThread extends HandlerThread implements Handler.Callback {
        private Handler mLoaderThreadHandler;

        public LoaderThread() {
            super(FileIconLoader.LOADER_THREAD_NAME);
        }

        /**
         * Sends a message to this thread to load requested photos.
         */
        public void requestLoading() {
            if (this.mLoaderThreadHandler == null) {
                this.mLoaderThreadHandler = new Handler(this.getLooper(), this);
            }
            this.mLoaderThreadHandler.sendEmptyMessage(0);
        }

        /**
         * Receives the above message, loads photos and then sends a message to
         * the main thread to process them.
         */
        @Override
		public boolean handleMessage(Message msg) {
            Iterator<FileIconLoader.FileId> iterator = FileIconLoader.this.mPendingRequests.values().iterator();
            while (iterator.hasNext()) {
                FileIconLoader.FileId id = iterator.next();
                FileIconLoader.ImageHolder holder = FileIconLoader.mImageCache.get(id.mPath);
                if (holder != null && holder.state == FileIconLoader.ImageHolder.NEEDED) {
                    // Assuming atomic behavior
                    holder.state = FileIconLoader.ImageHolder.LOADING;
                    switch (id.mCategory) {
                        case Apk:
                            Drawable icon = Util.getApkIcon(FileIconLoader.this.mContext, id.mPath);
                            holder.setImage(icon);
                            break;
                        case Picture:
                        case Video:
                            boolean isVideo = id.mCategory == FileCategoryHelper.FileCategory.Video;
                            if (id.mId == 0)
                                id.mId = FileIconLoader.this.getDbId(id.mPath, isVideo);
                            if (id.mId == 0) {
                                Log.e("FileIconLoader", "Fail to get dababase id for:" + id.mPath);
                            }
                            holder.setImage(isVideo ? this.getVideoThumbnail(id.mId) : this.getImageThumbnail(id.mId));
                            break;
                    }

                    holder.state = FileIconLoader.ImageHolder.LOADED;
                    FileIconLoader.mImageCache.put(id.mPath, holder);
                }
            }

            FileIconLoader.this.mMainThreadHandler.sendEmptyMessage(FileIconLoader.MESSAGE_ICON_LOADED);
            return true;
        }

        private static final int MICRO_KIND = 3;

        private Bitmap getImageThumbnail(long id) {
            return MediaStore.Images.Thumbnails.getThumbnail(FileIconLoader.this.mContext.getContentResolver(), id, FileIconLoader.LoaderThread.MICRO_KIND, null);
        }

        private Bitmap getVideoThumbnail(long id) {
            return Thumbnails.getThumbnail(FileIconLoader.this.mContext.getContentResolver(), id, FileIconLoader.LoaderThread.MICRO_KIND, null);
        }
    }
}
