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

import android.mtp.MtpConstants;

import java.util.HashMap;

public class MediaFile {

    // Audio file types
    public static final int FILE_TYPE_MP3 = 1;
    public static final int FILE_TYPE_M4A = 2;
    public static final int FILE_TYPE_WAV = 3;
    public static final int FILE_TYPE_AMR = 4;
    public static final int FILE_TYPE_AWB = 5;
    public static final int FILE_TYPE_WMA = 6;
    public static final int FILE_TYPE_OGG = 7;
    public static final int FILE_TYPE_AAC = 8;
    public static final int FILE_TYPE_MKA = 9;
    public static final int FILE_TYPE_FLAC = 10;
    private static final int FIRST_AUDIO_FILE_TYPE = MediaFile.FILE_TYPE_MP3;
    private static final int LAST_AUDIO_FILE_TYPE = MediaFile.FILE_TYPE_FLAC;

    // MIDI file types
    public static final int FILE_TYPE_MID = 11;
    public static final int FILE_TYPE_SMF = 12;
    public static final int FILE_TYPE_IMY = 13;
    private static final int FIRST_MIDI_FILE_TYPE = MediaFile.FILE_TYPE_MID;
    private static final int LAST_MIDI_FILE_TYPE = MediaFile.FILE_TYPE_IMY;

    // Video file types
    public static final int FILE_TYPE_MP4 = 21;
    public static final int FILE_TYPE_M4V = 22;
    public static final int FILE_TYPE_3GPP = 23;
    public static final int FILE_TYPE_3GPP2 = 24;
    public static final int FILE_TYPE_WMV = 25;
    public static final int FILE_TYPE_ASF = 26;
    public static final int FILE_TYPE_MKV = 27;
    public static final int FILE_TYPE_MP2TS = 28;
    public static final int FILE_TYPE_AVI = 29;
    public static final int FILE_TYPE_WEBM = 30;
    private static final int FIRST_VIDEO_FILE_TYPE = MediaFile.FILE_TYPE_MP4;
    private static final int LAST_VIDEO_FILE_TYPE = MediaFile.FILE_TYPE_WEBM;

    // Image file types
    public static final int FILE_TYPE_JPEG = 31;
    public static final int FILE_TYPE_GIF = 32;
    public static final int FILE_TYPE_PNG = 33;
    public static final int FILE_TYPE_BMP = 34;
    public static final int FILE_TYPE_WBMP = 35;
    public static final int FILE_TYPE_WEBP = 36;
    private static final int FIRST_IMAGE_FILE_TYPE = MediaFile.FILE_TYPE_JPEG;
    private static final int LAST_IMAGE_FILE_TYPE = MediaFile.FILE_TYPE_WEBP;

    public static class MediaFileType {
        public final int fileType;
        public final String mimeType;

        MediaFileType(int fileType, String mimeType) {
            this.fileType = fileType;
            this.mimeType = mimeType;
        }
    }

    private static final HashMap<String, MediaFile.MediaFileType> sFileTypeMap = new HashMap<String, MediaFile.MediaFileType>();
    private static final HashMap<String, Integer> sMimeTypeMap = new HashMap<String, Integer>();

    static void addFileType(String extension, int fileType, String mimeType) {
        MediaFile.sFileTypeMap.put(extension, new MediaFile.MediaFileType(fileType, mimeType));
        MediaFile.sMimeTypeMap.put(mimeType, Integer.valueOf(fileType));
    }

    static void addFileType(String extension, int fileType, String mimeType, int mtpFormatCode) {
        MediaFile.addFileType(extension, fileType, mimeType);
    }

    static {
        MediaFile.addFileType("MP3", MediaFile.FILE_TYPE_MP3, "audio/mpeg", MtpConstants.FORMAT_MP3);
        MediaFile.addFileType("M4A", MediaFile.FILE_TYPE_M4A, "audio/mp4", MtpConstants.FORMAT_MPEG);
        MediaFile.addFileType("WAV", MediaFile.FILE_TYPE_WAV, "audio/x-wav", MtpConstants.FORMAT_WAV);
        MediaFile.addFileType("AMR", MediaFile.FILE_TYPE_AMR, "audio/amr");
        MediaFile.addFileType("AWB", MediaFile.FILE_TYPE_AWB, "audio/amr-wb");
        MediaFile.addFileType("OGG", MediaFile.FILE_TYPE_OGG, "application/ogg", MtpConstants.FORMAT_OGG);
        MediaFile.addFileType("OGA", MediaFile.FILE_TYPE_OGG, "application/ogg", MtpConstants.FORMAT_OGG);
        MediaFile.addFileType("AAC", MediaFile.FILE_TYPE_AAC, "audio/aac", MtpConstants.FORMAT_AAC);
        MediaFile.addFileType("AAC", MediaFile.FILE_TYPE_AAC, "audio/aac-adts", MtpConstants.FORMAT_AAC);
        MediaFile.addFileType("MKA", MediaFile.FILE_TYPE_MKA, "audio/x-matroska");

        MediaFile.addFileType("MPEG", MediaFile.FILE_TYPE_MP4, "video/mpeg", MtpConstants.FORMAT_MPEG);
        MediaFile.addFileType("MPG", MediaFile.FILE_TYPE_MP4, "video/mpeg", MtpConstants.FORMAT_MPEG);
        MediaFile.addFileType("MP4", MediaFile.FILE_TYPE_MP4, "video/mp4", MtpConstants.FORMAT_MPEG);
        MediaFile.addFileType("M4V", MediaFile.FILE_TYPE_M4V, "video/mp4", MtpConstants.FORMAT_MPEG);
        MediaFile.addFileType("3GP", MediaFile.FILE_TYPE_3GPP, "video/3gpp", MtpConstants.FORMAT_3GP_CONTAINER);
        MediaFile.addFileType("3GPP", MediaFile.FILE_TYPE_3GPP, "video/3gpp", MtpConstants.FORMAT_3GP_CONTAINER);
        MediaFile.addFileType("3G2", MediaFile.FILE_TYPE_3GPP2, "video/3gpp2", MtpConstants.FORMAT_3GP_CONTAINER);
        MediaFile.addFileType("3GPP2", MediaFile.FILE_TYPE_3GPP2, "video/3gpp2", MtpConstants.FORMAT_3GP_CONTAINER);
        MediaFile.addFileType("MKV", MediaFile.FILE_TYPE_MKV, "video/x-matroska");
        MediaFile.addFileType("WEBM", MediaFile.FILE_TYPE_WEBM, "video/webm");
        MediaFile.addFileType("TS", MediaFile.FILE_TYPE_MP2TS, "video/mp2ts");
        MediaFile.addFileType("AVI", MediaFile.FILE_TYPE_AVI, "video/avi");

        MediaFile.addFileType("JPG", MediaFile.FILE_TYPE_JPEG, "image/jpeg", MtpConstants.FORMAT_EXIF_JPEG);
        MediaFile.addFileType("JPEG", MediaFile.FILE_TYPE_JPEG, "image/jpeg", MtpConstants.FORMAT_EXIF_JPEG);
        MediaFile.addFileType("GIF", MediaFile.FILE_TYPE_GIF, "image/gif", MtpConstants.FORMAT_GIF);
        MediaFile.addFileType("PNG", MediaFile.FILE_TYPE_PNG, "image/png", MtpConstants.FORMAT_PNG);
        MediaFile.addFileType("BMP", MediaFile.FILE_TYPE_BMP, "image/x-ms-bmp", MtpConstants.FORMAT_BMP);
        MediaFile.addFileType("WBMP", MediaFile.FILE_TYPE_WBMP, "image/vnd.wap.wbmp");
        MediaFile.addFileType("WEBP", MediaFile.FILE_TYPE_WEBP, "image/webp");
    }

    public static boolean isAudioFileType(int fileType) {
        return fileType >= MediaFile.FIRST_AUDIO_FILE_TYPE && fileType <= MediaFile.LAST_AUDIO_FILE_TYPE || fileType >= MediaFile
                .FIRST_MIDI_FILE_TYPE && fileType <= MediaFile.LAST_MIDI_FILE_TYPE;
    }

    public static boolean isVideoFileType(int fileType) {
        return fileType >= MediaFile.FIRST_VIDEO_FILE_TYPE && fileType <= MediaFile.LAST_VIDEO_FILE_TYPE;
    }

    public static boolean isImageFileType(int fileType) {
        return fileType >= MediaFile.FIRST_IMAGE_FILE_TYPE && fileType <= MediaFile.LAST_IMAGE_FILE_TYPE;
    }

    public static MediaFile.MediaFileType getFileType(String path) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0)
            return null;
        return MediaFile.sFileTypeMap.get(path.substring(lastDot + 1).toUpperCase());
    }
}
