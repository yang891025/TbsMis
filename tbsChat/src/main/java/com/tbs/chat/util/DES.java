package com.tbs.chat.util;

import java.security.*;   
  
import javax.crypto.Cipher;   
import javax.crypto.SecretKey;   
import javax.crypto.SecretKeyFactory;   
import javax.crypto.spec.DESKeySpec;   
  
/**  
 * 字符串工具集合  
 * @author  
 */  
public class DES {
  
    private static final String PASSWORD_CRYPT_KEY = ".?'_=1dt"; //密钥   
    private final static String DES = "DES"; //DES算法名称   
  
    /**  
     * 加密  
     * @param src 数据源  
     * @param key 密钥，长度必须是8的倍数  
     * @return 返回加密后的数据  
     * @throws Exception  
     */  
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {   
        // DES算法要求有一个可信任的随机数源   
        SecureRandom sr = new SecureRandom();   
        // 从原始密匙数据创建DESKeySpec对象   
        DESKeySpec dks = new DESKeySpec(key); 
      
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象   
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);   
        
        // Cipher对象实际完成加密操作   
        Cipher cipher = Cipher.getInstance(DES);  
       
        // 用密匙初始化Cipher对象   
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);   
       
        // 现在，获取数据并加密   
        // 正式执行加密操作   
        return cipher.doFinal(src);   
    }   
  
    /**  
     * 解密  
     *   
     * @param src  
     *            数据源  
     * @param key  
     *            密钥，长度必须是8的倍数  
     * @return 返回解密后的原始数据  
     * @throws Exception  
     */  
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {   
        // DES算法要求有一个可信任的随机数源   
        SecureRandom sr = new SecureRandom();   
        // 从原始密匙数据创建一个DESKeySpec对象   
        DESKeySpec dks = new DESKeySpec(key);   
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成   
        // 一个SecretKey对象   
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);   
        SecretKey securekey = keyFactory.generateSecret(dks);   
        // Cipher对象实际完成解密操作   
        Cipher cipher = Cipher.getInstance(DES);   
        // 用密匙初始化Cipher对象   
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);   
        // 现在，获取数据并解密   
        // 正式执行解密操作   
        return cipher.doFinal(src);   
    }   
  
    /**  
     * 二行制转字符串  
     * @param b  
     * @return  
     */  
    public static String byte2hex(byte[] b) {  
    	System.out.println(b.length + "...");
    	for(int i=0;i<b.length;i++){
    		System.out.println(b[i]);
    	}
        String hs = "";   
  
        String stmp = "";   
  
        for (int n = 0; n < b.length; n++) {   
  
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));   
            if (stmp.length() == 1)   
                hs = hs + "0" + stmp;   
            else  
                hs = hs + stmp;   
        }   
  
        return hs.toUpperCase();   
  
    }   
  
    /**  
     * 字符串二行制  
     * @param b  
     * @return  
     */  
    public static byte[] hex2byte(byte[] b) {   
  
        if ((b.length % 2) != 0)   
            throw new IllegalArgumentException("长度不是偶数");   
        byte[] b2 = new byte[b.length / 2];   
        for (int n = 0; n < b.length; n += 2) {   
            String item = new String(b, n, 2);   
            b2[n / 2] = (byte) Integer.parseInt(item, 16);   
        }   
        return b2;   
    }  
    
    public static String encrypt(String src){
    	
    	byte b[] = null;
    	try {
			b = encrypt(src.getBytes(), PASSWORD_CRYPT_KEY.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String encryptKey = byte2hex(b);   
        return encryptKey;
    }
    
    public static String decrypt(String src){
    	byte b[] = null;
    	try {
			b = decrypt(hex2byte(src.getBytes()), PASSWORD_CRYPT_KEY.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String decryptKey = new String(b);
    	return decryptKey;
    }
  
    public static void main(String[] args) {
		String s = "password=123456";
		String s2 = encrypt(s);
		String s3 = decrypt(s2);
		System.out.println(s);
		System.out.println(s2);
		System.out.println(s3);
	}  
    
    
    
    
}    