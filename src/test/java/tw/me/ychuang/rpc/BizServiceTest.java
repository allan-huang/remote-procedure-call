package tw.me.ychuang.rpc;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.client.ClientChannelManager;
import tw.me.ychuang.rpc.client.ClientProperties;
import tw.me.ychuang.rpc.exception.ServerSideException;
import tw.me.ychuang.rpc.server.ServerChannelManager;
import tw.me.ychuang.rpc.server.ServerProperties;

public class BizServiceTest {
	private static final Logger log = LoggerFactory.getLogger(BizServiceTest.class);

	static {
		// initiate Log4J factory setting
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		boolean serverStartUp = ServerChannelManager.getInstance().startUp();
		Assert.assertEquals(true, serverStartUp);

		boolean clientStartUp = ClientChannelManager.getInstance().startUp();
		Assert.assertEquals(true, clientStartUp);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ClientProperties.getInstance().unload();
		ServerProperties.getInstance().unload();

		ClientChannelManager.getInstance().shutdown();
		ServerChannelManager.getInstance().shutdown();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void echo() {
		String randomString = RandomStringUtils.random(500, true, true);
		String feedback = BizServiceStub.getInstance().echo(randomString);
		String sampling = feedback.substring(0, randomString.length());

		Assert.assertEquals(randomString, sampling);
	}

	@Test
	public void createUser() {
		long id = RandomUtils.nextLong(0, Long.MAX_VALUE);
		User user = BizServiceStub.getInstance().createUser(id, new Date());

		Assert.assertNotNull(user);
		Assert.assertEquals(id, user.getId());
	}

	@Test
	public void updateUser() {
		long id = RandomUtils.nextLong(0, Long.MAX_VALUE);

		User user = BizServiceStub.getInstance().createUser(id, new Date());
		user.setClientSideTime(new Date());

		BizServiceStub.getInstance().updateUser(user);

		Assert.assertNotNull(user);
		Assert.assertEquals(id, user.getId());
	}

	@Test(expected = ServerSideException.class)
	public void findUser() throws Exception {
		long userId = RandomUtils.nextLong(0, Long.MAX_VALUE);
		User user = BizServiceStub.findUser(userId);
	}

	@Test
	public void deleteUser() {
		long id = RandomUtils.nextLong(0, Long.MAX_VALUE);
		boolean successful = BizServiceStub.deleteUser(id);

		Assert.assertTrue(successful);
	}
}
