package net.viperfish.journal.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.framework.Journal;

public class JournalCellJPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9007986605167426162L;

	/**
	 * Create the panel.
	 */
	public JournalCellJPanel(Journal journal, boolean mouseOver,
			boolean selected) {
		// setBackground(UIManager.getColor("List.background"));
		if (selected) {
			setBackground(UIManager.getColor("List.selectionForeground"));
		}
		setLayout(new MigLayout("", "[50px][grow]", "[22px]"));

		JLabel lblTitle = new JLabel(journal.getSubject());
		lblTitle.setFont(GraphicalUserInterface.defaultDialogTitleFont
				.deriveFont(((float) 16)));
		add(lblTitle, "cell 0 0,alignx left,aligny center");

		JLabel lblDate = new JLabel(journal.getDate().toString());
		add(lblDate, "cell 1 0,alignx right,aligny center");

		// TODO
		if (mouseOver) {
			JLabel lblMore = new JLabel("--- more ---");
			add(lblMore, "cell 0 1");
		}

	}
}