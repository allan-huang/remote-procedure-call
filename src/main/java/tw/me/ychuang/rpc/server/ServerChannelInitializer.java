package tw.me.ychuang.rpc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates the needed channel handlers in server-side.
 * 
 * @author Y.C. Huang
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static final Logger log = LoggerFactory.getLogger(ServerChannelInitializer.class);

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
	private static final ChannelHandlerAdapter serverHandler = new ChannelHandlerAdapter();

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
	public ServerChannelInitializer(EventExecutorGroup executorGroup) {
		super();
		this.executorGroup = executorGroup;

		Configuration config = ServerProperties.getInstance().getConfiguration();
		this.compression = config.getBoolean("server.channel.stream.compression", false);
	}

	/*
	 * (non-Javadoc)
	 * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	 */
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();

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

		// server hander is responsible for as a remoting call skeleton
		pipeline.addLast(this.executorGroup, "serverHandler", serverHandler);
	}
}
