package com.jagex.util;

public final class StringUtils {

	public static String format(String string) {
		if (!string.isEmpty()) {
			char[] chars = string.toCharArray();
			for (int index = 0; index < chars.length; index++) {
				if (chars[index] == '_') {
					chars[index] = ' ';

					if (index + 1 < chars.length && chars[index + 1] >= 'a' && chars[index + 1] <= 'z') {
						chars[index + 1] = (char) (chars[index + 1] - 32);
					}
				}
			}
			if (chars[0] >= 'a' && chars[0] <= 'z') {
				chars[0] = (char) (chars[0] - 32);
			}
			return new String(chars);
		}
		return string;
	}
}