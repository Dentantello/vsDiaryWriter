package net.viperfish.journal.operation;

import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public class DeleteEntryOperation implements Operation {
	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	public DeleteEntryOperation(Long i) {
		this.id = i;
		db = ComponentProvider.getEntryDatabase();
		indexer = ComponentProvider.getIndexer();
	}

	@Override
	public void execute() {
		db.removeEntry(id);
		indexer.delete(id);
	}

}
