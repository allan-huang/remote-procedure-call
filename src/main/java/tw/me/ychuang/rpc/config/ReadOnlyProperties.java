package tw.me.ychuang.rpc.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public abstract class ReadOnlyProperties extends BasePathProperties {
	private final Set<ReadOnlyListener> listeners = new HashSet<>();

	/**
	 * default constructor, don't remove it!
	 */
	protected ReadOnlyProperties() {
		super();
	}

	public final void register(ReadOnlyListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener is null");
		}

		boolean successful = this.listeners.add(listener);

		if (successful) {
			listener.loadConfiguration(this.config);
		}
	}

	public final void unregister(ReadOnlyListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

	public final Collection<ReadOnlyListener> getListeners() {
		return this.listeners;
	}

	public final void load() {
		if (this.config != null) {
			return;
		}

		this.log.info("Start to load a properties file. url: {}", this.getFileUrl());

		try {
			this.config = new PropertiesConfiguration();
			this.config.setURL(this.getFileUrl());
			this.config.load();

		} catch (ConfigurationException e) {
			throw new ConfigLoadException("Fail to load a properties file.", e).addContextValue("File URL", this.getFileUrl());
		}

		for (ReadOnlyListener listener : this.listeners) {
			listener.loadConfiguration(this.config);
		}
	}
}
