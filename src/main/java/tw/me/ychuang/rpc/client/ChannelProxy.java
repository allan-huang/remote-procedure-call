package tw.me.ychuang.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.Command;
import tw.me.ychuang.rpc.Constants;
import tw.me.ychuang.rpc.Request;
import tw.me.ychuang.rpc.Response;
import tw.me.ychuang.rpc.ResponseFuture;
import tw.me.ychuang.rpc.exception.ClientSideException;
import tw.me.ychuang.rpc.exception.RpcException;
import tw.me.ychuang.rpc.json.JsonSerializer;

/**
 * Wraps a Netty channel in client-side and send a command and receive a response.<br>
 * It’s like one JDBC Connection conceptually.
 * 
 * @author Y.C. Huang
 */
public class ChannelProxy {
	private static final Logger log = LoggerFactory.getLogger(ChannelProxy.class);

	/**
	 * A set of enumeration types for Channel State Type
	 * 
	 * @author Y.C. Huang
	 */
	public enum ChannelStateType {
		inactive, active, stopped, paused;

		public static List<String> asLabels() {
			List<String> labels = new ArrayList<String>();
			for (ChannelStateType type : ChannelStateType.values()) {
				labels.add(type.toString());
			}
			return labels;
		}

		public static boolean checkType(String type) {
			boolean valided = false;
			if (StringUtils.isNotBlank(type)) {
				List<String> validType = ChannelStateType.asLabels();
				if (validType.contains(type)) {
					valided = true;
				}
			}
			return valided;
		}
	}

	/**
	 * A kind of constructor
	 * 
	 * @param id channel's unique id
	 * @param serverHost The host / IP of the remote host
	 * @param serverPort The port number that the remote host listens on
	 * @param channel a Netty channel
	 * @param bootstrap the retained Netty bootstrap for reconnecting purpose
	 */
	public ChannelProxy(int id, String serverHost, int serverPort, Channel channel, Bootstrap bootstrap) {
		super();
		this.id = id;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.setChannel(channel);
		this.bootstrap = bootstrap;
	}

	/**
	 * A response future pool
	 */
	private final ConcurrentMap<Long, ResponseFuture<Response>> futurePool = new ConcurrentHashMap<>();

	/**
	 * The generator of a request's unique id
	 */
	private long idCounter = 0;

	/**
	 * Gets the next id for identify the response future from future pool
	 * 
	 * @return unique id
	 */
	private synchronized Long nextId() {
		if (Long.MAX_VALUE == this.idCounter) {
			this.idCounter = 0;
		}
		++this.idCounter;

		return this.idCounter;
	}

	/**
	 * The number of retry to connect a remote server
	 */
	private int retryCounter = Constants.MAX_RETRY_TIMES;

	/**
	 * Counts down the number of retry to connect a remote server
	 */
	public synchronized int retryCountDown() {
		--this.retryCounter;

		return this.retryCounter;
	}

	/**
	 * Resets the number of retry to connect a remote server
	 */
	public void resetRetryCounter() {
		this.retryCounter = Constants.MAX_RETRY_TIMES;
	}

	/**
	 * The state of a channel proxy
	 */
	private volatile ChannelStateType channelState = ChannelStateType.inactive;

	/**
	 * Gets the state of this channel
	 * 
	 * @return the state of this channel
	 */
	public ChannelStateType getChannelState() {
		return this.channelState;
	}

	/**
	 * Asks that channel whether is stopped or not
	 * 
	 * @return result
	 */
	public boolean isStopped() {
		return this.channelState == ChannelStateType.stopped;
	}

	/**
	 * Stops or not stop channel
	 * 
	 * @param stopped
	 */
	public void setStopped(boolean stopped) {
		if (stopped) {
			this.channelState = ChannelStateType.stopped;
		} else {
			if (this.channel.isActive()) {
				this.channelState = ChannelStateType.active;
			} else {
				this.channelState = ChannelStateType.inactive;
			}
		}
	}

	/**
	 * Returns true if this channel whether is paused or not
	 * 
	 * @return true if this channel whether is paused or not
	 */
	public boolean isPaused() {
		return this.channelState == ChannelStateType.paused;
	}

	/**
	 * Lets this channel be paused
	 */
	public void paused() {
		this.channelState = ChannelStateType.paused;
	}

	/**
	 * Resumes this channel
	 */
	public void resume() {
		if (this.channel.isActive()) {
			this.channelState = ChannelStateType.active;
		} else {
			this.channelState = ChannelStateType.inactive;
		}
	}

	/**
	 * Return true if this channel whether is available a or not
	 * 
	 * @return true if this channel whether is available a or not
	 */
	public boolean isAvailable() {
		boolean result = false;
		if (this.isPaused() || this.isStopped()) {
			result = false;
		} else {
			result = this.channel.isActive();
		}

		return result;
	}

	/**
	 * Channel Proxy's id
	 */
	private final int id;

	/**
	 * Getter method for field 'id'
	 * 
	 * @return unqiue id of channel proxy
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * The host / IP of the remote host
	 */
	private final String serverHost;

	/**
	 * Getter method for field 'serverHost'
	 * 
	 * @return The host / IP of the remote host
	 */
	public String getServerHost() {
		return this.serverHost;
	}

	/**
	 * The port number that the remote host listens on
	 */
	private final int serverPort;

	/**
	 * Getter method for field 'serverPort'
	 * 
	 * @return The port number that the remote host listens on
	 */
	public int getServerPort() {
		return this.serverPort;
	}

	/**
	 * A Netty channel
	 */
	private Channel channel;

	/**
	 * Getter method for field 'channel'
	 * 
	 * @return a Netty channel
	 */
	public Channel getChannel() {
		return this.channel;
	}

	/**
	 * Setter method for field 'channel'
	 * 
	 * @param channel a Netty channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;

		// update latest state of channel
		if (this.channel.isActive()) {
			this.channelState = ChannelStateType.active;
		} else {
			this.channelState = ChannelStateType.inactive;
		}
	}

	/**
	 * A retained Netty bootstrap in channel proxy for reconnecting purpose
	 */
	private Bootstrap bootstrap;

	/**
	 * Getter method for field 'bootstrap'
	 * 
	 * @return a Netty bootstrap
	 */
	public Bootstrap getBootstrap() {
		return this.bootstrap;
	}

	/**
	 * Setter method for field 'bootstrap'
	 * 
	 * @param channel a Netty bootstrap
	 */
	public void setBootstrap(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	/**
	 * The channel manager, also it is a container that manages all channel proxies.
	 */
	private ClientChannelManager manager;

	/**
	 * Getter method for field 'manager'
	 * 
	 * @return a channel manager
	 */
	public ClientChannelManager getManager() {
		return this.manager;
	}

	/**
	 * Setter method for field 'manager'
	 * 
	 * @param manager a channel manager
	 */
	public void setManager(ClientChannelManager manager) {
		this.manager = manager;
	}

	/**
	 * Sends a command to a remote server by a Netty channel
	 * 
	 * @param command a command
	 * @return A future of response implements an asynchronous future pattern.
	 * @throws RpcException if there is no available channel
	 */
	public ResponseFuture<Response> send(Command command) throws RpcException {
		if (false == this.isAvailable()) {
			RpcException error = new ClientSideException("This channel proxy is unavailable. channel proxy: " + this);
			log.warn(error.getMessage(), error);
			throw error;
		}

		// get a unique id for each request
		final Long id = this.nextId();
		log.debug("Start to send a request. id: {}", id);

		// serialize a request with a command to a json message
		Request request = new Request(id, command);
		String requestJson = JsonSerializer.getInstance().toJson(request, Request.class);

		// append a request's boundary
		StringBuilder requestMsg = new StringBuilder(requestJson);
		requestMsg.append(Constants.REQUEST_BOUNDARY);
		ByteBuf reqMsgByteBuf = Unpooled.copiedBuffer(requestMsg.toString(), CharsetUtil.UTF_8);

		// prepare a response future for waiting a real response
		ResponseFuture<Response> future = new ResponseFuture<>(id, this.futurePool);
		this.futurePool.put(id, future);

		// write a json message into this channel
		ChannelFuture channelFuture = this.channel.writeAndFlush(reqMsgByteBuf);
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					log.info("Finish to send a request to the remote server. id: {}", id);

				} else {
					Throwable cause = future.cause();
					log.error("Fail to send a request to the remote server.", cause);
				}
			}
		});

		return future;
	}

	/**
	 * Receives a response and finishes the corresponding response future
	 * 
	 * @param response a real response
	 * @throws RpcException if the corresponding response future can't be found.
	 */
	public void receive(Response response) throws RpcException {
		Long id = response.getId();
		ResponseFuture<Response> future = this.futurePool.get(id);
		
		if (future != null) {
			future.commit(response);
			log.info("Receive a response and commit a result. id: {}, channel proxy: {}", id, this);

		} else {
			throw new ClientSideException("Fail to find any matching future of response.").addContextValue("Id", id).addContextValue("Channel Proxy", this);
		}
	}

	/**
	 * Cancels the waiting requests are retained in this channel proxy
	 */
	public void cancelWaitingRequests() {
		log.info("Cancel all waiting requests. channel proxy: {}", this);

		ResponseFuture<Response> future = null;
		Set<Entry<Long, ResponseFuture<Response>>> futureEntries = this.futurePool.entrySet();
		for (Entry<Long, ResponseFuture<Response>> futureEntry : futureEntries) {
			Long id = futureEntry.getKey();
			future = futureEntry.getValue();

			boolean successful = future.cancel(true);
			if (successful) {
				log.debug("Cancel a waiting request. id: {}", id);
			}
		}
		this.futurePool.clear();
	}

	/**
	 * Returns the number of the waiting requests are retained in this channel proxy
	 * 
	 * @return the number of the waiting requests
	 */
	public int getWaitingRequestSize() {
		return this.futurePool.size();
	}

	/**
	 * Returns true if this channel proxy contains any waiting requests
	 * 
	 * @return true if this channel proxy contains any waiting requests
	 */
	public boolean hasWaitingRequests() {
		return !this.futurePool.isEmpty();
	}

	/**
	 * Overwrites the toString method
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String format = String.format("ClientChannel-%02d", this.id);

		StringBuilder result = new StringBuilder(50);
		result.append(format);
		result.append('/');
		result.append(this.serverHost).append(':');
		result.append(this.serverPort);
		result.append('/');
		result.append(this.channelState);

		return result.toString();
	}
}
