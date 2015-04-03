package tw.me.ychuang.rpc.exception;

import org.apache.commons.lang3.exception.ContextedException;

/**
 * An exception occurred in RPC system.
 * 
 * @author Y.C. Huang
 */
public class RpcException extends ContextedException {
	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 */
	public RpcException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 */
	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the cause of this exception
	 */
	public RpcException(Throwable cause) {
		super(cause);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.lang3.exception.ContextedException#addContextValue(java.lang.String, java.lang.Object)
	 */
	public RpcException addContextValue(String label, Object value) {
		super.addContextValue(label, value);

		return this;
	}
}
