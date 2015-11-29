package net.viperfish.journal.operation;

import java.util.HashSet;
import java.util.Set;

import net.viperfish.journal.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public class SearchEntryOperation implements OperationWithResult<Set<Journal>> {

	private String query;
	private Set<Journal> result;
	private boolean done;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	public SearchEntryOperation(String query) {
		this.query = query;
		result = new HashSet<Journal>();
		done = false;
		db = Configuration.getDataSourceFactory().createDatabaseObject();
		indexer = Configuration.getIndexerFactory().createIndexer();
	}

	@Override
	public void execute() {
		try {
			Iterable<Long> indexResult = indexer.search(query);
			for (Long id : indexResult) {
				Journal j = db.getEntry(id);
				if (j == null) {
					indexer.delete(id);
					continue;
				}
				result.add(j);
			}
		} finally {
			done = true;
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	@Override
	public synchronized boolean isDone() {
		return done;
	}

	@Override
	public synchronized Set<Journal> getResult() {
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
				break;
			}
		}
		return result;
	}

}
