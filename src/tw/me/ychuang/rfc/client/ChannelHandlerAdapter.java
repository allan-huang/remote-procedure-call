package tw.me.ychuang.rfc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rfc.Constants;
import tw.me.ychuang.rfc.Response;
import tw.me.ychuang.rfc.exception.ClientSideException;
import tw.me.ychuang.rfc.json.JsonSerializer;

/**
 * The most important channel handler.<br>
 * Processes all data that is sent to or received from channel in client-side.
 * 
 * @author Y.C. Huang
 */
@Sharable
public class ChannelHandlerAdapter extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(ChannelHandlerAdapter.class);

	/**
	 * A default constructor
	 */
	public ChannelHandlerAdapter() {
		super();
	}

	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {
		// nothing to do
	}

	/**
	 * Handles an inactive channel and tries to reconnects original remote server
	 */
	@Override
	public void channelInactive(final ChannelHandlerContext context) throws Exception {
		log.info("Client is disconnected from server: {}", context.channel().remoteAddress());

		ChannelProxy channelProxy = ClientChannelManager.getInstance().findChannelProxy(context.channel());
		if (channelProxy == null || channelProxy.isStopped()) {
			log.warn("Fail to find any matching proxy of client channel or this client channel had been stopped.");
			return;
		}

		log.info("Reconnects to remote server after {} seconds.", Constants.RECONNECT_DELAY);

		// delay several seconds to reconnect the original remote server
		EventLoop eventLoop = context.channel().eventLoop();
		eventLoop.schedule(new Runnable() {
			@Override
			public void run() {
				reconnect(context);
			}
		}, Constants.RECONNECT_DELAY, TimeUnit.SECONDS);
	}

	/**
	 * Reconnects to the original remote server
	 */
	private static void reconnect(final ChannelHandlerContext context) {
		final ChannelProxy channelProxy = ClientChannelManager.getInstance().findChannelProxy(context.channel());
		if (channelProxy == null) {
			log.warn("Fail to find any matching proxy of client channel or this client channel had been stopped.");
			return;
		}

		int retryCounter = channelProxy.retryCountDown();
		if (retryCounter <= 0) {
			// close this broken channel and cancel all waiting requests in this channel
			ClientChannelManager.getInstance().stopChannelProxy(channelProxy);
			log.error("Cannot reconnect the remote server for exceeding the max allowable times. channel proxy: {}", channelProxy);
			return;
		}

		// clone the original configuration of bootstrap for rebuilding a connection between client and server
		final Bootstrap newBootstrap = channelProxy.getBootstrap().clone();

		// reconnect the original remote server
		ChannelFuture future = newBootstrap.connect();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					Channel newChannel = future.channel();
					channelProxy.setChannel(newChannel);
					channelProxy.setBootstrap(newBootstrap);
					channelProxy.resetRetryCounter();

					log.info("Finish to reconnect the remote server. channel proxy: {}", channelProxy);

				} else {
					Throwable cause = future.cause();
					log.warn("Fail to reconnect to the remote server. channel proxy: {}", channelProxy, cause);

					future.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							reconnect(context);
						}
					}, Constants.RECONNECT_DELAY, TimeUnit.SECONDS);
				}
			}
		});
	}

	/**
	 * Handles a channel to read a coming message and convert it to a response
	 */
	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		try {
			// read a message and convert it to a response
			String responseJson = (String) message;
			Response response = JsonSerializer.getInstance().fromJson(responseJson, Response.class);
			log.debug("Receive a request. id: {}", response.getId());

			// find the original channel proxy and let the proxy receive it
			ChannelProxy channelProxy = ClientChannelManager.getInstance().findChannelProxy(context.channel());
			if (channelProxy != null) {
				channelProxy.receive(response);
			} else {
				throw new ClientSideException("Fail to find any matching proxy of client channel.").addContextValue("Remote Address", context.channel()
						.remoteAddress());
			}
		} finally {
			ReferenceCountUtil.release(message);
		}
	}

	/**
	 * Tries to close a broken channel
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		ChannelProxy channelProxy = ClientChannelManager.getInstance().findChannelProxy(context.channel());
		if (channelProxy != null) {
			ClientChannelManager.getInstance().stopChannelProxy(channelProxy);
		}

		// log exception if any error occurred
		log.error("Fail to hold a connection between client and server. channel proxy: {}", channelProxy, cause);

		// close channel on exception
		context.close();
	}
}
