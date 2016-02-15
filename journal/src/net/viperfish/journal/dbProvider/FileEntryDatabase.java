package net.viperfish.journal.dbProvider;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.json.JsonGenerator;
import net.viperfish.utils.file.IOFile;

/**
 * an EntryDatabase that is based on a file for persistent, does not flush until
 * flush is called
 * 
 * @author sdai
 *
 */
public class FileEntryDatabase implements EntryDatabase {

	private final IOFile file;
	private FileMemoryStructure struct;

	/**
	 * the memory structure of journals in a FileEntryDatabase
	 * 
	 * @see FileEntryDatabase
	 * @author sdai
	 *
	 */
	protected class FileMemoryStructure {
		private Long id;
		private Map<Long, Journal> data;

		public FileMemoryStructure() {
			id = new Long(0);
			data = new TreeMap<>();
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Map<Long, Journal> getData() {
			return data;
		}

		public void setData(Map<Long, Journal> data) {
			this.data = data;
		}

		public void incrementID() {
			id += 1;
		}

	}

	public FileEntryDatabase(IOFile ioFile) {
		this.file = ioFile;
		struct = new FileMemoryStructure();
	}

	@Override
	public Journal addEntry(Journal j) {
		j.setId(struct.getId());
		struct.getData().put(j.getId(), j);
		struct.incrementID();
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		Journal j = new Journal(struct.getData().get(id));
		struct.getData().remove(id);
		return j;
	}

	@Override
	public Journal getEntry(Long id) {
		return struct.getData().get(id);
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		removeEntry(id);
		j.setId(id);
		struct.getData().put(id, j);
		return j;
	}

	@Override
	public List<Journal> getAll() {
		List<Journal> result = new LinkedList<>();
		for (Entry<Long, Journal> i : struct.getData().entrySet()) {
			result.add(i.getValue());
		}
		return result;
	}

	@Override
	public void clear() {
		struct.getData().clear();
		struct.setId(new Long(0));
	}

	/**
	 * flush the data into the file in JSON format
	 */
	public synchronized void flush() {
		JsonGenerator generator = new JsonGenerator();
		try {
			String toWrite = generator.toJson(struct);
			file.write(toWrite, StandardCharsets.UTF_16);
		} catch (JsonGenerationException | JsonMappingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * load the data from the file in JSON format
	 */
	public synchronized void load() {
		String buf = file.read(StandardCharsets.UTF_16);
		if (buf.length() == 0) {
			return;
		}
		JsonGenerator generator = new JsonGenerator();
		try {
			struct = generator.fromJson(FileMemoryStructure.class, buf);
		} catch (JsonParseException | JsonMappingException e) {
			throw new RuntimeException(e);
		}
	}

}
