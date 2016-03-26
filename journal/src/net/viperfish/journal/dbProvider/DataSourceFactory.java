package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.EntryDatabase;

/**
 * a factory that will return an instance of a EntryDatabase
 * 
 * @author sdai
 * @see EntryDatabase
 */
interface DataSourceFactory {

	/**
	 * get a dao object
	 * 
	 * @return a new or cached dao;
	 */
	public EntryDatabase getDatabaseObject();

	/**
	 * create a new dao object
	 * 
	 * @return the new dao
	 */
	public EntryDatabase createDatabaseObject();

	/**
	 * clean up
	 */
	public void cleanUp();

	/**
	 * set the data directory for the application
	 * 
	 * @param dir
	 *            the data directory
	 */
	public void setDataDirectory(File dir);

	/**
	 * refresh the EntryDatabase to reflect change in configuration or settings
	 * 
	 *
	 */
	public void refresh();
}
