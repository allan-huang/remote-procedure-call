package tw.me.ychuang.rpc.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import tw.me.ychuang.rpc.exception.ConfigLoadException;

/**
 * An abstract class for supporting a read-only configuration fuction by observer pattern.<br>
 * Notifies all listeners to read a loaded configuration when a associated properties file has been loaded.
 * 
 * @author Y.C. Huang
 */
public abstract class ReadOnlyProperties extends ClasspathProperties {
	/**
	 * A set of {@link ReadOnlyListener} that will be notified
	 */
	private final Set<ReadOnlyListener> listeners = new HashSet<>();

	/**
	 * default constructor, don't remove it!
	 */
	protected ReadOnlyProperties() {
		super();
	}

	/**
	 * Registers a {@link ReadOnlyListener}.
	 * 
	 * @param a listener
	 */
	public final void register(ReadOnlyListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener is null");
		}

		boolean successful = this.listeners.add(listener);

		if (successful) {
			if (this.config == null) {
				this.load();
			}
			
			listener.loadConfiguration(this.config);
		}
	}

	/**
	 * Unregisters a {@link AutoReloadListener}.
	 * 
	 * @param a listener
	 */
	public final void unregister(ReadOnlyListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Gets all registered listeners.
	 * 
	 * @return listeners
	 */
	public final Collection<ReadOnlyListener> getListeners() {
		return this.listeners;
	}

	/*
	 * (non-Javadoc)
	 * @see tw.me.ychuang.rpc.config.BasePathProperties#load()
	 */
	public final void load() {
		if (this.config != null) {
			return;
		}

		this.log.info("Start to load a properties file. url: {}", this.getFileUrl());

		// load a properties file
		try {
			this.config = new PropertiesConfiguration();
			// don't use any delimiter to convert an complex property values to a string array
			this.config.setDelimiterParsingDisabled(true);

			this.config.setURL(this.getFileUrl());
			this.config.load();

		} catch (ConfigurationException e) {
			throw new ConfigLoadException("Fail to load a properties file.", e).addContextValue("File URL", this.getFileUrl());
		}

		// notifiy all listeners to read a loaded configuration
		for (ReadOnlyListener listener : this.listeners) {
			listener.loadConfiguration(this.config);
		}
	}
}
