package tw.me.ychuang.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.Constants;
import tw.me.ychuang.rpc.Constants.ChannelSelectionType;
import tw.me.ychuang.rpc.config.AutoReloadListener;
import tw.me.ychuang.rpc.exception.ClientSideException;
import tw.me.ychuang.rpc.exception.RpcException;

/**
 * Manages all channel proxies, starts up and shuts down Netty thread pool.<br>
 * It’s like one JDBC Driver Manager conceptually.
 * 
 * @author Y.C. Huang
 */
public class ClientChannelManager implements AutoReloadListener {
	private static final Logger log = LoggerFactory.getLogger(ClientChannelManager.class);

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final ClientChannelManager INSTANCE = new ClientChannelManager();
	}

	public static ClientChannelManager getInstance() {
		return LazyHolder.INSTANCE;
	}

	private ClientChannelManager() {
		super();

		ClientProperties.getInstance().register(this);
	}

	/**
	 * A channel proxy pool
	 */
	private final ConcurrentMap<Integer, ChannelProxy> channelPool = new ConcurrentHashMap<>();

	/**
	 * A concurrent lock implements synchronized block
	 */
	private final Lock selectorLock = new ReentrantLock();

	/**
	 * A concurrent condition implements wait and signal method
	 */
	private final Condition selectorCondition = this.selectorLock.newCondition();

	/**
	 * the index of channel pool
	 */
	private int channelIndex = 0;

	/**
	 * Gets the next index for selecting the next channel proxy from channel pool
	 * 
	 * @return index
	 */
	private synchronized Integer nextIndex() {
		if (Integer.MAX_VALUE == this.channelIndex) {
			this.channelIndex = 0;
		}
		int poolSize = this.channelPool.size();
		++this.channelIndex;
		int index = this.channelIndex % poolSize;

		return index;
	}

	/**
	 * Shares the same EventExecutorGroup to prevent allocation of unnecessary threads
	 */
	private static EventExecutorGroup executorGroup;

	/**
	 * Shares the same NioEventLoopGroup to prevent allocation of unnecessary threads
	 */
	private static NioEventLoopGroup eventLoopGroup;

	/**
	 * This channel manager whether has been started or not.
	 */
	private boolean started = false;

	/**
	 * Returns true if this channel manager has been started.
	 * 
	 * @return true if this channel manager has been started
	 */
	public boolean isStarted() {
		return this.started;
	}

	/**
	 * Starts up a set of Netty channels by according to a property file.
	 * 
	 * @return whether remoting client is started up or not
	 */
	public boolean startUp() {
		if (this.started) {
			return false;
		}

		PropertiesConfiguration config = ClientProperties.getInstance().getConfiguration();
		if (config.isEmpty()) {
			return false;
		}

		List<RemoteServer> remoteServerList = new ArrayList<>();

		Iterator<String> hostKeys = config.getKeys("remote.server.host");
		while (hostKeys.hasNext()) {
			String host = hostKeys.next();

			String serverHost = config.getString(host);
			String serverId = StringUtils.substringAfterLast(host, ".");

			StringBuilder portKey = new StringBuilder("remote.server.port");
			portKey.append('.').append(serverId);

			int serverProt = config.getInt(portKey.toString(), 0);
			if (serverProt != 0) {
				int serverSize = remoteServerList.size() + 1;
				RemoteServer remoteServer = new RemoteServer(serverSize, serverHost, serverProt);
				remoteServerList.add(remoteServer);

				log.info("Find a pair of remote.server.host: {}, remote.server.port: {}", serverHost, serverProt);
			}
		}

		if (remoteServerList.isEmpty()) {
			return false;
		}

		log.info("Start to start up a Netty Channel...");

		int evtExecutorSize = config.getInt("client.event.executor.size", Constants.DEFAULT_THREAD_SIZE);
		log.info("Find client.event.executor.size: {}", evtExecutorSize);

		// initiate specific threads for Netty Client
		executorGroup = new DefaultEventExecutorGroup(evtExecutorSize);
		eventLoopGroup = new NioEventLoopGroup();

		int totalChannel = config.getInt("client.channel.size", Constants.DEFAULT_THREAD_SIZE);
		log.info("Find client.channel.size: {}", totalChannel);

		Channel channel = null;
		ChannelProxy channelProxy = null;
		Bootstrap bootstrap = null;
		ChannelFuture future = null;

		for (int i = 0; i < totalChannel; i++) {
			if (remoteServerList.isEmpty()) {
				log.warn("Cannot connect any remote servers.");
				break;
			}

			int serverIdx = i % remoteServerList.size();
			final RemoteServer remoteServer = remoteServerList.get(serverIdx);

			// bootstraps the client
			if (bootstrap == null) {
				bootstrap = new Bootstrap();

				// specify EventLoopGroup to handle client events. NioEventLoopGroup is used, as the NIO-Transport should be used
				// specify channel type; use correct one for NIO-Transport
				bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);

				// set InetSocketAddress to which client connects
				bootstrap.remoteAddress(new InetSocketAddress(remoteServer.getServerHost(), remoteServer.getServerPort()));

				// specify ChannelHandler, using ChannelInitializer, called once connection established and channel created
				ClientChannelInitializer initializer = new ClientChannelInitializer(executorGroup);
				bootstrap.handler(initializer);

				// configure the connect timeout option.
				bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Constants.CONNECT_TIMEOUT);

			} else {
				// just clone a copy of bootstrap
				bootstrap = bootstrap.clone();
				// set InetSocketAddress to which client connects
				bootstrap.remoteAddress(new InetSocketAddress(remoteServer.getServerHost(), remoteServer.getServerPort()));
			}

			// connect the client to remote server the wait until the awaitUninterruptibly() method is completed
			future = bootstrap.connect().awaitUninterruptibly();

			if (future.isDone()) {
				if (future.isSuccess()) {
					channel = future.channel();
					channelProxy = new ChannelProxy(i + 1, remoteServer.getServerHost(), remoteServer.getServerPort(), channel, bootstrap);

					this.addChannelProxy(channelProxy);
					this.started = true;
					log.info("Finish to start up a Netty Channel. channel proxy: {}", channelProxy);

				} else {
					// remove the unconnectable server
					i = i - 1;
					remoteServerList.remove(serverIdx);

					Throwable cause = future.cause();
					log.error("Fail to connect to a remote server. remote server: {}", remoteServer, cause);
				}
			}
		}

		return true;
	}

	/**
	 * Shuts down Netty thread pool.
	 */
	public void shutdown() {
		if (false == this.started) {
			return;
		}

		log.info("Start to shutdown a Netty Client...");

		// Shutdown bootstrap and thread pools; release all resources
		if (executorGroup != null) {
			executorGroup.shutdownGracefully().awaitUninterruptibly();
		}

		if (eventLoopGroup != null) {
			eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
		}

		this.shutdownAllChannelProxies();

		log.info("Finish to shutdown a Netty Client.");
	}

	@Override
	public void loadConfiguration(PropertiesConfiguration config) {
		// nothing to do
	}

	@Override
	public void refreshConfiguration(PropertiesConfiguration config) {
		log.info("Receive a event notifies that the client properties file has been updated.");

		this.shutdown();
		this.startUp();

		log.info("Finish to restart the client channel manager.");
	}

	private void addChannelProxy(ChannelProxy newChannelProxy) {
		this.channelPool.put(newChannelProxy.getId(), newChannelProxy);
		newChannelProxy.setManager(this);
	}

	/**
	 * Gets the specified channel proxy by its id
	 * 
	 * @param id a channel proxy id
	 * @return the matching channel proxy
	 */
	public ChannelProxy findChannelProxyById(int id) {
		if (id <= 0) {
			return null;
		}

		return this.channelPool.get(id);
	}

	/**
	 * Gets the specified channel proxy by the corresponding Netty Channel
	 * 
	 * @param channel a Netty Channel
	 * @return the matching channel proxy
	 */
	public ChannelProxy findChannelProxy(Channel channel) {
		if (channel == null) {
			return null;
		}

		ChannelProxy result = null;
		Collection<ChannelProxy> proxyCol = this.channelPool.values();
		for (ChannelProxy proxy : proxyCol) {
			if (channel.equals(proxy.getChannel())) {
				result = proxy;
				break;
			}
		}

		return result;
	}

	/**
	 * Finds the matching channel proxies by a host and a port
	 * 
	 * @param serverHost the IP / host of remote server
	 * @param serverPort the port number of that Netty server listens on.
	 * @return all matching channel proxies
	 */
	public List<ChannelProxy> findChannelProxies(String serverHost, int serverPort) {
		ChannelProxy proxy = null;
		Entry<Integer, ChannelProxy> proxyEntry = null;
		List<ChannelProxy> matchingProxyList = new ArrayList<>();

		if (StringUtils.isBlank(serverHost)) {
			matchingProxyList.addAll(this.channelPool.values());
		} else {
			Iterator<Entry<Integer, ChannelProxy>> iterProxies = this.channelPool.entrySet().iterator();
			while (iterProxies.hasNext()) {
				proxyEntry = iterProxies.next();
				proxy = proxyEntry.getValue();

				if (proxy.getServerHost().equals(serverHost) && (serverPort < 0 || proxy.getServerPort() == serverPort)) {
					// avoid it taking long time to access channelPool
					matchingProxyList.add(proxy);
				}

			}
		}

		return matchingProxyList;
	}

	/**
	 * Lists all channel proxies and shows their status.
	 * 
	 * @return all channel proxies
	 */
	public List<ChannelProxy> listChannelProxies() {
		List<ChannelProxy> channelProxyList = new ArrayList<>(this.channelPool.values());
		Collections.sort(channelProxyList, new Comparator<ChannelProxy>() {
			@Override
			public int compare(ChannelProxy o1, ChannelProxy o2) {
				return o1.getId() - o2.getId();
			}
		});

		return channelProxyList;
	}

	/**
	 * Dispatches one of channel proxies of the different server to the stub.
	 * 
	 * @return an available channel proxy
	 * @throws RpcException if there is no available channel.
	 */
	public ChannelProxy selectChannelProxy() throws RpcException {
		Configuration config = ClientProperties.getInstance().getConfiguration();
		String selectionType = config.getString("client.channel.selection.type", ChannelSelectionType.round_robin.toString());

		log.info("Start to select a channel proxy by {} rule.", selectionType);

		int poolSize = this.channelPool.size();
		if (this.channelPool.isEmpty()) {
			throw new ClientSideException("Fail to find any available channels.");
		}

		ChannelProxy result = null;
		ChannelSelectionType channelSelType = ChannelSelectionType.valueOf(selectionType);

		switch (channelSelType) {
			case round_robin:
				result = this.selectChannelProxyByRoundRobin();
				break;

			case workload:
				result = this.selectChannelProxyByWorkload();
				break;

			default:
				result = this.selectChannelProxyByRoundRobin();
		}

		log.info("Select a proxy of client channel. channel proxy: {}", result);

		return result;
	}

	/**
	 * Dispatches one of channel proxies of the different server to the stub by according to the Round-Robin rule.
	 * 
	 * @return an available channel proxy
	 * @throws RpcException if there is no available channel.
	 */
	private ChannelProxy selectChannelProxyByRoundRobin() throws RpcException {
		log.debug("Start to select a channel proxy by Round-Robin rule.");

		int poolSize = this.channelPool.size();
		if (this.channelPool.isEmpty()) {
			throw new ClientSideException("Fail to find any available channels.");
		}

		ChannelProxy candidate = null;
		ChannelProxy result = null;

		int selectCounter = poolSize;
		do {
			Integer index = this.nextIndex();
			candidate = this.channelPool.get(index);

			if (candidate != null && candidate.isAvailable()) {
				// find an available channel proxy
				result = candidate;
			} else {
				// Skip the broken channel proxy and finds the next available channel proxy.
				--selectCounter;
			}

			if (selectCounter <= 0) {
				// If no channel proxy is available, all stubs will wait until any channel proxies is reconnected.
				this.selectorLock.lock();
				try {
					this.selectorCondition.await();

				} catch (InterruptedException e) {
					// nothing to do
				} finally {
					this.selectorLock.unlock();
				}

				// reset selector counter for re-selecting one available channel
				selectCounter = poolSize;
			}
		} while (result == null);

		log.info("Select a proxy of client channel. channel proxy: {}", result);

		return result;
	}

	/**
	 * Lists and sorts all channel proxies in ascending order by workload
	 * 
	 * @return all sorted channel proxies in ascending order
	 */
	private List<ChannelProxy> sortChannelProxiesByWorkload() {
		List<ChannelProxy> channelProxyList = new ArrayList<>(this.channelPool.values());
		Collections.sort(channelProxyList, new Comparator<ChannelProxy>() {
			@Override
			public int compare(ChannelProxy o1, ChannelProxy o2) {
				int result = o1.getWaitingRequestSize() - o2.getWaitingRequestSize();
				if (result == 0) {
					result = o1.getId() - o2.getId();
				}

				return result;
			}
		});

		return channelProxyList;
	}

	/**
	 * Dispatches one of channel proxies of the different server to the stub by according to the workload rule.
	 * 
	 * @return an available channel proxy
	 * @throws RpcException if there is no available channel.
	 */
	private ChannelProxy selectChannelProxyByWorkload() throws RpcException {
		log.debug("Start to select a channel proxy by workload rule.");

		int poolSize = this.channelPool.size();
		if (this.channelPool.isEmpty()) {
			throw new ClientSideException("Fail to find any available channels.");
		}

		ChannelProxy candidate = null;
		ChannelProxy result = null;

		// sorted all channel proxies by the number of waiting requests per channel proxy in ascending order
		List<ChannelProxy> sortedChannelProxyList = this.sortChannelProxiesByWorkload();

		int index = 0;
		int selectCounter = poolSize;
		do {
			candidate = sortedChannelProxyList.get(index);

			if (candidate != null && candidate.isAvailable()) {
				// find an available channel proxy
				result = candidate;
			} else {
				// Skip the broken channel proxy and finds the next available channel proxy.
				--selectCounter;
				++index;
			}

			if (selectCounter <= 0) {
				// If no channel proxy is available, all stubs will wait until any channel proxies is reconnected.
				this.selectorLock.lock();
				try {
					this.selectorCondition.await();

				} catch (InterruptedException e) {
					// nothing to do
				} finally {
					this.selectorLock.unlock();
				}

				// reset selector counter for re-selecting one available channel
				selectCounter = poolSize;
				index = 0;
			}
		} while (result == null);

		log.info("Select a proxy of client channel. channel proxy: {}", result);

		return result;
	}

	/**
	 * The Netty Channels are paused and don’t accept any command temporarily.<br>
	 * These channels are connected with the same server host and port.
	 * 
	 * @param serverHost the IP / host of remote server
	 * @param serverPort the port number of that Netty server listens on.
	 */
	public void pauseChannelProxies(String serverHost, int serverPort) {
		List<ChannelProxy> candidateList = this.findChannelProxies(serverHost, serverPort);

		for (ChannelProxy candidate : candidateList) {
			this.pauseChannelProxy(candidate);
		}
	}

	/**
	 * A Netty Channel is paused and don’t accept any command temporarily.
	 * 
	 * @param channelProxy the specified channel proxy
	 */
	private void pauseChannelProxy(final ChannelProxy channelProxy) {
		channelProxy.paused();
		log.info("Pause a channel proxy from pool. channel proxy: {}", channelProxy);

		if (false == channelProxy.hasWaitingRequests()) {
			return;
		}

		final Channel channel = channelProxy.getChannel();
		EventLoop eventLoop = channel.eventLoop();
		eventLoop.schedule(new Runnable() {
			@Override
			public void run() {
				// cancel all waiting requests belong to this channel
				channelProxy.cancelWaitingRequests();
			}
		}, Constants.CANCEL_WAITING_REQUEST_DELAY, TimeUnit.SECONDS);
	}

	/**
	 * Closes the Netty channels and stops accepting any command.<br>
	 * These channels are connected with the same server host and port.
	 * 
	 * @param serverHost the IP / host of remote server
	 * @param serverPort the port number of that Netty server listens on.
	 */
	public void stopChannelProxies(String serverHost, int serverPort) {
		List<ChannelProxy> candidateList = this.findChannelProxies(serverHost, serverPort);

		for (ChannelProxy candidate : candidateList) {
			this.stopChannelProxy(candidate);
		}
	}

	/**
	 * Closes a Netty channel and stops accepting any command.
	 * 
	 * @param channelProxy the specified channel proxy
	 */
	public void stopChannelProxy(final ChannelProxy channelProxy) {
		channelProxy.setStopped(true);

		final Channel channel = channelProxy.getChannel();
		EventLoop eventLoop = channel.eventLoop();
		eventLoop.schedule(new Runnable() {
			@Override
			public void run() {
				if (channelProxy.hasWaitingRequests()) {
					// cancel all waiting requests belong to this channel
					channelProxy.cancelWaitingRequests();
				}
				// close this unused channel
				channel.close();
			}
		}, Constants.CANCEL_WAITING_REQUEST_DELAY, TimeUnit.SECONDS);

		log.info("Stop a channel proxy from pool. channel proxy: {}", channelProxy);
	}

	/**
	 * Shuts down all channel proxies and cancel all waiting requests.
	 */
	private void shutdownAllChannelProxies() {
		ChannelProxy proxy = null;
		Entry<Integer, ChannelProxy> proxyEntry = null;
		List<ChannelProxy> candidates = new ArrayList<>();

		Iterator<Entry<Integer, ChannelProxy>> iterProxies = this.channelPool.entrySet().iterator();
		while (iterProxies.hasNext()) {
			proxyEntry = iterProxies.next();
			proxy = proxyEntry.getValue();

			iterProxies.remove();
			proxy.setManager(null);
			proxy.setStopped(true);
			candidates.add(proxy);
			log.info("Stop a channel proxy from pool. channel proxy: {}", proxy);
		}

		Channel channel = null;
		for (ChannelProxy candidate : candidates) {
			channel = candidate.getChannel();

			if (candidate.hasWaitingRequests()) {
				// cancel all waiting requests belong to this channel
				candidate.cancelWaitingRequests();
			}
			// close this unused channel
			channel.close();
		}
	}

	/**
	 * Reconnects Netty channels and start to accept any command again.<br>
	 * These channels are connected with the same server host and port.
	 * 
	 * @param serverHost the IP / host of remote server
	 * @param serverPort the port number of that Netty server listens on.
	 */
	public void restartChannelProxies(String serverHost, int serverPort) {
		List<ChannelProxy> candidateList = this.findChannelProxies(serverHost, serverPort);

		if (candidateList.isEmpty()) {
			return;
		}

		for (ChannelProxy candidate : candidateList) {
			if (candidate.isStopped()) {
				this.restartChannelProxy(candidate);

			} else {
				candidate.resume();
				log.info("Resume a channel proxy from pool. channel proxy: {}", candidate);
			}
		}

		this.selectorLock.lock();
		try {
			this.selectorCondition.signalAll();

		} finally {
			this.selectorLock.unlock();
		}
	}

	/**
	 * Reconnects Netty channels and start to accept any command again.
	 * 
	 * @param channelProxy the specified channel proxy
	 */
	private void restartChannelProxy(final ChannelProxy channelProxy) {
		if (false == channelProxy.isStopped()) {
			log.info("This channel proxy is not stopped. channel proxy: {}", channelProxy);
			return;
		}

		// clone the original configuration of bootstrap for rebuilding a connection between client and server
		final Bootstrap newBootstrap = channelProxy.getBootstrap().clone();

		// reconnect the channel to remote server
		ChannelFuture future = newBootstrap.connect();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					Channel newChannel = future.channel();
					channelProxy.setChannel(newChannel);
					channelProxy.setBootstrap(newBootstrap);

					log.info("Finish to reconnect the remote server. channel proxy: {}", channelProxy);

				} else {
					Throwable cause = future.cause();
					log.warn("Fail to reconnect to the remote server. channel proxy: {}", channelProxy, cause);
				}
			}
		});

		log.info("Restart a channel proxy from pool. channel proxy: {}", channelProxy);
	}

	private static class RemoteServer {
		public RemoteServer(int id, String serverHost, int serverPort) {
			this.id = id;
			this.serverHost = serverHost;
			this.serverPort = serverPort;
		}

		private int id;

		public int getId() {
			return this.id;
		}

		private String serverHost;

		public String getServerHost() {
			return this.serverHost;
		}

		private int serverPort;

		public int getServerPort() {
			return this.serverPort;
		}

		public void setServerPort(int serverPort) {
			this.serverPort = serverPort;
		}

		public String toString() {
			String format = String.format("RemoteServer-%02d", this.id);

			StringBuilder result = new StringBuilder(50);
			result.append(format);
			result.append('/');
			result.append(this.serverHost).append(':');
			result.append(this.serverPort);

			return result.toString();
		}
	}
}
