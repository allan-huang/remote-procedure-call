package tw.me.ychuang.rpc.config;

import org.apache.commons.configuration.PropertiesConfiguration;

public interface ReadOnlyListener {

	void loadConfiguration(PropertiesConfiguration config);

}
