package tw.me.ychuang.rpc.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import tw.me.ychuang.rpc.Constants;

/**
 * An abstract class for supporting a auto-reload configuration fuction by observer pattern.<br>
 * Notifies all listeners to read a refreshed configuration when a associated properties file has been modified.
 * 
 * @author Y.C. Huang
 */
public abstract class AutoReloadProperties extends ClassPathProperties {
	/**
	 * A set of {@link AutoReloadListener} that will be notified
	 */
	private final Set<AutoReloadListener> listeners = new HashSet<>();

	/**
	 * Delay the delay between the termination of one execution and the commencement of the next
	 */
	private long triggerPeriod = 0;

	/**
	 * Unit the time unit of the initialDelay and delay parameters
	 */
	private TimeUnit unit = null;

	/**
	 * A trigger is responsible for periodically checking whether the properties file is modified.
	 */
	private ScheduledExecutorService trigger = Executors.newSingleThreadScheduledExecutor();

	/**
	 * default constructor, don't remove it!
	 */
	protected AutoReloadProperties() {
		super();
	}

	/**
	 * Registers a {@link AutoReloadListener}.
	 * 
	 * @param a listener
	 */
	public final void register(AutoReloadListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener is null");
		}

		boolean successful = this.listeners.add(listener);

		if (successful) {
			listener.loadConfiguration(this.config);
		}
	}

	/**
	 * Unregisters a {@link AutoReloadListener}.
	 * 
	 * @param a listener
	 */
	public final void unregister(AutoReloadListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Gets all registered listeners.
	 * 
	 * @return listeners
	 */
	public final Collection<AutoReloadListener> getListeners() {
		return this.listeners;
	}

	/*
	 * (non-Javadoc)
	 * @see tw.me.ychuang.rpc.config.BasePathProperties#load()
	 */
	public final void load() {
		this.load(Constants.DEFAULT_PERIOD, Constants.DEFAULT_UNIT);
	}

	/**
	 * Load a specfied properties file
	 * 
	 * @param triggerPeriod Delay the delay between the termination of one execution
	 * @param unit Unit the time unit of the initialDelay and delay parameters
	 */
	public final void load(long triggerPeriod, TimeUnit unit) {
		synchronized (this) {
			if (this.config != null) {
				return;
			}
		}

		if (triggerPeriod < 1) {
			throw new IllegalArgumentException("Invalid triggerPeriod: " + triggerPeriod);
		}
		this.triggerPeriod = triggerPeriod;

		if (unit == null) {
			throw new IllegalArgumentException("unit is null");
		}
		this.unit = unit;

		this.log.info("Start to load a properties file. url: {}", this.getFileUrl());

		// load a properties file
		try {
			this.config = new PropertiesConfiguration();
			this.config.setURL(this.getFileUrl());
			this.config.load();

		} catch (ConfigurationException e) {
			throw new ConfigLoadException("Fail to load a properties file.", e).addContextValue("File URL", this.getFileUrl());
		}

		// notifiy all listeners to read a loaded configuration
		for (AutoReloadListener listener : this.listeners) {
			listener.loadConfiguration(this.config);
		}

		final FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();

		long refreshDelay = this.unit.toMillis(this.triggerPeriod);
		strategy.setRefreshDelay(refreshDelay);

		this.config.setReloadingStrategy(strategy);
		this.config.setDelimiterParsingDisabled(true);

		// periodically check whether the properties filed is modified
		this.trigger.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				boolean modified = strategy.reloadingRequired();
				AutoReloadProperties.this.log.debug("The properties file is modified? {}, url: {}", modified, AutoReloadProperties.this.getFileUrl());

				if (modified) {
					// force the configuration to load the modified properties file
					AutoReloadProperties.this.config.reload();

					// notifiy all listeners to reread a refreshed configuration
					for (AutoReloadListener listener : AutoReloadProperties.this.listeners) {
						listener.refreshedConfiguration(AutoReloadProperties.this.config);
					}
				}
			}
		}, 0, this.triggerPeriod, this.unit);
	}

	/**
	 * Shuts down the binded trigger thread.
	 */
	public final void unload() {
		synchronized (this) {
			if (this.config == null) {
				return;
			}
		}

		this.listeners.clear();

		if (this.trigger != null) {
			this.trigger.shutdown();
		}
	}
}
