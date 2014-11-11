package com.neurotec.samples.biometrics.standards;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.neurotec.biometrics.standards.ANBinaryImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANField;
import com.neurotec.biometrics.standards.ANImageASCIIBinaryRecord;
import com.neurotec.biometrics.standards.ANImageBinaryRecord;
import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANRecordDataType;
import com.neurotec.biometrics.standards.ANRecordType;
import com.neurotec.biometrics.standards.ANSignatureRepresentationType;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANType3Record;
import com.neurotec.biometrics.standards.ANType4Record;
import com.neurotec.biometrics.standards.ANType5Record;
import com.neurotec.biometrics.standards.ANType6Record;
import com.neurotec.biometrics.standards.ANType8Record;
import com.neurotec.biometrics.standards.ANType9Record;
import com.neurotec.biometrics.standards.ANValidationLevel;
import com.neurotec.biometrics.standards.swing.ANView;
import com.neurotec.images.NImage;
import com.neurotec.images.NImageFormat;
import com.neurotec.images.NImages;
import com.neurotec.io.NFile;
import com.neurotec.lang.NCore;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;
import com.neurotec.samples.biometrics.standards.swing.ANRecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType10RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType13RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType14RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType15RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType16RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType17RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType1RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType3RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType4RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType5RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType6RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType7RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType8RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType99RecordCreationFrame;
import com.neurotec.samples.biometrics.standards.swing.ANType9RecordCreationFrame;
import com.neurotec.samples.swing.NPropertyGrid;
import com.neurotec.samples.util.LicenseManager;
import com.neurotec.samples.util.Utils;
import com.neurotec.swing.AboutBox;
import com.neurotec.util.NVersion;

public final class MainFrame extends JFrame implements ActionListener, MainFrameEventListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	private static final String APPLICATION_NAME = "ANSI/NIST File Editor";
	private static final String TEMPLATE_FILTER_NAME = "ANSI/NIST Files";
	private static final String TEMPLATE_FILTER = "*.an;*.an2;*.eft;*.lff;*.lffs;*.int;*.nist;*.fiif";
	private static final String TEMPLATE_FILTER_STRING = TEMPLATE_FILTER_NAME + " (" + TEMPLATE_FILTER + ")|" + TEMPLATE_FILTER;
	private static final String TEMPLATE_DEFAULT_EXT = "an2";
	private static final String ALL_FILES_FILTER_STRING = "All Files (*.*)|*.*";
	private static final String TEMPLATE_OPEN_FILE_FILTER_STRING = TEMPLATE_FILTER_STRING + "|" + ALL_FILES_FILTER_STRING;
	private static final String TEMPLATE_SAVE_FILE_FILTER_STRING = TEMPLATE_FILTER_STRING;
	private static final String NTEMPLATE_SAVE_FILE_FILTER_STRING = "NTemplate Files (*.dat)|*.dat|All Files (*.*)|*.*";
	private static final String NFRECORD_SAVE_FILE_FILTER_STRING = "NFRecord Files (*.dat)|*.dat|All Files (*.*)|*.*";

	// ==============================================
	// Private fields
	// ==============================================

	private ANTemplate template;
	private File openedFile;
	private boolean isModified;
	private int templateIndex;
	private String name;

	private RecordTableModel recordTableModel;
	private FieldsTableModel fieldsTableModel;

	private ANRecordType createdRecordType;
	private final ANTemplateSettings settings = ANTemplateSettings.getInstance();

	// ==============================================
	// Private GUI components
	// ==============================================

	private JMenuItem menuItemNew;
	private JMenuItem menuItemOpen;
	private JMenuItem menuItemClose;
	private JMenuItem menuItemSave;
	private JMenuItem menuItemSaveAs;
	private JMenuItem menuItemSaveAsNTemplate;
	private JMenuItem menuItemChangeVersion;
	private JMenuItem menuItemExit;

	private JMenuItem menuItemAddType2;
	private JMenuItem menuItemAddType3;
	private JMenuItem menuItemAddType4;
	private JMenuItem menuItemAddType5;
	private JMenuItem menuItemAddType6;
	private JMenuItem menuItemAddType7;
	private JMenuItem menuItemAddType8;
	private JMenuItem menuItemAddType9;
	private JMenuItem menuItemAddType10;
	private JMenuItem menuItemAddType13;
	private JMenuItem menuItemAddType14;
	private JMenuItem menuItemAddType15;
	private JMenuItem menuItemAddType16;
	private JMenuItem menuItemAddType17;
	private JMenuItem menuItemAddType99;

	private JMenuItem menuItemAddNotValidated;
	private JMenuItem menuItemRemoveRecords;
	private JMenuItem menuItemClear;
	private JMenuItem menuItemSaveRecordData;
	private JMenuItem menuItemSaveImage;
	private JMenuItem menuItemSaveAsNFRecord;
	private JMenuItem menuItemAddField;
	private JMenuItem menuItemEditField;
	private JMenuItem menuItemRemoveFields;

	private JMenuItem menuItemVersions;
	private JMenuItem menuItemRecordTypes;
	private JMenuItem menuItemCharsets;
	private JMenuItem menuItemValidate;

	private JMenuItem menuItemAbout;

	private JButton btnNew;
	private JButton btnOpen;
	private JButton btnSave;
	private JButton btnAdd;
	private JButton btnRemoveRecords;
	private JButton btnAddField;
	private JButton btnEditField;
	private JButton btnRemoveFields;

	private JMenuItem popupMenuItemAddType2;
	private JMenuItem popupMenuItemAddType3;
	private JMenuItem popupMenuItemAddType4;
	private JMenuItem popupMenuItemAddType5;
	private JMenuItem popupMenuItemAddType6;
	private JMenuItem popupMenuItemAddType7;
	private JMenuItem popupMenuItemAddType8;
	private JMenuItem popupMenuItemAddType9;
	private JMenuItem popupMenuItemAddType10;
	private JMenuItem popupMenuItemAddType13;
	private JMenuItem popupMenuItemAddType14;
	private JMenuItem popupMenuItemAddType15;
	private JMenuItem popupMenuItemAddType16;
	private JMenuItem popupMenuItemAddType17;
	private JMenuItem popupMenuItemAddType99;
	private JMenuItem popupMenuItemAddNotValidated;

	private JPopupMenu addPopup;

	private JFileChooser openFileDialog;
	private JFileChooser saveFileDialog;
	private JFileChooser folderBrowserDialog;
	private JFileChooser imageSaveFileDialog;
	private JFileChooser recordDataSaveFileDialog;
	private JFileChooser nTemplateSaveFileDialog;
	private JFileChooser nfRecordSaveFileDialog;

	private JTabbedPane tabbedPane;
	private JTable recordTable;
	private JTable fieldsTable;
	private JLabel lblNoProperties;
	private JMenu addMenu;
	private NPropertyGrid propertyGrid;
	private ANView anView;
	private JPanel highLevelPanel;
	private CardLayout highLevelCardLayout;

	// ==============================================
	// Public constructor
	// ==============================================

	public MainFrame() {
		super();
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
		}

		initializeComponents();
		loadFileChoosers();

		setIconImage(Utils.createIconImage("images/Logo16x16.png"));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				settings.save();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				closeSample();
			}
		});

		try {
			menuItemAbout.setText(AboutBox.getName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		onRecordsChanged();
		updateTitle();

	}

	// ==============================================
	// Private methods
	// ==============================================

	private void loadFileChoosers() {
		openFileDialog = new JFileChooser();
		saveFileDialog = new JFileChooser();
		folderBrowserDialog = new JFileChooser();
		imageSaveFileDialog = new JFileChooser();
		recordDataSaveFileDialog = new JFileChooser();
		nTemplateSaveFileDialog = new JFileChooser();
		nfRecordSaveFileDialog = new JFileChooser();

		setFileFilters(openFileDialog, TEMPLATE_OPEN_FILE_FILTER_STRING);
		setFileFilters(saveFileDialog, TEMPLATE_SAVE_FILE_FILTER_STRING);
		setFileFilters(nTemplateSaveFileDialog, NTEMPLATE_SAVE_FILE_FILTER_STRING);
		setFileFilters(nfRecordSaveFileDialog, NFRECORD_SAVE_FILE_FILTER_STRING);
		saveFileDialog.setAcceptAllFileFilterUsed(false);
		folderBrowserDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		folderBrowserDialog.setAcceptAllFileFilterUsed(false);

		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImages.getSaveFileFilterString()));
	}

	private void setFileFilters(JFileChooser fileChooser, String filterString) {

		StringTokenizer filterStringToknizer = new StringTokenizer(filterString, "|");

		while (filterStringToknizer.hasMoreTokens()) {
			final String filterName = filterStringToknizer.nextToken();
			String filterValues = filterStringToknizer.nextToken();
			StringTokenizer valueTokenizer = new StringTokenizer(filterValues, ";");

			final List<String> values = new ArrayList<String>();
			while (valueTokenizer.hasMoreTokens()) {
				String value = valueTokenizer.nextToken().replaceAll("\\*", "").replaceAll("\\.", "");
				if (!value.isEmpty()) {
					values.add(value);
				}
			}

			if (values.size() > 0) {
				javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {

					@Override
					public String getDescription() {
						return filterName;
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}

						String path = f.getAbsolutePath().toLowerCase();
						for (String extension : values) {
							if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
								return true;
							}
						}
						return false;
					}
				};
				fileChooser.addChoosableFileFilter(filter);
				fileChooser.setFileFilter(filter);
			}
		}

	}

	private void initializeComponents() {
		createMenuBar();

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		leftSplitPane.setLeftComponent(createRecordsPanel());
		leftSplitPane.setRightComponent(createFieldsTabbedPane());
		leftSplitPane.setDividerLocation(155);

		anView = new ANView();
		JScrollPane imageScrollPane = new JScrollPane(anView);

		mainSplitPane.setLeftComponent(leftSplitPane);
		mainSplitPane.setRightComponent(imageScrollPane);
		mainSplitPane.setDividerLocation(330);

		contentPane.add(createToolBar(), BorderLayout.BEFORE_FIRST_LINE);
		contentPane.add(mainSplitPane, BorderLayout.CENTER);
		pack();
	}

	private JScrollPane createRecordsPanel() {
		recordTableModel = new RecordTableModel();
		recordTable = new JTable(recordTableModel);
		recordTable.getTableHeader().setReorderingAllowed(false);
		recordTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedRecordChanged();
			}
		});

		JScrollPane scrollRecordTable = new JScrollPane(recordTable);
		recordTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		recordTable.getColumnModel().getColumn(1).setPreferredWidth(170);
		recordTable.getColumnModel().getColumn(2).setPreferredWidth(50);

		TableColumn recordColumn = recordTable.getColumnModel().getColumn(3);
		recordTable.getColumnModel().removeColumn(recordColumn);

		return scrollRecordTable;
	}

	private JTabbedPane createFieldsTabbedPane() {
		tabbedPane = new JTabbedPane();

		highLevelCardLayout = new CardLayout();
		highLevelPanel = new JPanel(highLevelCardLayout);

		lblNoProperties = new JLabel("No properties are available");
		JPanel noPropertiesPanel = new JPanel();
		noPropertiesPanel.setBackground(Color.WHITE);
		noPropertiesPanel.setOpaque(true);
		noPropertiesPanel.add(lblNoProperties);

		propertyGrid = new NPropertyGrid(true, new ANTemplatePropertiesTable());
		highLevelPanel.add(noPropertiesPanel, "label");
		highLevelPanel.add(propertyGrid, "propertyGrid");
		highLevelCardLayout.show(highLevelPanel, "label");

		tabbedPane.addTab("High level", highLevelPanel);
		tabbedPane.addTab("Low level", createFieldsPanel());
		return tabbedPane;

	}

	private JScrollPane createFieldsPanel() {
		fieldsTableModel = new FieldsTableModel();
		fieldsTable = new JTable(fieldsTableModel);
		fieldsTable.getTableHeader().setReorderingAllowed(false);
		fieldsTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (getSelectedField() != null) {
						editField();
					}
				}
			}
		});

		fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				onSelectedFieldChanged();
			}
		});

		fieldsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				Boolean readOnly = (Boolean) table.getModel().getValueAt(row, 4);
				c.setEnabled(!readOnly);
				return c;
			}

		});

		fieldsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		fieldsTable.getColumnModel().getColumn(1).setPreferredWidth(170);
		fieldsTable.getColumnModel().getColumn(2).setPreferredWidth(120);

		TableColumn fieldColumn = fieldsTable.getColumnModel().getColumn(3);
		TableColumn readOnlyColumn = fieldsTable.getColumnModel().getColumn(4);
		fieldsTable.getColumnModel().removeColumn(fieldColumn);
		fieldsTable.getColumnModel().removeColumn(readOnlyColumn);

		JScrollPane scrollFieldsTable = new JScrollPane(fieldsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return scrollFieldsTable;
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu helpMenu = new JMenu("Help");
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(this);
		helpMenu.add(menuItemAbout);

		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createToolsMenu());
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	private JMenu createToolsMenu() {
		JMenu toolsMenu = new JMenu("Tools");

		menuItemVersions = new JMenuItem("Versions");
		menuItemVersions.addActionListener(this);

		menuItemRecordTypes = new JMenuItem("Record types");
		menuItemRecordTypes.addActionListener(this);

		menuItemCharsets = new JMenuItem("Charsets");
		menuItemCharsets.addActionListener(this);

		menuItemValidate = new JMenuItem("Validate");
		menuItemValidate.addActionListener(this);

		toolsMenu.add(menuItemVersions);
		toolsMenu.add(menuItemRecordTypes);
		toolsMenu.add(menuItemCharsets);
		toolsMenu.addSeparator();
		toolsMenu.add(menuItemValidate);
		return toolsMenu;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");

		menuItemNew = new JMenuItem("New");
		menuItemNew.addActionListener(this);
		menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menuItemNew.setMnemonic(KeyEvent.VK_N);

		menuItemOpen = new JMenuItem("Open...");
		menuItemOpen.addActionListener(this);
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menuItemOpen.setMnemonic(KeyEvent.VK_O);

		menuItemClose = new JMenuItem("Close");
		menuItemClose.addActionListener(this);

		menuItemSave = new JMenuItem("Save");
		menuItemSave.addActionListener(this);
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		menuItemSave.setMnemonic(KeyEvent.VK_S);

		menuItemSaveAs = new JMenuItem("Save as...");
		menuItemSaveAs.addActionListener(this);

		menuItemSaveAsNTemplate = new JMenuItem("Save as NTemplate");
		menuItemSaveAsNTemplate.addActionListener(this);

		menuItemChangeVersion = new JMenuItem("Change version");
		menuItemChangeVersion.addActionListener(this);

		menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);

		fileMenu.add(menuItemNew);
		fileMenu.add(menuItemOpen);
		fileMenu.add(menuItemClose);
		fileMenu.add(menuItemSave);
		fileMenu.add(menuItemSaveAs);
		fileMenu.addSeparator();
		fileMenu.add(menuItemSaveAsNTemplate);
		fileMenu.addSeparator();
		fileMenu.add(menuItemChangeVersion);
		fileMenu.addSeparator();
		fileMenu.add(menuItemExit);
		return fileMenu;
	}

	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu("Edit");

		menuItemRemoveRecords = new JMenuItem("Remove record(s)");
		menuItemRemoveRecords.addActionListener(this);

		menuItemClear = new JMenuItem("Clear records");
		menuItemClear.addActionListener(this);

		menuItemSaveRecordData = new JMenuItem("Save record data...");
		menuItemSaveRecordData.addActionListener(this);

		menuItemSaveImage = new JMenuItem("Save image...");
		menuItemSaveImage.addActionListener(this);

		menuItemSaveAsNFRecord = new JMenuItem("Save as NFRecord...");
		menuItemSaveAsNFRecord.addActionListener(this);

		menuItemAddField = new JMenuItem("Add field...");
		menuItemAddField.addActionListener(this);

		menuItemEditField = new JMenuItem("Edit field");
		menuItemEditField.addActionListener(this);

		menuItemRemoveFields = new JMenuItem("Remove field(s)");
		menuItemRemoveFields.addActionListener(this);

		editMenu.add(createAddMenu());
		editMenu.add(menuItemRemoveRecords);
		editMenu.add(menuItemClear);
		editMenu.addSeparator();
		editMenu.add(menuItemSaveRecordData);
		editMenu.add(menuItemSaveImage);
		editMenu.add(menuItemSaveAsNFRecord);
		editMenu.addSeparator();
		editMenu.add(menuItemAddField);
		editMenu.add(menuItemEditField);
		editMenu.add(menuItemRemoveFields);
		return editMenu;
	}

	private JMenu createAddMenu() {
		addMenu = new JMenu("Add");

		menuItemAddType2 = new JMenuItem("Add type-2 record...");
		menuItemAddType2.addActionListener(this);

		menuItemAddType3 = new JMenuItem("Add type-3 record...");
		menuItemAddType3.addActionListener(this);

		menuItemAddType4 = new JMenuItem("Add type-4 record...");
		menuItemAddType4.addActionListener(this);

		menuItemAddType5 = new JMenuItem("Add type-5 record...");
		menuItemAddType5.addActionListener(this);

		menuItemAddType6 = new JMenuItem("Add type-6 record...");
		menuItemAddType6.addActionListener(this);

		menuItemAddType7 = new JMenuItem("Add type-7 record...");
		menuItemAddType7.addActionListener(this);

		menuItemAddType8 = new JMenuItem("Add type-8 record...");
		menuItemAddType8.addActionListener(this);

		menuItemAddType9 = new JMenuItem("Add type-9 record...");
		menuItemAddType9.addActionListener(this);

		menuItemAddType10 = new JMenuItem("Add type-10 record...");
		menuItemAddType10.addActionListener(this);

		menuItemAddType13 = new JMenuItem("Add type-13 record...");
		menuItemAddType13.addActionListener(this);

		menuItemAddType14 = new JMenuItem("Add type-14 record...");
		menuItemAddType14.addActionListener(this);

		menuItemAddType15 = new JMenuItem("Add type-15 record...");
		menuItemAddType15.addActionListener(this);

		menuItemAddType16 = new JMenuItem("Add type-16 record...");
		menuItemAddType16.addActionListener(this);

		menuItemAddType17 = new JMenuItem("Add type-17 record...");
		menuItemAddType17.addActionListener(this);

		menuItemAddType99 = new JMenuItem("Add type-99 record...");
		menuItemAddType99.addActionListener(this);

		menuItemAddNotValidated = new JMenuItem("Add record (not validated)");
		menuItemAddNotValidated.addActionListener(this);

		addMenu.add(menuItemAddType2);
		addMenu.add(menuItemAddType3);
		addMenu.add(menuItemAddType4);
		addMenu.add(menuItemAddType5);
		addMenu.add(menuItemAddType6);
		addMenu.add(menuItemAddType7);
		addMenu.add(menuItemAddType8);
		addMenu.add(menuItemAddType9);
		addMenu.add(menuItemAddType10);
		addMenu.add(menuItemAddType13);
		addMenu.add(menuItemAddType14);
		addMenu.add(menuItemAddType15);
		addMenu.add(menuItemAddType16);
		addMenu.add(menuItemAddType17);
		addMenu.add(menuItemAddType99);
		addMenu.addSeparator();
		addMenu.add(menuItemAddNotValidated);
		return addMenu;
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		btnNew = createToolbarIconButton(Utils.createIcon("images/New.png"), "New");
		btnOpen = createToolbarIconButton(Utils.createIcon("images/OpenFolder.png"), "Open");
		btnSave = createToolbarIconButton(Utils.createIcon("images/Save.png"), "Save");
		btnAdd = createToolbarTextButton("Add", new Dimension(40, 20));
		btnRemoveRecords = createToolbarTextButton("Remove record(s)", new Dimension(105, 20));
		btnAddField = createToolbarTextButton("Add field...", new Dimension(70, 20));
		btnEditField = createToolbarTextButton("Edit field", new Dimension(60, 20));
		btnRemoveFields = createToolbarTextButton("Remove field(s)", new Dimension(95, 20));

		createAddPopup();

		toolBar.add(btnNew);
		toolBar.add(btnOpen);
		toolBar.add(btnSave);
		toolBar.addSeparator();
		toolBar.add(btnAdd);
		toolBar.add(btnRemoveRecords);
		toolBar.addSeparator();
		toolBar.add(btnAddField);
		toolBar.add(btnEditField);
		toolBar.add(btnRemoveFields);
		return toolBar;

	}

	private JButton createToolbarIconButton(Icon icon, String toolTipText) {
		JButton button = new JButton(icon);
		button.setPreferredSize(new Dimension(20, 20));
		button.setMaximumSize(new Dimension(20, 20));
		button.setMinimumSize(new Dimension(20, 20));
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFocusPainted(false);
		return button;
	}

	private JButton createToolbarTextButton(String text, Dimension size) {
		JButton button = new JButton(text);
		button.setPreferredSize(size);
		button.setMaximumSize(size);
		button.setMinimumSize(size);
		button.setToolTipText(text);
		button.addActionListener(this);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setFocusPainted(false);
		return button;
	}

	private void createAddPopup() {
		addPopup = new JPopupMenu();

		popupMenuItemAddType2 = new JMenuItem("Add type-2 record...");
		popupMenuItemAddType2.addActionListener(this);

		popupMenuItemAddType3 = new JMenuItem("Add type-3 record...");
		popupMenuItemAddType3.addActionListener(this);

		popupMenuItemAddType4 = new JMenuItem("Add type-4 record...");
		popupMenuItemAddType4.addActionListener(this);

		popupMenuItemAddType5 = new JMenuItem("Add type-5 record...");
		popupMenuItemAddType5.addActionListener(this);

		popupMenuItemAddType6 = new JMenuItem("Add type-6 record...");
		popupMenuItemAddType6.addActionListener(this);

		popupMenuItemAddType7 = new JMenuItem("Add type-7 record...");
		popupMenuItemAddType7.addActionListener(this);

		popupMenuItemAddType8 = new JMenuItem("Add type-8 record...");
		popupMenuItemAddType8.addActionListener(this);

		popupMenuItemAddType9 = new JMenuItem("Add type-9 record...");
		popupMenuItemAddType9.addActionListener(this);

		popupMenuItemAddType10 = new JMenuItem("Add type-10 record...");
		popupMenuItemAddType10.addActionListener(this);

		popupMenuItemAddType13 = new JMenuItem("Add type-13 record...");
		popupMenuItemAddType13.addActionListener(this);

		popupMenuItemAddType14 = new JMenuItem("Add type-14 record...");
		popupMenuItemAddType14.addActionListener(this);

		popupMenuItemAddType15 = new JMenuItem("Add type-15 record...");
		popupMenuItemAddType15.addActionListener(this);

		popupMenuItemAddType16 = new JMenuItem("Add type-16 record...");
		popupMenuItemAddType16.addActionListener(this);

		popupMenuItemAddType17 = new JMenuItem("Add type-17 record...");
		popupMenuItemAddType17.addActionListener(this);

		popupMenuItemAddType99 = new JMenuItem("Add type-99 record...");
		popupMenuItemAddType99.addActionListener(this);

		popupMenuItemAddNotValidated = new JMenuItem("Add record (not validated)");
		popupMenuItemAddNotValidated.addActionListener(this);

		addPopup.add(popupMenuItemAddType2);
		addPopup.add(popupMenuItemAddType3);
		addPopup.add(popupMenuItemAddType4);
		addPopup.add(popupMenuItemAddType5);
		addPopup.add(popupMenuItemAddType6);
		addPopup.add(popupMenuItemAddType7);
		addPopup.add(popupMenuItemAddType8);
		addPopup.add(popupMenuItemAddType9);
		addPopup.add(popupMenuItemAddType10);
		addPopup.add(popupMenuItemAddType13);
		addPopup.add(popupMenuItemAddType14);
		addPopup.add(popupMenuItemAddType15);
		addPopup.add(popupMenuItemAddType16);
		addPopup.add(popupMenuItemAddType17);
		addPopup.add(popupMenuItemAddType99);
		addPopup.addSeparator();
		addPopup.add(popupMenuItemAddNotValidated);
	}

	private void removeRecords() {
		int selCount = recordTable.getSelectedRowCount();
		if (selCount > 0) {
			if (JOptionPane.showConfirmDialog(this, "Are you sure you want to remove selected records?", "Remove records?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				int[] selIndices = recordTable.getSelectedRows();
				Arrays.sort(selIndices);

				for (int i = selCount - 1; i >= 0; i--) {
					int index = selIndices[i];
					template.getRecords().remove(index);
					recordTableModel.removeRow(index);
				}

				int index = selIndices[0] == template.getRecords().size() ? template.getRecords().size() - 1 : selIndices[0];
				recordTable.setRowSelectionInterval(index, index);
				recordTable.updateUI();
				onTemplateModified();
			}
		}
	}

	private void clearRecords() {
		template.getRecords().clear();
		if (recordTable.getRowCount() > 0) {
			for (int i = recordTable.getRowCount() - 1; i > 0; i--) {
				recordTableModel.removeRow(i);
			}
		}

		if (fieldsTable.getSelectedRowCount() != 0) {
			onSelectedRecordChanged();
		} else {
			recordTable.setRowSelectionInterval(0, 0);
		}

		recordTable.updateUI();
		onTemplateModified();
	}

	private void addField() {
		NVersion version = template.getVersion();
		ANRecord selectedRecord = getSelectedRecord();
		ANRecordType selectedRecordType = selectedRecord.getRecordType();
		FieldNumberFrame form = new FieldNumberFrame(this, this);

		form.setTitle("Add field");
		form.setVersion(version);
		form.setRecordType(selectedRecordType);
		form.setValidationLevel(getRecordValidationLevel(selectedRecord.isValidated()));
		form.showFieldNumberDialog(true);
	}

	private void removeField() {
		try {
			ANRecord selectedRecord = getSelectedRecord();
			int selCount = fieldsTable.getSelectedRowCount();
			int[] selIndices = fieldsTable.getSelectedRows();
			Arrays.sort(selIndices);
			for (int i = selCount - 1; i >= 0; i--) {
				int index = selIndices[i];
				selectedRecord.getFields().remove(index);
				fieldsTableModel.removeRow(index);
			}
			int index = selIndices[0] == selectedRecord.getFields().size() ? selectedRecord.getFields().size() - 1 : selIndices[0];
			fieldsTable.setRowSelectionInterval(index, index);
		} finally {
			fieldsTable.updateUI();
		}
		onSelectedRecordModified();
	}

	private void saveRecordData() {
		ANRecord selectedRecord = getSelectedRecord();
		int selectedRecordType = selectedRecord.getRecordType().getNumber();
		String ext = null;
		if (selectedRecord.isValidated()) {
			if (selectedRecordType == 8) {
				ANType8Record type8Record = (ANType8Record) selectedRecord;
				ext = type8Record.getSignatureRepresentationType() == ANSignatureRepresentationType.SCANNED_UNCOMPRESSED ? "raw" : null;
			} else if (selectedRecordType == 5 || selectedRecordType == 6) {
				ANBinaryImageCompressionAlgorithm compressionAlgo;
				if (selectedRecordType == 5) {
					compressionAlgo = ((ANType5Record) selectedRecord).getCompressionAlgorithm();
				} else {
					compressionAlgo = ((ANType6Record) selectedRecord).getCompressionAlgorithm();
				}
				ext = compressionAlgo == ANBinaryImageCompressionAlgorithm.NONE ? "raw" : null;

			} else if (selectedRecordType == 3 || selectedRecordType == 4 || selectedRecord instanceof ANImageASCIIBinaryRecord) {
				ANImageCompressionAlgorithm compressionAlgo;
				if (selectedRecordType == 3) {
					compressionAlgo = ((ANType3Record) selectedRecord).getCompressionAlgorithm();
				} else if (selectedRecordType == 4) {
					compressionAlgo = ((ANType4Record) selectedRecord).getCompressionAlgorithm();
				} else {
					compressionAlgo = ((ANImageASCIIBinaryRecord) selectedRecord).getCompressionAlgorithm();
				}

				switch (compressionAlgo) {
				case NONE:
					ext = "raw";
					break;
				case WSQ_20:
					ext = "wsq";
					break;
				case JPEG_B:
				case JPEG_L:
					ext = "jpg";
					break;
				case JP2:
				case JP2_L:
					ext = "jp2";
					break;
				case PNG:
					ext = "png";
					break;
				case VENDOR:
					break;
				default:
					throw new AssertionError("Can not happen");
				}

			}
		}
		if (ext == null) {
			ext = "dat";
		}
		recordDataSaveFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		recordDataSaveFileDialog.setSelectedFile(new File("data." + ext));
		if (recordDataSaveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				settings.setLastDirectory(recordDataSaveFileDialog.getSelectedFile().getParentFile().getPath());
				NFile.writeAllBytes(recordDataSaveFileDialog.getSelectedFile().getPath(), selectedRecord.getData());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.toString());
			}
		}
	}

	private void saveImage() {
		ANRecord selectedRecord = getSelectedRecord();
		if (selectedRecord == null) {
			return;
		}

		int selectedRecordType = selectedRecord.getRecordType().getNumber();
		NImageFormat imageFormat = null;
		if (selectedRecordType == 8) {

			ANType8Record type8Record = (ANType8Record) selectedRecord;
			if (type8Record.getSignatureRepresentationType() == ANSignatureRepresentationType.SCANNED_UNCOMPRESSED) {
				imageFormat = NImageFormat.getTIFF();
			}

		} else if (selectedRecordType == 5 || selectedRecordType == 6) {

			ANBinaryImageCompressionAlgorithm compressionAlgo;
			if (selectedRecordType == 5) {
				compressionAlgo = ((ANType5Record) selectedRecord).getCompressionAlgorithm();
			} else {
				compressionAlgo = ((ANType6Record) selectedRecord).getCompressionAlgorithm();
			}
			imageFormat = compressionAlgo == ANBinaryImageCompressionAlgorithm.NONE ? NImageFormat.getTIFF() : null;

		} else if (selectedRecordType == 3 || selectedRecordType == 4 || selectedRecord instanceof ANImageASCIIBinaryRecord) {
			ANImageCompressionAlgorithm compressionAlgo;

			if (selectedRecordType == 3) {
				compressionAlgo = ((ANType3Record) selectedRecord).getCompressionAlgorithm();
			} else if (selectedRecordType == 4) {
				compressionAlgo = ((ANType4Record) selectedRecord).getCompressionAlgorithm();
			} else {
				compressionAlgo = ((ANImageASCIIBinaryRecord) selectedRecord).getCompressionAlgorithm();
			}

			switch (compressionAlgo) {
			case NONE:
				imageFormat = NImageFormat.getTIFF();
				break;
			case WSQ_20:
				imageFormat = NImageFormat.getWSQ();
				break;
			case JPEG_B:
			case JPEG_L:
				imageFormat = NImageFormat.getJPEG();
				break;
			case JP2:
			case JP2_L:
				imageFormat = NImageFormat.getJPEG2K();
				break;
			case PNG:
				imageFormat = NImageFormat.getPNG();
				break;
			case VENDOR:
				break;
			default:
				throw new AssertionError("Can not happen");
			}
		}

		if (imageFormat == null) {
			imageFormat = NImageFormat.getTIFF();
		}

		imageSaveFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		imageSaveFileDialog.setSelectedFile(null);
		if (imageSaveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

			try {
				settings.setLastDirectory(imageSaveFileDialog.getSelectedFile().getParentFile().getPath());
				String savePath = imageSaveFileDialog.getSelectedFile().getPath() + "." + imageFormat.getDefaultFileExtension();

				NImage image;
				if (selectedRecord instanceof ANImageBinaryRecord) {
					ANImageBinaryRecord imageBinaryRecord = (ANImageBinaryRecord) selectedRecord;
					image = imageBinaryRecord.toNImage();
				} else {
					ANImageASCIIBinaryRecord imageAsciiBinaryRecord = (ANImageASCIIBinaryRecord) selectedRecord;
					image = imageAsciiBinaryRecord.toNImage();
				}
				image.save(savePath);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, ex.toString(), "Can not save image", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveAsNFRecord() {
		ANType9Record type9Record = (ANType9Record) getSelectedRecord();
		nfRecordSaveFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		nfRecordSaveFileDialog.setSelectedFile(null);

		if (nfRecordSaveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				settings.setLastDirectory(nfRecordSaveFileDialog.getSelectedFile().getParentFile().getPath());
				String savePath = nfRecordSaveFileDialog.getSelectedFile().getPath();
				if (nfRecordSaveFileDialog.getFileFilter().getDescription().equals("NFRecord Files (*.dat)")) {
					if (savePath.lastIndexOf(".") == -1) {
						savePath = savePath + ".dat";
					}
				}
				NFile.writeAllBytes(savePath, type9Record.toNFRecord().save());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.toString(), "Can not save as NFRecord", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveAsNTemplate() {
		nTemplateSaveFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		nTemplateSaveFileDialog.setSelectedFile(null);

		if (nTemplateSaveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				settings.setLastDirectory(nTemplateSaveFileDialog.getSelectedFile().getParentFile().getPath());
				String savePath = nTemplateSaveFileDialog.getSelectedFile().getPath();
				if (nTemplateSaveFileDialog.getFileFilter().getDescription().equals("NTemplate Files (*.dat)")) {
					if (savePath.lastIndexOf(".") == -1) {
						savePath = savePath + ".dat";
					}
				}
				NFile.writeAllBytes(savePath, template.toNTemplate().save());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.toString(), "Can not save as NTemplate", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void changeVersion() {
		VersionFrame form = new VersionFrame(this, this);
		form.setTitle("Select Version");
		form.setSelectedVersion(template.getVersion());
		form.setLocationRelativeTo(this);
		form.setVisible(true);
	}

	private void versions() {
		VersionFrame form = new VersionFrame(this, this);
		form.setUseSelectMode(false);
		form.setTitle("Versions");
		form.setLocationRelativeTo(this);
		form.setVisible(true);
	}

	private void recordTypes() {
		RecordTypeFrame form = new RecordTypeFrame(this, this);
		form.setUseSelectMode(false);
		form.setTitle("Record Types");
		form.setLocationRelativeTo(this);
		form.setVisible(true);
	}

	private void charsets() {
		CharsetFrame form = new CharsetFrame(this);
		form.setUseSelectMode(false);
		form.setTitle("Charsets");
		form.setLocationRelativeTo(this);
		form.setVisible(true);
	}

	private void validateRecords() {

		folderBrowserDialog.setCurrentDirectory(new File(settings.getLastValidateDirectory()));
		if (folderBrowserDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			settings.setLastValidateDirectory(folderBrowserDialog.getSelectedFile().getPath());
			settings.save();
			OptionsFrame options = new OptionsFrame(this, this);
			options.showOptionsDialog(false);
		}
	}

	private void addType2Record() {
		ANRecordCreationFrame idcForm = new ANRecordCreationFrame(this, this);
		idcForm.showRecordCreationFrame(ANRecordCreationFrame.CREATE_RECORD_TYPE_2);
	}

	private void addType3Record() {
		addValidatedRecord(new ANType3RecordCreationFrame(this, this));
	}

	private void addType4Record() {
		addValidatedRecord(new ANType4RecordCreationFrame(this, this));
	}

	private void addType5Record() {
		addValidatedRecord(new ANType5RecordCreationFrame(this, this));
	}

	private void addType6Record() {
		addValidatedRecord(new ANType6RecordCreationFrame(this, this));
	}

	private void addType7Record() {
		addValidatedRecord(new ANType7RecordCreationFrame(this, this));
	}

	private void addType8Record() {
		addValidatedRecord(new ANType8RecordCreationFrame(this, this));
	}

	private void addType9Record() {
		addValidatedRecord(new ANType9RecordCreationFrame(this, this));
	}

	private void addType10Record() {
		addValidatedRecord(new ANType10RecordCreationFrame(this, this));
	}

	private void addType13Record() {
		addValidatedRecord(new ANType13RecordCreationFrame(this, this));
	}

	private void addType14Record() {
		addValidatedRecord(new ANType14RecordCreationFrame(this, this));
	}

	private void addType15Record() {
		addValidatedRecord(new ANType15RecordCreationFrame(this, this));
	}

	private void addType16Record() {
		addValidatedRecord(new ANType16RecordCreationFrame(this, this));
	}

	private void addType17Record() {
		addValidatedRecord(new ANType17RecordCreationFrame(this, this));
	}

	private void addType99Record() {
		addValidatedRecord(new ANType99RecordCreationFrame(this, this));
	}

	private void addRecord() {
		RecordTypeFrame form = new RecordTypeFrame(this, this);
		form.setLocationRelativeTo(this);
		form.setVisible(true);
	}

	private void onTemplateModified() {
		if (!isModified) {
			isModified = true;
			updateTitle();
		}
	}

	private void onSelectedFieldChanged() {
		ANField selectedField = getSelectedField();
		menuItemEditField.setEnabled(selectedField != null);
		btnEditField.setEnabled(selectedField != null);

		boolean canRemove = fieldsTable.getSelectedRowCount() != 0;
		NVersion version = template == null ? ANTemplate.VERSION_CURRENT : template.getVersion();
		ANRecord selectedRecord = getSelectedRecord();
		ANRecordType selectedRecordType = selectedRecord == null ? null : selectedRecord.getRecordType();
		ANValidationLevel validationLevel = getRecordValidationLevel(selectedRecord == null || selectedRecord.isValidated());

		for (int row : fieldsTable.getSelectedRows()) {
			ANField field = (ANField) fieldsTable.getModel().getValueAt(row, 3);
			int fieldNumber = field.getNumber();
			if (FieldNumberFrame.isFieldStandard(selectedRecordType, version, fieldNumber, validationLevel)) {
				canRemove = false;
				break;
			}
		}
		menuItemRemoveFields.setEnabled(canRemove);
		btnRemoveFields.setEnabled(canRemove);
	}

	private void onSelectedRecordModified() {
		if (!propertyGrid.hasFocus()) {
			propertyGrid.setSource(getSelectedRecord());
		}
		updateFieldListViewItem(0, (ANField) fieldsTableModel.getValueAt(0, 3));

		anView.invalidate();
		onTemplateModified();
	}

	private void onRecordsChanged() {
		recordTableModel.clearAllData();
		if (template != null) {
			for (ANRecord record : template.getRecords()) {
				addRecordListViewItem(record);
			}
			recordTable.setRowSelectionInterval(0, 0);
		} else {
			onSelectedRecordChanged();
		}

		recordTable.updateUI();
		boolean isTemplateNotNull = template != null;
		menuItemClose.setEnabled(isTemplateNotNull);
		menuItemSave.setEnabled(isTemplateNotNull);
		menuItemSaveAs.setEnabled(isTemplateNotNull);
		menuItemSaveAsNTemplate.setEnabled(isTemplateNotNull);
		menuItemChangeVersion.setEnabled(isTemplateNotNull);
		btnSave.setEnabled(isTemplateNotNull);
		addMenu.setEnabled(isTemplateNotNull);
		menuItemClear.setEnabled(isTemplateNotNull);
		btnAdd.setEnabled(isTemplateNotNull);

	}

	private void onSelectedRecordChanged() {
		ANRecord selectedRecord = getSelectedRecord();

		if (selectedRecord == null) {
			lblNoProperties.setText("No record is selected");
			highLevelCardLayout.show(highLevelPanel, "label");
		} else if (!selectedRecord.isValidated()) {
			lblNoProperties.setText("Selected record is not validated");
			highLevelCardLayout.show(highLevelPanel, "label");
		} else {
			propertyGrid.setSource(selectedRecord);
			highLevelCardLayout.show(highLevelPanel, "propertyGrid");
		}
		highLevelPanel.updateUI();

		fieldsTableModel.clearAllData();
		anView.setRecord(selectedRecord);
		if (selectedRecord != null) {
			for (ANField field : selectedRecord.getFields()) {
				fieldsTableModel.addRow(createFieldListViewItem(selectedRecord, field));
			}
			if (fieldsTable.getRowCount() > 0) {
				fieldsTable.setRowSelectionInterval(0, 0);
			}
		}
		fieldsTable.updateUI();

		int[] selected = recordTable.getSelectedRows();
		boolean enableRemove = selected.length > 0;
		for (int i : selected) {
			if (i == 0) {
				enableRemove = false;
				break;
			}
		}

		btnRemoveRecords.setEnabled(enableRemove);
		menuItemRemoveRecords.setEnabled(enableRemove);
		menuItemSaveRecordData.setEnabled(selectedRecord != null &&
				(selectedRecord.getRecordType().getDataType() == ANRecordDataType.BINARY ||
				selectedRecord.getRecordType().getDataType() == ANRecordDataType.ASCII_BINARY));
		menuItemSaveImage.setEnabled(selectedRecord != null && selectedRecord.isValidated() &&
				((selectedRecord instanceof ANImageBinaryRecord &&
						(selectedRecord.getRecordType().getNumber() != 8 ||
						((ANType8Record) selectedRecord).getSignatureRepresentationType() != ANSignatureRepresentationType.VECTOR_DATA)) ||
						selectedRecord instanceof ANImageASCIIBinaryRecord));
		menuItemSaveAsNFRecord.setEnabled(selectedRecord != null && selectedRecord.isValidated() &&
				selectedRecord.getRecordType().getNumber() == 9 &&
				((ANType9Record) selectedRecord).isHasMinutiae());
		boolean addFieldEnabled = selectedRecord != null && selectedRecord.getRecordType().getDataType() != ANRecordDataType.BINARY;
		btnAddField.setEnabled(addFieldEnabled);
		menuItemAddField.setEnabled(addFieldEnabled);
		onSelectedFieldChanged();
	}

	private Object[] addRecordListViewItem(ANRecord record) {
		if (record == null) {
			return null;
		}

		String type = String.format("Type-%s%s", record.getRecordType().getNumber(), record.isValidated() ? "" : "*");
		String recordName = record.getRecordType().getName();
		String idc = "";
		if (record.getRecordType() != ANRecordType.getType1()) {
			idc = String.valueOf(record.getIdc());
		}

		Object[] values = new Object[] { type, recordName, idc, record };
		recordTableModel.addRow(values);
		return values;
	}

	private ANRecord getSelectedRecord() {
		if (recordTable.getSelectedRowCount() == 1) {
			return (ANRecord) recordTable.getModel().getValueAt(recordTable.getSelectedRows()[0], 3);

		}
		return null;
	}

	private ANValidationLevel getRecordValidationLevel(boolean recordIsValidated) {
		return recordIsValidated && template != null ? template.getValidationLevel() : ANValidationLevel.MINIMAL;
	}

	private Object[] createFieldListViewItem(ANRecord record, ANField field) {
		NVersion version = template.getVersion();
		ANRecordType recordType = record.getRecordType();
		int fieldNumber = field.getNumber();

		boolean isReadOnly = FieldNumberFrame.isFieldStandard(recordType, version, fieldNumber, getRecordValidationLevel(record.isValidated()));
		boolean isKnown = recordType.isFieldKnown(version, fieldNumber);
		String id = isKnown ? recordType.getFieldId(version, fieldNumber) : "UNK";
		String fieldName = isKnown ? recordType.getFieldName(version, fieldNumber) : "Unknown field";
		if (id != "") {
			fieldName = String.format("%s (%s)", fieldName, id);
		}
		return new Object[] { field.getHeader(), fieldName, FieldFrame.getFieldValue(field), field, isReadOnly };
	}

	private void updateFieldListViewItem(int row, ANField field) {
		fieldsTableModel.setValueAt(FieldFrame.getFieldValue(field), row, 2);
	}

	private ANField getSelectedField() {
		if (fieldsTable.getSelectedRowCount() == 1) {
			return (ANField) fieldsTableModel.getValueAt(fieldsTable.getSelectedRows()[0], 3);

		}
		return null;
	}

	private void editField() {
		FieldFrame form = new FieldFrame(this);
		form.setField(getSelectedField());
		ANRecord selectedRecord = getSelectedRecord();
		form.setReadOnly(FieldNumberFrame.isFieldStandard(selectedRecord.getRecordType(), template.getVersion(), form.getField().getNumber(), getRecordValidationLevel(selectedRecord.isValidated())));
		form.setLocationRelativeTo(this);
		form.setVisible(true);
		if (form.isModified()) {
			updateFieldListViewItem(fieldsTable.getSelectedRows()[0], (ANField) fieldsTableModel.getValueAt(fieldsTable.getSelectedRows()[0], 3));
			onSelectedRecordModified();
		}
	}

	private void updateTitle() {
		if (name == null) {
			setTitle(APPLICATION_NAME);
		} else {
			String modifiedStatus = isModified ? "*" : "";
			setTitle(String.format("%s%s [V%s, VL: %s] - %s", name, modifiedStatus, template.getVersion(), template.getValidationLevel(), APPLICATION_NAME));
		}
	}

	private void setTemplate(ANTemplate value, File file) {
		boolean newTemplate = template != value;
		template = value;
		openedFile = file;
		if (newTemplate) {
			onRecordsChanged();
		}

		isModified = false;

		if (template == null) {
			name = null;
		} else if (file == null) {
			name = String.format("NewFile%s", ++templateIndex);
		} else {
			String fileName = file.getName();
			name = fileName.substring(0, fileName.lastIndexOf("."));
		}
		updateTitle();
	}

	private boolean newTemplate() {
		String tot = "NEUR";
		String dai = "NeurotecDest";
		String ori = "NeurotecOrig";
		String tcn = "00001";
		ANValidationLevel validation = settings.getNewValidationLevel();
		ANType1RecordCreationFrame type1CreateForm = new ANType1RecordCreationFrame(this, this);

		type1CreateForm.setTransactionType(tot);
		type1CreateForm.setDestinationAgency(dai);
		type1CreateForm.setOriginatingAgency(ori);
		type1CreateForm.setTransactionControl(tcn);
		type1CreateForm.setValidationLevel(validation);
		type1CreateForm.setUseNistMinutiaNeighbors(settings.isUseNistMinutiaNeighborsNew());
		type1CreateForm.setUseTwoDigitIdc(settings.isUseTwoDigitIdcNew());
		type1CreateForm.setUseTwoDigitFieldNumber(settings.isUseTwoDigitFieldNumberNew());
		type1CreateForm.setUseTwoDigitFieldNumberType1(settings.isUseTwoDigitFieldNumberType1New());

		type1CreateForm.showRecordCreationFrame(ANRecordCreationFrame.CREATE_RECORD_TYPE_1);
		return false;
	}

	private FileFilter[] getFilters() {
		List<FileFilter> filters = new ArrayList<FileFilter>();
		StringTokenizer filtersTokenizer = new StringTokenizer(TEMPLATE_FILTER, ";");
		while (filtersTokenizer.hasMoreTokens()) {
			String ext = filtersTokenizer.nextToken();
			final String extension = ext.substring(ext.lastIndexOf(".") + 1, ext.length());
			filters.add(new FileFilter() {

				public boolean accept(File pathname) {
					if (pathname.getName().endsWith(extension)) {
						return true;
					}
					return false;
				}
			});
		}
		return filters.toArray(new FileFilter[filters.size()]);
	}

	private boolean saveTemplate(File file) {
		try {
			template.save(file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.toString(), "Can not save ANSI/NIST file", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		setTemplate(template, file);
		return true;
	}

	private boolean fileSavePrompt() {
		if (isModified) {
			switch (JOptionPane.showConfirmDialog(this, "ANSI/NIST file modified. Save changes?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION)) {
			case JOptionPane.YES_OPTION:
				return fileSave();
			case JOptionPane.NO_OPTION:
				return true;
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:
				return false;
			default:
				throw new AssertionError("Cannot happen");
			}
		}
		return true;
	}

	private boolean fileNew() {
		if (fileSavePrompt()) {
			return newTemplate();
		}
		return false;
	}

	private void fileOpen() {
		if (fileSavePrompt()) {
			openFileDialog.setSelectedFile(null);
			openFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
			if (openFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				settings.setLastDirectory(openFileDialog.getSelectedFile().getParentFile().getPath());
				openedFile = openFileDialog.getSelectedFile();
				OptionsFrame options = new OptionsFrame(this, this);
				options.showOptionsDialog(true);
			}
		}
	}

	private void fileClose() {
		if (fileSavePrompt()) {
			setTemplate(null, null);
		}
	}

	private boolean fileSave() {
		return openedFile == null ? fileSaveAs() : saveTemplate(openedFile);
	}

	private boolean fileSaveAs() {
		File selectedFile = openedFile != null ? openedFile : new File(name);
		saveFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
		saveFileDialog.setSelectedFile(selectedFile);
		if (saveFileDialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return false;
		}
		settings.setLastDirectory(saveFileDialog.getSelectedFile().getParentFile().getPath());
		File newFile = new File(saveFileDialog.getSelectedFile() + "." + TEMPLATE_DEFAULT_EXT);
		return saveTemplate(newFile);
	}

	private void addValidatedRecord(ANRecordCreationFrame createForm) {
		createForm.setTemplate(template);
		createForm.showRecordCreationFrame(ANRecordCreationFrame.CREATE_VALIDATE_RECORD);
	}

	private void closeSample() {
		if (fileSavePrompt()) {
			try {
				MainFrame.this.dispose();
				LicenseManager.getInstance().releaseAll();
			} finally {
				NCore.shutdown();
			}
		}
	}

	// ===================================================
	// Public interface methods MainFrameEventHandler
	// ===================================================

	public void fieldNumberSelected(int fieldNumber) {
		ANRecord selectedRecord = getSelectedRecord();
		if (selectedRecord.getFields() != null && fieldNumber != -1) {
			if (selectedRecord.getFields().contains(fieldNumber)) {
				JOptionPane.showMessageDialog(this, "The record already contains a field with the same number", APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			} else {
				ANField field = selectedRecord.getFields().add(fieldNumber, "");
				int fieldIndex = selectedRecord.getFields().indexOf(field);
				fieldsTableModel.insertRow(fieldIndex, createFieldListViewItem(selectedRecord, field));
				fieldsTable.clearSelection();
				fieldsTable.setRowSelectionInterval(fieldIndex, fieldIndex);
				tabbedPane.setSelectedIndex(1);
				onSelectedRecordModified();
				editField();
			}
		}
	}

	public void versionChanged(NVersion selectedVersion) {
		if (selectedVersion.getValue() != template.getVersion().getValue()) {
			if (JOptionPane.showConfirmDialog(this, "Some information may be lost.", "Confirm", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				try {
					template.setVersion(selectedVersion);
					onSelectedRecordChanged();
					isModified = true;
					updateTitle();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this, ex.toString(), "Can not change ANSI/NIST file version", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public void recordTypeSelected(ANRecordType recordType) {
		createdRecordType = recordType;
		ANRecordCreationFrame idcForm = new ANRecordCreationFrame(this, this);
		idcForm.showRecordCreationFrame(ANRecordCreationFrame.CREATE_RECORD_TYPE_ANY);
	}

	public boolean newRecordCreated(int type, int idc, ANRecord createdRecord) {

		int index;
		switch (type) {
		case ANRecordCreationFrame.CREATE_RECORD_TYPE_ANY:
			recordTable.clearSelection();
			addRecordListViewItem(template.getRecords().add(createdRecordType, idc));
			index = recordTable.getRowCount() - 1;
			recordTable.setRowSelectionInterval(index, index);
			onTemplateModified();
			return true;

		case ANRecordCreationFrame.CREATE_RECORD_TYPE_2:
			recordTable.clearSelection();
			addRecordListViewItem(template.getRecords().addType2(idc));
			index = recordTable.getRowCount() - 1;
			recordTable.setRowSelectionInterval(index, index);
			onTemplateModified();
			return true;

		case ANRecordCreationFrame.CREATE_VALIDATE_RECORD:
			if (createdRecord == null) {
				return false;
			}
			recordTable.clearSelection();
			addRecordListViewItem(createdRecord);
			index = recordTable.getRowCount() - 1;
			recordTable.setRowSelectionInterval(index, index);
			onTemplateModified();
			return true;

		default:
			throw new AssertionError("Invalid record type");
		}

	}

	public boolean newType1RecordCreated(ANType1RecordCreationFrame type1CreateForm) {
		int flags = 0;
		String tot = "NEUR";
		String dai = "NeurotecDest";
		String ori = "NeurotecOrig";
		String tcn = "00001";
		ANValidationLevel validation = settings.getNewValidationLevel();

		validation = type1CreateForm.getValidationLevel();
		settings.setNewValidationLevel(validation);
		settings.setUseNistMinutiaNeighborsNew(type1CreateForm.isUseNistMinutiaNeighbors());
		settings.setUseTwoDigitIdcNew(type1CreateForm.isUseTwoDigitIdc());
		settings.setUseTwoDigitFieldNumberNew(type1CreateForm.isUseTwoDigitFieldNumber());
		settings.setUseTwoDigitFieldNumberType1New(type1CreateForm.isUseTwoDigitFieldNumberType1());
		settings.save();

		if (type1CreateForm.isUseNistMinutiaNeighbors()) {
			flags |= ANTemplate.FLAG_USE_NIST_MINUTIA_NEIGHBORS;
		}
		if (type1CreateForm.isUseTwoDigitIdc()) {
			flags |= ANTemplate.FLAG_USE_TWO_DIGIT_IDC;
		}
		if (type1CreateForm.isUseTwoDigitFieldNumber()) {
			flags |= ANTemplate.FLAG_USE_TWO_DIGIT_FIELD_NUMBER;
		}
		if (type1CreateForm.isUseTwoDigitFieldNumberType1()) {
			flags |= ANTemplate.FLAG_USE_TWO_DIGIT_FIELD_NUMBER_TYPE_1;
		}

		ANTemplate newTemplate;
		switch (validation) {
		case MINIMAL:
			newTemplate = new ANTemplate(flags);
			break;
		case STANDARD:
			tot = type1CreateForm.getTransactionType();
			dai = type1CreateForm.getDestinationAgency();
			ori = type1CreateForm.getOriginatingAgency();
			tcn = type1CreateForm.getTransactionControl();
			newTemplate = new ANTemplate(tot, dai, ori, tcn, flags);
			break;
		default:
			throw new AssertionError("This cannot happaen");
		}

		setTemplate(newTemplate, null);
		btnEditField.setEnabled(false);
		return true;
	}

	public void optionsSelected(boolean isTemplateOpenMode, ANValidationLevel validation, int flags) {
		if (isTemplateOpenMode) {
			try {
				ANTemplate newTemplate = new ANTemplate(openedFile.getPath(), validation, flags);
				setTemplate(newTemplate, openedFile);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, ex.toString(), "Can not open ANSI/NIST file", JOptionPane.ERROR_MESSAGE);
			}

		} else {
			ValidationFrame form = new ValidationFrame(this);
			form.setPath(folderBrowserDialog.getSelectedFile());
			form.setFilters(getFilters());
			form.setValidationLevel(validation);
			form.setFlags(flags);
			form.setLocationRelativeTo(this);
			form.setVisible(true);
		}
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnAdd) {
			addPopup.show(this, btnAdd.getLocation().x + 7, btnAdd.getLocation().y + 70);
		} else if (source == menuItemRemoveRecords || source == btnRemoveRecords) {
			removeRecords();
		} else if (source == menuItemClear) {
			clearRecords();
		} else if (source == menuItemAddField || source == btnAddField) {
			addField();
		} else if (source == menuItemEditField || source == btnEditField) {
			editField();
		} else if (source == menuItemRemoveFields || source == btnRemoveFields) {
			removeField();
		} else if (source == menuItemAbout) {
			AboutBox.show();
		} else if (source == menuItemSaveRecordData) {
			saveRecordData();
		} else if (source == menuItemSaveImage) {
			saveImage();
		} else if (source == menuItemSaveAsNFRecord) {
			saveAsNFRecord();
		} else if (source == menuItemSaveAsNTemplate) {
			saveAsNTemplate();
		} else if (source == menuItemNew || source == btnNew) {
			fileNew();
		} else if (source == menuItemOpen || source == btnOpen) {
			fileOpen();
		} else if (source == menuItemClose) {
			fileClose();
		} else if (source == menuItemSave || source == btnSave) {
			fileSave();
		} else if (source == menuItemSaveAs) {
			fileSaveAs();
		} else if (source == menuItemChangeVersion) {
			changeVersion();
		} else if (source == menuItemExit) {
			closeSample();
		} else if (source == menuItemVersions) {
			versions();
		} else if (source == menuItemRecordTypes) {
			recordTypes();
		} else if (source == menuItemCharsets) {
			charsets();
		} else if (source == menuItemValidate) {
			validateRecords();
		} else if (source == menuItemAddType2 || source == popupMenuItemAddType2) {
			addType2Record();
		} else if (source == menuItemAddType3 || source == popupMenuItemAddType3) {
			addType3Record();
		} else if (source == menuItemAddType4 || source == popupMenuItemAddType4) {
			addType4Record();
		} else if (source == menuItemAddType5 || source == popupMenuItemAddType5) {
			addType5Record();
		} else if (source == menuItemAddType6 || source == popupMenuItemAddType6) {
			addType6Record();
		} else if (source == menuItemAddType7 || source == popupMenuItemAddType7) {
			addType7Record();
		} else if (source == menuItemAddType8 || source == popupMenuItemAddType8) {
			addType8Record();
		} else if (source == menuItemAddType9 || source == popupMenuItemAddType9) {
			addType9Record();
		} else if (source == menuItemAddType10 || source == popupMenuItemAddType10) {
			addType10Record();
		} else if (source == menuItemAddType13 || source == popupMenuItemAddType13) {
			addType13Record();
		} else if (source == menuItemAddType14 || source == popupMenuItemAddType14) {
			addType14Record();
		} else if (source == menuItemAddType15 || source == popupMenuItemAddType15) {
			addType15Record();
		} else if (source == menuItemAddType16 || source == popupMenuItemAddType16) {
			addType16Record();
		} else if (source == menuItemAddType17 || source == popupMenuItemAddType17) {
			addType17Record();
		} else if (source == menuItemAddType99 || source == popupMenuItemAddType99) {
			addType99Record();
		} else if (source == menuItemAddNotValidated || source == popupMenuItemAddNotValidated) {
			addRecord();
		}

	}

	// =============================================================
	// Private class RecordTableModel model for records table
	// =============================================================

	private static final class RecordTableModel extends DefaultTableModel {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Private fields
		// ==============================================

		private String[] columnNames = { "Type", "Name", "Idc", "Record" };

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
			return false;
		}

	}

	// =============================================================
	// Private class FieldsTableModel model for fields table
	// =============================================================

	private static final class FieldsTableModel extends DefaultTableModel {

		// ==============================================
		// Private static fields
		// ==============================================

		private static final long serialVersionUID = 1L;

		// ==============================================
		// Private fields
		// ==============================================

		private String[] columnNames = { "Number", "Name", "Value", "Field", "ReadOnly" };

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
