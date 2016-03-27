package net.viperfish.journal.framework;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Concurrent operation executor with a thread pool
 * 
 * @author sdai
 *
 */
final class ThreadPoolOperationExecutor extends OperationExecutor {

	private ExecutorService pool;

	private class OperationRunner implements Runnable {

		private Operation o;

		OperationRunner(Operation toRun) {
			this.o = toRun;
		}

		@Override
		public void run() {
			try {
				o.execute();
			} catch (Throwable s) {
				ThreadPoolOperationExecutor.this.notifyObservers(s);
			}

		}

	}

	public ThreadPoolOperationExecutor() {
		pool = Executors.newSingleThreadExecutor();
	}

	@Override
	public void submit(Operation o) {
		pool.submit(new OperationRunner(o));
	}

	@Override
	public void terminate() {
		pool.shutdown();

	}

}
