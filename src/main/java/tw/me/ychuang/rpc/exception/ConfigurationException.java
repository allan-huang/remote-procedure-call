package tw.me.ychuang.rpc.exception;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionContext;

/**
 * Exception thrown when failed to load a properties file.
 * 
 * @author Y.C. Huang
 */
public class ConfigurationException extends ContextedRuntimeException {
	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the cause of this exception
	 */
	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 * @param context a context stores the contextual information
	 */
	public ConfigurationException(String message, Throwable cause, ExceptionContext context) {
		super(message, cause, context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.lang3.exception.ContextedRuntimeException#addContextValue(java.lang.String, java.lang.Object)
	 */
	public ConfigurationException addContextValue(String label, Object value) {
		super.addContextValue(label, value);

		return this;
	}
}
