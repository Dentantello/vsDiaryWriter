package net.viperfish.journal.secureProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.Digesters;

public class SecurityConfigComposite extends Composite {

	private Combo encAlgSelector;
	private Combo encModeSelector;
	private Combo encPadSelector;
	private Combo macTypeSelector;
	private Combo macAlgSelector;
	private Label kdfLabel;
	private Combo kdfCombo;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SecurityConfigComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label encryptionAlgLabel = new Label(this, SWT.NONE);
		encryptionAlgLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encryptionAlgLabel.setText("Encryption Algorithm");

		encAlgSelector = new Combo(this, SWT.READ_ONLY);
		encAlgSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label encModeLabel = new Label(this, SWT.NONE);
		encModeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encModeLabel.setText("Encryption Mode");

		encModeSelector = new Combo(this, SWT.READ_ONLY);
		encModeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label encPadLabel = new Label(this, SWT.NONE);
		encPadLabel.setText("Encryption Padding");

		encPadSelector = new Combo(this, SWT.READ_ONLY);
		encPadSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label macTypeLabel = new Label(this, SWT.NONE);
		macTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macTypeLabel.setText("Mac Type");

		macTypeSelector = new Combo(this, SWT.READ_ONLY);
		macTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macTypeSelector.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillInMacAlg();
			}

		});

		Label macAlgLabel = new Label(this, SWT.NONE);
		macAlgLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macAlgLabel.setText("Mac Algorithm");

		macAlgSelector = new Combo(this, SWT.READ_ONLY);
		macAlgSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		kdfLabel = new Label(this, SWT.NONE);
		kdfLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		kdfLabel.setText("KDF Hash");

		kdfCombo = new Combo(this, SWT.READ_ONLY);
		kdfCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fillIn();

	}

	public void save() {
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, encAlgSelector.getText());
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, encModeSelector.getText());
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, encPadSelector.getText());
		Configuration.setProperty(BlockCipherMacTransformer.MAC_TYPE, macTypeSelector.getText());
		Configuration.setProperty(BlockCipherMacTransformer.MAC_ALGORITHM, macAlgSelector.getText());
		Configuration.setProperty(BlockCipherMacTransformer.KDF_HASH, kdfCombo.getText());

	}

	private void fillIn() {
		for (String i : BlockCiphers.getSupportedBlockCipher()) {
			encAlgSelector.add(i);
		}
		for (String i : BlockCiphers.getSupportedBlockCipherMode()) {
			encModeSelector.add(i);
		}
		for (String i : BlockCiphers.getSupportedBlockCipherPadding()) {
			encPadSelector.add(i);
		}
		for (String i : Digesters.getSupportedDigest()) {
			kdfCombo.add(i);
		}
		macTypeSelector.add("CMAC");
		macTypeSelector.add("GMAC");
		macTypeSelector.add("CBCMAC");
		macTypeSelector.add("CFBMAC");
		macTypeSelector.add("HMAC");
		macTypeSelector.setText("HMAC");
		encPadSelector.setText("PKCS7Padding");
		encModeSelector.setText("CFB");
		encAlgSelector.setText("AES");
		kdfCombo.setText("SHA256");
		fillInMacAlg();
	}

	private void fillInMacAlg() {
		macAlgSelector.setItems(new String[0]);
		if (macTypeSelector.getText().equals("CMAC") || macTypeSelector.getText().equals("CBCMAC")
				|| macTypeSelector.getText().equals("CFBMAC")) {
			for (String i : BlockCiphers.getSupportedBlockCipher()) {
				macAlgSelector.add(i);
			}
		} else if (macTypeSelector.getText().equals("GMAC")) {
			for (String i : BlockCiphers.getGmacAlgorithms()) {
				macAlgSelector.add(i);
			}
		} else {
			for (String i : Digesters.getSupportedDigest()) {
				macAlgSelector.add(i);
			}
		}
		macAlgSelector.setText("SHA256");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
