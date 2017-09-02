package net.viperfish.journal2.transaction;

import java.io.IOException;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.TransactionWithResult;

final class GetJournalTransaction extends TransactionWithResult<Journal> {

	private JournalEncryptor enc;
	private CrudRepository<Journal, Long> db;
	private Long id;

	public GetJournalTransaction(Long id, CrudRepository<Journal, Long> db, JournalEncryptor enc) {
		this.id = id;
		this.db = db;
		this.enc = enc;
	}

	@Override
	public void execute() throws IOException {
		Journal result = db.findOne(id);
		result = enc.decryptJournal(result);
		this.setResult(result);
	}

}
