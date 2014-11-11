package com.neurotec.samples.biometrics.standards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import com.neurotec.biometrics.standards.ANPenVector;
import com.neurotec.samples.biometrics.standards.events.PenVectorCreationListener;

public final class CreateANPenVectorArrayFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private ANPenVector[] vectors;
	private PenVectorCreationListener listener;

	// ==============================================
	// Private GUI components
	// ==============================================

	private JButton btnOK;
	private JButton btnCancel;

	private JTable dataTable;
	private PenVectorTableModel dataTableModel;

	// ==============================================
	// Public constructor
	// ==============================================

	public CreateANPenVectorArrayFrame(JDialog owner, PenVectorCreationListener listener) {
		super(owner, "Edit pen vectors", true);
		this.listener = listener;
		setPreferredSize(new Dimension(290, 315));
		setResizable(false);
		initializeComponents();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		dataTableModel = new PenVectorTableModel();
		dataTable = new JTable(dataTableModel);
		dataTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane tableScrollPane = new JScrollPane(dataTable);

		dataTable.getColumnModel().getColumn(1).setCellEditor(new PenVectorTableCellEditor());
		dataTable.getColumnModel().getColumn(2).setCellEditor(new PenVectorTableCellEditor());
		dataTable.getColumnModel().getColumn(3).setCellEditor(new PenVectorTableCellEditor());
		dataTable.getColumnModel().getColumn(1).setCellRenderer(new PenVectorTableCellRenderer());
		dataTable.getColumnModel().getColumn(2).setCellRenderer(new PenVectorTableCellRenderer());
		dataTable.getColumnModel().getColumn(3).setCellRenderer(new PenVectorTableCellRenderer());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnOK);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(btnCancel);

		contentPane.add(tableScrollPane, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
		pack();
	}

	// ==============================================
	// Public methods
	// ==============================================

	public ANPenVector[] getVectors() {
		if (vectors != null) {
			return vectors.clone();
		}
		return null;
	}

	public void setVectors(ANPenVector[] value) {
		dataTableModel.clearAllData();
		if (value != null) {
			vectors = value.clone();

			for (ANPenVector item : vectors) {
				dataTableModel.addRow(new Object[] { "*", item.x, item.y, item.pressure });
			}

		} else {
			vectors = null;
		}
		dataTableModel.addRow(new Object[] { "*", "", "", "" });
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnOK) {
			if (dataTable.isEditing()) {
				dataTable.getCellEditor().stopCellEditing();
			}

			boolean isOk = true;
			List<ANPenVector> list = new ArrayList<ANPenVector>();
			for (int row = 0; row < dataTableModel.getRowCount() - 1; row++) {
				int x = 0;
				int y = 0;
				int pressure = 0;

				try {
					x = Integer.parseInt(dataTable.getModel().getValueAt(row, 1).toString());
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					isOk = false;
				}

				try {
					y = Integer.parseInt(dataTable.getModel().getValueAt(row, 2).toString());
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					isOk = false;
				}

				try {
					pressure = Integer.parseInt(dataTable.getModel().getValueAt(row, 3).toString());
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					isOk = false;
				}

				if (!isOk) {
					break;
				}
				list.add(new ANPenVector(x, y, (short) pressure));
			}

			if (isOk) {
				setVectors(list.toArray(new ANPenVector[list.size()]));
				listener.vectorsCreated(getVectors());
				dispose();
			}
		} else if (source == btnCancel) {
			dispose();
		}
	}

	// ====================================================
	// Private class table model for pen vector table
	// ====================================================

	private static final class PenVectorTableModel extends DefaultTableModel {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Private fields
		// ==============================================

		private String[] columnNames = { "", "X", "columnY", "Pressure" };

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
		// Public overridden methods
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
			if (column == 0) {
				return false;
			}
			return true;
		}

	}

	// ====================================================
	// Private class cell editor for pen vector table
	// ====================================================

	private final class PenVectorTableCellEditor extends AbstractCellEditor implements TableCellEditor {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Private fields
		// ==============================================

		private JTextField component = new JTextField();
		private KeyListener keyListener;

		// ==============================================
		// Package private constructor
		// ==============================================

		PenVectorTableCellEditor() {
			super();
			keyListener = new KeyAdapter() {

				@Override
				public void keyTyped(KeyEvent e) {
					if (component.getText().length() == 0) {
						dataTableModel.addRow(new Object[] { "*", "", "", "" });
					}
				}
			};
		}

		// ==============================================
		// Public interface methods
		// ==============================================

		public Object getCellEditorValue() {
			return component.getText();
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			String stringValue = (String) value;
			component.setText(stringValue);
			if (row == dataTable.getRowCount() - 1) {
				component.addKeyListener(keyListener);
			} else {
				component.removeKeyListener(keyListener);
			}
			return component;
		}

		@Override
		public void cancelCellEditing() {
			component.removeKeyListener(keyListener);
			super.cancelCellEditing();
		}

		@Override
		public boolean stopCellEditing() {
			component.removeKeyListener(keyListener);
			return super.stopCellEditing();
		}

	}

	// ============================================================
	// Private class table cell renderer for pen vector table
	// ============================================================

	private final class PenVectorTableCellRenderer extends DefaultTableCellRenderer {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Public overridden methods
		// ==============================================

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JComponent component = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			component.setForeground(Color.BLACK);
			if (column != 0) {
				if (row < dataTable.getRowCount() - 1) {
					if (column == 3) {
						try {
							Integer.parseInt((String) value);
							component.setBackground(Color.WHITE);
						} catch (NumberFormatException e) {
							component.setBackground(Color.RED);
						}
					} else {
						try {
							Integer.parseInt((String) value);
							component.setBackground(Color.WHITE);
						} catch (NumberFormatException e) {
							component.setBackground(Color.RED);
						}
					}
				} else {
					component.setBackground(Color.WHITE);
				}
			} else {
				if (row == dataTable.getRowCount() - 1) {
					((JLabel) component).setText("*");
				} else if (isSelected) {
					((JLabel) component).setText(">");
				} else if (dataTable.getEditingRow() == row) {
					((JLabel) component).setText("~");
				}
			}
			return component;
		}
	}

}
