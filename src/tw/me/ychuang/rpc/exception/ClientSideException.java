package tw.me.ychuang.rpc.exception;

/**
 * An exception occurred in client-side.
 * 
 * @author Y.C. Huang
 */
public class ClientSideException extends RpcException {
	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 */
	public ClientSideException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 */
	public ClientSideException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the cause of this exception
	 */
	public ClientSideException(Throwable cause) {
		super(cause);
	}
}
