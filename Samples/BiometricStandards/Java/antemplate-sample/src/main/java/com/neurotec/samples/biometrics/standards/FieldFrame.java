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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.neurotec.biometrics.standards.ANField;
import com.neurotec.biometrics.standards.ANSubField;
import com.neurotec.samples.biometrics.standards.events.ItemChangeListener;

public final class FieldFrame extends JDialog implements ActionListener, ItemChangeListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Public static methods
	// ==============================================

	public static String getFieldValue(ANField field) {
		StringBuilder value = new StringBuilder();
		value.setLength(0);
		boolean manySubFields = field.getSubFields().size() > 1;
		int sfi = 0;
		for (ANSubField subField : field.getSubFields()) {
			if (sfi != 0) {
				value.append(',');
			}

			if (manySubFields) {
				value.append('{');
			}

			int ii = 0;
			for (String item : subField.getItems()) {
				if (ii != 0) {
					value.append('|');
				}
				value.append(item);
				ii++;
			}

			if (manySubFields) {
				value.append('}');
			}
			sfi++;
		}
		return value.toString();
	}

	// ==============================================
	// Private fields
	// ==============================================

	private ANField field;
	private boolean isReadOnly;
	private boolean isModified;
	private Vector<String> vectorSubFields;
	private Vector<String> vectorItems;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JLabel lblValue;
	private JButton btnEdit;
	private JList lstSubFields;
	private JList lstItems;

	private JButton btnSubFieldsAdd;
	private JButton btnSubFieldsInsert;
	private JButton btnSubFieldsRemove;
	private JButton btnSubFieldsEdit;

	private JButton btnItemsAdd;
	private JButton btnItemsInsert;
	private JButton btnItemsRemove;
	private JButton btnItemsEdit;

	private JButton btnClose;

	// ==============================================
	// Public constructor
	// ==============================================

	public FieldFrame(Frame owner) {
		super(owner, "Edit Field", true);
		setPreferredSize(new Dimension(680, 390));
		setMinimumSize(new Dimension(600, 300));
		initializeComponents();
		onFieldChanged();
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if (field != null && field.getSubFields().size() == 1 && field.getSubFields().get(0).getItems().size() == 1 && !isReadOnly) {
					editField();
				}
			}

		});
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3, 3, 3, 3);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		contentPane.add(createTopPanel(), c);

		c.gridy = 2;
		contentPane.add(createButtonPanel(), c);

		c.gridy = 1;
		c.weightx = 0.5;
		c.weighty = 1;
		c.gridwidth = 1;
		contentPane.add(createSubFieldsPanel(), c);

		c.gridx = 1;
		contentPane.add(createItemsPanel(), c);

		pack();

	}

	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

		lblValue = new JLabel();
		lblValue.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (field != null && field.getSubFields().size() == 1 && field.getSubFields().get(0).getItems().size() == 1) {
						editField();
					}
				}
			}
		});

		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(this);

		topPanel.add(Box.createHorizontalStrut(10));
		topPanel.add(new JLabel("Value:"));
		topPanel.add(Box.createHorizontalStrut(20));
		topPanel.add(lblValue);
		topPanel.add(Box.createGlue());
		topPanel.add(btnEdit);
		topPanel.add(Box.createHorizontalStrut(10));
		return topPanel;
	}

	private JPanel createSubFieldsPanel() {
		JPanel subfieldsPanel = new JPanel(new BorderLayout(5, 5));
		subfieldsPanel.setBorder(BorderFactory.createTitledBorder("Subfields"));

		vectorSubFields = new Vector<String>();
		lstSubFields = new JList(vectorSubFields);
		lstSubFields.setFixedCellHeight(16);
		lstSubFields.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedSubFieldChanged();
			}
		});

		lstSubFields.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ANSubField selectedSubField = getSelectedSubField();
					if (selectedSubField != null && selectedSubField.getItems().size() == 1) {
						editSubField();
					}
				}
			}
		});

		JScrollPane subFieldsScrollPane = new JScrollPane(lstSubFields);
		subFieldsScrollPane.setPreferredSize(new Dimension(295, 185));

		subfieldsPanel.add(subFieldsScrollPane, BorderLayout.CENTER);
		subfieldsPanel.add(createSubFieldsButtonPanel(), BorderLayout.AFTER_LAST_LINE);

		return subfieldsPanel;
	}

	private JPanel createSubFieldsButtonPanel() {
		JPanel subFieldsButtonPanel = new JPanel();
		subFieldsButtonPanel.setLayout(new BoxLayout(subFieldsButtonPanel, BoxLayout.X_AXIS));

		btnSubFieldsAdd = new JButton("Add");
		btnSubFieldsAdd.addActionListener(this);
		btnSubFieldsInsert = new JButton("Insert");
		btnSubFieldsInsert.addActionListener(this);
		btnSubFieldsRemove = new JButton("Remove");
		btnSubFieldsRemove.addActionListener(this);
		btnSubFieldsEdit = new JButton("Edit");
		btnSubFieldsEdit.addActionListener(this);

		subFieldsButtonPanel.add(btnSubFieldsAdd);
		subFieldsButtonPanel.add(Box.createHorizontalStrut(5));
		subFieldsButtonPanel.add(btnSubFieldsInsert);
		subFieldsButtonPanel.add(Box.createHorizontalStrut(5));
		subFieldsButtonPanel.add(btnSubFieldsRemove);
		subFieldsButtonPanel.add(Box.createHorizontalStrut(5));
		subFieldsButtonPanel.add(btnSubFieldsEdit);
		subFieldsButtonPanel.add(Box.createGlue());
		return subFieldsButtonPanel;
	}

	private JPanel createItemsPanel() {
		JPanel itemsPanel = new JPanel(new BorderLayout(5, 5));
		itemsPanel.setBorder(BorderFactory.createTitledBorder("Items"));
		vectorItems = new Vector<String>();
		lstItems = new JList(vectorItems);
		lstItems.setFixedCellHeight(16);
		lstItems.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedItemChanged();
			}
		});

		lstItems.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (getSelectedItemIndex() != -1) {
						editItem();
					}
				}
			}
		});

		JScrollPane itemsScrollPane = new JScrollPane(lstItems);
		itemsScrollPane.setPreferredSize(new Dimension(335, 185));

		itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
		itemsPanel.add(createItemsButtonPanel(), BorderLayout.AFTER_LAST_LINE);
		return itemsPanel;
	}

	private JPanel createItemsButtonPanel() {
		JPanel itemsButtonPanel = new JPanel();
		itemsButtonPanel.setLayout(new BoxLayout(itemsButtonPanel, BoxLayout.X_AXIS));

		btnItemsAdd = new JButton("Add");
		btnItemsAdd.addActionListener(this);
		btnItemsInsert = new JButton("Insert");
		btnItemsInsert.addActionListener(this);
		btnItemsRemove = new JButton("Remove");
		btnItemsRemove.addActionListener(this);
		btnItemsEdit = new JButton("Edit");
		btnItemsEdit.addActionListener(this);

		itemsButtonPanel.add(btnItemsAdd);
		itemsButtonPanel.add(Box.createHorizontalStrut(5));
		itemsButtonPanel.add(btnItemsInsert);
		itemsButtonPanel.add(Box.createHorizontalStrut(5));
		itemsButtonPanel.add(btnItemsRemove);
		itemsButtonPanel.add(Box.createHorizontalStrut(5));
		itemsButtonPanel.add(btnItemsEdit);
		itemsButtonPanel.add(Box.createGlue());
		return itemsButtonPanel;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnClose = new JButton("Close");
		btnClose.addActionListener(this);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnClose);
		buttonPanel.add(Box.createHorizontalStrut(10));
		return buttonPanel;
	}

	private void updateField() {
		if (field != null) {
			lblValue.setText(getFieldValue(field));
		} else {
			lblValue.setText("");
		}
		btnEdit.setEnabled(field != null && field.getSubFields().size() == 1 && field.getSubFields().get(0).getItems().size() == 1);
		isModified = true;
	}

	private void onFieldChanged() {
		vectorSubFields.clear();
		if (field != null) {
			for (ANSubField subField : field.getSubFields()) {
				vectorSubFields.add(getSubFieldValue(subField));
			}
			lstSubFields.updateUI();
			lstSubFields.setSelectedIndex(0);
		}
		lstSubFields.updateUI();
		updateField();
		isModified = false;
	}

	private void editField() {
		ItemFrame form = new ItemFrame(this, this);
		form.setValue(field.getValue());
		form.setTitle("Edit Field");
		form.setReadOnly(isReadOnly);
		form.showItemFrame(FieldFrameOperation.EDIT_FIELD);
	}

	private void onIsReadOnlyChanged() {
		updateSubFieldControls();
		updateItemControls();
	}

	private ANSubField getSelectedSubField() {
		if (lstSubFields.getSelectedIndices().length == 1) {

			return field.getSubFields().get(lstSubFields.getSelectedIndex());
		}
		return null;
	}

	private String getSubFieldValue(ANSubField subField) {
		StringBuilder value = new StringBuilder();
		value.setLength(0);
		int ii = 0;
		for (String item : subField.getItems()) {
			if (ii != 0) {
				value.append('|');
			}
			value.append(item);
			ii++;
		}
		return value.toString();
	}

	private void onSelectedSubFieldChanged() {
		ANSubField selectedSubField = getSelectedSubField();
		vectorItems.clear();
		if (selectedSubField != null) {
			for (String item : selectedSubField.getItems()) {
				vectorItems.add(item);
			}
			lstItems.updateUI();
			lstItems.setSelectedIndex(0);
		}
		lstItems.updateUI();
		updateSubFieldControls();
		updateItemControls();
	}

	private void updateSubFieldControls() {
		ANSubField selectedSubField = getSelectedSubField();
		btnSubFieldsAdd.setEnabled(field != null && !isReadOnly);
		btnSubFieldsInsert.setEnabled(!isReadOnly && selectedSubField != null);
		int sc = lstSubFields.getSelectedIndices().length;
		btnSubFieldsRemove.setEnabled(!isReadOnly && sc != 0 && sc != field.getSubFields().size());
		btnSubFieldsEdit.setEnabled(selectedSubField != null && selectedSubField.getItems().size() == 1);
	}

	private void updateSubField(int index) {
		vectorSubFields.set(index, getSubFieldValue(field.getSubFields().get(index)));
		lstSubFields.updateUI();
		updateField();
		updateSubFieldControls();
	}

	private void updateSelectedSubField() {
		updateSubField(lstSubFields.getSelectedIndex());
	}

	private void editSubField() {
		ANSubField selectedSubField = getSelectedSubField();
		ItemFrame form = new ItemFrame(this, this);
		form.setValue(selectedSubField.getValue());
		form.setTitle("Edit Subfield");
		form.setReadOnly(isReadOnly);
		form.showItemFrame(FieldFrameOperation.EDIT_SUB_FIELD);
	}

	private int getSelectedItemIndex() {
		return lstItems.getSelectedIndices().length == 1 ? lstItems.getSelectedIndex() : -1;
	}

	private void onSelectedItemChanged() {
		updateItemControls();
	}

	private void updateItemControls() {
		ANSubField selectedSubField = getSelectedSubField();
		int selectedItemIndex = getSelectedItemIndex();
		btnItemsAdd.setEnabled(selectedSubField != null && !isReadOnly);
		btnItemsInsert.setEnabled(!isReadOnly && selectedItemIndex != -1);
		int sc = lstItems.getSelectedIndices().length;
		btnItemsRemove.setEnabled(selectedSubField != null && !isReadOnly && sc != 0 && sc != selectedSubField.getItems().size());
		btnItemsEdit.setEnabled(selectedItemIndex != -1);
	}

	private void editItem() {
		ANSubField selectedSubField = getSelectedSubField();
		int selectedItemIndex = getSelectedItemIndex();
		ItemFrame form = new ItemFrame(this, this);
		form.setValue(selectedSubField.getItems().get(selectedItemIndex));
		form.setReadOnly(isReadOnly);
		form.showItemFrame(FieldFrameOperation.EDIT_ITEM);
	}

	private void addSubField() {
		ItemFrame form = new ItemFrame(this, this);
		form.setTitle("Add Subfield");
		form.showItemFrame(FieldFrameOperation.ADD_SUB_FIELD);
	}

	private void insertSubField() {
		ItemFrame form = new ItemFrame(this, this);
		form.setTitle("Insert Subfield");
		form.showItemFrame(FieldFrameOperation.INSERT_SUB_FIELD);
	}

	private void removeSubField() {
		int selCount = lstSubFields.getSelectedIndices().length;
		int[] selIndices = lstSubFields.getSelectedIndices();
		Arrays.sort(selIndices);
		for (int i = selCount - 1; i >= 0; i--) {
			int index = selIndices[i];
			field.getSubFields().remove(index);
			vectorSubFields.remove(index);
			lstSubFields.updateUI();
		}

		lstSubFields.updateUI();
		updateField();
		lstSubFields.clearSelection();

		int subFieldCount = field.getSubFields().size();
		lstSubFields.setSelectedIndex(selIndices[0] == subFieldCount ? subFieldCount - 1 : selIndices[0]);
		lstSubFields.updateUI();
	}

	private void addItem() {
		ItemFrame form = new ItemFrame(this, this);
		form.setTitle("Add Item");
		form.showItemFrame(FieldFrameOperation.ADD_ITEM);
	}

	private void insertItem() {
		ItemFrame form = new ItemFrame(this, this);
		form.setTitle("Insert Item");
		form.showItemFrame(FieldFrameOperation.INSERT_ITEM);
	}

	private void removeItem() {
		ANSubField selectedSubField = getSelectedSubField();
		int selCount = lstItems.getSelectedIndices().length;
		int[] selIndices = lstItems.getSelectedIndices();
		Arrays.sort(selIndices);

		for (int i = selCount - 1; i >= 0; i--) {
			int index = selIndices[i];
			selectedSubField.getItems().remove(index);
			vectorItems.remove(index);
		}

		lstItems.updateUI();
		updateSelectedSubField();
		lstItems.clearSelection();

		int itemCount = selectedSubField.getItems().size();
		lstItems.setSelectedIndex(selIndices[0] == itemCount ? itemCount - 1 : selIndices[0]);
		lstItems.updateUI();
	}

	// ==============================================
	// Public methods
	// ==============================================

	public ANField getField() {
		return field;
	}

	public void setField(ANField field) {
		this.field = field;
		onFieldChanged();
	}

	public boolean isModified() {
		return isModified;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
		onIsReadOnlyChanged();
	}

	public void itemChanged(FieldFrameOperation operation, String value) {
		ANSubField selectedSubField = getSelectedSubField();
		int index;
		int currentIndex;
		switch (operation) {
		case ADD_ITEM:
			if (selectedSubField != null) {
				selectedSubField.getItems().addEx(value);
				vectorItems.add(value);
				lstItems.updateUI();
				updateSelectedSubField();
				lstItems.clearSelection();
				index = selectedSubField.getItems().size() - 1;
				lstItems.setSelectedIndex(index);
			}
			break;

		case ADD_SUB_FIELD:
			field.getSubFields().add(value);
			vectorSubFields.add("");
			lstSubFields.updateUI();
			index = field.getSubFields().size() - 1;
			updateSubField(index);
			lstSubFields.clearSelection();
			lstSubFields.setSelectedIndex(index);
			break;

		case EDIT_FIELD:
			field.setValue(value);
			updateSelectedSubField();
			currentIndex = lstSubFields.getSelectedIndex();
			lstSubFields.clearSelection();
			lstSubFields.setSelectedIndex(currentIndex);
			break;

		case EDIT_ITEM:
			if (selectedSubField != null) {
				int selectedItemIndex = getSelectedItemIndex();
				selectedSubField.getItems().set(selectedItemIndex, value);
				vectorItems.set(selectedItemIndex, value);
				lstItems.updateUI();
				updateSelectedSubField();
				lstItems.clearSelection();
				lstItems.setSelectedIndex(selectedItemIndex);
			}
			break;

		case EDIT_SUB_FIELD:
			selectedSubField.setValue(value);
			updateSelectedSubField();
			currentIndex = lstSubFields.getSelectedIndex();
			lstSubFields.clearSelection();
			lstSubFields.setSelectedIndex(currentIndex);
			break;

		case INSERT_ITEM:
			if (selectedSubField != null) {
				index = getSelectedItemIndex();
				selectedSubField.getItems().add(index, value);
				vectorItems.add(index, value);
				lstItems.updateUI();
				updateSelectedSubField();
				lstItems.clearSelection();
				lstItems.setSelectedIndex(index);
			}
			break;

		case INSERT_SUB_FIELD:
			index = lstSubFields.getSelectedIndex();
			field.getSubFields().add(index, value);
			vectorSubFields.add(index, "");
			lstSubFields.updateUI();
			updateSubField(index);
			lstSubFields.clearSelection();
			lstSubFields.setSelectedIndex(index);
			break;

		default:
			throw new AssertionError("Cannot happen");
		}
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnEdit) {
			editField();
		} else if (source == btnSubFieldsAdd) {
			addSubField();
		} else if (source == btnSubFieldsInsert) {
			insertSubField();
		} else if (source == btnSubFieldsRemove) {
			removeSubField();
		} else if (source == btnSubFieldsEdit) {
			editSubField();
		} else if (source == btnItemsAdd) {
			addItem();
		} else if (source == btnItemsInsert) {
			insertItem();
		} else if (source == btnItemsRemove) {
			removeItem();
		} else if (source == btnItemsEdit) {
			editItem();
		} else if (source == btnClose) {
			dispose();
		}
	}

}
