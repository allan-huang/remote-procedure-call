package test.tw.me.ychuang.rfc;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rfc.Command;
import tw.me.ychuang.rfc.Response;
import tw.me.ychuang.rfc.ResponseFuture;
import tw.me.ychuang.rfc.Result;
import tw.me.ychuang.rfc.client.ClientChannelManager;
import tw.me.ychuang.rfc.client.ChannelProxy;
import tw.me.ychuang.rfc.client.ClientMeasurer;
import tw.me.ychuang.rfc.exception.ClientSideException;
import tw.me.ychuang.rfc.exception.ServerSideException;

/**
 * Creates a command, selects a channel proxy, sends the command, and gets a result finally.
 * 
 * @author Y.C. Huang
 */
public class BizServiceStub {
	private static final Logger log = LoggerFactory.getLogger(BizServiceStub.class);

	private static final String SKELETON_CLASS = "test.tw.me.ychuang.rfc.BizServiceSkeleton";

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final BizServiceStub INSTANCE = new BizServiceStub();
	}

	public static BizServiceStub getInstance() {
		return LazyHolder.INSTANCE;
	}

	private BizServiceStub() {
		super();
	}

	/**
	 * Echoes a random string
	 * 
	 * @param randomString a random string
	 * @return an original random string appends the specified string
	 */
	public String echo(String randomString) {
		long startTime = System.nanoTime();

		// create a command with stub name and method name by naming rule
		Command command = new Command(SKELETON_CLASS, "echo", false);
		command.addParameter(randomString, String.class);

		String echoString = null;
		try {
			// select an available channel proxy by channel manager
			ChannelProxy channelProxy = ClientChannelManager.getInstance().selectChannelProxy();
			// send a command to a remote server via this chanel proxy and get a response future
			ResponseFuture<Response> future = channelProxy.send(command);
			// block the current stub thread until getting a real response
			Response response = future.get();

			if (future.isCancelled()) {
				throw new ClientSideException("A response future is cancelled.");
			}

			// get a result from a real response
			Result result = response.getResult();
			if (false == result.isExceptional()) {
				echoString = (String) result.getReturn();
			} else {
				throw new ServerSideException((String) result.getReturn());
			}
			log.info("Finish to echo a random string in client-side.");

		} catch (Exception e) {
			log.error("Fail to echo a random string.", e);

		} finally {
			ClientMeasurer.measure(startTime);
		}

		return echoString;
	}

	/**
	 * Create a user
	 * 
	 * @param newId a new user id
	 * @param clientSideTime update time in client side
	 * @return a user
	 */
	public User createUser(long newId, Date clientSideTime) {
		long startTime = System.nanoTime();

		// create a command with stub name and method name by naming rule
		Command command = new Command(SKELETON_CLASS, "createUser", false);
		command.addParameter(newId, long.class);
		command.addParameter(clientSideTime, Date.class);

		User user = null;
		try {
			// select an available channel proxy by channel manager
			ChannelProxy channelProxy = ClientChannelManager.getInstance().selectChannelProxy();
			// send a command to a remote server via this chanel proxy and get a response future
			ResponseFuture<Response> future = channelProxy.send(command);
			// block the current stub thread until getting a real response
			Response response = future.get();

			if (future.isCancelled()) {
				throw new ClientSideException("A response future is cancelled.");
			}

			// get a result from a real response
			Result result = response.getResult();
			if (false == result.isExceptional()) {
				user = (User) result.getReturn();
			} else {
				throw new ServerSideException((String) result.getReturn());
			}
			log.info("Finish to create a user in client-side.");

		} catch (Exception e) {
			log.error("Fail to create a user.", e);

		} finally {
			ClientMeasurer.measure(startTime);
		}

		return user;
	}

	/**
	 * Update a user
	 * 
	 * @param user a user
	 */
	public void updateUser(User user) {
		long startTime = System.nanoTime();

		// create a command with stub name and method name by naming rule
		Command command = new Command(SKELETON_CLASS, "updateUser", false);
		command.addParameter(user, User.class);

		try {
			// select an available channel proxy by channel manager
			ChannelProxy channelProxy = ClientChannelManager.getInstance().selectChannelProxy();
			// send a command to a remote server via this chanel proxy and get a response future
			ResponseFuture<Response> future = channelProxy.send(command);
			// block the current stub thread until getting a real response
			Response response = future.get();

			if (future.isCancelled()) {
				throw new ClientSideException("A response future is cancelled.");
			}

			// get a result from a real response
			Result result = response.getResult();
			if (false == result.isExceptional()) {
				Void voidObj = (Void) result.getReturn();
			} else {
				throw new ServerSideException((String) result.getReturn());
			}
			log.info("Finish to update a user in client-side.");

		} catch (Exception e) {
			log.error("Fail to update a user.", e);

		} finally {
			ClientMeasurer.measure(startTime);
		}
	}

	/**
	 * Find a user
	 * 
	 * @param id a user id
	 * @return a user
	 * @throws Exception any exception
	 */
	public static User findUser(long id) throws Exception {
		long startTime = System.nanoTime();

		// create a command with stub name and method name by naming rule
		Command command = new Command(SKELETON_CLASS, "findUser", true);
		command.addParameter(id, long.class);

		User user = null;
		try {
			// select an available channel proxy by channel manager
			ChannelProxy channelProxy = ClientChannelManager.getInstance().selectChannelProxy();
			// send a command to a remote server via this chanel proxy and get a response future
			ResponseFuture<Response> future = channelProxy.send(command);
			// block the current stub thread until getting a real response
			Response response = future.get();

			if (future.isCancelled()) {
				throw new ClientSideException("A response future is cancelled.");
			}

			// get a result from a real response
			Result result = response.getResult();
			if (false == result.isExceptional()) {
				user = (User) result.getReturn();
			} else {
				throw new ServerSideException((String) result.getReturn());
			}
			log.info("Finish to find a user in client-side.");

		} catch (Exception e) {
			log.error("Fail to find any user.", e);
			throw e;

		} finally {
			ClientMeasurer.measure(startTime);
		}

		return user;
	}

	/**
	 * Delete a user
	 * 
	 * @param id a user id
	 * @return true if the user has been deleted successfully
	 */
	public static boolean deleteUser(long id) {
		long startTime = System.nanoTime();

		// create a command with stub name and method name by naming rule
		Command command = new Command(SKELETON_CLASS, "deleteUser", true);
		command.addParameter(id, long.class);

		boolean successful = false;
		try {
			// select an available channel proxy by channel manager
			ChannelProxy channelProxy = ClientChannelManager.getInstance().selectChannelProxy();
			// send a command to a remote server via this chanel proxy and get a response future
			ResponseFuture<Response> future = channelProxy.send(command);
			// block the current stub thread until getting a real response
			Response response = future.get();

			if (future.isCancelled()) {
				throw new ClientSideException("A response future is cancelled.");
			}

			// get a result from a real response
			Result result = response.getResult();
			if (false == result.isExceptional()) {
				successful = (boolean) result.getReturn();
			} else {
				throw new ServerSideException((String) result.getReturn());
			}
			log.info("Finish to delete a user in client-side.");

		} catch (Exception e) {
			log.error("Fail to delete a user.", e);
		} finally {
			ClientMeasurer.measure(startTime);
		}

		return successful;
	}
}
