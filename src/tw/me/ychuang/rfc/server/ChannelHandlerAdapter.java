package tw.me.ychuang.rfc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rfc.Command;
import tw.me.ychuang.rfc.Constants;
import tw.me.ychuang.rfc.Request;
import tw.me.ychuang.rfc.Response;
import tw.me.ychuang.rfc.Result;
import tw.me.ychuang.rfc.json.JsonSerializer;

/**
 * The most important channel handler.<br>
 * Processes all data that is sent to or received from channel in server-side.
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
	 * Handles a channel to read a coming message. And then converts it to a request,<br>
	 * invokes a matching skeleton via a command executor, send a response with a result back finally.
	 */
	@Override
	public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
		final long startTime = System.nanoTime();

		try {
			String requestJson = (String) message;
			Request request = JsonSerializer.getInstance().fromJson(requestJson, Request.class);
			log.debug("Receive a request. id: {}", request.getId());

			if (request.isHeartbeat()) {
				// nothing to do since this request is a HEARTBEAT signal
				log.debug("Receive a HEARTBEAT signal. id: {}", request.getId());
				return;
			}

			// deserialize a json message to a request and get a command from it
			final Long id = request.getId();
			Command command = request.getCommand();

			// execute the business logic and obtain a result by a interface of command executor
			Result result = CommandExecutor.getInstance().execute(command);

			// serialize a response with a result to a json message
			Response response = new Response(id, result, result.getReturnClass().getName());
			String responseJson = JsonSerializer.getInstance().toJson(response, Response.class);

			// append a request's boundary
			StringBuilder responseMsg = new StringBuilder(responseJson);
			responseMsg.append(Constants.REQUEST_BOUNDARY);
			ByteBuf resMsgByteBuf = Unpooled.copiedBuffer(responseMsg.toString(), CharsetUtil.UTF_8);

			// write a json message back this channel
			ChannelFuture future = context.channel().write(resMsgByteBuf);
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					if (future.isSuccess()) {
						log.info("Finish to receive a request and return a response back. id: {}", id);

					} else {
						Throwable cause = future.cause();
						log.error("Fail to receive a request and return a response back.", future.cause());
					}

					ServerMeasurer.measure(startTime);
				}
			});
		} finally {
			ReferenceCountUtil.release(message);
		}
	}

	/**
	 * Flushs all previous written messages (that are pending) to the remote peer
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext context) {
		// flush all previous written messages (that are pending) to the remote peer
		context.flush();
	}

	/**
	 * Tries to close a broken channel
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
		// log exception if any error occurred
		log.error("Fail to hold a connection between client and server.", cause);

		// close channel on exception
		context.close();
	}
}
