package tw.me.ychuang.rpc.config;

import org.apache.commons.configuration.PropertiesConfiguration;

public interface AutoReloadListener extends ReadOnlyListener {

	void refreshConfiguration(PropertiesConfiguration config);

}
