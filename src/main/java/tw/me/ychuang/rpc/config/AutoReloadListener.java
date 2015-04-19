package tw.me.ychuang.rpc.config;

import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * A implemented listener interface in observer pattern will be notified if a associated properties file has be modified.
 * 
 * @author Y.C. Huang
 */
public interface AutoReloadListener extends ReadOnlyListener {
	/**
	 * Obtains a refreshed configuration
	 * 
	 * @param refreshedConfig a refreshed configuration
	 */
	void refreshedConfiguration(PropertiesConfiguration refreshedConfig);

}
