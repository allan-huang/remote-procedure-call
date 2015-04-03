package tw.me.ychuang.rpc.exception;

/**
 * An exception occurred in server-side.
 * 
 * @author Y.C. Huang
 */
public class ServerSideException extends RpcException {
	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 */
	public ServerSideException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 */
	public ServerSideException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the cause of this exception
	 */
	public ServerSideException(Throwable cause) {
		super(cause);
	}
}
