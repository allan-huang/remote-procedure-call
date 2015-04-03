package tw.me.ychuang.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.Command;
import tw.me.ychuang.rpc.Result;

/**
 * Finds the matching callee and invokes it by naming rule and Java Reflection API.
 * 
 * @author Y.C. Huang
 */
public abstract class CommandExecutor {
	private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final CommandExecutor INSTANCE = new CommandReflector();
	}

	public static CommandExecutor getInstance() {
		return LazyHolder.INSTANCE;
	}

	protected CommandExecutor() {
		super();
	}

	/**
	 * Dispatches a command to the matching skeleton, executes the method of skeleton by the command,<br>
	 * and return a result finally.
	 * 
	 * @param command a command
	 * @return a result with a return or an exception
	 */
	public abstract Result execute(Command command);
}
