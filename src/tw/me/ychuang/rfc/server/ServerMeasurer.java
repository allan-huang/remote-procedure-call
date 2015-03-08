package tw.me.ychuang.rfc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performance measurement tool in server-side.
 * 
 * @author Y.C. Huang
 */
public class ServerMeasurer {
	private static final Logger log = LoggerFactory.getLogger(ServerMeasurer.class);

	private static long execCounter;

	private static long totalExecTime;

	private static long maxExecTime;

	private static long minExecTime;

	private static long avgExecTime() {
		long avgExecTime = 0;
		if (execCounter > 0) {
			avgExecTime = totalExecTime / execCounter;
		}
		return avgExecTime;
	}

	private static double toMilliseconds(long nanoseconds) {
		return nanoseconds / 1000000.0;
	}

	public static synchronized void measure(long startTime) {
		long estimatedTime = System.nanoTime() - startTime;
		totalExecTime += estimatedTime;

		if (maxExecTime == 0 && minExecTime == 0) {
			maxExecTime = estimatedTime;
			minExecTime = estimatedTime;
		} else {
			// one should use t1 - t0 < 0, not t1 < t0, because of the possibility of numerical overflow.
			if (estimatedTime - maxExecTime > 0) {
				maxExecTime = estimatedTime;
			} else if (estimatedTime - minExecTime < 0) {
				minExecTime = estimatedTime;
			}
		}
		++execCounter;
	}

	public static void showStatistics() {
		if (execCounter <= 0) {
			return;
		}

		long avgExecTime = avgExecTime();
		log.info("Server-side measurement in millisecond, total exec count: {}, avg exec time: {}, max exec time: {}, min exec time: {}", execCounter,
				toMilliseconds(avgExecTime), toMilliseconds(maxExecTime), toMilliseconds(minExecTime));
	}

	public static void resetStatistics() {
		execCounter = 0;
		totalExecTime = 0;
		maxExecTime = 0;
		minExecTime = 0;
	}
}
