package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.framework.file.IOFile;
import net.viperfish.framework.file.TextIOStreamHandler;

/**
 * creates Text File based EntryDatabase
 * 
 * @author sdai
 *
 */
final class TextFileEntryDatabaseFactory extends FileEntryDatabaseFactory {

	@Override
	protected IOFile createIOFile(File dataFile) {
		return new IOFile(dataFile, new TextIOStreamHandler());
	}

}
