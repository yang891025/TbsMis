package com.tbs.tbsmis.file;

import com.tbs.fts.IFtMSGNotify;

public class FtMSGNotify implements IFtMSGNotify
{

    private static boolean bStart;
    private static long iTotal;
    private static long iProg;
    private static boolean bCancelFlag;
    private static String FlieName;
    private static int iCancelFlag;
    private static long PiProg;
    private static int fileCount;
    private static int Cfilecount;
    private static String desPath = "";
    private static String allPath = "";
    private static String allmessage = "";
    private static long totalSize;
    private static long iSize;

    public FtMSGNotify() {
    }

    public void setTotal(long bytes) {
        if (!FtMSGNotify.bStart) {

            FtMSGNotify.bStart = true;
        }
        FtMSGNotify.iTotal = bytes;
    }

    public long getTotal() {
        return FtMSGNotify.iTotal;
    }

    public boolean getbStart() {
        return FtMSGNotify.bStart;
    }

    public void setProg(long bytes) {
        FtMSGNotify.iProg = bytes;
    }

    public long getProg() {
        return FtMSGNotify.iProg;
    }

    public String CurrentFile() {
        return FtMSGNotify.FlieName;
    }

    public boolean getCancelFlag() {
        return FtMSGNotify.bCancelFlag;
    }

    public void setCancelFlag(boolean bFlag) {
        FtMSGNotify.bCancelFlag = bFlag;
    }


    public void setCurrFile(String strFile) {
        // TODO Auto-generated method stub
        FtMSGNotify.FlieName = strFile;
    }

    public int getiCancelFlag() {
        return FtMSGNotify.iCancelFlag;
    }

    public void setiCancelFlag(int iCancelFlag) {
        FtMSGNotify.iCancelFlag = iCancelFlag;
    }

    public long getPiProg() {
        return FtMSGNotify.PiProg;
    }

    public void setPiProg(long piProg) {
        FtMSGNotify.PiProg = piProg;
    }


    public int getCfilecount() {
        return FtMSGNotify.Cfilecount;
    }

    public void setCfilecount(int cfilecount) {
        FtMSGNotify.Cfilecount = cfilecount;
    }

    public int getFileCount() {
        return FtMSGNotify.fileCount;
    }

    public void setFileCount(int fileCount) {
        FtMSGNotify.fileCount = fileCount;
    }

    public String getDesPath() {
        return FtMSGNotify.desPath;
    }

    public void setDesPath(String desPath) {
        FtMSGNotify.desPath = desPath;
    }

    public String getAllPath() {
        return FtMSGNotify.allPath;
    }

    public void setAllPath(String allPath) {
        FtMSGNotify.allPath = allPath;
    }

    public long getTotalSize() {
        return FtMSGNotify.totalSize;
    }

    public void setTotalSize(long totalSize) {
        FtMSGNotify.totalSize = totalSize;
    }

    public String getAllmessage() {
        return FtMSGNotify.allmessage;
    }

    public void setAllmessage(String allmessage) {
        FtMSGNotify.allmessage = allmessage;
    }

    public long getiSize() {
        return FtMSGNotify.iSize;
    }

    public void setiSize(long iSize) {
        FtMSGNotify.iSize = iSize;
    }
}
