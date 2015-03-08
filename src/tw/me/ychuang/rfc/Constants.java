package tw.me.ychuang.rfc;

import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * System constants
 * 
 * @author Y.C. Huang
 */
public final class Constants {

	public static int DEFAULT_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;

	public static int RECONNECT_DELAY = 15;

	public static int MAX_RETRY_TIMES = 3;

	public static int CANCEL_WAITING_REQUEST_DELAY = 7;

	public static int CONNECT_TIMEOUT = 15000;

	public static int HEARTBEAT_PERIOD = 60;

	public static String ISO_8601_PATTERN = "yyyy-MM-dd'T'hh:mm:ssZ";

	public static String REQUEST_BOUNDARY = new String(new byte[] { 0 }, CharsetUtil.UTF_8);

	/**
	 * Channel Selection Type<br>
	 * <ul>
	 * <li>Round-Robin</li>
	 * <li>Workload</li>
	 * </ul>
	 */
	public enum ChannelSelectionType {
		round_robin, workload;

		public static List<String> asLabels() {
			List<String> labels = new ArrayList<String>();
			for (ChannelSelectionType type : ChannelSelectionType.values()) {
				labels.add(type.toString());
			}
			return labels;
		}
	}

	/**
	 * The caller should be prevented from constructing objects of this class, by declaring this private constructor.
	 */
	private Constants() {
		// this prevents even the native class from
		// calling this ctor as well :
		throw new AssertionError();
	}
}
