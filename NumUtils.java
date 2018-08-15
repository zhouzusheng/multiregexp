package cn.kbyte.utils.phone;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumUtils {
	private static final Character[] CN_NUMERIC = { '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'};
	private static Map<Character, Integer> cnNumeric = init();

	private static final Pattern pattern = Pattern.compile("[零幺一两二三四五六七八九十百千万亿]+");

	private static Map<Character, Integer> init() {
		Map<Character, Integer> cnNumeric = new HashMap<Character, Integer>(40, 0.85f);
		for (int j = 0; j < CN_NUMERIC.length; j++)
			cnNumeric.put(CN_NUMERIC[j], j + 1);
		cnNumeric.put('零', 0);
		cnNumeric.put('幺', 1);
		cnNumeric.put('两', 2);
		cnNumeric.put('十', 10);
		cnNumeric.put('拾', 10);
		cnNumeric.put('百', 100);
		cnNumeric.put('佰', 100);
		cnNumeric.put('千', 1000);
		cnNumeric.put('仟', 1000);
		cnNumeric.put('万', 10000);
		cnNumeric.put('亿', 100000000);
		return cnNumeric;
	}

	public static long getCNNumeric(char c) {
		Integer i = cnNumeric.get(c);
		if (i == null)
			return -1;
		return i.intValue();
	}

	public static long cnNumericToArabic(String cnn) {
		cnn = cnn.trim();
		if (cnn.length() == 1)
			return getCNNumeric(cnn.charAt(0));

		int yi = -1, wan = -1, qian = -1, bai = -1, shi = -1;
		long val = 0;
		yi = cnn.lastIndexOf('亿');
		if (yi > -1) {
			val += cnNumericToArabic(cnn.substring(0, yi)) * 100000000;
			if (yi < cnn.length() - 1)
				cnn = cnn.substring(yi + 1, cnn.length());
			else
				cnn = "";

			if (cnn.length() == 1) {
				long arbic = getCNNumeric(cnn.charAt(0));
				if (arbic <= 10)
					val += arbic * 10000000;
				cnn = "";
			}
		}
		wan = cnn.lastIndexOf('万');
		if (wan > -1) {
			val += cnNumericToArabic(cnn.substring(0, wan)) * 10000;
			if (wan < cnn.length() - 1)
				cnn = cnn.substring(wan + 1, cnn.length());
			else
				cnn = "";
			if (cnn.length() == 1) {
				long arbic = getCNNumeric(cnn.charAt(0));
				if (arbic <= 10)
					val += arbic * 1000;
				cnn = "";
			}
		}

		qian = cnn.lastIndexOf('千');
		if (qian > -1) {
			val += cnNumericToArabic(cnn.substring(0, qian)) * 1000;
			if (qian < cnn.length() - 1)
				cnn = cnn.substring(qian + 1, cnn.length());
			else
				cnn = "";
			if (cnn.length() == 1) {
				long arbic = getCNNumeric(cnn.charAt(0));
				if (arbic <= 10)
					val += arbic * 100;
				cnn = "";
			}
		}

		bai = cnn.lastIndexOf('百');
		if (bai > -1) {
			val += cnNumericToArabic(cnn.substring(0, bai)) * 100;
			if (bai < cnn.length() - 1)
				cnn = cnn.substring(bai + 1, cnn.length());
			else
				cnn = "";
			if (cnn.length() == 1) {
				long arbic = getCNNumeric(cnn.charAt(0));
				if (arbic <= 10)
					val += arbic * 10;
				cnn = "";
			}
		}

		shi = cnn.lastIndexOf('十');
		if (shi > -1) {
			if (shi == 0)
				val += 1 * 10;
			else
				val += cnNumericToArabic(cnn.substring(0, shi)) * 10;
			if (shi < cnn.length() - 1)
				cnn = cnn.substring(shi + 1, cnn.length());
			else
				cnn = "";
		}

		cnn = cnn.trim();
		for (int j = 0; j < cnn.length(); j++)
			val += getCNNumeric(cnn.charAt(j)) * Math.pow(10, cnn.length() - j - 1);

		return val;
	}

	public static long qCNNumericToArabic( String cnn ) {
		int val = 0;
		cnn = cnn.trim();
		for ( int j = 0; j < cnn.length(); j++ )
		val += getCNNumeric(cnn.charAt(j))
		* Math.pow(10, cnn.length() - j - 1);
		return val;
	}
	
	public static String convertNum(String input, int min) {
		StringBuilder builder = new StringBuilder();
		int lastpos = 0;
		Matcher m = pattern.matcher(input);
		while(m.find()) {
			builder.append(input, lastpos, m.start());
			String value = m.group();
			if(value.length() >= min) {
				long val = cnNumericToArabic(value);
				builder.append(val);
			} else 
				builder.append(value);
			lastpos = m.end();
		}
		if(lastpos < input.length()) {
			builder.append(input, lastpos, input.length());
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		long val = 0;
		long s = System.nanoTime();
		
		val = cnNumericToArabic("一三九一一二六");
		System.out.println(val);
		System.out.println(convertNum("打电话给张三一打电话给一三九一一二六一三九一一二六", 3));
		System.out.println(convertNum("发张大千", 3));
		//val = cnNumericToArabic("一九九八", true);
		long e = System.nanoTime();
		System.out.format("Done["+val+"], cost: %.5fsec\n", ((float)(e - s)) / 1E9);
	}
}
