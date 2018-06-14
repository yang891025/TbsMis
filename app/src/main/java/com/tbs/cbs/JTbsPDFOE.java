package com.tbs.cbs;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2006-7-28
 * Time: 11:39:43
 * To change this template use File | Settings | File Templates.
 */
public class JTbsPDFOE {
    static
    {
	System.loadLibrary("TBSPDFOE");
        System.loadLibrary("TBSPDFOE");
    }

    private JTbsPDFOE(){}

    public static native String GetAuthMode(String strPDFFile);
    public static native int EnPDF(String StrPDFFile, String strUserName, String strUserInfo, int nDays, int nDenyAll);
    public static native int AuthPDF(String strPDFFile, String strMachineInfo, int nMachineCode, int nDenyAll);
    public static native int AddLocalMachine(String strPath);
}
