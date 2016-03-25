package net.viperfish.journal.secureProvider;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal.operation.OperationExecutors;
import net.viperfish.journal.operation.OperationFactories;

class BlockCipherMacPreference extends PreferencePage {

	private SecurityConfigComposite com;

	public BlockCipherMacPreference() {
		super("Block Cipher MAC Encryption");
		this.setDescription(
				"Configures the settings for Encryption using a block cipher, mac, and a compression algorithm");
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new SecurityConfigComposite(arg0, SWT.NONE);
		return com;
	}

	@Override
	protected void performDefaults() {
		com.defaultAll();
	}

	@Override
	public boolean performOk() {
		OperationExecutors.getExecutor()
				.submit(OperationFactories.getOperationFactory().getChangeConfigOperaion(com.save()));
		return com.validate();
	}

}
