package test.java;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class StubDataSourceFactory implements DataSourceFactory {

	private DatabaseStub db;

	public StubDataSourceFactory() {
		db = new DatabaseStub();
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return db;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

}
