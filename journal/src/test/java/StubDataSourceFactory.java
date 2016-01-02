package test.java;

import java.io.File;

import net.viperfish.journal.dbProvider.DataSourceFactory;
import net.viperfish.journal.framework.EntryDatabase;

public class StubDataSourceFactory implements DataSourceFactory {

	private DatabaseStub db;

	public StubDataSourceFactory() {
		db = new DatabaseStub();
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return new DatabaseStub();
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataDirectory(File dir) {
		// TODO Auto-generated method stub

	}

	@Override
	public EntryDatabase getDatabaseObject() {
		return db;
	}

}
