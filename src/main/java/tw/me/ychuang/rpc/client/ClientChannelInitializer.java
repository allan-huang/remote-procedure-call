package tw.me.ychuang.rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.ClasspathProperties;
import tw.me.ychuang.rpc.Constants;

/**
 * Creates the needed channel handlers in client-side.
 *
 * @author Y.C. Huang
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static final Logger log = LoggerFactory.getLogger(ClientChannelInitializer.class);

	/**
	 * Handles the idle channels.
	 */
	private static final HeartbeatHandler heartbeatHandler = new HeartbeatHandler();

	/**
	 * Decodes a received string.
	 */
	private static final StringDecoder utf8Decoder = new StringDecoder(CharsetUtil.UTF_8);

	/**
	 * Encodes a requested string.
	 */
	private static final StringEncoder utf8Encoder = new StringEncoder(CharsetUtil.UTF_8);

	/**
	 * The most important channel handler for processing business logic.
	 */
	private static final ChannelHandlerAdapter clientHandler = new ChannelHandlerAdapter();

	/**
	 * A Netty EventExecutorGroup
	 */
	private final EventExecutorGroup executorGroup;

	/**
	 * The indication of whether stream compression will be enabled.
	 */
	private boolean compression = false;

	/**
	 * A kind of constructor
	 *
	 * @param executorGroup a Netty EventExecutorGroup
	 */
	public ClientChannelInitializer(EventExecutorGroup executorGroup) {
		super();
		this.executorGroup = executorGroup;

		ClasspathProperties config = ClientProperties.getInstance();
		this.compression = config.getBoolean("client.channel.stream.compression", false);
	}

	/*
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	 */
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();

		// use the IdleStateHandler to get notified if you haven't received or sent data for dozens of seconds.
		// If this is the case, a heartbeat will be written to the remote peer, and if this fails the connection is closed.
		pipeline.addLast(this.executorGroup, "idleStateHandler", new IdleStateHandler(0, 0, Constants.HEARTBEAT_PERIOD, TimeUnit.SECONDS));
		pipeline.addLast(this.executorGroup, "heartbeatHandler", heartbeatHandler);

		if (this.compression) {
			// Enable stream compression
			pipeline.addLast(this.executorGroup, "deflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
			pipeline.addLast(this.executorGroup, "inflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
		}

		// NUL (0x00) is a message delimiter
		pipeline.addLast(this.executorGroup, "framer", new DelimiterBasedFrameDecoder(8192, Delimiters.nulDelimiter()));

		// string encoder / decoder are responsible for encoding / decoding an UTF-8 string
		pipeline.addLast(this.executorGroup, "encoder", utf8Encoder);
		pipeline.addLast(this.executorGroup, "decoder", utf8Decoder);

		// client hander is responsible for as a remoting call stub
		pipeline.addLast(this.executorGroup, "clientHandler", clientHandler);
	}
}
