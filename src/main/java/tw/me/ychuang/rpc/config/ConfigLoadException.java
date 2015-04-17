package tw.me.ychuang.rpc.config;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionContext;

@SuppressWarnings("serial")
public class ConfigLoadException extends ContextedRuntimeException {

	public ConfigLoadException() {
		super();
	}

	public ConfigLoadException(String message) {
		super(message);
	}

	public ConfigLoadException(Throwable cause) {
		super(cause);
	}

	public ConfigLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigLoadException(String message, Throwable cause, ExceptionContext context) {
		super(message, cause, context);
	}

	public ConfigLoadException addContextValue(String label, Object value) {
		super.addContextValue(label, value);

		return this;
	}
}
