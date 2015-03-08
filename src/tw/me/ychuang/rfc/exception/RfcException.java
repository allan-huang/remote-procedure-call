package tw.me.ychuang.rfc.exception;

import org.apache.commons.lang3.exception.ContextedException;

/**
 * An exception occurred in RFC system.
 * 
 * @author Y.C. Huang
 */
public class RfcException extends ContextedException {
	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 */
	public RfcException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 */
	public RfcException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the cause of this exception
	 */
	public RfcException(Throwable cause) {
		super(cause);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.lang3.exception.ContextedException#addContextValue(java.lang.String, java.lang.Object)
	 */
	public RfcException addContextValue(String label, Object value) {
		super.addContextValue(label, value);

		return this;
	}
}
