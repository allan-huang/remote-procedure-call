package tw.me.ychuang.rfc;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import tw.me.ychuang.rfc.client.ClientChannelManager;
import tw.me.ychuang.rfc.server.ServerChannelManager;

/**
 * The main function of Netty 4-based Remote Function Call (RFC) system.
 * 
 * @author Y.C. Huang
 */
public class Main {
	static {
		// initiate SLF4J Logger Factory setting
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
	}

	public static void main(String[] args) throws Exception {
		ServerChannelManager.getInstance().startUp();
		ClientChannelManager.getInstance().startUp();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ClientChannelManager.getInstance().shutdown();
				ServerChannelManager.getInstance().shutdown();
			}
		});
	}
}
