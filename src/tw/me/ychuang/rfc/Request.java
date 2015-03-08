package tw.me.ychuang.rfc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container that wraps a command. It’s like one HTTP Request conceptually.
 * 
 * @author Y.C. Huang
 */
public class Request {
	private static final Logger log = LoggerFactory.getLogger(Request.class);

	/**
	 * A specific kind of request is sent to the remote server if it idles too long.
	 */
	public static final Request HEARTBEAT = new Request(0L, null);

	/**
	 * A kind of constructor
	 * 
	 * @param id unique id
	 * @param command a command
	 */
	public Request(Long id, Command command) {
		super();
		this.id = id;
		this.command = command;
	}

	/**
	 * Request's id
	 */
	private final Long id;

	/**
	 * Getter method for field 'id'
	 * 
	 * @return request's unqiue id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * A command is wrapped in this request
	 */
	private final Command command;

	/**
	 * Getter method for field 'command'
	 * 
	 * @return a command
	 */
	public Command getCommand() {
		return this.command;
	}

	/**
	 * A convenience method
	 * 
	 * @return this request whether a heartbeat message or not
	 */
	public boolean isHeartbeat() {
		return this.id == 0;
	}

	@Override
	public String toString() {
		String format = String.format("Request-%d", this.id);

		return format;
	}
}
