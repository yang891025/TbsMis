package com.tbs.tbsmis.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private final int stub_id;
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private final Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    // 线程池
    ExecutorService executorService;

    public ImageLoader(Context context,int stub_id) {
        this.fileCache = new FileCache(context);
        this.executorService = Executors.newFixedThreadPool(5);
        this.stub_id = stub_id;
    }

    // 当进入listview时默认的图片，可换成你自己的默认图片
    //final int stub_id = drawable.dot_avatar;

    // 最主要的方法
    public void DisplayImage(String url, ImageView imageView) {
        this.imageViews.put(imageView, url);
        // 先从内存缓存中查找
        Bitmap bitmap = this.memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        else {
            // 若没有的话则开启新线程加载图片
            this.queuePhoto(url, imageView);
            imageView.setImageResource(this.stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        ImageLoader.PhotoToLoad p = new ImageLoader.PhotoToLoad(url, imageView);
        this.executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = this.fileCache.getFile(url);
        //System.out.println("file = " + f.getAbsolutePath());

        // 先从文件缓存中查找是否有
        Bitmap b = this.decodeFile(f);
        if (b != null)
            return b;
        if(url.toLowerCase().contains("http://")) {
            // 最后从指定的url中下载图片
            //System.out.println("url = " + url);
            try {
                Bitmap bitmap = null;
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl
                        .openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                ImageLoader.CopyStream(is, os);
                os.close();
                bitmap = this.decodeFile(f);
                return bitmap;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

        }else{

            getMediaType mide = new getMediaType();
            mide.initReflect();
            int type = mide.getMediaFileType(url);
            boolean isVideo = mide.isVideoFile(type);
            if(isVideo){
                return getVideoThumbnail(url,120,120, MediaStore.Images.Thumbnails.MICRO_KIND);

            }else {
                return getImageThumbnail(url,120,120);
            }
        }
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            Options o = new Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            Options o2 = new Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            this.url = u;
            this.imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        ImageLoader.PhotoToLoad photoToLoad;

        PhotosLoader(ImageLoader.PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (ImageLoader.this.imageViewReused(this.photoToLoad))
                return;
            Bitmap bmp = ImageLoader.this.getBitmap(this.photoToLoad.url);
            ImageLoader.this.memoryCache.put(this.photoToLoad.url, bmp);
            if (ImageLoader.this.imageViewReused(this.photoToLoad))
                return;
            ImageLoader.BitmapDisplayer bd = new ImageLoader.BitmapDisplayer(bmp, this.photoToLoad);
            // 更新的操作放在UI线程中
            Activity a = (Activity) this.photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 防止图片错位
     *
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(ImageLoader.PhotoToLoad photoToLoad) {
        String tag = this.imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    // 用于在UI线程中更新界面
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageLoader.PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, ImageLoader.PhotoToLoad p) {
            this.bitmap = b;
            this.photoToLoad = p;
        }

        @Override
        public void run() {
            if (ImageLoader.this.imageViewReused(this.photoToLoad))
                return;
            if (this.bitmap != null)
                this.photoToLoad.imageView.setImageBitmap(this.bitmap);
            else
                this.photoToLoad.imageView.setImageResource(ImageLoader.this.stub_id);
        }
    }

    public void clearCache() {
        this.memoryCache.clear();
        this.fileCache.clear();
    }

    public void memoryCache() {
        this.memoryCache.clear();
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        //bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        MediaMetadataRetriever media = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            bitmap = media.getFrameAtTime();
            return bitmap;
        }else{
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }
}