package tw.me.ychuang.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.Constants;

/**
 * Starts up and shuts down Netty thread pool.
 * 
 * @author Y.C. Huang
 */
public class ServerChannelManager {
	private static final Logger log = LoggerFactory.getLogger(ServerChannelManager.class);

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final ServerChannelManager INSTANCE = new ServerChannelManager();
	}

	public static ServerChannelManager getInstance() {
		return LazyHolder.INSTANCE;
	}

	private ServerChannelManager() {
		super();
	}

	/**
	 * Shares the same EventExecutorGroup to prevent allocation of unnecessary threads
	 */
	private static EventExecutorGroup executorGroup;

	/**
	 * Shares the same parent NioEventLoopGroup to prevent allocation of unnecessary threads
	 */
	private static NioEventLoopGroup parentEventLoopGroup;

	/**
	 * Shares the same child NioEventLoopGroup to prevent allocation of unnecessary threads
	 */
	private static NioEventLoopGroup childEventLoopGroup;

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
	 * Starts up a set of Netty thread pool and listen on a port by according to a property file.
	 * 
	 * @return whether remoting client is started up or not
	 */
	public boolean startUp() {
		if (this.started) {
			return false;
		}

		ServerProperties propertyLoader = ServerProperties.getInstance();
		if (propertyLoader.isEmpty()) {
			return false;
		}

		Set<Integer> localPorts = new HashSet<>();
		Iterator<Entry<Object, Object>> iterEntries = propertyLoader.entrySet().iterator();
		while (iterEntries.hasNext()) {
			Entry<Object, Object> entry = iterEntries.next();
			String key = (String) entry.getKey();

			if (StringUtils.startsWith(key, "server.local.port")) {
				String value = (String) entry.getValue();

				Integer localPort = NumberUtils.createInteger(value);
				localPorts.add(localPort);

				log.info("Find a server.local.port: {}", localPort);
			}
		}

		if (localPorts.isEmpty()) {
			return false;
		}

		log.info("Start to start up a Netty Server...");

		int evtExecutorSize = propertyLoader.getPropertyAsInt("server.event.executor.size", Constants.DEFAULT_THREAD_SIZE);
		int serverIoThreadSize = propertyLoader.getPropertyAsInt("server.io.thread.size", Constants.DEFAULT_THREAD_SIZE);

		log.info("Find server.event.executor.size: {}, server.io.thread.size: {}", evtExecutorSize, serverIoThreadSize);

		// initiate specific threads for Netty Server
		executorGroup = new DefaultEventExecutorGroup(evtExecutorSize);
		parentEventLoopGroup = new NioEventLoopGroup(1);
		childEventLoopGroup = new NioEventLoopGroup(serverIoThreadSize);

		Channel channel = null;
		ServerBootstrap bootstrap = null;
		ChannelFuture future = null;
		Iterator<Integer> iterLocalPort = localPorts.iterator();

		for (int i = 0; iterLocalPort.hasNext(); i++) {
			final Integer localPort = iterLocalPort.next();

			// bootstraps the server
			if (bootstrap == null) {
				bootstrap = new ServerBootstrap();

				// specifies NIO transport, local socket address
				bootstrap.group(parentEventLoopGroup, childEventLoopGroup).channel(NioServerSocketChannel.class);
				bootstrap.localAddress(new InetSocketAddress(localPort.intValue()));

				// adds handler to channel pipeline
				ServerChannelInitializer initializer = new ServerChannelInitializer(executorGroup);
				bootstrap.childHandler(initializer);

				// We are writing a TCP/IP server, so we are allowed to set the socket options such as tcpNoDelay and keepAlive.
				bootstrap.option(ChannelOption.SO_BACKLOG, 128);
				bootstrap.option(ChannelOption.TCP_NODELAY, true);
				bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

			} else {
				// just clone a copy of bootstrap
				bootstrap = bootstrap.clone();
				bootstrap.localAddress(new InetSocketAddress(localPort.intValue()));
			}

			// bind the server to local poart the wait until the awaitUninterruptibly() method is completed
			future = bootstrap.bind().awaitUninterruptibly();

			if (future.isDone()) {
				if (future.isSuccess()) {
					channel = future.channel();
					this.started = true;

					StringBuilder channelInfo = new StringBuilder(50);
					channelInfo.append(String.format("ServerChannel-%02d", i + 1));
					channelInfo.append('/');
					channelInfo.append("0:0:0:0").append(':');
					channelInfo.append(localPort.intValue());

					log.info("Finish to start up a Netty Server. channel info: {}", channelInfo);

				} else {
					Throwable cause = future.cause();
					log.error("Fail to bind server on port. local port: {}", localPort, cause);
				}
			}
		}

		return true;
	}

	/**
	 * shuts down Netty thread pool.
	 */
	public void shutdown() {
		if (false == this.started) {
			return;
		}

		log.info("Start to shutdown a Netty Server...");

		// Shutdown bootstrap and thread pools; release all resources
		if (executorGroup != null) {
			executorGroup.shutdownGracefully().awaitUninterruptibly();
		}

		if (parentEventLoopGroup != null) {
			parentEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
		}

		if (childEventLoopGroup != null) {
			childEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
		}

		log.info("Finish to shutdown a Netty Server.");
	}
}
