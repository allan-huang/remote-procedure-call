package tw.me.ychuang.rpc.client;

import tw.me.ychuang.rpc.config.AutoReloadProperties;

public class ClientProperties extends AutoReloadProperties {

	private static final String PROPERTIES_CLASSPATH = "/rpc-client.properties";

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final ClientProperties INSTANCE = new ClientProperties();
	}

	public static ClientProperties getInstance() {
		return LazyHolder.INSTANCE;
	}

	private ClientProperties() {
		super();
	}

	@Override
	public String getPropertiesClasspath() {
		return PROPERTIES_CLASSPATH;
	}
}
