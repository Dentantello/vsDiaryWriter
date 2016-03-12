package net.viperfish.journal.indexProvider;

import java.util.Collection;

import net.viperfish.journal.framework.ConfigPage;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Provider;
import net.viperfish.utils.index.Indexer;

/**
 * provides implementation of indexer this one provides an Apache Lucene indexer
 * 
 * @author sdai
 *
 */
public class ViperfishIndexerProvider implements Provider<Indexer<Journal>> {

	private JournalIndexer indexer;

	private Indexer<Journal> lazyLoadIndexer() {
		if (indexer == null) {
			indexer = new JournalIndexer();
		}
		return indexer;
	}

	public ViperfishIndexerProvider() {
		indexer = null;
	}

	@Override
	public Indexer<Journal> newInstance() {
		return new JournalIndexer();
	}

	@Override
	public Indexer<Journal> getInstance() {
		return lazyLoadIndexer();
	}

	@Override
	public Indexer<Journal> newInstance(String instance) {
		if (instance.equals("LuceneIndexer")) {
			return new JournalIndexer();
		}
		return null;
	}

	@Override
	public Indexer<Journal> getInstance(String instance) {
		if (instance.equals("LuceneIndexer")) {
			return lazyLoadIndexer();
		}
		return null;
	}

	@Override
	public String[] getSupported() {
		return new String[] { "LuceneIndexer" };
	}

	@Override
	public String getName() {
		return "viperfish";
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultInstance(String instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDefaultInstance() {
		return "LuceneIndexer";
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		indexer = null;

	}

	@Override
	public Collection<Class<? extends ConfigPage>> getConfigPages() {
		// TODO Auto-generated method stub
		return null;
	}

}
