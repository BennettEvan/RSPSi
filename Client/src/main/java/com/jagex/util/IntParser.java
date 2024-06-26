package com.jagex.util;

public class IntParser {

	public static int parseInt(String string, int defaultVal) {
		int returnVal;
		try {
			returnVal = Integer.parseInt(string);
		} catch(NumberFormatException ex) {
			returnVal = defaultVal;
		}
		return returnVal;
	}
}
