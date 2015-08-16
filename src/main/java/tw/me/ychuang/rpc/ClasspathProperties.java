package tw.me.ychuang.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.exception.ConfigurationException;

/**
 * Tries to look up the file name on the classpath and loads the specified properties file.
 *
 * @author Y.C. Huang
 */
public class ClasspathProperties extends Properties {
	private static final Logger log = LoggerFactory.getLogger(ClasspathProperties.class);

	/**
	 * A kind of the constructor.
	 *
	 * @param classpath the URL of the specified properties file on the classpath.
	 * @throws ConfigurationException if failed to load a properties file.
	 */
	public ClasspathProperties(String classpath) throws ConfigurationException {
		this(classpath, false);
	}

	/**
	 * A kind of the constructor.
	 *
	 * @param classpath the URL of the specified properties file on the classpath.
	 * @param throwOnMissing a flag whether an exception should be thrown for a missing value
	 * @throws ConfigurationException if failed to load a properties file.
	 */
	public ClasspathProperties(String classpath, boolean throwOnMissing) throws ConfigurationException {
		this.classpath = classpath;
		this.throwOnMissing = throwOnMissing;

		this.load(classpath);
	}

	/**
	 * The URL of the specified properties file on the classpath.
	 */
	private String classpath;

	/**
	 * Getter method for field 'classpath'
	 *
	 * @return The URL of the specified properties file on the classpath.
	 */
	public String getClasspath() {
		return this.classpath;
	}

	/**
	 * A flag whether an exception should be thrown for a missing value.
	 */
	private boolean throwOnMissing;

	/**
	 * Getter method for field 'throwOnMissing'
	 *
	 * @return Returns true if an exception should be thrown for a missing value.
	 */
	public boolean isThrowOnMissing() {
		return this.throwOnMissing;
	}

	/**
	 * Tries to look up the file name on the classpath and loads the specified properties file.
	 *
	 * @param classpath the URL of the specified properties file on the classpath.
	 */
	private void load(String classpath) {
		URL configUrl = ClasspathProperties.class.getResource(classpath);
		log.info("Try to load a properties file from classpath: {}", configUrl);

		InputStream input = ClasspathProperties.class.getResourceAsStream(classpath);
		try {
			this.load(input);

		} catch (IOException e) {
			throw new ConfigurationException("Fail to load a properties file.", e).addContextValue("Classpath", this.classpath);

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// nothing to do
				}
			}
		}
	}

	/**
	 * Throws an exception for a missing value.
	 *
	 * @param key the key of a missing value.
	 */
	private void throwMissingPropertyException(String key) {
		throw new NoSuchElementException(String.format("Key '%s' does not map to an existing object!", key));
	}

	/**
	 * Gets the list of the keys contained in the properties.
	 *
	 * @return an iterator of keys.
	 */
	public Iterator<String> getKeys() {
		Set<String> keys = new HashSet<String>();

		for (Object key : this.keySet()) {
			String stringKey = (String) key;
			keys.add(stringKey);
		}

		return keys.iterator();
	}

	/**
	 * Gets the list of the keys contained in the properties.
	 *
	 * @param prefix the prefix to test against.
	 * @return an iterator of keys that match the prefix.
	 */
	public Iterator<String> getKeys(String prefix) {
		Set<String> keys = new HashSet<>();

		for (Object key : this.keySet()) {
			String stringKey = (String) key;
			boolean matching = StringUtils.startsWith(stringKey, prefix);

			if (matching) {
				keys.add(stringKey);
			}
		}

		return keys.iterator();
	}

	/**
	 * Gets a boolean associated with the given properties key.
	 *
	 * @param key the properties key.
	 * @return the boolean value of this key.
	 */
	public boolean getBoolean(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return false;
			}
		}

		String value = this.getProperty(key);
		boolean booleanValue = BooleanUtils.toBoolean(value);

		return booleanValue;
	}

	/**
	 * Gets a boolean associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the boolean value of this key.
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		boolean booleanValue = BooleanUtils.toBoolean(value);

		return booleanValue;
	}

	/**
	 * Gets a boolean associated with the given properties key and tries to convert it into a Boolean object.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the boolean value of this key converted to a Boolean object.
	 */
	public Boolean getBoolean(String key, Boolean defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		boolean booleanValue = BooleanUtils.toBoolean(value);

		return booleanValue;
	}

	/**
	 * Gets a byte associated with the given properties key.
	 *
	 * @param key the properties key.
	 * @return the byte value of this key.
	 */
	public byte getByte(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return 0;
			}
		}

		String value = this.getProperty(key);
		byte byteValue = NumberUtils.toByte(value);

		return byteValue;
	}

	/**
	 * Gets a byte associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the byte value of this key.
	 */
	public byte getByte(String key, byte defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		byte byteValue = NumberUtils.toByte(value);

		return byteValue;
	}

	/**
	 * Gets a byte associated with the given properties key and tries to convert it into a Byte object.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the byte value of this key converted to a Byte object.
	 */
	public Byte getByte(String key, Byte defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		byte byteValue = NumberUtils.toByte(value);

		return byteValue;
	}

	/**
	 * Gets a double associated with the given properties key.
	 *
	 * @param key the properties key.
	 * @return the double value of this key.
	 */
	public double getDouble(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return 0d;
			}
		}

		String value = this.getProperty(key);
		double doubleValue = NumberUtils.toDouble(value);

		return doubleValue;
	}

	/**
	 * Gets a double associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the double value of this key.
	 */
	public double getDouble(String key, double defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		double doubleValue = NumberUtils.toDouble(value);

		return doubleValue;
	}

	/**
	 * Gets a double associated with the given properties key and tries to convert it into a Double object.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the double value of this key converted to a Double object.
	 */
	public Double getDouble(String key, Double defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		Double doubleValue = NumberUtils.createDouble(value);

		return doubleValue;
	}

	/**
	 * Gets a float associated with the given properties key.
	 *
	 * @param key the properties key.
	 * @return the float value of this key.
	 */
	public float getFloat(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return 0f;
			}
		}

		String value = this.getProperty(key);
		float floatValue = NumberUtils.toFloat(value);

		return floatValue;
	}

	/**
	 * Gets a float associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the float value of this key.
	 */
	public float getFloat(String key, float defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		float floatValue = NumberUtils.toFloat(value);

		return floatValue;
	}

	/**
	 * Gets a float associated with the given properties key and tries to convert it into a Float object.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the float value of this key converted to a Float object.
	 */
	public Float getFloat(String key, Float defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		Float floatValue = NumberUtils.createFloat(value);

		return floatValue;
	}

	/**
	 * Gets a int associated with the given properties key.
	 *
	 * @param key the properties key.
	 * @return the int value of this key.
	 */
	public int getInt(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return 0;
			}
		}

		String value = this.getProperty(key);
		int intValue = NumberUtils.toInt(value);

		return intValue;
	}

	/**
	 * Gets a int associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the int value of this key.
	 */
	public int getInt(String key, int defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		int intValue = NumberUtils.toInt(value);

		return intValue;
	}

	/**
	 * Gets a int associated with the given properties key and tries to convert it into a Integer object.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the int value of this key converted to a Integer object.
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		Integer intValue = NumberUtils.createInteger(value);

		return intValue;
	}

	/**
	 * Gets a long associated with the given properties key.
	 *
	 * @param key the properties key.
	 * @return the long value of this key.
	 */
	public long getLong(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return 0l;
			}
		}

		String value = this.getProperty(key);
		long longValue = NumberUtils.toLong(value);

		return longValue;
	}

	/**
	 * Gets a long associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the long value of this key.
	 */
	public long getLong(String key, long defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		long longValue = NumberUtils.toLong(value);

		return longValue;
	}

	/**
	 * Gets a long associated with the given properties key and tries to convert it into a Long object.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the long value of this key converted to a Long object.
	 */
	public Long getLong(String key, Long defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		Long longValue = NumberUtils.createLong(value);

		return longValue;
	}

	/**
	 * Gets a short associated with the given properties key.
	 *
	 * @param key the properties key.
	 * @return the short value of this key.
	 */
	public short getShort(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return 0;
			}
		}

		String value = this.getProperty(key);
		short shortValue = NumberUtils.toShort(value);

		return shortValue;
	}

	/**
	 * Gets a short associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the short value of this key.
	 */
	public short getShort(String key, short defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		short shortValue = NumberUtils.toShort(value);

		return shortValue;
	}

	/**
	 * Gets a short associated with the given properties key and tries to convert it into a Short object.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the short value of this key converted to a Short object.
	 */
	public Short getShort(String key, Short defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		short shortValue = NumberUtils.toShort(value);

		return shortValue;
	}

	/**
	 * Gets a BigDecimal associated with the given properties key.<br>
	 *
	 * @param key the properties key.
	 * @return the BigDecimal value of this key.
	 */
	public BigDecimal getBigDecimal(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return BigDecimal.ZERO;
			}
		}

		String value = this.getProperty(key);
		BigDecimal bigDecimalValue = NumberUtils.createBigDecimal(value);

		return bigDecimalValue;
	}

	/**
	 * Gets a BigDecimal associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the BigDecimal value of this key.
	 */
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		BigDecimal bigDecimalValue = NumberUtils.createBigDecimal(value);

		return bigDecimalValue;
	}

	/**
	 * Gets a BigInteger associated with the given properties key.<br>
	 *
	 * @param key the properties key.
	 * @return the BigInteger value of this key.
	 */
	public BigInteger getBigInteger(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return BigInteger.ZERO;
			}
		}

		String value = this.getProperty(key);
		BigInteger bigIntegerValue = NumberUtils.createBigInteger(value);

		return bigIntegerValue;
	}

	/**
	 * Gets a BigInteger associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the BigInteger value of this key.
	 */
	public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		BigInteger bigIntegerValue = NumberUtils.createBigInteger(value);

		return bigIntegerValue;
	}

	/**
	 * Gets a String associated with the given properties key.<br>
	 *
	 * @param key the properties key.
	 * @return the String value of this key.
	 */
	public String getString(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				this.throwMissingPropertyException(key);
			} else {
				return null;
			}
		}

		String value = this.getProperty(key);

		return value;
	}

	/**
	 * Gets a String associated with the given properties key.<br>
	 * If the property has no value or the key is non-existent, the passed in default value will be returned.
	 *
	 * @param key the properties key.
	 * @param defaultValue the default value.
	 * @return the String value of this key.
	 */
	public String getString(String key, String defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);

		if (value == null) {
			return defaultValue;
		}

		return value;
	}
}
