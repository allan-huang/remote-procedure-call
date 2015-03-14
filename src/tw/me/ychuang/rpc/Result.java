package tw.me.ychuang.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container that wraps a object that callee is invoked and returns.<br>
 * This object is a general object or an exception.
 * 
 * @author Y.C. Huang
 */
public class Result {
	private static final Logger log = LoggerFactory.getLogger(Result.class);

	/**
	 * A specific kind of return is Void
	 */
	public static final Result VOID_RETURN = new Result(null, Void.class);

	/**
	 * A kind of constructor
	 * 
	 * @param returnObj a return
	 * @param returnClass a class of a return
	 */
	public Result(Object returnObj, Class returnClass) {
		super();
		this.returnObj = returnObj;
		this.returnClass = returnClass;
		this.exceptional = Throwable.class.isAssignableFrom(returnClass);
	}

	private final Object returnObj;

	public Object getReturn() {
		return this.returnObj;
	}

	private final Class returnClass;

	public Class getReturnClass() {
		return this.returnClass;
	}

	private final boolean exceptional;

	public boolean isExceptional() {
		return this.exceptional;
	}
}
