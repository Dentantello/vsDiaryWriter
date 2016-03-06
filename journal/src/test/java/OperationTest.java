package test.java;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.authProvider.HashAuthManager;
import net.viperfish.journal.framework.AuthManagers;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.indexProvider.JournalIndexer;
import net.viperfish.journal.operation.AddEntryOperation;
import net.viperfish.journal.operation.ChangePasswordOperation;
import net.viperfish.journal.operation.ClearEntriesOperation;
import net.viperfish.journal.operation.DeleteEntryOperation;
import net.viperfish.journal.operation.EditContentOperation;
import net.viperfish.journal.operation.EditSubjectOperation;
import net.viperfish.journal.operation.ExportJournalOperation;
import net.viperfish.journal.operation.GetAllOperation;
import net.viperfish.journal.operation.GetEntryOperation;
import net.viperfish.journal.operation.ImportEntriesOperation;
import net.viperfish.journal.operation.SearchEntryOperation;
import net.viperfish.journal.operation.SetPasswordOperation;
import net.viperfish.journal.secureProvider.BlockCipherMacTransformer;
import net.viperfish.utils.file.CommonFunctions;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;
import net.viperfish.utils.serialization.JsonGenerator;

public class OperationTest {

	private EntryDatabase db;
	private JournalIndexer indexer;
	private AuthenticationManager mger;
	private final ExecutorService threadpool;

	private void setupConfig() {
		Configuration.setProperty(ConfigMapping.PORTABLE, true);
		JournalApplication.initModules();
		JournalApplication.defaultProviders();
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, "AES");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, "CFB");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, "PKCS7PADDING");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_ALGORITHM, "MD5");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_TYPE, "HMAC");
		Configuration.setProperty(BlockCipherMacTransformer.KDF_HASH, "SHA256");
		Configuration.setProperty(ConfigMapping.AUTH_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.AUTH_COMPONENT, "Hash");
		Configuration.setProperty(ConfigMapping.DB_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.DB_COMPONENT, "MemoryHashMap");
		Configuration.setProperty(ConfigMapping.INDEX_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.INDEXER_COMPONENT, "LuceneIndexer");
		Configuration.setProperty(ConfigMapping.TRANSFORMER_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.TRANSFORMER_COMPONENT, "BlockCipherMAC");
		Configuration.setProperty(BlockCipherMacTransformer.COMPRESSION, "GZ");
		Configuration.setProperty(HashAuthManager.HASH_ALG, "SHA256");
	}

	public OperationTest() {
		setupConfig();
		initComponents();
		threadpool = Executors.newCachedThreadPool();
	}

	private void executeAsyncOperation(final Operation ops) {
		threadpool.execute(new Runnable() {

			@Override
			public void run() {
				ops.execute();

			}

		});
	}

	private boolean isSorted(List<Journal> result) {
		for (int i = 1; i < result.size(); ++i) {
			if (result.get(i).compareTo(result.get(i - 1)) < 0) {
				return false;
			}
		}
		return true;
	}

	@Test
	public void testAddEntryOperation() {
		cleanUp();
		Journal toAdd = new Journal();
		toAdd.setContent("test");
		toAdd.setDate(new Date());
		toAdd.setSubject("test");
		new AddEntryOperation(toAdd).execute();
		toAdd.setId(0L);
		Journal get = db.getEntry(0L);
		Assert.assertEquals("test", get.getSubject());
		Assert.assertEquals("test", get.getContent());
		Assert.assertEquals(toAdd.getDate().getTime(), get.getDate().getTime());
		Assert.assertEquals(toAdd.getId(), get.getId());
		Assert.assertEquals(true, indexer.contains(0L));
		cleanUp();
	}

	@Test
	public void testDeleteEntryOperation() {
		cleanUp();
		Journal toAdd = new Journal();
		toAdd.setContent("test");
		toAdd.setDate(new Date());
		toAdd.setSubject("test");
		Journal result = db.addEntry(toAdd);
		Long id = result.getId();
		toAdd.setId(id);
		result = db.getEntry(id);
		Assert.assertEquals(true, result.equals(toAdd));
		new DeleteEntryOperation(id).execute();
		result = db.getEntry(id);
		Assert.assertEquals(null, result);
		Assert.assertEquals(false, indexer.contains(id));
		cleanUp();
	}

	@Test
	public void testEditSubjectOperation() {
		cleanUp();
		Journal test = new Journal();
		test.setSubject("unedited");
		test.setDate(new Date());
		test.setContent("mary has a little lamb");
		Journal result = db.addEntry(test);
		Long id = result.getId();
		new EditSubjectOperation(id, "edited").execute();
		result = db.getEntry(id);
		Assert.assertEquals("edited", result.getSubject());
		cleanUp();
	}

	@Test
	public void testEditContentOperation() {
		cleanUp();
		Journal test = new Journal();
		test.setSubject("test");
		test.setDate(new Date());
		test.setContent("unedited");
		Journal result = db.addEntry(test);
		Long id = result.getId();
		new EditContentOperation(id, "edited").execute();
		result = db.getEntry(id);
		Assert.assertEquals("edited", result.getContent());
		cleanUp();
	}

	@Test
	public void testGetEntryOperation() {
		cleanUp();
		Journal test = new Journal();
		test.setContent("test");
		test.setDate(new Date());
		test.setSubject("test");
		Journal result = db.addEntry(test);
		test.setId(result.getId());
		OperationWithResult<Journal> o = new GetEntryOperation(test.getId());
		executeAsyncOperation(o);
		result = o.getResult();
		Assert.assertEquals(true, o.isDone());
		Assert.assertEquals(test.getId(), result.getId());
		Assert.assertEquals(test.getContent(), result.getContent());
		Assert.assertEquals(test.getSubject(), result.getSubject());
		Assert.assertEquals(test.getDate().getTime(), result.getDate().getTime());
		cleanUp();
	}

	@Test
	public void testSearchEntryOperation() {
		cleanUp();
		Journal t1 = new Journal();
		t1.setContent("test 1");
		t1.setDate(new Date());
		t1.setSubject("test 1");
		Journal t2 = new Journal();
		t2.setSubject("test 2");
		t2.setDate(new Date());
		t2.setContent("test 2");
		Journal t3 = new Journal();
		t3.setContent("random stuff");
		t3.setSubject("something");
		t3.setDate(new Date());
		t1 = db.addEntry(t1);
		t2 = db.addEntry(t2);
		t3 = db.addEntry(t3);
		indexer.add(t1);
		indexer.add(t2);
		indexer.add(t3);
		SearchEntryOperation ops = new SearchEntryOperation("test");
		executeAsyncOperation(ops);
		Set<Journal> s = ops.getResult();
		Assert.assertEquals(true, s.contains(t1));
		Assert.assertEquals(true, s.contains(t2));
		Assert.assertEquals(false, s.contains(t3));
		cleanUp();
	}

	@Test
	public void testGetAllOperation() {
		cleanUp();
		addEntries(100);
		GetAllOperation getAll = new GetAllOperation();
		executeAsyncOperation(getAll);
		List<Journal> result = getAll.getResult();
		Assert.assertEquals(true, isSorted(result));
		Assert.assertEquals(100, result.size());
		cleanUp();
	}

	@Test
	public void testClearOperation() {
		cleanUp();
		List<Long> ids = addEntries(10);
		ClearEntriesOperation c = new ClearEntriesOperation();
		c.execute();
		Assert.assertEquals(0, db.getAll().size());
		for (Long i : ids) {
			Assert.assertEquals(false, indexer.contains(i));
		}
		cleanUp();
	}

	@Test
	public void testExportOperation() {
		cleanUp();
		addEntries(20, "toExport");
		List<Journal> all = db.getAll();
		for (Journal i : all) {
			i.setId(null);
		}
		try {
			String expectedOutput = new JsonGenerator().toJson(all.toArray(new Journal[1]));
			new ExportJournalOperation("test.txt").execute();
			IOFile exportFile = new IOFile(new File("test.txt"), new TextIOStreamHandler());
			String actualOutput = exportFile.read(StandardCharsets.UTF_16);
			Assert.assertEquals(expectedOutput, actualOutput);
		} catch (JsonGenerationException | JsonMappingException e) {
			throw new RuntimeException(e);
		}
		cleanUp();
	}

	@Test
	public void testImportOperation() {
		cleanUp();
		addEntries(20, "testImport");
		exportJournals(db.getAll());
		cleanUp();
		new ImportEntriesOperation("test.txt").execute();
		List<Journal> imported = db.getAll();
		int count = 0;
		for (Journal i : imported) {
			Assert.assertEquals("testImport", i.getSubject());
			Assert.assertEquals("testImport", i.getContent());
			++count;
		}
		Assert.assertEquals(20, count);
	}

	@Test
	public void testChangePassword() {
		cleanUp();
		addEntries(20, "testPassword");
		new ChangePasswordOperation("newPass").execute();
		Assert.assertEquals("newPass", mger.getPassword());
		for (Journal i : db.getAll()) {
			Assert.assertEquals("testPassword", i.getSubject());
			Assert.assertEquals("testPassword", i.getContent());
		}
		cleanUp();
	}

	@Test
	public void testSetPassword() {
		cleanUp();
		new SetPasswordOperation("test-set").execute();
		Assert.assertEquals("test-set", mger.getPassword());
		cleanUp();
	}

	@AfterClass
	public static void cleanUpDirectory() {
		JournalApplication.revert();
		File testTxt = new File("test.txt");
		if (testTxt.exists()) {
			CommonFunctions.delete(testTxt);
		}
	}

	public void cleanUp() {
		mger.clear();
		db.clear();
		indexer.clear();
		mger.setPassword("test");
	}

	private void exportJournals(List<Journal> src) {
		try {
			String exported = new JsonGenerator().toJson(src);
			IOFile exportFile = new IOFile(new File("test.txt"), new TextIOStreamHandler());
			exportFile.write(exported, StandardCharsets.UTF_16);
		} catch (JsonGenerationException | JsonMappingException e) {
			throw new RuntimeException(e);
		}

	}

	private List<Long> addEntries(int howMany) {
		return addEntries(howMany, "test");
	}

	private List<Long> addEntries(int howMany, String allContent) {
		List<Long> resultList = new LinkedList<>();
		for (int i = 0; i < howMany; ++i) {
			Journal j = new Journal();
			j.setContent(allContent);
			j.setSubject(allContent);
			Journal result = db.addEntry(j);
			resultList.add(result.getId());
		}
		return resultList;
	}

	private void initComponents() {
		mger = AuthManagers.INSTANCE.getAuthManager();
		mger.setPassword("test");
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = (JournalIndexer) Indexers.INSTANCE.getIndexer();
	}
}
