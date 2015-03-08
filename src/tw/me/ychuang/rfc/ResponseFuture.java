package tw.me.ychuang.rfc;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps an asynchronous computation into a synchronous (blocking) computation.<br>
 * A future of response implements an asynchronous future pattern.
 * 
 * @author Y.C. Huang
 */
public class ResponseFuture<T> implements Future<T> {
	private static final Logger log = LoggerFactory.getLogger(ResponseFuture.class);

	/**
	 * A real response
	 */
	private volatile T response;

	/**
	 * True if this response future has been cancelled
	 */
	private volatile boolean cancelled = false;

	/**
	 * Blocks the current stub thread until getting a real response or a timeout
	 */
	private final CountDownLatch responseLatch;

	/**
	 * A pool retains all response futures are waiting to receive a real response returned from server
	 */
	private final ConcurrentMap<Long, ResponseFuture<T>> futurePool;

	/**
	 * A kind of constructor
	 * 
	 * @param id response's id
	 * @param futurePool response future pool
	 */
	public ResponseFuture(Long id, ConcurrentMap<Long, ResponseFuture<T>> futurePool) {
		super();
		this.id = id;
		this.responseLatch = new CountDownLatch(1);
		this.futurePool = futurePool;
	}

	/**
	 * Cancels this future and singals blocked stub thread
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (this.isDone()) {
			return false;

		} else {
			this.responseLatch.countDown();
			this.cancelled = true;
			this.futurePool.remove(this.getId());

			return false == this.isDone();
		}
	}

	/**
	 * Response's id
	 */
	private final Long id;

	/**
	 * Getter method for field 'id'
	 * 
	 * @return response's id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Whether this future is cancelled or not
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	/**
	 * Whether this future is done or not
	 */
	@Override
	public boolean isDone() {
		return this.responseLatch.getCount() == 0;
	}

	/**
	 * Blocks the current stub thread until getting a real response
	 */
	@Override
	public T get() throws InterruptedException {
		try {
			this.responseLatch.await();

		} catch (InterruptedException e) {
			this.futurePool.remove(this.getId());
			throw e;
		}

		return this.response;
	}

	/**
	 * Blocks the current stub thread until getting a real response or a timeout
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			this.responseLatch.await(timeout, unit);

		} catch (InterruptedException e) {
			this.futurePool.remove(this.getId());
			throw e;
		}

		return this.response;
	}

	/**
	 * Commits a response for signaling the blocking stub thread
	 * 
	 * @param response a real response
	 */
	public void commit(T response) {
		this.response = response;
		this.responseLatch.countDown();

		this.futurePool.remove(this.getId());
	}
}
