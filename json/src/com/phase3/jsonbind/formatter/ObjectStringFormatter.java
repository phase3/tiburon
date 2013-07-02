package com.phase3.jsonbind.formatter;

import org.apache.log4j.*;

import java.math.*;
import java.text.*;
import java.util.*;

/**
 *
 */
public class ObjectStringFormatter {
	private static final Logger log = Logger.getLogger(ObjectStringFormatter.class);

	public ObjectStringFormatter() {
	}

	public Object parse(Class typeClass, String stringValue) throws ParseException {
		if (stringValue == null || stringValue.length() == 0) {
			return null;
		}
		if (typeClass == String.class) {
			return stringValue;
		} else if (typeClass == Date.class) {
			//String dateFormat = this.format.substring(5, this.format.length()-1);
			SimpleDateFormat sdf = new SimpleDateFormat();
			return sdf.parse(stringValue);
		} else if (typeClass == Integer.class || typeClass == int.class) {
			stringValue = stringValue.replace(",",""); //todo, what about i18n???
			if (stringValue.indexOf(".")> 0) {
				stringValue = stringValue.substring(0, stringValue.indexOf("."));
			}
			return Integer.parseInt(stringValue);
		} else if (	typeClass == Long.class || typeClass == long.class) {
			stringValue = stringValue.replace(",",""); //todo, what about i18n???
			if (stringValue.indexOf(".")> 0) {
				stringValue = stringValue.substring(0, stringValue.indexOf("."));
			}
			return Long.parseLong(stringValue);
		} else if (	typeClass == Float.class || typeClass == float.class) {
			stringValue = stringValue.replace(",",""); //todo, what about i18n???
			return Float.parseFloat(stringValue);
		} else if (	typeClass == Double.class || typeClass == double.class) {
			stringValue = stringValue.replace(",",""); //todo, what about i18n???
			return new Double (stringValue);
		} else if (	typeClass == BigDecimal.class) {
			stringValue = stringValue.replace(",",""); //todo, what about i18n???
			return new BigDecimal (stringValue);
		} else {
			throw new ParseException("Unknown data type", 0);
		}
	}

	public String format(final Object objectValue) {
		if (objectValue == null) {
			return "";
		}
//		if (objectValue instanceof String) {
//			return (String)objectValue;
//		} else if (objectValue instanceof Date) {
//			return ((Date)objectValue).toString();
//		} else if (objectValue instanceof Integer
//				|| objectValue instanceof Long
//				|| objectValue instanceof Double
//				|| objectValue instanceof Float
//				|| objectValue instanceof BigDecimal) {
//			return objectValue.toString();
//		} else if( objectValue instanceof Boolean) {
//			return objectValue.toString();
//		}
		return objectValue.toString();
	}
	private static String stripString(final String value, final String validChars) {
		return stripString(value, validChars, value.length());
	}
	// (916) 111-1111 ext 123
	// 01234567890123456789
	// 9161111111 ext 123
	private static String stripString(final String value, final String validChars, final int max) {
		if (value == null) {
			return "";
		}
		StringBuffer stripped = new StringBuffer();
		char chr;
		int pos = 0;
		for(int x=0;x<value.length();x++) {
			chr = value.charAt(x);
			if (validChars.indexOf(chr) != -1) {
				stripped.append(chr);
				pos++;
			}
			if (pos >= max) {
				stripped.append(value.substring(x+1).trim());
				break;
			}
		}
		return stripped.toString();

	}
	private static String buildMask(final String format, final String text) {
		if (text == null || text.length() == 0) {
			return "";
		}
		int pos =0;
		char chr;
		StringBuffer str = new StringBuffer();
		for(int x=0;x<format.length();x++) {
			chr = format.charAt(x);
			if (chr == '#') {
				str.append(text.charAt(pos));
				pos++;
				if (pos >= text.length()) {
					break;
				}
			} else if (chr == '*') {
				str.append(text.substring(pos));
				break;
			} else {
				str.append(chr);
			}
		}
		return str.toString();
	}
}
