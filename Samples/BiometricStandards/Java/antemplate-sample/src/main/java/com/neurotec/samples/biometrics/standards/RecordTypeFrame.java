package com.neurotec.samples.biometrics.standards;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

import com.neurotec.biometrics.standards.ANRecordType;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;
import com.neurotec.util.NVersion;

public final class RecordTypeFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private NVersion version = new NVersion((short) 0);
	private boolean useSelectMode = true;
	private RecordTypesTableModel tableModel;
	private MainFrameEventListener listener;
	private Frame owner;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JButton btnShowFields;
	private JTable tableRecordType;

	private JButton btnOK;
	private JButton btnCancel;

	private TableColumn dataTypeColumn;
	private TableColumn versionColumn;

	// ==============================================
	// Public constructor
	// ==============================================

	public RecordTypeFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "Record Type", true);
		this.owner = owner;
		this.listener = listener;
		setPreferredSize(new Dimension(550, 440));
		setMinimumSize(new Dimension(300, 200));
		initializeComponents();
		onUseSelectModeChanged();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		tableModel = new RecordTypesTableModel();
		tableRecordType = new JTable(tableModel);
		tableRecordType.getTableHeader().setReorderingAllowed(false);
		tableRecordType.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedRecordTypeChanged();
			}
		});

		tableRecordType.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (getRecordType() != null) {
						if (useSelectMode) {
							btnOKActionPerformed();
							RecordTypeFrame.this.dispose();
						} else {
							showFields();
						}
					}
				}
			}
		});

		tableRecordType.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		tableRecordType.getColumnModel().getColumn(0).setPreferredWidth(60);
		tableRecordType.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableRecordType.getColumnModel().getColumn(2).setPreferredWidth(90);
		tableRecordType.getColumnModel().getColumn(3).setPreferredWidth(60);

		TableColumn recordTypeColumn = tableRecordType.getColumnModel().getColumn(4);
		tableRecordType.getColumnModel().removeColumn(recordTypeColumn);

		dataTypeColumn = tableRecordType.getColumnModel().getColumn(2);
		versionColumn = tableRecordType.getColumnModel().getColumn(3);

		JScrollPane scrollTable = new JScrollPane(tableRecordType);

		mainPanel.add(new JLabel("Record type:"), BorderLayout.BEFORE_FIRST_LINE);
		mainPanel.add(scrollTable, BorderLayout.CENTER);
		mainPanel.add(createButtonPanel(), BorderLayout.AFTER_LAST_LINE);

		getContentPane().add(mainPanel);
		pack();
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnShowFields = new JButton("Show fields");
		btnShowFields.addActionListener(this);
		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		buttonPanel.add(btnShowFields);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOK);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(btnCancel);
		return buttonPanel;
	}

	private void updateRecords() {
		tableModel.clearAllData();
		for (ANRecordType recordType : ANRecordType.getTypes()) {
			NVersion recordVersion = recordType.getVersion();
			if (useSelectMode && (recordType.getNumber() == 1 || recordVersion.getValue() < version.getValue())) {
				continue;
			}

			int number = recordType.getNumber();
			String dataType = "";
			String versionString = "";
			if (!useSelectMode) {
				dataType = recordType.getDataType().toString();
				versionString = recordVersion.toString();
			}
			tableModel.addRow(new Object[] { number, recordType.getName(), dataType, versionString, recordType });
		}
		tableRecordType.updateUI();
	}

	private void onVersionChanged() {
		updateRecords();
	}

	private void onUseSelectModeChanged() {
		updateRecords();
		if (useSelectMode) {
			tableRecordType.getColumnModel().removeColumn(dataTypeColumn);
			tableRecordType.getColumnModel().removeColumn(versionColumn);
		} else {
			tableRecordType.getColumnModel().addColumn(dataTypeColumn);
			tableRecordType.getColumnModel().addColumn(versionColumn);
		}

		setPreferredSize(new Dimension(useSelectMode ? 380 : 530, getPreferredSize().height));
		btnShowFields.setVisible(!useSelectMode);
		btnOK.setVisible(useSelectMode);
		btnCancel.setText(useSelectMode ? "Cancel" : "Close");
		onSelectedRecordTypeChanged();
	}

	private void onSelectedRecordTypeChanged() {
		ANRecordType selectedRecordType = getRecordType();
		btnShowFields.setEnabled(!useSelectMode && selectedRecordType != null);
		btnOK.setEnabled(useSelectMode && selectedRecordType != null);
	}

	private void showFields() {
		FieldNumberFrame form = new FieldNumberFrame(owner, listener);
		form.setTitle("Fields");
		form.setUseSelectMode(false);
		form.setRecordType(getRecordType());
		form.showFieldNumberDialog(false);
	}

	private void btnOKActionPerformed() {
		if (useSelectMode) {
			listener.recordTypeSelected(getRecordType());
		}
	}

	// ==============================================
	// Public methods
	// ==============================================

	public NVersion getVersion() {
		return version;
	}

	public void setVersion(NVersion value) {
		if (version != value) {
			version = value;
			onVersionChanged();
		}
	}

	public boolean isUseSelectMode() {
		return useSelectMode;
	}

	public void setUseSelectMode(boolean value) {
		if (useSelectMode != value) {
			useSelectMode = value;
			onUseSelectModeChanged();
		}
	}

	public ANRecordType getRecordType() {
		if (tableRecordType.getSelectedRowCount() == 0) {
			return null;
		}
		return (ANRecordType) tableRecordType.getModel().getValueAt(tableRecordType.getSelectedRows()[0], 4);
	}

	public void setRecordType(ANRecordType value) {
		if (value == null) {
			tableRecordType.clearSelection();
		} else {
			int index = ANRecordType.getTypes().indexOf(value);
			tableRecordType.setRowSelectionInterval(index, index);
		}
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnShowFields) {
			showFields();
		} else if (source == btnOK) {
			btnOKActionPerformed();
			dispose();
		} else if (source == btnCancel) {
			dispose();
		}

	}

	// ==============================================
	// Private class extending DefaultTableModel
	// ==============================================

	private static final class RecordTypesTableModel extends DefaultTableModel {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Private fields
		// ==============================================

		private String[] columnNames = { "Number", "Name", "Data type", "Version", "RecordType" };

		// ==============================================
		// Private methods
		// ==============================================

		private void clearAllData() {
			if (getRowCount() > 0) {
				for (int i = getRowCount() - 1; i > -1; i--) {
					removeRow(i);
				}
			}
		}

		// ==============================================
		// Overridden methods
		// ==============================================

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public String getColumnName(int column) {
			try {
				return columnNames[column];
			} catch (Exception e) {
				return super.getColumnName(column);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

	}

}
