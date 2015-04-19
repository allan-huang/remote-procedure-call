package tw.me.ychuang.rpc;

import tw.me.ychuang.rpc.config.ReadOnlyProperties;

/**
 * A container that wraps a specified configuration for {@link Simulator}.
 * 
 * @author Y.C. Huang
 */
public class SimulatorProperties extends ReadOnlyProperties {
	private static final String PROPERTIES_CLASSPATH = "/rpc-simulator.properties";

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final SimulatorProperties INSTANCE = new SimulatorProperties();
	}

	public static SimulatorProperties getInstance() {
		return LazyHolder.INSTANCE;
	}

	private SimulatorProperties() {
		super();
	}

	@Override
	public String getPropertiesClasspath() {
		return PROPERTIES_CLASSPATH;
	}
}
