package tw.me.ychuang.rpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.Constants;
import tw.me.ychuang.rpc.Request;
import tw.me.ychuang.rpc.json.JsonSerializer;

/**
 * Sends a useless message to a remote server if it idles too long.
 * 
 * @author Y.C. Huang
 */
@Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(HeartbeatHandler.class);

	/*
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
		if (event instanceof IdleStateEvent) {
			// create a heartbeat message and convert it to a json message
			String requestJson = JsonSerializer.getInstance().toJson(Request.HEARTBEAT, Request.class);

			StringBuilder requestMsg = new StringBuilder(requestJson);
			// append a request's boundary
			requestMsg.append(Constants.REQUEST_BOUNDARY);
			ByteBuf reqMsgByteBuf = Unpooled.copiedBuffer(requestMsg.toString(), CharsetUtil.UTF_8);

			// send a heartbeat message to a remote server
			ChannelFuture future = context.channel().writeAndFlush(reqMsgByteBuf);
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					if (future.isSuccess()) {
						log.info("Finish to send a HEARTBEAT signal to the remote server while the state of channel is idle.");

					} else {
						Channel channel = future.channel();
						ChannelProxy channelProxy = ClientChannelManager.getInstance().findChannelProxy(channel);

						// log warning message, subsequent event handling is taken over by channelInactive method
						Throwable cause = future.cause();
						log.warn("Fail to send a HEARTBEAT signal to the remote server. channel proxy: {}", channelProxy, cause);
					}
				}
			});
		} else {
			// ignore the event since we don't care it, let it being handled by other event handlers
			super.userEventTriggered(context, event);
		}
	}
}
