package tw.me.ychuang.rpc;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import tw.me.ychuang.rpc.client.ClientChannelManager;
import tw.me.ychuang.rpc.client.ClientProperties;
import tw.me.ychuang.rpc.server.ServerChannelManager;
import tw.me.ychuang.rpc.server.ServerProperties;

/**
 * The main function of Netty 4-based Remote Function Call (RPC) system.
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

				ClientProperties.getInstance().unload();
				ServerProperties.getInstance().unload();

				// Disables the "shutdownHook" attribute of the configuration element inside the log4j2.xml and manually shutdowns Log4j system
				// Refer to http://stackoverflow.com/questions/17400136/how-to-log-within-shutdown-hooks-with-log4j2
				Configurator.shutdown((LoggerContext) LogManager.getContext());
			}
		});
	}
}
