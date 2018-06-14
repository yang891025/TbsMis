package com.tbs.tbsmis.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by TBS on 2016/11/1.
 */

public class getMediaType
{
    Class<?> mMediaFile, mMediaFileType;
    Method getFileTypeMethod, isAudioFileTypeMethod, isVideoFileTypeMethod, isImageFileTypeMethod;
    String methodName = "getBoolean";
    String getFileType = "getFileType";

    String isAudioFileType = "isAudioFileType";
    String isVideoFileType = "isVideoFileType";
    String isImageFileType = "isImageFileType";

    Field fileType;

    public void initReflect() {
        try {
            this.mMediaFile = Class.forName("android.media.MediaFile");
            this.mMediaFileType = Class.forName("android.media.MediaFile$MediaFileType");

            this.fileType = this.mMediaFileType.getField("fileType");

            this.getFileTypeMethod = this.mMediaFile.getMethod(this.getFileType, String.class);

            this.isAudioFileTypeMethod = this.mMediaFile.getMethod(this.isAudioFileType, int.class);
            this.isVideoFileTypeMethod = this.mMediaFile.getMethod(this.isVideoFileType, int.class);
            this.isImageFileTypeMethod = this.mMediaFile.getMethod(this.isImageFileType, int.class);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public int getMediaFileType(String path) {

        int type = 0;

        try {
            Object obj = this.getFileTypeMethod.invoke(this.mMediaFile, path);
            if (obj == null) {
                type = -1;
            } else {
                type = this.fileType.getInt(obj);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return type;
    }

    public boolean isAudioFile(int fileType) {
        boolean isAudioFile = false;
        try {
            isAudioFile = (Boolean) this.isAudioFileTypeMethod.invoke(this.mMediaFile, fileType);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return isAudioFile;
    }

    public boolean isVideoFile(int fileType) {
        boolean isVideoFile = false;
        try {
            isVideoFile = (Boolean) this.isVideoFileTypeMethod.invoke(this.mMediaFile, fileType);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return isVideoFile;
    }

    public boolean isImageFile(int fileType) {
        boolean isImageFile = false;
        try {
            isImageFile = (Boolean) this.isImageFileTypeMethod.invoke(this.mMediaFile, fileType);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return isImageFile;
    }
}
