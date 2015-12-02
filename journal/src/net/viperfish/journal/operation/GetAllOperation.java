package net.viperfish.journal.operation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.persistent.EntryDatabase;

public class GetAllOperation implements OperationWithResult<List<Journal>> {

	private EntryDatabase db;
	private List<Journal> result;
	private boolean done;

	public GetAllOperation() {
		db = JournalApplication.getDataSourceFactory().createDatabaseObject();
		result = new LinkedList<Journal>();
	}

	@Override
	public void execute() {
		try {
			result = db.getAll();
			Collections.sort(result);
		} finally {
			synchronized (this) {
				this.notifyAll();
				done = true;
			}
		}
	}

	@Override
	public synchronized boolean isDone() {
		return done;
	}

	@Override
	public synchronized List<Journal> getResult() {
		if (done) {
			return result;
		}
		while (true) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				return null;
			}
			if (done) {
				return result;
			}
		}
	}

}
