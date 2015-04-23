package tw.me.ychuang.rpc;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.client.ClientChannelManager;
import tw.me.ychuang.rpc.client.ClientMeasurer;
import tw.me.ychuang.rpc.server.ServerChannelManager;
import tw.me.ychuang.rpc.server.ServerMeasurer;

/**
 * Main function of all test cases.
 * 
 * @author Y.C. Huang
 */
public class Simulator {
	private static final Logger log = LoggerFactory.getLogger(Simulator.class);

	public static enum RequestModeType {
		urgent, heavy, normal, idle;

		public static List<String> asLabels() {
			List<String> labels = new ArrayList<String>();
			for (RequestModeType type : RequestModeType.values()) {
				labels.add(type.toString());
			}
			return labels;
		}

		public static boolean checkType(String type) {
			boolean valided = false;
			if (StringUtils.isNotBlank(type)) {
				List<String> validType = RequestModeType.asLabels();
				if (validType.contains(type)) {
					valided = true;
				}
			}
			return valided;
		}
	}

	static {
		// initiate SLF4J Logger Factory setting
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
	}

	public static void main(String[] args) throws Exception {
		ServerChannelManager.getInstance().startUp();
		boolean clientStartUp = ClientChannelManager.getInstance().startUp();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ClientChannelManager.getInstance().shutdown();
				ServerChannelManager.getInstance().shutdown();

				// ClientProperties.getInstance().unload();
				// ServerProperties.getInstance().unload();

				ClientMeasurer.showStatistics();
				ServerMeasurer.showStatistics();
			}
		});

		PropertiesConfiguration config = SimulatorProperties.getInstance().getConfiguration();
		if (config.isEmpty()) {
			return;
		}

		int repeatTimes = config.getInt("client.test.repeat.time", 1);
		int requestSize = config.getInt("client.request.size", 1);
		int requestPeriod = config.getInt("client.request.period", 100);
		int sampleLength = config.getInt("client.sample.length", 100);

		RequestModeType requestMode = RequestModeType.idle;
		String requestModeStr = config.getString("client.request.mode");
		RequestModeType requestModeType = null;
		if (RequestModeType.checkType(requestModeStr)) {
			requestMode = RequestModeType.valueOf(requestModeStr);
		}
		log.info("client.test.repeat.time: {}, client.request.size: {}, client.request.period: {}, client.sample.length: {}, client.request.mode: {}",
				repeatTimes, requestSize, requestPeriod, sampleLength, requestMode);

		if (clientStartUp && requestMode != null) {
			switch (requestMode) {
				case urgent:
					ForkJoinPool executor = new ForkJoinPool(100);
					for (int i = 0; i < repeatTimes; i++) {
						// for test purpose. Simulate a situation when a great number of urgent requests are coming at time
						invokeByUrgentMode(executor, requestSize, sampleLength);
						ClientMeasurer.showStatistics();
						ClientMeasurer.resetStatistics();
						log.info("Netty client repeatedly executes a set of test cases. counter: {}", i + 1);
						TimeUnit.SECONDS.sleep(3);
					}
					break;

				case heavy:
					// for test purpose. Simulate a situation when many requests are coming continuously
					invokeByContinuousMode(sampleLength, 500, requestPeriod, TimeUnit.MILLISECONDS);
					break;

				case normal:
					// for develop purpose. Debug system and analysis Netty framework
					invokeByContinuousMode(sampleLength, 5, 30, TimeUnit.SECONDS);
					break;

				default:
			}
		}
	}

	private static void echo(int randomLength) {
		try {
			// Test Case
			String randomString = RandomStringUtils.random(randomLength, true, true);
			String feedback = BizServiceStub.getInstance().echo(randomString);
			log.debug("Test Case No.5 Passed!");

			String target = feedback.substring(0, randomString.length());
			if (target.equals(randomString)) {
				log.debug("Execute a transaction via Client Channel Manager.");
			} else {
				log.error("Fail to test all cases.");
			}
		} catch (Throwable e) {
			log.error("Fail to test all cases.", e);
		}
	}

	private static void invokeByCrowdedMode(int requestSize, final int randomLength) {
		List<Thread> threadPool = new ArrayList<>();

		for (int i = 0; i < requestSize; i++) {
			String threadName = String.format("Request-%04d", i);
			Thread senderThread = new Thread(new Runnable() {
				@Override
				public void run() {
					echo(randomLength);
				}
			}, threadName);
			threadPool.add(senderThread);
		}

		for (Thread thread : threadPool) {
			thread.start();
		}
	}

	private static void invokeByUrgentMode(ForkJoinPool executor, int requestSize, final int randomLength) {
		Callable<Void> callable = null;
		List<Callable<Void>> callables = new ArrayList<>();
		for (int i = 0; i < requestSize; i++) {
			callable = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					echo(randomLength);
					return null;
				}
			};
			callables.add(callable);
		}

		final CountDownLatch invokeAllLatch = new CountDownLatch(1);
		final List<java.util.concurrent.Future<Void>> futures = executor.invokeAll(callables);

		ScheduledExecutorService checkExecutor = Executors.newSingleThreadScheduledExecutor();
		checkExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				boolean allDone = true;
				for (java.util.concurrent.Future<Void> future : futures) {
					if (false == future.isDone()) {
						allDone = false;
						break;
					}
				}

				if (allDone) {
					invokeAllLatch.countDown();
				}
			}
		}, 100, 100, TimeUnit.MILLISECONDS);

		try {
			invokeAllLatch.await();

		} catch (InterruptedException e) {
			// nothing to do
		}
		checkExecutor.shutdown();
	}

	private static void invokeByContinuousMode(final int randomLength, long initialDelay, long period, TimeUnit unit) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(8);
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				echo(randomLength);
			}
		}, initialDelay, period, unit);

		ScheduledExecutorService dailyExecutor = Executors.newSingleThreadScheduledExecutor();
		dailyExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				ClientMeasurer.showStatistics();
				ServerMeasurer.showStatistics();
			}
		}, 24, 24, TimeUnit.HOURS);
	}
}
