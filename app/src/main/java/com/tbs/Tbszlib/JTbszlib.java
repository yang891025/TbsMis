package com.tbs.Tbszlib;

public class JTbszlib {
	
    static
    {
    	try{
    	 System.loadLibrary("gnustl_shared");
        System.loadLibrary("TbszlibDll");
    	}catch(UnsatisfiedLinkError e)
    	{
    		
    	}
    }

    private JTbszlib(){
    	
    }

   
   public static native int EnZipDir(String strDir, String zipFile, int withdir, int overwrite, String strPassword);
   public static native int EnZipFile(String strFile, String strRelativePath, String zipFile, int withdir, int overwrite, String strPassword);
   public static native int EnZipAddFile(String strZipFile, String strFilePath, String strRelativePath, int withdir, int overwrite, String strPassword);
	public static native int EnZipAddDir(String strZipFile, String strDesDir, String strRelativePath, int withdir, int overwrite, String strPassword);
	public static native int EnZipAddBuf(String strZipFile, String strBuf, int len, String strFileName, String strPassword);
	public static native int EnZipAddFileList(String strZipFile, String strFileList, String strRelativePath, int withdir, int overwrite, String strPassword);
    
    public static native int UnZipFile(String strZipFile, String strDesPath, int withdir, String strPassword);
	public static native int UnZipAppointFile(String strZipFile, String strAppointFile, String strDesFile, int withdir, String strPassword);
	public static native String UnZipFileToBuf(String strZipFile, String strDesPath, String strPassword);
	public static native String UnZipFileList(String strZipFile, String strPassword);
	
	public static native long OpenZip(String strDir, int nType);
	public static native int EnZipDir(String strDir, long zipFile, int withdir, int overwrite, String strPassword, String strRelative);
	public static native int EnZipFile(String strFile, String strRelativePath, long zipFile, int withdir, int overwrite, String strPassword);
	public static native int EnZipAddBuf(long strZipFile, String strBuf, int len, String strFileName, String strPassword);
	public static native int EnZipAddFileList(long strZipFile, String strFileList, String strRelativePath, int withdir, int overwrite, String strPassword);
	public static native void CloseZip(long zipFile);
	

	public static void main(String []args)
	{
		//EnZipDir("/home/zhug/temp/test","/home/zhug/temp/test.zip",1,1,"");
	}
    
}
