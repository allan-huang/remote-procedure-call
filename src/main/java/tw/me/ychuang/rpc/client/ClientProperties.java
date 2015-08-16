package tw.me.ychuang.rpc.client;

import tw.me.ychuang.rpc.ClasspathProperties;

/**
 * A container that wraps a associated configuration for {@link ClientChannelManager}.
 *
 * @author Y.C. Huang
 */
public class ClientProperties extends ClasspathProperties {
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
		super(PROPERTIES_CLASSPATH);
	}
}
