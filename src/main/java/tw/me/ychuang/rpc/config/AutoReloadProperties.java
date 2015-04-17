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

public abstract class AutoReloadProperties extends BasePathProperties {
	private final Set<AutoReloadListener> listeners = new HashSet<>();

	private static final long DEFAULT_PERIOD = 1;

	private static final TimeUnit DEFAULT_UNIT = TimeUnit.MINUTES;

	private long triggerPeriod = 0;

	private TimeUnit unit = null;

	private ScheduledExecutorService trigger = Executors.newSingleThreadScheduledExecutor();

	/**
	 * default constructor, don't remove it!
	 */
	protected AutoReloadProperties() {
		super();
	}

	public final void register(AutoReloadListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener is null");
		}

		boolean successful = this.listeners.add(listener);

		if (successful) {
			listener.loadConfiguration(this.config);
		}
	}

	public final void unregister(AutoReloadListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

	public final Collection<AutoReloadListener> getListeners() {
		return this.listeners;
	}

	public final void load() {
		this.load(DEFAULT_PERIOD, DEFAULT_UNIT);
	}

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

		try {
			this.config = new PropertiesConfiguration();
			this.config.setURL(this.getFileUrl());
			this.config.load();

		} catch (ConfigurationException e) {
			throw new ConfigLoadException("Fail to load a properties file.", e).addContextValue("File URL", this.getFileUrl());
		}

		for (AutoReloadListener listener : this.listeners) {
			listener.loadConfiguration(this.config);
		}

		final FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();

		long refreshDelay = this.unit.toMillis(this.triggerPeriod);
		strategy.setRefreshDelay(refreshDelay);

		this.config.setReloadingStrategy(strategy);
		this.config.setDelimiterParsingDisabled(true);

		this.trigger.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				boolean modified = strategy.reloadingRequired();
				AutoReloadProperties.this.log.debug("The properties file is modified? {}, url: {}", modified, AutoReloadProperties.this.getFileUrl());

				if (modified) {
					AutoReloadProperties.this.config.reload();

					for (AutoReloadListener listener : AutoReloadProperties.this.listeners) {
						listener.refreshConfiguration(AutoReloadProperties.this.config);
					}
				}
			}
		}, 0, this.triggerPeriod, this.unit);
	}

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
