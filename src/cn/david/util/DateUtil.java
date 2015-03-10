package cn.david.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static DateFormat df = new SimpleDateFormat("HH:mm:ss");
	
	public static String printCurTime() {
		return df.format(new Date());
	}
}
