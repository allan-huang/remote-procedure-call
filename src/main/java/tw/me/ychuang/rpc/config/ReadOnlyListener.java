package tw.me.ychuang.rpc.config;

import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * A implemented listener interface in observer pattern will be notified if a associated properties file has be loaded.
 * 
 * @author Y.C. Huang
 */
public interface ReadOnlyListener {
	/**
	 * Obtains a loaded configuration
	 * 
	 * @param loadedConfig a loaded configuration
	 */
	void loadConfiguration(PropertiesConfiguration loadedConfig);
}
