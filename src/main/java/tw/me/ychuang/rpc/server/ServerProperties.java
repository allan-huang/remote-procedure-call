package tw.me.ychuang.rpc.server;

import tw.me.ychuang.rpc.config.AutoReloadProperties;

public class ServerProperties extends AutoReloadProperties {

	private static final String PROPERTIES_CLASSPATH = "/rpc-server.properties";

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final ServerProperties INSTANCE = new ServerProperties();
	}

	public static ServerProperties getInstance() {
		return LazyHolder.INSTANCE;
	}

	private ServerProperties() {
		super();
	}

	@Override
	public String getPropertiesClasspath() {
		return PROPERTIES_CLASSPATH;
	}
}
