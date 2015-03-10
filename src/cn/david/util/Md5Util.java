package cn.david.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

	private static MessageDigest md ;
	
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("no such algorithm");
		}
	}
	public static String encode(String str) {
		byte[] b = null;
		String out = "";
		try {
			b = str.getBytes("UTF-8");
			byte[] b1 = md.digest(b);
			out = new String(b1,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("nsupportedEncoding");
		}
		
		return out;
	}
	
}
