package tw.me.ychuang.rpc;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a persistent set of properties for RPC system.
 * 
 * @author Y.C. Huang
 */
public class RpcProperties extends Properties {
	private static final Logger log = LoggerFactory.getLogger(RpcProperties.class);

	/**
	 * The class path of the specified property file
	 */
	private String propertyFilePath;

	/**
	 * A default constructor
	 * 
	 * @param propertyFilePath the class path of the specified property file
	 */
	public RpcProperties(String propertyFilePath) {
		super();

		URL propFileUrl = RpcProperties.class.getResource(propertyFilePath);
		if (propFileUrl != null) {
			this.propertyFilePath = propertyFilePath;
		}

		log.info("The property file path in: {}", propFileUrl);

		InputStream propFileIn = RpcProperties.class.getResourceAsStream(this.propertyFilePath);
		try {
			this.load(propFileIn);

		} catch (Exception e) {
			log.error("Fail to load the {}", this.propertyFilePath, e);
		}
	}

	/**
	 * Gets the value of the specified property and tries to convert it to integer type
	 * 
	 * @param key the name of the specified property
	 * @return an integer value of the specified property
	 */
	public int getPropertyAsInt(String key) {
		String value = this.getProperty(key);
		int result = NumberUtils.toInt(value);

		log.debug("A key: {}, value: {} in {}", key, value, this.propertyFilePath);

		return result;
	}

	/**
	 * Gets the value of the specified property and converts it to integer type
	 * 
	 * @param key the name of the specified property
	 * @param defaultValue gets a default integer value if conversion failed.
	 * @return an integer value of the specified property
	 */
	public int getPropertyAsInt(String key, int defaultValue) {
		String value = this.getProperty(key, String.valueOf(defaultValue));
		int result = NumberUtils.toInt(value);

		log.debug("A key: {}, value: {}, default: {} in {}.", key, value, defaultValue, this.propertyFilePath);

		return result;
	}

	/**
	 * Gets the value of the specified property and tries to convert it to boolean type
	 * 
	 * @param key the name of the specified property
	 * @return a boolean value of the specified property
	 */
	public boolean getPropertyAsBool(String key) {
		String value = this.getProperty(key);
		boolean result = BooleanUtils.toBoolean(value);

		log.debug("A key: {}, value: {} in {}", key, value, this.propertyFilePath);

		return result;
	}

	/**
	 * Gets the value of the specified property and converts it to boolean type
	 * 
	 * @param key the name of the specified property
	 * @param defaultValue gets a default boolean value if conversion failed.
	 * @return an boolean value of the specified property
	 */
	public boolean getPropertyAsBool(String key, boolean defaultValue) {
		String value = this.getProperty(key, String.valueOf(defaultValue));
		boolean result = BooleanUtils.toBoolean(value);

		log.debug("A key: {}, value: {}, default: {} in {}.", key, value, defaultValue, this.propertyFilePath);

		return result;
	}
}
