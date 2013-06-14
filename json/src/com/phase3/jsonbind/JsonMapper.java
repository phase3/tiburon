package com.phase3.jsonbind;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.phase3.jsonbind.formatter.*;
import com.phase3.jsonbind.mask.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class JsonMapper {
	ObjectMapper jacksonMapper = new ObjectMapper();

	private ObjectStringFormatter formatter = new ObjectStringFormatter();
	private HashMap<Class,Method[]> classMethodCache = new HashMap<Class, Method[]>();
	//private HashMap<String, String> classMethodNameCache = new HashMap<String, String>();
	private boolean isPretty = false;
	private int indentLevel = 0;

	//private boolean forceEmptyLists = false; // todo
	private JsonMask mask;

	public boolean isPretty() {
		return isPretty;
	}

	public void setPretty(boolean pretty) {
		this.isPretty = pretty;
	}

	public <T> T deserialize(String json, Class<T> rootClass) throws IOException {
		return jacksonMapper.readValue(json, rootClass);
	}
	public String serializeListAsString(List list) throws Exception {
		if (list == null || list.size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		indentLevel++;
		if (isPretty) {
			sb.append("\r\n");
			sb.append(indent());
		}
		boolean any = false;
		for(Object o : list) {
			if (any) {
				sb.append(",");
				if (isPretty) {
					sb.append("\r\n");
					sb.append(indent());
				}
			}

			if (isPretty) {
				writePrettyString(sb, o, "");
			} else {
				writeString(sb, o, "");
			}
			any = true;
		}
		indentLevel--;
		if (isPretty) {
			sb.append("\r\n");
		}
		sb.append("]");
		return sb.toString();
	}
	//todo, write stream
	public String serializeAsString(Object o) throws Exception {
		if (o == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();

		return isPretty?writePrettyString(sb, o, ""):writeString(sb, o, "");
	}
	//todo, write stream
	private String writeString(StringBuffer sb, Object o, String maskPath) throws IllegalAccessException, InvocationTargetException {
		// ignore no value
		if (o == null) {
			return null;
		}
		sb.append("{");
		// get the object method list (including inherited)
		Class<?> oClass = o.getClass();

		Method[] list = classMethodCache.get(oClass);
		if (list ==null) {
			list = oClass.getMethods();
			int listLength = list.length;
			ArrayList<Method> aList = new ArrayList<Method>();

			for(int x=0;x<listLength;x++) {
				Method m = list[x];

				if (m.isSynthetic() || m.getGenericParameterTypes().length > 0) {
					// don't process synthetic or methods with parameters
					continue;
				}

				String methodName = m.getName();
				if (!(methodName.startsWith("is") || methodName.startsWith("get")) || methodName.equals("getClass")) {
					// only process methods that are "getters"
					continue;
				}
				JsonIgnore ignore = m.getAnnotation(JsonIgnore.class);
				if (ignore != null && ignore.value()) {
					//ignore methods that have been marked as "ignore"
					continue;
				}

				aList.add(m);
			}
			list = aList.toArray(new Method[aList.size()]);
			classMethodCache.put(oClass, list);

		}

		int listLength = list.length;
		boolean any = false;
		for(int x=0;x<listLength;x++) {
			Method m = list[x];

			String methodName = m.getName();
			String name = null;

			JsonProperty propName = m.getAnnotation(JsonProperty.class);
			if (propName != null && propName.value() != null) {
				// use declared name rather than extracted name
				name = propName.value();
			} else {
				name = methodName.substring(methodName.startsWith("is")?2:3);
				name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
			}

			if (mask != null) {
				// if we have a mask follow its pattern
				if (!mask.isIncluded(maskPath + name)) {
					// ignore variables that don't match
					continue;
				}
			}
			// get the object value
			Object value = m.invoke(o);
			if (value == null) {
				continue;
				//todo: express nulls as an option?
			}

			if (any) {
				sb.append(",");
			}
			// write the variable name
			sb.append("\"");
			sb.append(name);
			sb.append("\":");
			if (m.getReturnType() == String.class) {
				sb.append("\"");
				// escape the contents, and format as needed
				sb.append(escape(formatter.format(value)));
				sb.append("\"");
				any = true;
			} else if (m.getReturnType().isPrimitive()) {
				sb.append(formatter.format(value));
				any = true;
			} else if (m.getReturnType() == Long.class) {
				sb.append(formatter.format(value));
				any = true;
			} else if (m.getReturnType() == Date.class) {
				sb.append(((Date)value).getTime());
				any = true;
			} else if (m.getReturnType().getAnnotation(JsonValue.class) != null){

				// so... if we are a JsonValue class (e.g. toString())
				// or a primitive
				// or a string then write the value
				sb.append("\"");
				// escape the contents, and format as needed
				sb.append(escape(formatter.format(value)));
				sb.append("\"");
				any = true;
			} else if (value instanceof List) {
				// if we are of type list, then traverse
				List objectList = (List)value;
				sb.append("[");

				boolean objAny = false;
				// for each object in list
				for(Object obj : objectList) {
					if (objAny) {
						sb.append(",");
					}
					// recurse and write its properties
					// build mask path
					writeString(sb, obj, maskPath + name + ".");
					objAny = true;
				}
				sb.append("]");

			} else {
				// break down object further and recurse
				writeString(sb, value, maskPath + name + ".");
				any = true;
			}

		}
		sb.append("}");

		return sb.toString();
	}
	private String writePrettyString(StringBuffer sb, Object o, String maskPath) throws IllegalAccessException, InvocationTargetException {
		// ignore no value
		if (o == null) {
			return null;
		}
		// get the object method list (including inherited)
		Class<?> oClass = o.getClass();
		sb.append("{\r\n");
		indentLevel++;

		Method[] list = classMethodCache.get(oClass);
		if (list ==null) {
			list = oClass.getMethods();
			int listLength = list.length;
			ArrayList<Method> aList = new ArrayList<Method>();

			for(int x=0;x<listLength;x++) {
				Method m = list[x];

				if (m.isSynthetic() || m.getGenericParameterTypes().length > 0) {
					// don't process synthetic or methods with parameters
					continue;
				}

				String methodName = m.getName();
				if (!(methodName.startsWith("is") || methodName.startsWith("get")) || methodName.equals("getClass")) {
					// only process methods that are "getters"
					continue;
				}
				JsonIgnore ignore = m.getAnnotation(JsonIgnore.class);
				if (ignore != null && ignore.value()) {
					//ignore methods that have been marked as "ignore"
					continue;
				}

				aList.add(m);
			}
			list = aList.toArray(new Method[aList.size()]);
			classMethodCache.put(oClass, list);

		}

		int listLength = list.length;
		boolean any = false;
		for(int x=0;x<listLength;x++) {
			Method m = list[x];

			String methodName = m.getName();
			String name = null;

			JsonProperty propName = m.getAnnotation(JsonProperty.class);
			if (propName != null && propName.value() != null) {
				// use declared name rather than extracted name
				name = propName.value();
			} else {
				name = methodName.substring(methodName.startsWith("is")?2:3);
				name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
			}

			if (mask != null) {
				// if we have a mask follow its pattern
				if (!mask.isIncluded(maskPath + name)) {
					// ignore variables that don't match
					continue;
				}
			}
			// get the object value
			Object value = m.invoke(o);
			if (value == null) {
				continue;
				//todo: express nulls as an option?
			}

			if (any) {
				sb.append(",\r\n");
			}
			sb.append(indent());
			// write the variable name
			sb.append("\"");
			sb.append(name);
			sb.append("\": ");
			if (m.getReturnType() == String.class) {
				sb.append("\"");
				// escape the contents, and format as needed
				sb.append(escape(formatter.format(value)));
				sb.append("\"");
				any = true;
			} else if (m.getReturnType().isPrimitive()) {
				sb.append(formatter.format(value));
				any = true;
			} else if (m.getReturnType() == Long.class) {
				sb.append(formatter.format(value));
				any = true;
			} else if (m.getReturnType() == Date.class) {
				sb.append(((Date)value).getTime());
				any = true;
			} else if (m.getReturnType().getAnnotation(JsonValue.class) != null){

				// so... if we are a JsonValue class (e.g. toString())
				// or a primitive
				// or a string then write the value
				sb.append("\"");
				// escape the contents, and format as needed
				sb.append(escape(formatter.format(value)));
				sb.append("\"");
				any = true;
			} else if (value instanceof List) {
				// if we are of type list, then traverse
				List objectList = (List)value;
				sb.append("[\r\n");
				indentLevel++;
				sb.append(indent());

				boolean objAny = false;
				// for each object in list
				for(Object obj : objectList) {
					if (objAny) {
						sb.append(",");
					}
					// recurse and write its properties
					// build mask path
					sb.append(writePrettyString(sb, obj, maskPath + name + "."));
					objAny = true;
				}
				indentLevel--;
				sb.append("]");

			} else {
				// break down object further and recurse
				sb.append(writePrettyString(sb, value, maskPath + name + "."));
				any = true;
			}

		}
		indentLevel--;

		sb.append("\r\n");
		sb.append(indent());

		sb.append("}");

		return sb.toString();
	}

	private String escape(String txt) {
		//return txt.replace("\"", "\\\"");
		StringBuffer sb = new StringBuffer(txt.length());
		char c;
		for(int x=0;x<txt.length();x++) {
			c = txt.charAt(x);
			if (c == '"') {
				sb.append("\\");
			}
			sb.append(c);
		}
		return sb.toString();
	}
	private String indent() {
		switch (indentLevel) {
			case 0:
				return "";
			case 1:
				return "\t";
			case 2:
				return "\t\t";
			case 3:
				return "\t\t\t";
			case 4:
				return "\t\t\t\t";
			case 5:
				return "\t\t\t\t\t";
			case 6:
				return "\t\t\t\t\t\t";
			case 7:
				return "\t\t\t\t\t\t\t";
			case 8:
				return "\t\t\t\t\t\t\t\t";
			case 9:
				return "\t\t\t\t\t\t\t\t\t";
			default:
				return "\t\t\t\t\t\t\t\t\t\t";
		}

	}

	public void setMask(JsonMask mask) {
		this.mask = mask;
	}

	public JsonMask getMask() {
		return mask;
	}
}
