package tw.me.ychuang.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.RpcProperties;

public class ClientProperties extends RpcProperties {
	private static final Logger log = LoggerFactory.getLogger(ClientProperties.class);

	private static final String PROPERTY_FILE_PATH = "/rpc-client.properties";

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
		super(PROPERTY_FILE_PATH);
	}
}
