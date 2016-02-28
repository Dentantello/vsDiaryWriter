package net.viperfish.journal.swtGui.conf;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal.framework.AuthManagers;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.JournalTransformers;
import net.viperfish.journal.framework.Provider;
import net.viperfish.utils.index.Indexer;

public class SystemSetupComposite extends Composite {

	private Combo dataStorageSelector;
	private Combo indexerSelector;
	private Combo authSelector;
	private Combo transformerSelector;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SystemSetupComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(4, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label dataLabel = new Label(this, SWT.NONE);
		dataLabel.setText("Data Storage");
		new Label(this, SWT.NONE);

		dataStorageSelector = new Combo(this, SWT.READ_ONLY);
		dataStorageSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		new Label(this, SWT.NONE);

		Label indexerLabel = new Label(this, SWT.NONE);
		indexerLabel.setText("Indexer");
		new Label(this, SWT.NONE);

		indexerSelector = new Combo(this, SWT.READ_ONLY);
		indexerSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label authManagerLabel = new Label(this, SWT.NONE);
		authManagerLabel.setText("Authentication");
		new Label(this, SWT.NONE);

		authSelector = new Combo(this, SWT.READ_ONLY);
		authSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label transformerLabel = new Label(this, SWT.NONE);
		transformerLabel.setText("Encryption");
		new Label(this, SWT.NONE);

		transformerSelector = new Combo(this, SWT.READ_ONLY);
		transformerSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fillIndexerSelector();
		fillInTransformer();
		fillInAuth();
		fillDataStorageSelector();

		dataStorageSelector.setText("H2Database");
		indexerSelector.setText("LuceneIndexer");
		authSelector.setText("HashAuthentication");
		transformerSelector.setText("BlockCipherMAC");

	}

	private void fillDataStorageSelector() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<EntryDatabase>> i : EntryDatabases.INSTANCE.getDatabaseProviders().entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			dataStorageSelector.add(i);
		}
		return;
	}

	private void fillIndexerSelector() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<Indexer<Journal>>> i : Indexers.INSTANCE.getIndexerProviders().entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			indexerSelector.add(i);
		}
	}

	private void fillInAuth() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<AuthenticationManager>> i : AuthManagers.INSTANCE.getAuthProviders().entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			authSelector.add(i);
		}
	}

	private void fillInTransformer() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<JournalTransformer>> i : JournalTransformers.INSTANCE.getSecureProviders()
				.entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			transformerSelector.add(i);
		}
	}

	public void save() {
		Configuration.setProperty(ConfigMapping.DB_COMPONENT, dataStorageSelector.getText());
		Configuration.setProperty(ConfigMapping.INDEXER_COMPONENT, indexerSelector.getText());
		Configuration.setProperty(ConfigMapping.AUTH_COMPONENT, authSelector.getText());
		Configuration.setProperty(ConfigMapping.TRANSFORMER_COMPONENT, transformerSelector.getText());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
