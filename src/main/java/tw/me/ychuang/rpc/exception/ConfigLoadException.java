package tw.me.ychuang.rpc.exception;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionContext;

/**
 * Exception thrown when failed to load a properties file.
 * 
 * @author Y.C. Huang
 */
public class ConfigLoadException extends ContextedRuntimeException {
	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 */
	public ConfigLoadException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause the cause of this exception
	 */
	public ConfigLoadException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 */
	public ConfigLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message the description of this exception
	 * @param cause the cause of this exception
	 * @param context a context stores the contextual information
	 */
	public ConfigLoadException(String message, Throwable cause, ExceptionContext context) {
		super(message, cause, context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.lang3.exception.ContextedRuntimeException#addContextValue(java.lang.String, java.lang.Object)
	 */
	public ConfigLoadException addContextValue(String label, Object value) {
		super.addContextValue(label, value);

		return this;
	}
}
