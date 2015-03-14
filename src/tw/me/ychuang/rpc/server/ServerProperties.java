package tw.me.ychuang.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.RpcProperties;

public class ServerProperties extends RpcProperties {
	private static final Logger log = LoggerFactory.getLogger(ServerProperties.class);

	private static final String PROPERTY_FILE_PATH = "/rpc-server.properties";

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
		super(PROPERTY_FILE_PATH);
	}
}
