package tw.me.ychuang.rpc.config;

import java.net.URL;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A properties file is placed in classpath
 * 
 * @author Y.C. Huang
 */
public abstract class ClassPathProperties {
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * A configuration represents a Properties file
	 */
	protected PropertiesConfiguration config;

	/**
	 * Gets a configuration. See {@link PropertiesConfiguration}
	 * 
	 * @return a configuration
	 */
	public final PropertiesConfiguration getConfiguration() {
		if (this.config == null) {
			this.load();
		}

		return this.config;
	}

	/**
	 * Gets an equivalent URL of a properties file in classpath
	 * 
	 * @return an equivalent URL
	 */
	public final URL getFileUrl() {
		URL fileUrl = ClassPathProperties.class.getResource(this.getPropertiesClasspath());

		if (fileUrl == null) {
			throw new ConfigLoadException("Fail to load a properties file.").addContextValue("Classpath", this.getPropertiesClasspath());
		}

		return fileUrl;
	}

	/**
	 * Load a specfied properties file
	 */
	public abstract void load();

	/**
	 * Gets a classpath of a properties file
	 * 
	 * @return a classpath
	 */
	public abstract String getPropertiesClasspath();
}
