package net.viperfish.journal.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationExecutor;

public class ThreadPoolOperationExecutor implements OperationExecutor {

	private ExecutorService pool;
	private List<Throwable> errors;
	private ExceptionHandler e;

	private class ExceptionHandler implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			errors.add(e);

		}

	}

	public ThreadPoolOperationExecutor() {
		errors = new LinkedList<Throwable>();
		errors = java.util.Collections.synchronizedList(errors);
		e = new ExceptionHandler();
		pool = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(e);
				return t;
			}
		});
	}

	@Override
	public void submit(final Operation o) {
		pool.submit(new Runnable() {

			@Override
			public void run() {
				o.execute();

			}
		});

	}

	@Override
	public boolean hasException() {
		return !errors.isEmpty();
	}

	@Override
	public Throwable getNextError() {
		return errors.get(0);
	}

	@Override
	public void terminate() {
		pool.shutdown();

	}

}
