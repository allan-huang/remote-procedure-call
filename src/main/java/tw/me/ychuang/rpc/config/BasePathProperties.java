package tw.me.ychuang.rpc.config;

import java.net.URL;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePathProperties {
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	protected PropertiesConfiguration config;

	public final PropertiesConfiguration getConfiguration() {
		if (this.config == null) {
			this.load();
		}

		return this.config;
	}

	public final URL getFileUrl() {
		URL fileUrl = BasePathProperties.class.getResource(this.getPropertiesClasspath());

		if (fileUrl == null) {
			throw new ConfigLoadException("Fail to load a properties file.").addContextValue("Class Path", this.getPropertiesClasspath());
		}

		return fileUrl;
	}

	public abstract void load();

	public abstract String getPropertiesClasspath();
}
