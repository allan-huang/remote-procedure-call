package test.tw.me.ychuang.rpc;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invokes the matching business logic object by a command and returns a result.
 * 
 * @author Y.C. Huang
 */
public class BizServiceSkeleton {
	private static final Logger log = LoggerFactory.getLogger(BizServiceSkeleton.class);

	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final BizServiceSkeleton INSTANCE = new BizServiceSkeleton();
	}

	public static BizServiceSkeleton getInstance() {
		return LazyHolder.INSTANCE;
	}

	private BizServiceSkeleton() {
		super();
	}

	/**
	 * Echoes a random string
	 * 
	 * @param randomString a random string
	 * @return an original random string appends the specified string
	 */
	public String echo(String randomString) {
		StringBuilder feedback = new StringBuilder(randomString);
		feedback.append("echo from ServiceSkeleton");

		return feedback.toString();
	}

	/**
	 * Create a user
	 * 
	 * @param newId a new user id
	 * @param clientSideTime update time in client side
	 * @return a user
	 */
	public User createUser(long id, Date clientSideTime) {
		String introduction = RandomStringUtils.random(500, true, true);
		String name = RandomStringUtils.randomAlphabetic(20);

		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setClientSideTime(clientSideTime);
		user.setServerSideTime(new Date());
		user.setIntroduction(introduction);

		log.info("Finish to create a user in server-side.");

		return user;
	}

	/**
	 * Update a user
	 * 
	 * @param user a user
	 */
	public void updateUser(User user) {
		String name = RandomStringUtils.randomAlphabetic(20);

		User origUser = new User();
		origUser.setId(user.getId());
		origUser.setName(name);
		origUser.setClientSideTime(user.getClientSideTime());
		origUser.setServerSideTime(new Date());
		origUser.setIntroduction(user.getIntroduction());

		log.info("Finish to update a user in server-side.");
	}

	/**
	 * Find a user
	 * 
	 * @param id a user id
	 * @return a user
	 * @throws Exception any exception
	 */
	public static User findUser(long id) throws Exception {
		throw new Exception("Fail to find any user");
	}

	/**
	 * Delete a user
	 * 
	 * @param id a user id
	 * @return true if the user has been deleted successfully
	 */
	public static boolean deleteUser(long id) {
		log.info("Finish to delete a user in server-side.");

		return true;
	}
}
