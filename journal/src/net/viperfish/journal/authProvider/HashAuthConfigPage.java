package net.viperfish.journal.authProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.viperfish.journal.swtGui.conf.ConfigPage;

public class HashAuthConfigPage implements ConfigPage {

	private Composite p;
	private HashAuthConfigComposite com;

	@Override
	public String getName() {
		return "Hash Authentication";
	}

	@Override
	public Composite getDisplay() {
		if (com == null) {
			com = new HashAuthConfigComposite(p, SWT.NONE);
		}
		return com;
	}

	@Override
	public void done() {
		com.save();

	}

	@Override
	public void setParent(Composite p) {
		this.p = p;
	}

}
