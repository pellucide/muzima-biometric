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
import java.util.Arrays;

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

import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANType1Record;
import com.neurotec.util.NVersion;

public final class CharsetFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private NVersion version = new NVersion((short) 0);
	private boolean useSelectMode = true;
	private boolean useUserDefinedCharsetIndex;
	private int[] standardCharsetIndicies;

	private StandardCharsetsTableModel tableModel;
	private GridBagConstraints c;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JLabel lblIndicies;
	private JLabel lblIndex;
	private JLabel lblName;
	private JLabel lblVersion;
	private JLabel lblStandard;
	private JLabel lblUserDefined;

	private JTable tableStandard;
	private JButton btnOK;
	private JButton btnCancel;

	private JTextField txtIndex;
	private JTextField txtName;
	private JTextField txtVersion;
	private JRadioButton radioStandard;
	private JRadioButton radioUserDefined;

	private TableColumn versionColumn;

	// ==============================================
	// Public constructor
	// ==============================================

	public CharsetFrame(Frame owner) {
		super(owner, "Charset", true);
		setPreferredSize(new Dimension(440, 305));
		initializeComponents();

		lblIndicies.setText(String.format("(%03d - %03d)", ANType1Record.CHARSET_USER_DEFINED_FROM, ANType1Record.CHARSET_USER_DEFINED_TO));
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
		mainPanelLayout.columnWidths = new int[] { 100, 100, 100, 100 };
		mainPanelLayout.rowHeights = new int[] { 25, 120, 25, 25, 25 };
		mainPanel.setLayout(mainPanelLayout);

		radioStandard = new JRadioButton("Standard charset:");
		radioStandard.addActionListener(this);
		lblStandard = new JLabel("Standard charsets:");

		radioUserDefined = new JRadioButton("User defined charset:");
		radioUserDefined.addActionListener(this);
		lblUserDefined = new JLabel("User defined charsets:");

		lblIndicies = new JLabel("(0-0)");
		lblIndex = new JLabel("Index:");
		lblName = new JLabel("Name:");
		lblVersion = new JLabel("Version:");
		txtIndex = new JTextField();
		txtName = new JTextField();
		txtVersion = new JTextField();

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3, 3, 3, 3);

		addToGridBagLayout(0, 0, 4, 1, mainPanel, radioStandard);
		addToGridBagLayout(0, 0, mainPanel, lblStandard);
		addToGridBagLayout(0, 1, 4, 1, 1, 1, mainPanel, createTable());
		addToGridBagLayout(0, 2, 1, 1, 0, 0, mainPanel, radioUserDefined);
		addToGridBagLayout(0, 2, mainPanel, lblUserDefined);
		addToGridBagLayout(1, 2, mainPanel, lblIndicies);
		addToGridBagLayout(0, 3, mainPanel, lblIndex);
		addToGridBagLayout(1, 3, mainPanel, lblName);
		addToGridBagLayout(3, 3, mainPanel, lblVersion);
		addToGridBagLayout(0, 4, mainPanel, txtIndex);
		addToGridBagLayout(1, 4, mainPanel, txtName);
		addToGridBagLayout(3, 4, mainPanel, txtVersion);

		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(createButtonPanel(), BorderLayout.AFTER_LAST_LINE);
		pack();

	}

	private JScrollPane createTable() {
		tableModel = new StandardCharsetsTableModel();
		tableStandard = new JTable(tableModel);
		tableStandard.getTableHeader().setReorderingAllowed(false);
		tableStandard.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableStandard.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedCharsetIndexChanged();
			}
		});

		tableStandard.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (useSelectMode && getCharsetIndex() != -1) {
						buttonOKActionPerformed();
					}
				}
			}
		});

		tableStandard.getColumnModel().getColumn(0).setPreferredWidth(70);
		tableStandard.getColumnModel().getColumn(1).setPreferredWidth(260);
		tableStandard.getColumnModel().getColumn(2).setPreferredWidth(70);
		TableColumn charsetIndexColumn = tableStandard.getColumnModel().getColumn(3);
		tableStandard.getColumnModel().removeColumn(charsetIndexColumn);

		versionColumn = tableStandard.getColumnModel().getColumn(2);

		JScrollPane scrollTable = new JScrollPane(tableStandard);
		scrollTable.setPreferredSize(new Dimension(400, 120));
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

	private void updateCharsets() {
		NVersion currentVersion = useSelectMode ? version : ANTemplate.VERSION_CURRENT;
		NVersion[] versions = ANTemplate.getVersions();
		standardCharsetIndicies = ANType1Record.getStandardCharsetIndexes(currentVersion);
		tableModel.clearAllData();

		for (int charsetIndex : standardCharsetIndicies) {
			String index = String.format("%03d", charsetIndex);
			String name = String.format("%s (%s)", ANType1Record.getStandardCharsetName(currentVersion, charsetIndex), ANType1Record.getStandardCharsetDescription(currentVersion, charsetIndex));
			String versionString = "";

			if (!useSelectMode) {
				NVersion knownVer = currentVersion;
				for (NVersion v : versions) {
					if (ANType1Record.isCharsetKnown(v, charsetIndex)) {
						knownVer = v;
						break;
					}
				}
				versionString = knownVer.toString();
			}
			tableModel.insertRow(charsetIndex, new Object[] { index, name, versionString, charsetIndex });
		}
		tableStandard.updateUI();
	}

	private void onVersionChanged() {
		updateCharsets();
	}

	private void onUseSelectModeChanged() {
		updateCharsets();
		if (useSelectMode) {
			tableStandard.getColumnModel().removeColumn(versionColumn);
		} else {
			setUseUserDefinedCharsetIndex(false);
			tableStandard.getColumnModel().addColumn(versionColumn);
		}
		setPreferredSize(new Dimension(useSelectMode ? 370 : 430, getPreferredSize().height));

		btnOK.setVisible(useSelectMode);
		lblIndex.setVisible(useSelectMode);
		txtIndex.setVisible(useSelectMode);
		lblName.setVisible(useSelectMode);
		txtName.setVisible(useSelectMode);
		lblVersion.setVisible(useSelectMode);
		txtVersion.setVisible(useSelectMode);
		radioUserDefined.setVisible(useSelectMode);
		radioStandard.setVisible(useSelectMode);
		lblStandard.setVisible(!useSelectMode);
		lblUserDefined.setVisible(!useSelectMode);
		btnCancel.setText(useSelectMode ? "Cancel" : "Close");
		onUseUserDefinedCharsetIndexChanged();
	}

	private void onUseUserDefinedCharsetIndexChanged() {
		radioStandard.setSelected(useSelectMode && !useUserDefinedCharsetIndex);
		lblStandard.setEnabled(!useSelectMode || !useUserDefinedCharsetIndex);
		radioUserDefined.setSelected(useSelectMode && useUserDefinedCharsetIndex);

		boolean enable = !useSelectMode || useUserDefinedCharsetIndex;
		lblIndex.setEnabled(enable);
		txtIndex.setEnabled(enable);
		lblName.setEnabled(enable);
		txtName.setEnabled(enable);

		onSelectedCharsetIndexChanged();
	}

	private void setUseUserDefinedCharsetIndex(boolean value) {
		if (useUserDefinedCharsetIndex != value) {
			useUserDefinedCharsetIndex = value;
			onUseUserDefinedCharsetIndexChanged();
		}
	}

	private void onSelectedCharsetIndexChanged() {
		btnOK.setEnabled(useSelectMode && (useUserDefinedCharsetIndex || tableStandard.getSelectedRowCount() != 0));
	}

	private void buttonOKActionPerformed() {
		if (useUserDefinedCharsetIndex) {
			int charsetIndex = getCharsetIndex();
			String errorMessage;
			if (charsetIndex == -1) {
				errorMessage = "User defined charset index is invalid";
			} else if (charsetIndex < 0) {
				errorMessage = "User defined charset index is less than zero";
			} else if (charsetIndex > ANType1Record.CHARSET_USER_DEFINED_TO) {
				errorMessage = "User defined charset index is greater than maximal allowed value";
			} else if (charsetIndex < ANType1Record.CHARSET_USER_DEFINED_FROM || charsetIndex > ANType1Record.CHARSET_USER_DEFINED_TO) {
				errorMessage = "User defined charset index is not in user defined charset range";
			} else {
				errorMessage = null;
			}

			if (errorMessage != null) {
				txtIndex.transferFocus();
				JOptionPane.showMessageDialog(this, errorMessage, this.getTitle(), JOptionPane.ERROR_MESSAGE);
			} else {
				dispose();
			}
		}
	}

	// ==============================================
	// Public methods
	// ==============================================

	public NVersion getVersion() {
		return version;
	}

	public void setVersion(NVersion version) {
		this.version = version;
		onVersionChanged();
	}

	public boolean isUseSelectMode() {
		return useSelectMode;
	}

	public void setUseSelectMode(boolean useSelectMode) {
		this.useSelectMode = useSelectMode;
		onUseSelectModeChanged();
	}

	public int getCharsetIndex() {
		if (useUserDefinedCharsetIndex) {
			try {
				return Integer.parseInt(txtIndex.getText());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return tableStandard.getSelectedRowCount() == 0 ? -1 : (int) tableStandard.getSelectedRows()[0];
	}

	public void setCharsetIndex(int value) {
		if (useUserDefinedCharsetIndex) {

			txtIndex.setText(value == -1 ? "" : String.valueOf(value));
		} else {
			if (value == -1) {
				tableStandard.clearSelection();
			} else {
				int index = Arrays.asList(standardCharsetIndicies).indexOf(value);
				tableStandard.setRowSelectionInterval(index, index);
			}
		}
	}

	public String getCharsetName() {
		if (useUserDefinedCharsetIndex) {
			return txtName.getText();
		} else if (tableStandard.getSelectedRowCount() == 0) {
			return null;
		} else {
			NVersion charsetVersion = useSelectMode ? version : ANTemplate.VERSION_CURRENT;
			return ANType1Record.getStandardCharsetName(charsetVersion, tableStandard.getSelectedRows()[0]);
		}
	}

	public void setCharsetName(String value) {
		if (useUserDefinedCharsetIndex) {
			txtName.setText(value);
		}
	}

	public String getCharsetVersion() {
		return txtVersion.getText();
	}

	public void setCharsetVersion(String version) {
		txtVersion.setText(version);
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == radioStandard) {
			setUseUserDefinedCharsetIndex(false);
		} else if (source == radioUserDefined) {
			setUseUserDefinedCharsetIndex(true);
		} else if (source == btnOK) {
			buttonOKActionPerformed();
		} else if (source == btnCancel) {
			dispose();
		}
	}

	// ==============================================
	// Private class extending DefaultTableModel
	// ==============================================

	private static final class StandardCharsetsTableModel extends DefaultTableModel {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Private fields
		// ==============================================

		private String[] columnNames = { "Index", "Name", "Version", "charsetIndex" };

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
			return 4;
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
