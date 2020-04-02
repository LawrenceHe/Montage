package com.zhaodongdb.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;

public class StringUtil {
	// GBK编码
	//public final static String ENCODE = "GBK"; // "GB18030";

	// ====================身份证验证参数==================
	final static int[] wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
	final static int[] vi = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };

	public static final String EMPTY = "";

	public static String getUnNullString(String inStr) {
		if (StringUtil.emptyOrNull(inStr)) {
			inStr = "";
		}
		return inStr;
	}

	public static boolean checkIp(String str) {
		boolean flag = false;
		String[] s = new String[4];
		// 检查是否只有数字和'.'。
		if (!str.matches("[0-9[\\.]]{1,16}")) {
			flag = false;
		}
		else {
			// 字符串中的数字字符分开存储一个数组中
			s = str.split("\\.");
			for (int i = 0; i < s.length; i++) {
				int a = Integer.parseInt(s[i]);
				// 转换为二进制进行匹配
				if (Integer.toBinaryString(a).matches("[0-1]{1,8}")) {
					flag = true;
				}
				else {
					flag = false;
					break;
				}
			}
		}
		// if(flag){
		// // System.out.println("ip is right");
		// }else{
		// // System.out.println("ip is wrong");
		// }
		return flag;
	}

	/**
	 * 校验字符串数组，是否EmptyOrNull
	 * 
	 * @param valueAndNameArr
	 *            二维数组
	 * @param errorInfo
	 * @return
	 */
	public static boolean checkEmptyOrNull(String[][] valueAndNameArr, StringBuilder errorInfo) {
		boolean flag = true;
		for (String[] valueAndName : valueAndNameArr) {
			if (StringUtil.emptyOrNull(valueAndName[0])) {
				errorInfo.append(valueAndName[1] + ",");
				flag = false;
			}
		}
		if (!flag) {
			errorInfo.append(" can't be emptyOrNull!");
		}
		return flag;
	}

	/**
	 * 判断字串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean emptyOrNull(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean equals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	public static boolean emptyOrNull(String... arrStr) {
		for (String str : arrStr) {
			if (emptyOrNull(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 除去string中的占位字符
	 *
	 * @param str old str
	 * @return new str
	 */
	public static String replaceBlank(String str) {
		if (emptyOrNull(str)) {
			return "";
		}
		return str.replace(" ", "");
	}

	/**
	 * 将string转为int ，异常时返回-1
	 * 
	 * @param s
	 * @return
	 */
	public static int toInt(String s) {
		int i = 0;
		try {
			i = Integer.parseInt(s);
		}
		catch (Exception e) {
			i = -1;
		}
		return i;
	}

	/**
	 * 将String转换为int，异常时，返回传入的{@code #defaultValue}
	 *
	 * @param str          需要转换为int的String
	 * @param defaultValue 异常时的默认值
	 * @return int
	 */
	public static int toInt(String str, int defaultValue) {
		int i;
		try {
			i = Integer.parseInt(str);
		}
		catch (Exception e) {
			i = defaultValue;
		}
		return i;
	}

	/**
	 * 将两个string转化成int并比较
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compareStrToInt(String s1, String s2) {
		int i = 0;
		try {
			i = Integer.parseInt(s1) - Integer.parseInt(s2);
		}
		catch (Exception e) {
		}
		return i;
	}

	/**
	 * 功能描述:cityID的String转Int方法，业务默认要求是0，这里为了确保业务正常，单独添加这个方法
	 * 
	 * <pre>
	 *     liuwj:   2013-4-6   下午3:57:08   新建
	 * </pre>
	 * 
	 * @param s
	 * @return
	 */
	public static int cityIDToInt(String s) {
		int i = 0;
		try {
			i = Integer.parseInt(s);
		}
		catch (Exception e) {
			i = 0;
		}
		return i;
	}

	/**
	 * 
	 * 功能描述:将value转换为string，并判断小数点后有无有效数值
	 * 
	 * <pre>
	 *     guzy:   2013-1-31      新建
	 * </pre>
	 * 
	 * @param value
	 * @return
	 */
	public static String toDecimalString(float value) {
		if (value < 0f) {
			return "";
		}

		if (value == 0f) {
			return "0";
		}

		// 因为value默认是带有小数点的, 所有转为String后需要判断小数点后部分是否为0, 是的话需要截取小数点前的部分返回即可
		String result = Float.toString(value);
		if (result != null) {
			int pointIdx = result.indexOf(".");
			if (pointIdx > 0) {
				int number = toInt(result.substring(pointIdx + 1));
				if (number == 0) {
					return result.substring(0, pointIdx);
				}
			}
			else {
				return "0";
			}
		}
		return result;
	}

	/**
	 * 
	 * 功能描述:将value（单位：分）转换为string（单位元），并根据末两位的值自动保留小数点
	 * 
	 * <pre>
	 *     guzy:   2013-2-4      新建
	 * </pre>
	 * 
	 * @param value
	 * @return
	 */
	public static String toDecimalString(int value) {
		if (value < 0) {
			return "";
		}

		if (value == 0) {
			return "0";
		}

		if (value % 100 == 0) {
			return String.valueOf(value / 100);
		}
		else {
			return Float.toString((value / 100.0f));
		}
	}

	public static String toDecimalString(long value) {
		if (value < 0) {
			return "";
		}

		if (value == 0) {
			return "0";
		}

		if (value % 100 == 0) {
			return String.valueOf(value / 100);
		}
		else {
			return Double.toString((value / 100.0d));
		}
	}

	/**
	 * 
	 * 功能描述:将value（单位：分）转换为string（单位元） 并四舍五入
	 * <pre>
	 *     xinyh:   2013-5-7      新建
	 * </pre>
	 *
	 * @param value
	 * @return
	 */
	public static String halfUpToDecimalString(int value) {
		if (value < 0) {
			return "";
		}

		if (value == 0) {
			return "0";
		}

		if (value % 100 == 0) {
			return String.valueOf(value / 100);
		}
		else {
			StringBuffer strBuff = new StringBuffer();
			strBuff.append(new BigDecimal(String.valueOf(value / 100.0f)).setScale(0, BigDecimal.ROUND_HALF_UP));
			return strBuff.toString();
		}
	}

	/**
	 * 将string 转为long,异常时返回-1
	 * 
	 * @param s
	 * @return
	 */
	public static long toLong(String s) {
		long i = 0L;
		try {
			i = Long.parseLong(s);
		}
		catch (Exception e) {
			i = -1L;
		}
		return i;
	}

	/**
	 * 将string 转为float,异常时返回-1
	 * 
	 * @param s
	 * @return
	 */
	public static float toFloat(String s) {
		float i = 0f;
		try {
			i = Float.parseFloat(s);
		}
		catch (Exception e) {
			i = -1f;
		}
		return i;
	}

	/**
	 * 将string 转为double,异常时返回-1
	 * 
	 * @param s
	 * @return
	 */
	public static double toDouble(String s) {
		double i = 0;
		try {
			i = Double.parseDouble(s);
		}
		catch (Exception e) {
			i = -1;
		}
		return i;
	}

	/**
	 * 替换掉机场名称中含有国际的字段
	 * 
	 * @param airportName
	 * @return
	 */
	public static String formatAirportName(String airportName) {
		if (emptyOrNull(airportName)) {
			return "";
		}
		if (airportName.contains("国际")) {
			return airportName.replace("国际", "");
		}
		return airportName;
	}

	/**
	 * 根据类型获取机票订单状态
	 * 
	 * code c--"已取消" p--处理中 r--全部退票 s--成交 t--部分退票 w--未处理 u--未提交 其他状态 --提交中
	 * 
	 * @return
	 */
	public static String getFlightOrderState(String code) {
		if (emptyOrNull(code)) {
			return null;
		}

		String c = code.toLowerCase();
		if (c.equals("c")) {
			return "已取消";
		}
		else if (c.equals("p")) {
			return "处理中";
		}
		else if (c.equals("r")) {
			return "全部退票";
		}
		else if (c.equals("s")) {
			return "成交";
		}
		else if (c.equals("t")) {
			return "部分退票";
		}
		else if (c.equals("w")) {
			return "未处理";
		}
		else if (c.equals("u")) {
			return "未提交";
		}
		else {
			return "提交中";
		}
	}

	/**
	 * 
	 * @param giftType
	 * @return 0送券 1返现 -1无
	 */
	public static byte checkIsBackMoney(String giftType) {
		if (!StringUtil.emptyOrNull(giftType)) {
			if (giftType.equalsIgnoreCase("L") || giftType.equalsIgnoreCase("U") || giftType.equalsIgnoreCase("R") || giftType.equalsIgnoreCase("T") || giftType.equalsIgnoreCase("S")) {
				return 0;
			}
			else if (giftType.equalsIgnoreCase("C") || giftType.equalsIgnoreCase("D")) {
				return 1;
			}
		}
		return -1;
	}

	/**
	 * 去掉机场名称中含有国际机场与机场的字段
	 * 
	 * @param airportName
	 * @return
	 */
	public static String shortAirportName(String airportName) {
		if (airportName != null) {
			int len = airportName.length();
			if (airportName.endsWith("国际机场")) {
				return airportName.substring(0, len - 4);
			}
			else if (airportName.endsWith("机场")) {
				return airportName.substring(0, len - 2);
			}
		}
		return "";
	}

	// /**
	// * 根据名字与国籍返回正确的国籍
	// *
	// * @param name
	// * @param nation
	// * @return
	// */
	// public static String isValidNationAndName(String name, String nation) {
	// if (!StringUtil.emptyOrNull(name) && !StringUtil.emptyOrNull(nation)) {
	// if ("CN".equals(nation)) {
	// if (!StringUtil.isValidCN(name)) {
	// return "CN";
	// }
	// } else if (!"CN".equals(nation) && !"HK".equals(nation) &&
	// !"MO".equals(nation) && !"TW".equals(nation)) {
	// if (!StringUtil.isValidENName(name)) {
	// return "EN";
	// }
	// }
	// }
	// return "";
	// }

	/**
	 * 取字串小数点前整数
	 * 
	 * @param s
	 * @return
	 */
	public static String toIntString(String s) {
		String intString = "";
		try {
			if (s.contains(".")) {
				intString = s.substring(0, s.indexOf("."));
			}
			else {
				intString = s;
			}
		}
		catch (Exception e) {

		}
		return intString;
	}

	/**
	 * 将null转为""
	 * 
	 * @param str
	 * @return
	 */
	public static String changeNullStr(String str) {
		if (str == null) {
			return "";
		}
		else {
			return str;
		}

	}

	/**
	 * 判断字串是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static int isNumString(String str) {
		Pattern p = Pattern.compile("[0-9]*");// 格式"a"
		Matcher m = p.matcher(str);
		if (m.matches()) {
			return 1;
		}
		else {
			return 0;
		}
	}

	// /**
	// * 判断字串是否是数字
	// *
	// * @param str
	// * @return
	// */
	// public static boolean isPassword(String str) {
	// Pattern p =
	// Pattern.compile("[A-Za-z0-9\\!\\#\\@\\$\\%\\^\\&\\*\\.\\~\\`\\(\\)\\-\\_\\+\\=\\[\\]\\{\\}\\|\\;\\:\\'\\,\\.\\<\\>\\?\\/]{7,20}$");
	// Matcher m = p.matcher(str);
	// boolean valid = m.matches();
	// return valid;
	// }

	// /**
	// * 是否是英文
	// *
	// * @param str
	// * @return
	// */
	// public static boolean isEnString(String str) {
	// Pattern p = Pattern.compile("[\\s]*[A-Za-z]+[\\s]*");// 格式"a"
	// Matcher m = p.matcher(str);
	// boolean valid = m.matches();
	// return valid;
	// }

	// /**
	// * 首字母是否中文
	// *
	// * @param name
	// * @return
	// */
	// public static boolean isFirstChCnStr(String name) {
	// if (emptyOrNull(name)) {
	// return false;
	// }
	// String str = name.substring(0, 1);
	// if (isEnString(str)) {
	// return false;
	// }
	// return true;
	// }

	/**
	 * email是否合法
	 * 
	 * @param email
	 * @return true or false
	 */
	// public static boolean isValidEMail(String email) {
	// Pattern p = Pattern.compile("\\S+@(\\S+\\.)+[\\S]{1,6}");
	// Matcher m = p.matcher(email);
	// boolean valid = m.matches();
	// return valid;
	// }

	/**
	 * 输入 的字串不包含特殊字符
	 * 
	 * @param string
	 * @return 不包则返回true 否则 返回 false
	 */
	public static boolean isConSpeCharacters(String string) {
		if (string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为中国籍，包含港澳台
	 * 
	 * @param nationality
	 * @return
	 */
	public static boolean isValidChineseNationality(String nationality) {
		if ("".equals(nationality)) {
			return true;
		}
		return "CNTWHKMO".indexOf(nationality.toUpperCase()) > -1;
	}

	// /**
	// * 邮编是否正确，只验证长度以及是否全为数字
	// *
	// * @param code
	// * @return
	// */
	// public static boolean isValidPostCode(String code) {
	// if (emptyOrNull(code))
	// return false;
	// if (code.length() != 6)
	// return false;
	// return isNumString(code);
	// }

	// /**
	// * 验证输入的手机号是否正确 首位为1 第二位为 3|4|5|8
	// *
	// * @param mobile
	// * @return
	// */
	// public static boolean isMobileNumber(String mobile) {
	// // //香港，澳门 手机
	// if (mobile.startsWith("0") && mobile.length() == 12) {
	// mobile = mobile.substring(1, mobile.length());
	// }
	// if (mobile.length() == 11) {
	// char firstNum = mobile.charAt(0);
	// if (firstNum == '1')
	// return true;
	// }
	//
	// return false;
	// }

	/**
	 * 航班号是否正确
	 * 
	 * @param flightid
	 * @return true or false
	 */
	public static boolean isFlightID(String flightid) {
		Pattern p = Pattern.compile("^(CA|CZ|FM|MU|BK|JD|EU|CN|NS|HU|VD|G5|HO|KY|3U|SC|ZH|GS|PN|JR|MF|8L|KN|QF|OS)+[A-Z0-9]{3,4}");
		Matcher m = p.matcher(flightid);
		boolean valid = m.matches();
		return valid;
	}

	// /**
	// * 身份证合法性验证，15位身份证只验证长度
	// *
	// * @param idcard
	// * @return
	// */
	// public static boolean verifyID(String idcard) {
	// if (null == idcard)
	// return false;
	// if (idcard.length() == 15) {
	// // 15位时只做长度验证
	// return true;
	// }
	// if (idcard.length() != 18) {
	// return false;
	// }
	// String verify = idcard.substring(17, 18);
	// if (verify.equalsIgnoreCase(getVerify(idcard))) {
	// return true;
	// }
	// return false;
	// }

	// /**
	// * 计算18位身份证明后一位
	// *
	// * @param eighteen
	// * @return 计算出来的最后一位字串
	// */
	// private static String getVerify(String eighteen) {
	// int remain = 0;
	// if (eighteen.length() == 18) {
	// eighteen = eighteen.substring(0, 17);
	// }
	// if (eighteen.length() == 17) {
	// int sum = 0;
	// for (int i = 0; i < 17; i++) {
	// String k = eighteen.substring(i, i + 1);
	// if ("-1".equalsIgnoreCase(k)) {
	// return "";
	// }
	// ai[i] = toInt(k);
	// }
	// for (int i = 0; i < 17; i++) {
	// sum += wi[i] * ai[i];
	// }
	// remain = sum % 11;
	// }
	// if (remain >= 0) {
	// return remain == 2 ? "x" : String.valueOf(vi[remain]);
	// } else {
	// return "";
	// }
	// }

	// /**
	// * 验证英文名合法性 符合五种格式中任意一种即可 a/b c| a/b | a b c| a b| a
	// *
	// * @param name
	// * @return true or false
	// */
	// public static boolean isValidENName(String name) {
	// Pattern p1 =
	// Pattern.compile("[\\s]*[A-Za-z]+/[A-Za-z]+[\\s]+[A-Za-z]+[\\s]*");//
	// 格式"a/b c"
	// Matcher m1 = p1.matcher(name);
	// Pattern p2 = Pattern.compile("[\\s]*[A-Za-z]+/[A-Za-z]+[\\s]*");//
	// 格式"a/b"
	// Matcher m2 = p2.matcher(name);
	// Pattern p3 =
	// Pattern.compile("[\\s]*[A-Za-z]+[\\s]+[A-Za-z]+[\\s]+[A-Za-z]+[\\s]*");//
	// 格式"a b c"
	// Matcher m3 = p3.matcher(name);
	// Pattern p4 = Pattern.compile("[\\s]*[A-Za-z]+[\\s]+[A-Za-z]+[\\s]*");//
	// 格式"a b"
	// Matcher m4 = p4.matcher(name);
	// Pattern p5 = Pattern.compile("[\\s]*[A-Za-z]+[\\s]*");// 格式"a"
	// Matcher m5 = p5.matcher(name);
	// boolean valid = m1.matches() || m2.matches() || m3.matches() ||
	// m4.matches() || m5.matches();
	// return valid;
	// }

	// /**
	// * 验证英文名合法性 符合两种格式中任意一种即可 a/b c| a/b
	// *
	// * @param name
	// * @return
	// */
	// public static boolean isValidENName4Flight(String name) {
	// Pattern p1 =
	// Pattern.compile("[\\s]*[A-Za-z]+/[A-Za-z]+[\\s]+[A-Za-z]+[\\s]*");//
	// 格式"a/b c"
	// Matcher m1 = p1.matcher(name);
	// Pattern p2 = Pattern.compile("[\\s]*[A-Za-z]+/[A-Za-z]+[\\s]*");//
	// 格式"a/b"
	// Matcher m2 = p2.matcher(name);
	// boolean valid = m1.matches() || m2.matches();
	// return valid;
	// }

	// /**
	// * 常旅客卡校验
	// *
	// * @author yrguo
	// * @param ffpCardId
	// * @return
	// */
	// public static boolean isFFPCardID(String ffpCardId) {
	// Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
	// Matcher m = p.matcher(ffpCardId);
	// return m.matches();
	// }

	/**
	 * 计算EditText中输入字串的长度(按字节计算)
	 * 
	 * @param temp
	 * @return
	 */
	public static int strlen(CharSequence temp) {
		if (temp == null || temp.length() <= 0) {
			return 0;
		}

		int len = 0;
		char c;
		for (int i = temp.length() - 1; i >= 0; i--) {
			c = temp.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				// 字母, 数字
				len++;
			}
			else {
				if (Character.isLetter(c)) { // 中文
					len += 2;
				}
				else { // 符号或控制字符
					len++;
				}
			}
		}

		return len;
	}

	/**
	 * 将年，月，日，时，分，拼成14位长度的字串
	 * 
	 * @param _year
	 *            年
	 * @param _month
	 *            月
	 * @param _day
	 *            日
	 * @param _hour
	 *            时
	 * @param _minute
	 *            分
	 * @return 14位长度的字串
	 */
	public static String formatDateString(int _year, int _month, int _day, int _hour, int _minute) {
		String value = String.valueOf(_year);
		if (_month < 10) {
			value += "0" + _month;
		}
		else {
			value += _month;
		}

		if (_day < 10) {
			value += "0" + _day;
		}
		else {
			value += _day;
		}

		if (_hour < 10) {
			value += "0" + _hour;
		}
		else {
			value += _hour;
		}

		if (_minute < 10) {
			value += "0" + _minute;
		}
		else {
			value += _minute;
		}

		value += "00";
		return value;
	}

	/**
	 * 将年，月，日拼成一个8位长的字串
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return 8位长的字串
	 */
	public static String formatDateString(int year, int month, int day) {
		String value = String.valueOf(year);
		while (value.length() < 4) {
			value = "0" + value;
		}
		if (month < 10) {
			value += "0" + month;
		}
		else {
			value += month;
		}

		if (day < 10) {
			value += "0" + day;
		}
		else {
			value += day;
		}

		return value;
	}

	/**
	 * 格式化机票折扣字符串
	 */
	public static String formatFlightRate(String oriStr) {
		if (emptyOrNull(oriStr)) {
			return "null";
		}

		StringBuffer rateText = new StringBuffer();
		if (toFloat(oriStr) >= 10) {
			/** 折扣的值>=1的话,显示无折扣, */
			rateText.append("全价");
			// rateText.append("全价");
		}
		else {
			DecimalFormat df = new DecimalFormat("########.0");

			rateText.append("");
			rateText.append(toDouble(df.format(toFloat(oriStr))));
			// rateText.append(toDouble(df.format(toFloat(oriStr) * 10)));
			rateText.append("折");
		}

		return rateText.toString();
	}

	/**
	 * MD5
	 * 
	 * @param source
	 * @return
	 */
	public static String getMD5(byte[] source) {
		StringBuilder sb = new StringBuilder();
		java.security.MessageDigest md5 = null;
		try {
			md5 = java.security.MessageDigest.getInstance("MD5");
			md5.update(source);
		}
		catch (NoSuchAlgorithmException e) {
		}
		if (md5 != null) {
			for (byte b : md5.digest()) {
				sb.append(String.format("%02X", b));
			}
		}
		return sb.toString();
	}

	/**
	 * 处理特殊要求字符串，将顿号换成换行符
	 */
	public static String processString(String s) {
		String r = "";
		if (s != null && !"".equals(s)) {
			r = s.replaceAll("、", "\n");
		}
		return r;
	}

	/**
	 * 将字串中换行符(BR)替换成\n,空格替换为 " "
	 * 
	 * @param str
	 * @return
	 */
	public static String formatInfo(String str) {
		String strInfo = str.replace("<BR/>", "\n");
		strInfo = strInfo.replace("<br/>", "\n");
		strInfo = strInfo.replace("<br>", "\n");
		strInfo = strInfo.replace("&nbsp", "");
		strInfo = strInfo.replace("<t>", "    ");
		return strInfo;
	}

	/**
	 * 将str中含有src的替换成aim str与src 不能为空。
	 * 
	 * @param str
	 * @param src
	 * @param aim
	 * @return
	 */
	public static String replaceStr(String str, String src, String aim) {
		if (emptyOrNull(str) || emptyOrNull(src)) {
			return "";
		}
		if (str.contains(src)) {
			return str.replace(src, aim);
		}
		return str;
	}

	/**
	 * 返现值
	 * 
	 * @param giftValue
	 * @return
	 */
	public static String getBackMoneyString(String giftValue) {
		float value = Float.valueOf(giftValue);
		if (value > 1.0f) {
			return ((int) value) + "元";
		}
		else {
			int value1 = (int) (value * 100);
			return value1 + "%";
		}
	}

	public static int getSeekBarProgress(int value, int minValue, int midValue, int maxValue) {
		if (value < 0) {
			return 0;
		}
		if (value <= midValue) {
			return 50 * (value - minValue) / (midValue - minValue);
		}
		else {
			return 50 + 50 * (value - midValue) / (maxValue - midValue);
		}
	}

	public static int getSeekBarValue(int progress, int minValue, int midValue, int maxValue) {
		if (progress < 0) {
			return 0;
		}

		if (progress <= 50) {
			return (int) ((midValue - minValue) * (progress / 50.00)) + minValue;
		}
		else {
			int overValue = (int) ((progress - 50) * (maxValue - midValue) / 50.00);
			return midValue + overValue;
		}
	}

	/**
	 * string是否有效
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isValidStr(String inputStr) {
		char[] charArray = inputStr.toCharArray();
		int length = charArray.length;
		for (int i = 0; i < length; i++) {
			if (!(charArray[i] >= '0' && charArray[i] <= '9') && !(charArray[i] >= 'A' && charArray[i] <= 'z') && charArray[i] != '_') {
				return false;
			}
		}

		return true;
	}

	/**
	 * 返回一个中间有线的SpannableString
	 * 
	 * @param s
	 * @return
	 */
	public static SpannableString getMidLineStr(String s) {
		SpannableString ss = new SpannableString(s);
		ss.setSpan(new StrikethroughSpan(), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ss;
	}

	/**
	 * 
	 * @param firstStr
	 * @param secondStr
	 * @return
	 */
	public static boolean stringCompare(String firstStr, String secondStr) {
		if (StringUtil.emptyOrNull(firstStr)) {
			return false;
		}
		if (firstStr.compareTo(secondStr) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 根据机型标志字段获取机型
	 * 
	 * @param craftKind
	 * @return
	 */
	public static String getCraftKindStr(String craftKind) {
		String craftKindStr = "";
		if (craftKind.equalsIgnoreCase("M")) {
			craftKindStr = "中机型";
		}
		else if (craftKind.equalsIgnoreCase("S")) {
			craftKindStr = "小机型";
		}
		else if (craftKind.equalsIgnoreCase("L")) {
			craftKindStr = "大机型";
		}
		return craftKindStr;
	}

	public static String getDateMessage(String[] sa) {
		String s = "";
		if (sa != null && sa.length >= 4) {
			s += sa[0];
			if (toInt(sa[1]) < 10) {
				s += "0" + sa[1];
			}
			else {
				s += sa[1];
			}
			if (toInt(sa[2]) < 10) {
				s += "0" + sa[2];
			}
			else {
				s += sa[2];
			}
		}
		return s;
	}

	/**
	 * 超过四位的字串前两位后加上:
	 * 
	 * @param str
	 * @return
	 */
	public static String processTimeStr(String str) {
		String s = "";
		if (str != null && str.length() >= 4) {
			s = str.substring(0, 2) + ":" + str.substring(2, str.length());
		}
		return s;
	}

//	public static String getInfoIdList(String[] infos) {
//		int infoCount = 10;
//		int infoLen = 10;
//		StringBuilder infoStringBuilder = new StringBuilder();
//		int size = infos.length;
//		for (int i = 0; i < size; i++) {
//			infoStringBuilder.append(FillStr(infos[i], infoLen));
//		}
//		int loop = infoCount - size;
//		for (int i = 0; i < loop; i++) {
//			infoStringBuilder.append(FillStr("", infoLen));
//		}
//		return infoStringBuilder.toString();
//	}

	/**
	 * 将日期拆分并将年月日存入数组中
	 * 
	 * @param date
	 * @return
	 */
	public static int[] getDateField(String date) {
		if (date != null && date.length() >= 8) {
			int[] fields = new int[3];
			fields[0] = toInt(date.substring(0, 4));
			String month = date.substring(4, 6);
			fields[1] = toInt(month) - 1;
			String day = date.substring(6, 8);
			fields[2] = toInt(day);
			return fields;
		}
		return null;
	}

	/**
	 * 将年，月，日 ，三个值拼成 YYYY-MM-DD
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String dateToString(int year, int month, int day) {
		if (year < 0 || month < 0 || day < 0) {
			return "";
		}
		else {
			return year + "-" + month + "-" + day;
		}
	}

	/**
	 * 将字串中的两个:替换成.
	 * 
	 * @param d
	 * @return
	 */
	public static String parseTude(String d) {
		String r = "";
		if (d != null && !"".equals(d)) {
			int i = d.indexOf(":");
			r += d.substring(0, i) + ".";
			String ss = d.substring(i + 1, d.length());
			int si = ss.indexOf(":");
			r += ss.substring(0, si) + ".";
			r += ss.substring(si + 1);
		}
		return r;
	}

	/** 12-03修改：大舱位(ClassGrade);0:Y(经济舱);2:C(公务舱);3:F(头等舱) */
	public static String getFlightClassName(String code) {
		if ("Y".equalsIgnoreCase(code)) {
			return "经济舱";
		}
		else if ("F".equalsIgnoreCase(code)) {
			return "头等舱";
		}
		else if ("A".equalsIgnoreCase(code)) {
			return "不限";
		}
		else if ("C".equalsIgnoreCase(code)) {
			return "公务舱";
		}
		return "";
	}

	/**
	 * 获取航班类型 C/F合并
	 */
	public static String getFlightClassName2(String code) {
		if ("Y".equalsIgnoreCase(code)) {
			return "经济舱";
		}
		else if ("F".equalsIgnoreCase(code)) {
			return "公务舱/头等舱";
		}
		else if ("A".equalsIgnoreCase(code)) {
			return "不限";
		}
		else if ("C".equalsIgnoreCase(code)) {
			return "公务舱/头等舱";
		}
		return "";
	}

	/** 12-17修改：（商务舱改为 公务舱） 大舱位(ClassGrade);0:Y(经济舱);2:C(公务舱);3:F(头等舱) */
	public static String getFlightClassNameByFlag(int flag) {
		String subClassName = "";
		if (flag == 0) {
			subClassName = "经济舱";

		}
		else if (flag == 2) {
			subClassName = "公务舱";
			// subClassName = "商务舱";
		}
		else if (flag == 3) {
			subClassName = "头等舱";
		}
		return subClassName;
	}

	/** 12-02新增(ClassForDisplay)：舱位标签（显示用），1: 高端; 2: 超值; 3: 豪华; 7:空中 */
	public static String getFlightSubClassLabelByFlag(int flag) {
		String subClassName = "";
		switch (flag) {
		case 1:
			subClassName = "高端";
			break;
		case 2:
			subClassName = "超值";

			break;
		case 3:
			subClassName = "豪华";
			break;
		case 7:
			subClassName = "空中";
			break;
		default:
			break;
		}
		return subClassName;
	}

	/** 大舱位中文（显示用--ClassForDisplay），1: 高端经济舱; 2: 超值头等舱; 3: 超级经济舱; 4:商务舱; 5:头等舱; 6:经济舱 */
	/** 11-18:修改：大舱位中文（显示用），1: 高端经济舱; 2: 超值头等舱; 3: 豪华; 4:公务舱; 5:头等舱; 6:经济舱 */
	public static String getFlightSubClassNameByFlag(int flag) {
		String subClassName = "";
		switch (flag) {
		case 1:
			subClassName = "高端经济舱";
			break;
		case 2:
			subClassName = "超值头等舱";

			break;
		case 3:

			subClassName = "豪华经济舱";
			break;
		case 4:
			subClassName = "公务舱";
			// subClassName = "商务舱";

			break;
		case 5:
			subClassName = "头等舱";

			break;
		case 6:
			subClassName = "经济舱";

			break;

		default:
			break;
		}

		return subClassName;
	}

	/**
	 * 1.name为繁体且证件类型为7(回乡证)或8(台胞证)或10(港澳通行证)时返回提示语句 "国内航班不接受繁体字登机，请提供证件上的简体中文姓名或英文姓名" 2.name为英文，国籍为中国，证件类型为身份证，军人证，外国人永久居留证时返回提示语“请确认您提交的姓名与证件上姓名是否一致” 3其他情况返回""
	 * 
	 * @param name
	 * @param nationality
	 * @param idCardType
	 *            7回乡证,8台胞证,10港澳通行证
	 * @param birthday
	 * @return
	 */
	// public static String isValidCNName(String name, String nationality,
	// String idCardType, String birthday) {
	//
	// if (isValidTWCN(name) && (idCardType.equals("7") ||
	// idCardType.equals("8") || idCardType.equals("10"))) {
	// return "国内航班不接受繁体字登机，请提供证件上的简体中文姓名或英文姓名";
	// }
	//
	// if (isValidENName(name) && nationality.equalsIgnoreCase("CN") &&
	// (idCardType.equals("1") || idCardType.equals("4") ||
	// idCardType.equals("20") || idCardType.equals("00"))) {
	// return "请确认您提交的姓名与证件上姓名是否一致";
	// }
	// return "";
	// }

	/**
	 * 在srcStr的position位置，插入insertStr
	 * 
	 * @author liuwj 2012-6-5
	 * @param srcStr
	 * @param insertStr
	 * @param position
	 * @return
	 */
	public static String insertSymbolInStrPotion(String srcStr, String insertStr, int position) {
		if (emptyOrNull(srcStr)) {
			return "";
		}
		String str = "";
		if (position > srcStr.length()) {
			return str;
		}
		StringBuffer showStringBuffer = new StringBuffer(srcStr.length() + insertStr.length());
		String tmep = srcStr.substring(position, srcStr.length());
		showStringBuffer.append(srcStr.substring(0, position));
		showStringBuffer.append(insertStr);
		showStringBuffer.append(tmep);
		str = showStringBuffer.toString();
		return str;
	}

	/**
	 * 校验yyyyMMdd日期是否合法
	 * 
	 * @author liuwj 2012-7-19下午3:21:59
	 * @param date
	 * @return 八位的年月日
	 */
	public static boolean isDateRight(String date) {
		if (date.length() == 8) {
			int year = -1;
			int month = -1;
			int day = -1;
			boolean isLeapYear = false;// 闰年
			year = toInt(date.substring(0, 4));
			month = toInt(date.substring(4, 6));
			day = toInt(date.substring(6, 8));
			if (year / 4 == 0 && year / 100 != 0) {
				isLeapYear = true;// 闰年
			}
			if (year / 400 == 0) {
				isLeapYear = true;// 闰年
			}
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				if (day <= 31 && day >= 1) {
					return true;
				}
				break;
			case 2:
				if (isLeapYear) {
					if (day <= 29 && day >= 1) {
						return true;
					}
				}
				else {
					if (day <= 28 && day >= 1) {
						return true;
					}
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				if (day <= 30 && day >= 1) {
					return true;
				}
				break;
			default:
				return false;
			}
			return false;
		}
		return false;
	}

	/**
	 * 计算航班的两个日期
	 * 
	 * @author liuwj 2012-6-6
	 * @param dateStr1
	 * @param dateStr2
	 * @param minusPlus
	 * @return
	 */
	public static int calcTwoDateUnsign(String dateStr1, String dateStr2) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long diff;

		// 获得两个时间的毫秒时间差异
		try {
			diff = sd.parse(dateStr2).getTime() - sd.parse(dateStr1).getTime();
			long day = diff / nd;// 计算差多少天
			return Math.abs((int) (day));
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 计算两个String类型的时间之和
	 * 
	 * @author liuwj 2012-7-17下午6:09:44
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int plusGlobalTwoTime(String time1, String time2) {
		int timeA = 0, timeB = 0;
		if (!emptyOrNull(time1)) {
			timeA = toInt(time1);
		}
		if (!emptyOrNull(time2)) {
			timeB = toInt(time2);
		}
		return timeA + timeB;
	}

	/**
	 * 将 酒店评分 格式化 ，小数点后只有一位 。
	 * 
	 * @param originGrade
	 * @param isShowZero
	 *            :为 0 是否显示 ：true = 显示 0.0 | false = “”
	 * @return
	 */
	public static String fromatGrade(String originGrade, boolean isShowZero) {
		String grade = "0.0";
		DecimalFormat decimalFormat = new DecimalFormat("########.0"); // 数字格式化
		grade = decimalFormat.format(StringUtil.toFloat(originGrade));
		double d = StringUtil.toDouble(grade);
		if (d <= 0) {
			if (!isShowZero) {
				grade = "";
			}
			else {
				grade = "0.0";
			}
		}
		return grade;

	}

	/**
	 * 公里转化米 支持小数
	 * 
	 * @return
	 */
	public static String kilometreToMetre(String kilometre) {
		String retStr = "";
		if (StringUtil.emptyOrNull(kilometre)) {
			return retStr;
		}
		float floatData = StringUtil.toFloat(kilometre) * 1000;
		if (floatData == -1000f) {
			return retStr;
		}
		retStr = StringUtil.toIntString(floatData + "");
		return retStr;
	}

	/**
	 * 米 转化 公里 支持小数
	 * 
	 * @return
	 */
	public static String metreToKilometre(String metre) {
		String retStr = "";
		if (StringUtil.emptyOrNull(metre)) {
			return retStr;
		}
		if (StringUtil.toInt(metre) == -1) {
			return retStr;
		}
		float floatData = StringUtil.toInt(metre) / (1000f);
		retStr = floatData + "";
		return retStr;
	}

	/**
	 * 截取字符串前几位，如果不满的话，直接return原字符串
	 * 
	 * @author liuwj 2012-11-9下午3:35:54
	 * @param content
	 * @param length
	 * @return
	 */
	public static String subString(String content, int length) {
		if (content.length() < length) {
			return content;
		}
		else {
			return content.substring(0, length);
		}
	}

	/**
	 * 以GBK编码格式计算字符串长度, 半角字符=1个长度, 全角字符=2个长度
	 * @param text 字符串
	 * @return 字符串长度
	 */
	public static int getSBCCaseLength(String text) {
		if (text == null || text.length() == 0) {
			return 0;
		}
		try {
			return text.getBytes("GBK").length;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 
	 * 功能描述:4.1-4.5星算4.5星 4.6星到4.9星算5星
	 * 
	 * <pre>
	 *     dqwang:   2013-2-28      新建
	 * </pre>
	 * 
	 * @param value
	 * @return
	 */
	public static float parseToHalfInteger(float value) {
		if (value > 0) {
			int valueInt = (int) value;
			if (value > valueInt + 0.5f) {
				return valueInt + 1;
			}
			else if (value > valueInt) {
				return valueInt + 0.5f;
			}
			else {
				return value;
			}
		}
		else {
			return 0.0f;
		}
	}

	/**
	 * 功能描述:格式化货币符号
	 * 
	 * <pre>
	 *     junyingding:   2013-3-25      新建
	 * </pre>
	 * 
	 * @param currency
	 *            货币类型
	 * @return 美元$，人民币￥，其他货币不变
	 */
	public static String getFormatCurrency(String currency) {
		if (currency == null || currency.length() == 0) {
			return "";
		}

		if ("RMB".equalsIgnoreCase(currency) || "CNY".equalsIgnoreCase(currency)) {
			return "￥";
		} /*
		 * else if ("USD".equalsIgnoreCase(currency)) {
		 * return "$";
		 * }
		 */
		else {
			return currency;
		}
	}

	/**
	 * 
	 * 功能描述:电话分隔
	 * 
	 * <pre>
	 *     lbie:   2013-3-21      新建
	 * </pre>
	 * 
	 * @param value
	 * @param splitChar
	 * @return
	 */
	public static String[] getStringArray(String value, String splitChar) {
		String[] array = null;
		if (!StringUtil.emptyOrNull(value)) {
			array = value.split(splitChar);
		}
		return array;
	}

	/**
	 * 解析Extension 格式如 ：key1=value1|key2=value2|。。。。。
	 * 
	 * <pre>
	 *     cxb:   2013-3-31      新建
	 * </pre>
	 * 
	 * @param extensionStr
	 *            格式如 ：key1=value1|key2=value2|。。。。。
	 * @return
	 */
	public static Map<String, String> handleExtensionStrToMap(String extensionStr) {
		Map<String, String> retMap = new HashMap<String, String>();

		if (StringUtil.emptyOrNull(extensionStr)) {
			return retMap;
		}

		String[] content = extensionStr.split("\\|");
		if (content == null || content.length <= 0) {
			return retMap;
		}
		for (String temp : content) {
			if (StringUtil.emptyOrNull(temp)) {
				continue;
			}
			int index = temp.indexOf("=");
			if (index <= 0) {// =dsa 没有key的不解析
				continue;
			}
			try {
				String key = temp.substring(0, index);
				String value = temp.substring(index + 1, temp.length());
				retMap.put(key.trim(), value.trim());
			}
			catch (Exception e) {
				continue;
			}
		}
		return retMap;
	}

	/**
	 * 解析Extension ,根据key返回value。如果没有对应的key，返回""
	 * 
	 * @param extensionStr
	 *            格式如 ：key1=value1|key2=value2|。。。。。
	 * @param key
	 * @return value
	 */
	public static String handleExtensionStrToGetValue(String extensionStr, String key) {
		Map<String, String> retMap = handleExtensionStrToMap(extensionStr);
		if (retMap.containsKey(key)) {
			return retMap.get(key);
		}
		else {
			return "";
		}
	}

	/**
	 * 功能描述:出发日起价截取前2个日期+...
	 * <pre>
	 *     junyingding:   2013-4-9      新建
	 * </pre>
	 *
	 * @param startPrice
	 * @return
	 */
	public static String getShortPriceDate(String priceDate) {
		if (StringUtil.emptyOrNull(priceDate)) {
			return "";
		}
		String[] strArray = priceDate.split("、");
		if (null != strArray && strArray.length > 2) {
			StringBuilder sb = new StringBuilder();
			sb.append(strArray[0]).append("、").append(strArray[1]).append("...");
			return sb.toString();
		}
		return priceDate;
	}

	/**
	 * 
	 * 功能描述:判断字符串是否含中文
	 * <pre>
	 *     youj:   2013-5-8      新建
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {

		if (emptyOrNull(str)) {
			return false;
		}

		char[] charArr = str.toCharArray();

		for (char c : charArr) {

			if (isContainChinese(c)) {
				return true;
			}

		}

		return false;
	}

	private static boolean isContainChinese(char c) {

		Character.UnicodeBlock cu = Character.UnicodeBlock.of(c);

		return cu == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || cu == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || cu == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
		        || cu == Character.UnicodeBlock.GENERAL_PUNCTUATION || cu == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || cu == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ? true
		        : false;

	}

	/**
	 * 
	 * 功能描述:传入秒数，如果超过一小时返回 hh:mm:ss否则 返回mm:ss格式字串
	 * <pre>
	 *     xinyh:   2013-5-10      新建
	 * </pre>
	 *
	 * @param time
	 * @return
	 */
	public static String getTalkTime(int time) {
		int hour = time / 3600;
		int minu = (time % 3600) / 60;
		int sec = time % 60;
		String str = "";
		if (hour == 0) {
			str = String.format("%02d:%02d", minu, sec);
		}
		else {
			str = String.format("%02d:%02d:%02d", hour, minu, sec);
		}
		return str;
	}

	/**
	 * 
	 * 功能描述,按num位数截取字符串并以endString结尾.如果不够num位的,直接返回.
	 * 默认endString为"..."
	 *  <pre>
	 *     xubin:   2013-6-14      新建
	 * </pre>
	 * @param content原字符串
	 * @param num截取位数
	 * @param endString结尾字符串
	 * @return 转换后的字符串
	 */
	public static String cutStringByNum(String content, int num, String endString) {
		String end = endString;
		if (end == null) {
			end = "...";
		}
		String str = "";
		if (content.length() > num) {
			str = content.substring(0, num) + end;
		}
		else {
			str = content;
		}
		return str;
	}

	public static String convertDispatchFee(int value) {

		if (value == 0) {

			return "0";

		}

		if (value % 100 == 0) {

			return String.valueOf(value / 100);

		}
		else {

			return Float.toString((value / 100.0f));

		}

	}

	public static int convertStringToIntOnlyForFocusFlight(String string) {
		byte[] b = string.getBytes();
		int value = 0;
		for (int i = 0; i < b.length; i++) {
			int n = (b[i] < 0 ? b[i] + 256 : (int) b[i]) << (8 * i);
			value += n;
		}
		return Math.abs(value);
	}

	/**
	 * 
	 * 功能描述:计算两个坐标之间的距离
	 * 
	 * <pre>
	 *     dqwang:   2013-1-27      新建
	 * </pre>
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * 6378.137;
		s = Math.round(s * 10000);
		s = s / 10000;
		return s;// 计算单位：公里
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 
	 * @param hotelStar
	 *            需要显示的星级/评级数组
	 * @param level
	 *            等级
	 * @return 显示的星级
	 */
	public static String getShowStar(String[] hotelStar, int level) {
		if (hotelStar == null || hotelStar.length < level) {
			return "";
		}
		String str = "";
		if (level > 0 && level < 6) {
			str = hotelStar[level - 1];
		}
		return str;
	}

	/**
	 * 小数字符串（"0.05"）转换为百分比字符串（"5%"）
	 *
	 * jbai:   2013-8-14      新建
	 *
	 * @param decimal
	 * @return
	 */
	public static String decimalToPercent(String decimal) {
		float value = -1;
		if (!emptyOrNull(decimal)) {
			try {
				value = Float.parseFloat(decimal);
			}
			catch (NumberFormatException e) {
			}
		}
		if (value == -1) {
			return "";
		}
		else {
			DecimalFormat format = new DecimalFormat("0.#%");
			return format.format(value);
		}
	}

	/**
	 * 传入以分为单位的价格数字,转换成元以后,若有小数,去掉小数部分,整数部分+1.负数返回0
	 * 例如110返回1.1元
	 * @param price 以分为单位的价格数字
	 * @return
	 */
	public static String getCeilPriceString(int price) {
		String priceText = "";
		if (price <= 0) {
			return "0";
		}
		if (price % 100 != 0) {
			priceText = String.valueOf(price / 100 + 1);
		}
		else {
			priceText = String.valueOf(price / 100);
		}
		return priceText;
	}

	/**
	 * 保留一位小数，第二位小数四舍五入.
	 * 如果value为非数字，返回0.0
	 * @param value
	 * @return
	 */
	public static String toOneDecimal(String value) {
		Double huashi = 0d;
		try {
			huashi = Double.parseDouble(value);
		}
		catch (Exception e) {
		}

		DecimalFormat format = new DecimalFormat("#0.0");
		String buf = format.format(huashi).toString();
		return buf;
	}

	/**
	 * DateTime是值类型， 故目前无法给出一个空的数据. 约定如下：
	 * DateTime 0001-01-01 00:00:00表示为空日期;00010101000000
	 * Date 0001-01-01表示为空日期;00010101
	 * 
	 * create by DingJunYing for v5.0
	 * 约定来自高文厂
	 * 
	 * @param dateTime
	 * @return 日期为空返回true
	 */
	public static boolean isDateTimeEmpty(String dateTime) {
		final String emptyDate = "00010101";// 8位空日期
		final String emptyDateTime = "00010101000000";// 14位空日期
		if (StringUtil.emptyOrNull(dateTime)) {
			return true;
		}
		// 14位时间
		if (dateTime.equals(emptyDateTime)) {
			return true;
		}
		// 8位日期
		if (dateTime.equals(emptyDate)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据指定的长度,自动加上换行符
	 * 
	 * @param s
	 * @param num 多少字符换行
	 * @return
	 */
	public static String autoLineFeed(String s, int num) {
		StringBuilder sb = new StringBuilder();
		float size = 0f;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (size >= num) {
				sb.append("\n");
				size = 0f;
			}
			Character.UnicodeBlock cUB = Character.UnicodeBlock.of(c);
			if (cUB != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
				sb.append(c);
				size += 0.5;
				;
			}
			else {
				sb.append(c);
				size += 1;
			}
		}
		return sb.toString();
	}

	/**
	 * 拆分字符串，拆分后将字符换转换为aaaa(bb)样式
	 * @param text
	 * @return
	 */
	public static String getTextBySplitStr(String text) {
		String[] arr = StringUtil.getStringArray(text, "\\|");
		String result = "";
		if (arr != null) {
			if (arr.length == 1) {
				result = arr[0];
			}
			else if (arr.length == 2) {
				result = arr[0] + "(" + arr[1] + ")";
			}
		}
		return result;
	}

	/**
	 * StringUtils.isNotBlank(null) = false
	 * StringUtils.isNotBlank("") = false
	 * StringUtils.isNotBlank(" ") = false
	 * StringUtils.isNotBlank("bob") = true
	 * StringUtils.isNotBlank(" bob ") = true
	 * 
	 * @return
	 */
	public static Boolean isNotBlank(String text) {
		if (text == null || "".equals(text)) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}

	public static String ifBlankDefault(String text, String defaultString) {
		if (text == null || "".equals(text)) {
			return defaultString;
		}
		else {
			return text;
		}
	}

	/**
	 * 
	 * 方法描述 判断字符串是否为 yyyy-MM-dd日期格式
	 * 
	 * @param date
	 * @return
	 * @author yechen @date 2014年6月16日 上午11:25:02
	 */
	public static boolean isDate(String date) {
		if (emptyOrNull(date)) {
			return false;
		}

		String regex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(date);
		return m.matches();
	}

	/**
	* URL过滤
	*/
	private static final Pattern URI_FILE_PATTERN = Pattern.compile("\\.(zip|rar|tar|apk|gz|z|exe|dmg|wav|mp3|mpeg|rm|avi|ram|doc|ppt|pdf|xls|xlsx|rtf|tmp|bat|shell|swf)$");

	public static boolean isFileForUrl(String strUrl) {
		String lastSegment = Uri.parse(strUrl).getLastPathSegment();
		if (TextUtils.isEmpty(lastSegment)) {
			return false;
		}

		return URI_FILE_PATTERN.matcher(lastSegment.toLowerCase()).find();
	}

	public static String trimXSSString(String string) {
		return string.replace("<", "").replace(">", "");

	}

	/**
	 * split一个text，返回指定位置的字符串，如果不存在或者数据异常，则返回null
	 * @param content 要切割的字符串
	 * @param splitContent 切割的标志
	 * @param position 返回的数组指定position的内容
	 * @return
	 */
	public static String getSplitTextWithinPosition(String content, String splitContent, int position) {
		String result = null;
		if (!StringUtil.emptyOrNull(content) && !StringUtil.emptyOrNull(splitContent)) {
			String[] temp = content.split(splitContent);
			if (temp != null && temp.length >= position + 1) {
				result = temp[position];
			}
		}
		return result;
	}

	/***
	 * 判断 String 是否是 int zhengfuzhengshu正负整数
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIntegerString(String input) {
		Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
		return mer.find();
	}


	/***
	 *  exception对象转化成字符串描述
	 * @param e exception对象
	 * @return exception对应的描述
	 */
	public static String getDescriptionFromException(Exception e) {
        if (e == null) {
            return  "";
        }

		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
            String ret = sw.toString();
            return ret;
		} catch (Exception e2) {
            return "Bad getDescriptionFromException:"+e2.toString();
		}
	}

	public static String escapeSql(String str) {
		if (StringUtil.emptyOrNull(str)) {
			return "";
		}
		// ' --> ''
		str = str.replaceAll("'", "''");

		// " --> ""
		str = str.replaceAll("\"", "\"\"");

		// \ -->  (remove backslashes)
		str = str.replaceAll("\\\\", "");
		return str;
	}

	public static boolean isEmpty(String source) {
		return source == null || source.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

	public static String subStringBetween(String source, String start, String end) {
		if (source == null || start == null || end == null) {
			return null;
		}
		int indexOf = source.indexOf(start);
		if (indexOf == -1) return null;
		int indexOf2 = source.indexOf(end, start.length() + indexOf);
		return indexOf2 != -1 ? source.substring(start.length() + indexOf, indexOf2) : null;
	}

	public static String subStringAfter(String source, String prefix) {
		if (isEmpty(source)) return source;
		if (prefix == null) return EMPTY;
		int indexOf = source.indexOf(prefix);
		return indexOf != -1 ? source.substring(indexOf + prefix.length()) : EMPTY;

	}

	public static String trim(String str) {
		return str == null ? null : str.trim();
	}

	public static boolean isBlank(String str) {
		if (str != null) {
			int length = str.length();
			if (length != 0) {
				for (int i = 0; i < length; i++) {
					if (!Character.isWhitespace(str.charAt(i))) {
						return false;
					}
				}
				return true;
			}
		}
		return true;
	}

	public static String join(Object[] objArr, String str) {
		return objArr == null ? null : join(objArr, str, 0, objArr.length);
	}

	public static String join(Object[] objArr, String str, int i, int i2) {
		if (objArr == null) {
			return null;
		}
		if (str == null) {
			str = EMPTY;
		}
		int i3 = i2 - i;
		if (i3 <= 0) {
			return EMPTY;
		}
		StringBuilder stringBuilder = new StringBuilder(((objArr[i] == null ? 128 : objArr[i].toString().length()) + str.length()) * i3);
		for (int i4 = i; i4 < i2; i4++) {
			if (i4 > i) {
				stringBuilder.append(str);
			}
			if (objArr[i4] != null) {
				stringBuilder.append(objArr[i4]);
			}
		}
		return stringBuilder.toString();
	}
}
