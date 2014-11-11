package com.neurotec.samples.biometrics.standards;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANRecordDataType;
import com.neurotec.biometrics.standards.ANRecordType;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANType1Record;
import com.neurotec.biometrics.standards.ANValidationLevel;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;
import com.neurotec.util.NRange;
import com.neurotec.util.NVersion;

public final class FieldNumberFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public static methods
	// ==============================================

	public static boolean isFieldStandard(ANRecordType recordType, NVersion version, int fieldNumber, ANValidationLevel validationLevel) {
		if (fieldNumber == ANRecord.FIELD_LEN) {
			return true;
		}
		if (recordType == ANRecordType.getType1() && fieldNumber == ANType1Record.FIELD_VER) {
			return true;
		}
		if (recordType != ANRecordType.getType1() && fieldNumber == ANRecord.FIELD_IDC) {
			return true;
		}
		if (recordType.getDataType() != ANRecordDataType.ASCII && fieldNumber == ANRecord.FIELD_DATA) {
			return true;
		}
		if (validationLevel != ANValidationLevel.MINIMAL) {
			return recordType.isFieldStandard(version, fieldNumber);
		}
		if (recordType == ANRecordType.getType1()) {
			if (fieldNumber == ANType1Record.FIELD_CNT) {
				return true;
			}
		}
		return false;
	}

	// ==============================================
	// Private fields
	// ==============================================

	private NVersion version = new NVersion((short) 0);
	private ANRecordType recordType;
	private boolean useSelectMode = true;
	private ANValidationLevel validationLevel = ANValidationLevel.STANDARD;
	private boolean useUserDefinedFieldNumber = true;
	private int maxFieldNumber;
	private int[] standardFieldNumbers;
	private NRange[] userDefinedFieldNumbers;
	private StandardFieldsTableModel tableModel;
	private MainFrameEventListener listener;
	private boolean isFromMain;
	private GridBagConstraints c;
	private Frame owner;
	private TableColumn versionColumn;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JLabel lblFieldNumbers;
	private JLabel lblStandard;
	private JLabel lblUserDefined;
	private JRadioButton radioUserDefined;
	private JRadioButton radioStandard;
	private JTextField txtUserDefinedField;
	private JButton btnOK;
	private JButton btnCancel;
	private JTable tableStandard;

	// ==============================================
	// Public constructor
	// ==============================================

	public FieldNumberFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, "Field Number", true);
		this.owner = owner;
		this.listener = listener;
		setPreferredSize(new Dimension(500, 360));
		setMinimumSize(new Dimension(300, 300));
		initializeComponents();
		onUseSelectModeChanged();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		GridBagLayout mainPanelLayout = new GridBagLayout();
		mainPanelLayout.columnWidths = new int[] { 110, 270 };
		mainPanelLayout.rowHeights = new int[] { 25, 190, 25, 25 };
		mainPanel.setLayout(mainPanelLayout);

		radioStandard = new JRadioButton("Standard field:");
		radioStandard.addActionListener(this);

		lblStandard = new JLabel("Standard field:");

		radioUserDefined = new JRadioButton("User defined field:");
		radioUserDefined.addActionListener(this);

		lblUserDefined = new JLabel("User defined field:");
		lblFieldNumbers = new JLabel("(0-0)");
		txtUserDefinedField = new JTextField();

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

		addToGridBagLayout(0, 0, 2, 1, mainPanel, radioStandard);
		addToGridBagLayout(0, 0, mainPanel, lblStandard);
		addToGridBagLayout(0, 1, 2, 1, 1, 1, mainPanel, createTable());
		addToGridBagLayout(0, 2, 1, 1, 0, 0, mainPanel, radioUserDefined);
		addToGridBagLayout(0, 2, mainPanel, lblUserDefined);
		addToGridBagLayout(1, 2, 1, 1, mainPanel, lblFieldNumbers);
		addToGridBagLayout(0, 3, 1, 1, mainPanel, txtUserDefinedField);

		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(createButtonPanel(), BorderLayout.AFTER_LAST_LINE);
		pack();

	}

	private void addToGridBagLayout(int x, int y, JPanel parent, JComponent component) {
		c.gridx = x;
		c.gridy = y;
		parent.add(component, c);
	}

	private void addToGridBagLayout(int x, int y, int gridWidth, int geidHeight, JPanel parent, JComponent component) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = gridWidth;
		c.gridheight = geidHeight;
		parent.add(component, c);
	}

	private void addToGridBagLayout(int x, int y, int gridWidth, int geidHeight, double weightX, double weightY, JPanel parent, JComponent component) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = gridWidth;
		c.gridheight = geidHeight;
		c.weightx = weightX;
		c.weighty = weightY;
		parent.add(component, c);
	}

	private JScrollPane createTable() {
		tableModel = new StandardFieldsTableModel();
		tableStandard = new JTable(tableModel);
		tableStandard.getTableHeader().setReorderingAllowed(false);
		tableStandard.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableStandard.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedFieldNumberChanged();

			}
		});

		tableStandard.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (useSelectMode && getFieldNumber() != -1) {
						buttonOKActionPerformed();
					}
				}
			}
		});

		tableStandard.getColumnModel().getColumn(0).setPreferredWidth(70);
		tableStandard.getColumnModel().getColumn(0).setMaxWidth(70);
		tableStandard.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableStandard.getColumnModel().getColumn(1).setMaxWidth(290);
		tableStandard.getColumnModel().getColumn(2).setPreferredWidth(70);
		tableStandard.getColumnModel().getColumn(2).setMaxWidth(70);
		tableStandard.getColumnModel().getColumn(3).setPreferredWidth(75);
		tableStandard.getColumnModel().getColumn(3).setMaxWidth(75);

		TableColumn fieldNoColumn = tableStandard.getColumnModel().getColumn(4);
		tableStandard.getColumnModel().removeColumn(fieldNoColumn);

		versionColumn = tableStandard.getColumnModel().getColumn(2);

		JScrollPane scrollTable = new JScrollPane(tableStandard);
		scrollTable.setPreferredSize(new Dimension(390, 190));
		return scrollTable;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOK);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(btnCancel);
		return buttonPanel;
	}

	private String addNumbers(NRange[] numbers) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (NRange range : numbers) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(String.format("(%1$s.%2$03d - %1$s.%3$03d)", recordType.getNumber(), range.from, range.to));
			i++;
		}
		return sb.toString();
	}

	private void updateFields() {
		NVersion currentVersion = useSelectMode ? version : ANTemplate.VERSION_CURRENT;
		List<NVersion> versions = null;
		if (!useSelectMode && recordType != null) {
			NVersion[] vers = ANTemplate.getVersions();
			versions = new ArrayList<NVersion>(vers.length);
			for (NVersion v : vers) {
				if (v.getValue() >= recordType.getVersion().getValue()) {
					versions.add(v);
				}
			}
		}

		if (recordType != null) {
			maxFieldNumber = recordType.getMaxFieldNumber(currentVersion);
			standardFieldNumbers = recordType.getStandardFieldNumbers(currentVersion);
			StringBuilder sb = new StringBuilder();
			sb.append("<html>");

			if (useSelectMode) {
				if (validationLevel == ANValidationLevel.MINIMAL) {
					sb.append(String.format("(%1$s.001 - %1$s.%2$03d)", recordType.getNumber(), maxFieldNumber));
					sb.append("<br>");
					sb.append("UDF: ");
				}
				userDefinedFieldNumbers = recordType.getUserDefinedFieldNumbers(currentVersion);
				if (userDefinedFieldNumbers.length == 0) {
					sb.append("None");
				} else {
					sb.append(addNumbers(userDefinedFieldNumbers));
				}
			} else {
				userDefinedFieldNumbers = null;
				for (NVersion v : versions) {
					sb.append(String.format("%s: ", v));
					NRange[] udfNumbers = recordType.getUserDefinedFieldNumbers(v);
					if (udfNumbers.length == 0) {
						sb.append("None");
					} else {
						sb.append(addNumbers(udfNumbers));
					}
					sb.append("<br>");
				}
			}

			sb.append("</html>");
			lblFieldNumbers.setText(sb.toString());
			radioUserDefined.setEnabled(true);
			lblFieldNumbers.setEnabled(true);

		} else {
			maxFieldNumber = 0;
			standardFieldNumbers = null;
			userDefinedFieldNumbers = null;
			lblFieldNumbers.setText("");
			radioUserDefined.setEnabled(false);
			lblFieldNumbers.setEnabled(false);
		}

		tableModel.clearAllData();
		if (standardFieldNumbers != null) {
			for (int fieldNumber : standardFieldNumbers) {
				boolean isReadOnly = isFieldStandard(recordType, currentVersion, fieldNumber, validationLevel);
				if (!useSelectMode || !isReadOnly) {
					String number = String.format("%s.%03d", recordType.getNumber(), fieldNumber);
					String id = recordType.getFieldId(currentVersion, fieldNumber);
					String name = recordType.getFieldName(currentVersion, fieldNumber);
					boolean isMandatory = recordType.isFieldMandatory(currentVersion, fieldNumber);
					if (id != "") {
						name = String.format("%s (%s)", name, id);
					}

					String versionString = "";
					if (!useSelectMode) {
						NVersion knownVer = currentVersion;
						for (NVersion v : versions) {
							if (recordType.isFieldKnown(v, fieldNumber) && recordType.isFieldStandard(v, fieldNumber)) {
								knownVer = v;
								break;
							}
						}
						versionString = knownVer.toString();
					}

					String mandatoryString = "";
					if (!useSelectMode || validationLevel == ANValidationLevel.MINIMAL) {
						mandatoryString = isMandatory ? "Yes" : "No";
					}
					tableModel.addRow(new Object[] { number, name, versionString, mandatoryString, fieldNumber });
				}
			}
		}
		tableStandard.updateUI();
	}

	private void onVersionChanged() {
		updateFields();
	}

	private void onRecordTypeChanged() {
		updateFields();
	}

	private void updateGui() {
		setSize(new Dimension(useSelectMode ? 470 : 515, getPreferredSize().height));
		radioStandard.setEnabled(validationLevel == ANValidationLevel.MINIMAL);
		radioUserDefined.setText(validationLevel == ANValidationLevel.MINIMAL ? "Other field:" : "User-defined field:");

		int versionColumnIndex = 2;

		if (useSelectMode) {
			if (tableStandard.getColumnModel().getColumn(versionColumnIndex) == versionColumn) {
				tableStandard.getColumnModel().removeColumn(versionColumn);
			}
		} else {
			if (tableStandard.getColumnModel().getColumn(versionColumnIndex) != versionColumn) {
				tableStandard.getColumnModel().addColumn(versionColumn);
				tableStandard.moveColumn(3, 2);
			}
		}
		tableStandard.updateUI();
	}

	private void onUseSelectModeChanged() {
		updateFields();
		updateGui();
		if (!useSelectMode) {
			setUseUserDefinedFieldNumber(false);
		}
		btnOK.setVisible(useSelectMode);
		txtUserDefinedField.setVisible(useSelectMode);
		radioUserDefined.setVisible(useSelectMode);
		radioStandard.setVisible(useSelectMode);
		lblStandard.setVisible(!useSelectMode);
		lblUserDefined.setVisible(!useSelectMode);

		btnCancel.setText(useSelectMode ? "Cancel" : "Close");
		onUseUserDefinedFieldNumberChanged();
	}

	private void onValidationLevelChanged() {
		updateFields();
		updateGui();
		if (useSelectMode && validationLevel != ANValidationLevel.MINIMAL) {
			setUseUserDefinedFieldNumber(true);
		} else {
			setUseUserDefinedFieldNumber(false);
		}
	}

	private void onUseUserDefinedFieldNumberChanged() {
		radioStandard.setSelected(useSelectMode && !useUserDefinedFieldNumber);
		tableStandard.setEnabled(!useSelectMode || !useUserDefinedFieldNumber);
		radioUserDefined.setSelected(useSelectMode && useUserDefinedFieldNumber);
		txtUserDefinedField.setEnabled(!useSelectMode || useUserDefinedFieldNumber);
		onSelectedFieldNumberChanged();
	}

	private void setUseUserDefinedFieldNumber(boolean value) {
		if (useUserDefinedFieldNumber != value) {
			useUserDefinedFieldNumber = value;
			onUseUserDefinedFieldNumberChanged();
		}
	}

	private void onSelectedFieldNumberChanged() {
		btnOK.setEnabled(useSelectMode && (useUserDefinedFieldNumber || tableStandard.getSelectedRowCount() != 0));
	}

	private boolean isUserDefinedFieldNumber(int fieldNumber) {
		for (NRange range : userDefinedFieldNumbers) {
			if (fieldNumber >= range.from && fieldNumber <= range.to) {
				return true;
			}
		}
		return false;
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

	public ANRecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(ANRecordType value) {
		if (recordType != value) {
			recordType = value;
			onRecordTypeChanged();
		}
	}

	public boolean isUseSelectMode() {
		return useSelectMode;
	}

	public void setUseSelectMode(boolean value) {
		useSelectMode = value;
		onUseSelectModeChanged();
	}

	public ANValidationLevel getValidationLevel() {
		return validationLevel;
	}

	public void setValidationLevel(ANValidationLevel value) {
		if (validationLevel != value) {
			validationLevel = value;
			onValidationLevelChanged();
		}
	}

	public int getFieldNumber() {
		if (useUserDefinedFieldNumber) {
			String text = txtUserDefinedField.getText();
			if (text != null && !text.equals("")) {
				if (text.contains(".")) {
					text = text.trim();
					String prefix = String.format("%d.", recordType.getNumber());
					if (text.startsWith(prefix)) {
						text = text.substring(prefix.length());
					}
				}

				try {
					return Integer.parseInt(text);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return -1;
				}
			}
		}
		if (tableStandard.getSelectedRowCount() == 0) {
			return -1;
		}
		return (Integer) tableStandard.getModel().getValueAt(tableStandard.getSelectedRows()[0], 4);
	}

	public void setFieldNumber(int value) {
		if (useUserDefinedFieldNumber) {
			txtUserDefinedField.setText(value == -1 ? "" : String.valueOf(value));
		} else {
			if (value == -1) {
				tableStandard.clearSelection();
			} else {
				int index = Arrays.asList(standardFieldNumbers).indexOf(value);
				tableStandard.setRowSelectionInterval(index, index);
			}
		}
	}

	public void showFieldNumberDialog(boolean isFromMain) {
		this.isFromMain = isFromMain;
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private void buttonOKActionPerformed() {
		if (useUserDefinedFieldNumber) {
			int fieldNumber = getFieldNumber();
			String errorMessage;
			if (fieldNumber == -1) {
				errorMessage = "User defined field number is invalid";
			} else if (fieldNumber < 1) {
				errorMessage = "User defined field number is less than one";
			} else if (fieldNumber > maxFieldNumber) {
				errorMessage = "User defined field number is greater than maximal allowed value";
			} else if (validationLevel != ANValidationLevel.MINIMAL && !isUserDefinedFieldNumber(fieldNumber)) {
				errorMessage = "User defined field number is not in user defined field range";
			} else {
				errorMessage = null;
			}

			if (errorMessage != null) {
				txtUserDefinedField.requestFocus();
				JOptionPane.showMessageDialog(this, errorMessage, this.getTitle(), JOptionPane.ERROR_MESSAGE);
			}
		}
		if (isFromMain) {
			listener.fieldNumberSelected(this.getFieldNumber());
		}
		dispose();
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == radioStandard) {
			setUseUserDefinedFieldNumber(false);
		} else if (source == radioUserDefined) {
			setUseUserDefinedFieldNumber(true);
		} else if (source == btnCancel) {
			dispose();
		} else if (source == btnOK) {
			buttonOKActionPerformed();
		}
	}

	// ==============================================
	// Private class extending DefaultTableModel
	// ==============================================

	private static final class StandardFieldsTableModel extends DefaultTableModel {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Private fields
		// ==============================================

		private String[] columnNames = { "Number", "Name", "Version", "Mandatory", "FieldNo" };

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
