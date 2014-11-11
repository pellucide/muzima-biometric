package com.neurotec.samples.biometrics.standards;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;
import com.neurotec.util.NVersion;

public final class VersionFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private boolean useSelectMode = true;
	private final NVersion[] versions;
	private VersionsTableModel tableModel;
	private MainFrameEventListener listener;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JButton btnOK;
	private JButton btnCancel;

	private JTable tableVersion;

	// ==============================================
	// Public constructor
	// ==============================================

	public VersionFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "Version", true);
		this.listener = listener;
		setPreferredSize(new Dimension(390, 240));
		setMinimumSize(new Dimension(200, 200));
		initializeComponents();

		versions = ANTemplate.getVersions();
		for (NVersion version : versions) {
			tableModel.addRow(new Object[] { version.toString(), ANTemplate.getVersionName(version), version });
		}
		tableVersion.updateUI();
		onUseSelectModeChanged();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		tableModel = new VersionsTableModel();
		tableVersion = new JTable(tableModel);
		tableVersion.getTableHeader().setReorderingAllowed(false);
		tableVersion.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableVersion.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedVersionChanged();
			}
		});

		tableVersion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (useSelectMode && getSelectedVersion().getValue() != 0) {
						buttonOKActionPerformed();
						dispose();
					}
				}
			}
		});

		tableVersion.getColumnModel().getColumn(0).setPreferredWidth(50);
		tableVersion.getColumnModel().getColumn(1).setPreferredWidth(250);

		TableColumn versionColumn = tableVersion.getColumnModel().getColumn(2);
		tableVersion.getColumnModel().removeColumn(versionColumn);

		JScrollPane tableScrollPane = new JScrollPane(tableVersion);

		mainPanel.add(new JLabel("Versions:"), BorderLayout.BEFORE_FIRST_LINE);
		mainPanel.add(tableScrollPane, BorderLayout.CENTER);
		mainPanel.add(createButtonPanel(), BorderLayout.AFTER_LAST_LINE);

		getContentPane().add(mainPanel);
		pack();

	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(75, 25));
		btnOK.addActionListener(this);

		btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(75, 25));
		btnCancel.addActionListener(this);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOK);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(btnCancel);
		buttonPanel.add(Box.createHorizontalStrut(10));
		return buttonPanel;
	}

	private void onUseSelectModeChanged() {
		btnOK.setVisible(useSelectMode);
		btnCancel.setText(useSelectMode ? "Cancel" : "Close");
		onSelectedVersionChanged();
	}

	private void onSelectedVersionChanged() {
		btnOK.setEnabled(useSelectMode && tableVersion.getSelectedRowCount() != 0);
	}

	private void buttonOKActionPerformed() {
		listener.versionChanged(getSelectedVersion());
	}

	// ==============================================
	// Public methods
	// ==============================================

	public boolean isUseSelectMode() {
		return useSelectMode;
	}

	public void setUseSelectMode(boolean value) {
		if (useSelectMode != value) {
			useSelectMode = value;
			onUseSelectModeChanged();
		}
	}

	public NVersion getSelectedVersion() {
		if (tableVersion.getSelectedRowCount() == 0) {
			return new NVersion((short) 0);
		}
		return (NVersion) tableVersion.getModel().getValueAt(tableVersion.getSelectedRows()[0], 2);
	}

	public void setSelectedVersion(NVersion value) {
		if (value.getValue() == 0) {
			tableVersion.clearSelection();
		} else {
			int index = Arrays.asList(versions).indexOf(value);
			tableVersion.setRowSelectionInterval(index, index);
		}
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOK) {
			if (useSelectMode) {
				buttonOKActionPerformed();
			}
			dispose();
		} else if (source == btnCancel) {
			dispose();
		}
	}

	// ==============================================
	// Private class extending DefaultTableModel
	// ==============================================

	private static final class VersionsTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;
		private String[] columnNames = { "Value", "Name", "Version" };

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int column) {
			try {
				return columnNames[column];
			} catch (Exception e) {
				e.printStackTrace();
				return super.getColumnName(column);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
