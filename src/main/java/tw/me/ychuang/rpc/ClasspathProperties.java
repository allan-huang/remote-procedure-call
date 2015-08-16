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

public class ClasspathProperties extends Properties {
	private static final Logger log = LoggerFactory.getLogger(ClasspathProperties.class);

	public ClasspathProperties(String classpath) throws ConfigurationException {
		this(classpath, false);
	}

	public ClasspathProperties(String classpath, boolean throwOnMissing) throws ConfigurationException {
		this.classpath = classpath;
		this.throwOnMissing = throwOnMissing;

		this.load(classpath);
	}

	private String classpath;

	public String getClasspath() {
		return this.classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	private boolean throwOnMissing;

	public boolean isThrowOnMissing() {
		return this.throwOnMissing;
	}

	public void setThrowOnMissing(boolean throwOnMissing) {
		this.throwOnMissing = throwOnMissing;
	}

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

	private static void throwMissingPropertyException(String key) {
		throw new NoSuchElementException(String.format("Key '%s' does not map to an existing object!", key));
	}

	public boolean getBoolean(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return false;
			}
		}

		String value = this.getProperty(key);
		boolean booleanValue = BooleanUtils.toBoolean(value);

		return booleanValue;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		boolean booleanValue = BooleanUtils.toBoolean(value);

		return booleanValue;
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		boolean booleanValue = BooleanUtils.toBoolean(value);

		return booleanValue;
	}

	public byte getByte(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return 0;
			}
		}

		String value = this.getProperty(key);
		byte byteValue = NumberUtils.toByte(value);

		return byteValue;
	}

	public byte getByte(String key, byte defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		byte byteValue = NumberUtils.toByte(value);

		return byteValue;
	}

	public Byte getByte(String key, Byte defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		byte byteValue = NumberUtils.toByte(value);

		return byteValue;
	}

	public double getDouble(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return 0d;
			}
		}

		String value = this.getProperty(key);
		double doubleValue = NumberUtils.toDouble(value);

		return doubleValue;
	}

	public double getDouble(String key, double defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		double doubleValue = NumberUtils.toDouble(value);

		return doubleValue;
	}

	public Double getDouble(String key, Double defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		Double doubleValue = NumberUtils.createDouble(value);

		return doubleValue;
	}

	public float getFloat(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return 0f;
			}
		}

		String value = this.getProperty(key);
		float floatValue = NumberUtils.toFloat(value);

		return floatValue;
	}

	public float getFloat(String key, float defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		float floatValue = NumberUtils.toFloat(value);

		return floatValue;
	}

	public Float getFloat(String key, Float defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		Float floatValue = NumberUtils.createFloat(value);

		return floatValue;
	}

	public int getInt(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return 0;
			}
		}

		String value = this.getProperty(key);
		int intValue = NumberUtils.toInt(value);

		return intValue;
	}

	public int getInt(String key, int defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		int intValue = NumberUtils.toInt(value);

		return intValue;
	}

	public Integer getInteger(String key, Integer defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		Integer intValue = NumberUtils.createInteger(value);

		return intValue;
	}

	public long getLong(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return 0l;
			}
		}

		String value = this.getProperty(key);
		long longValue = NumberUtils.toLong(value);

		return longValue;
	}

	public long getLong(String key, long defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		long longValue = NumberUtils.toLong(value);

		return longValue;
	}

	public Long getLong(String key, Long defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		Long longValue = NumberUtils.createLong(value);

		return longValue;
	}

	public short getShort(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return 0;
			}
		}

		String value = this.getProperty(key);
		short shortValue = NumberUtils.toShort(value);

		return shortValue;
	}

	public short getShort(String key, short defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		short shortValue = NumberUtils.toShort(value);

		return shortValue;
	}

	public Short getShort(String key, Short defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		short shortValue = NumberUtils.toShort(value);

		return shortValue;
	}

	public BigDecimal getBigDecimal(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return BigDecimal.ZERO;
			}
		}

		String value = this.getProperty(key);
		BigDecimal bigDecimalValue = NumberUtils.createBigDecimal(value);

		return bigDecimalValue;
	}

	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		BigDecimal bigDecimalValue = NumberUtils.createBigDecimal(value);

		return bigDecimalValue;
	}

	public BigInteger getBigInteger(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return BigInteger.ZERO;
			}
		}

		String value = this.getProperty(key);
		BigInteger bigIntegerValue = NumberUtils.createBigInteger(value);

		return bigIntegerValue;
	}

	public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String value = this.getProperty(key);
		BigInteger bigIntegerValue = NumberUtils.createBigInteger(value);

		return bigIntegerValue;
	}

	public String getString(String key) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			if (this.throwOnMissing) {
				throwMissingPropertyException(key);
			} else {
				return null;
			}
		}

		String stringValue = this.getProperty(key);

		return stringValue;
	}

	public String getString(String key, String defaultValue) {
		boolean hasKey = this.containsKey(key);

		if (false == hasKey) {
			return defaultValue;
		}

		String stringValue = this.getProperty(key);

		return stringValue;
	}

	public Iterator<String> getKeys() {
		Set<String> keys = this.stringPropertyNames();

		return keys.iterator();
	}

	public Iterator<String> getKeys(String prefix) {
		Set<String> keys = this.stringPropertyNames();
		Set<String> matchings = new HashSet<>();

		for (String key : keys) {
			boolean matching = StringUtils.startsWith(key, prefix);

			if (matching) {
				matchings.add(key);
			}
		}

		return matchings.iterator();
	}
}
