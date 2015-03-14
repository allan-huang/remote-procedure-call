package tw.me.ychuang.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container that wraps a result object. It’s like one HTTP Response conceptually.
 * 
 * @author Y.C. Huang
 */
public class Response {
	private static final Logger log = LoggerFactory.getLogger(Response.class);

	/**
	 * A kind of constructor
	 * 
	 * @param id unique id
	 * @param result a result
	 */
	public Response(Long id, Result result, String resultClass) {
		super();
		this.id = id;
		this.result = result;
		this.resultClass = resultClass;
	}

	/**
	 * Response's id
	 */
	private final Long id;

	/**
	 * Getter method for field 'id'
	 * 
	 * @return response's unqiue id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * A result is wrapped in this response
	 */
	private final Result result;

	/**
	 * Getter method for field 'result'
	 * 
	 * @return a result
	 */
	public Result getResult() {
		return this.result;
	}

	/**
	 * A class name of result
	 */
	private final String resultClass;

	/**
	 * Getter method for field 'resultClass'
	 * 
	 * @return a class name of a result
	 */
	public String getResultClass() {
		return this.resultClass;
	}

	@Override
	public String toString() {
		String format = String.format("Response-%d", this.id);

		return format;
	}
}
