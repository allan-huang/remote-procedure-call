package tw.me.ychuang.rpc.json;

import java.util.HashMap;
import java.util.Map;

/**
 * A primitives utility that converts a primitive json string to a primitive wrapper type
 * 
 * @author Y.C. Huang
 */
public class PrimitiveWrapperUtils {
	private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();

	static {
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
		primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
	}

	private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();
	static {
		for (final Class<?> primitiveClass : primitiveWrapperMap.keySet()) {
			final Class<?> wrapperClass = primitiveWrapperMap.get(primitiveClass);
			if (false == primitiveClass.equals(wrapperClass)) {
				wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
			}
		}
	}

	public static boolean isPrimitiveOrWrapper(final Class<?> type) {
		if (type == null) {
			return false;
		}

		return type.isPrimitive() || isPrimitiveWrapper(type);
	}

	public static boolean isPrimitiveWrapper(final Class<?> type) {
		return wrapperPrimitiveMap.containsKey(type);
	}

	public static <T> T toPrimitive(String primitiveJson, Class<T> clazz) {
		Object result = null;

		if (clazz.isAssignableFrom(int.class)) {
			result = Integer.parseInt(primitiveJson);

		} else if (clazz.isAssignableFrom(long.class)) {
			result = Long.parseLong(primitiveJson);

		} else if (clazz.isAssignableFrom(short.class)) {
			result = Short.parseShort(primitiveJson);

		} else if (clazz.isAssignableFrom(float.class)) {
			result = Float.parseFloat(primitiveJson);

		} else if (clazz.isAssignableFrom(double.class)) {
			result = Double.parseDouble(primitiveJson);

		} else if (clazz.isAssignableFrom(byte.class)) {
			result = Byte.parseByte(primitiveJson);

		} else if (clazz.isAssignableFrom(boolean.class)) {
			result = Boolean.parseBoolean(primitiveJson);

		} else if (clazz.isAssignableFrom(char.class)) {
			result = primitiveJson.charAt(0);

		} else if (clazz.isAssignableFrom(Void.TYPE)) {
			result = "";
		}

		Class<?> wrapper = primitiveWrapperMap.get(clazz);

		return (T) wrapper.cast(result);
	}

	public static <T> T toPrimitiveWrapper(String primitiveJson, Class<T> clazz) {
		Object result = null;

		if (clazz.isAssignableFrom(Integer.class)) {
			result = Integer.valueOf(primitiveJson);

		} else if (clazz.isAssignableFrom(Long.class)) {
			result = Long.valueOf(primitiveJson);

		} else if (clazz.isAssignableFrom(Short.class)) {
			result = Short.valueOf(primitiveJson);

		} else if (clazz.isAssignableFrom(Float.class)) {
			result = Float.valueOf(primitiveJson);

		} else if (clazz.isAssignableFrom(Double.class)) {
			result = Double.valueOf(primitiveJson);

		} else if (clazz.isAssignableFrom(Byte.class)) {
			result = Byte.valueOf(primitiveJson);

		} else if (clazz.isAssignableFrom(Boolean.class)) {
			result = Boolean.valueOf(primitiveJson);

		} else if (clazz.isAssignableFrom(Character.class)) {
			result = Character.valueOf(primitiveJson.charAt(0));

		} else if (clazz.isAssignableFrom(Void.class)) {
			result = "";
		}

		return clazz.cast(result);
	}
}
