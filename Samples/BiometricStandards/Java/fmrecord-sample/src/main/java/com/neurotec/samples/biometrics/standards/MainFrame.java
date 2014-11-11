package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFFPMinutiaNeighbor;
import com.neurotec.biometrics.standards.BDIFFPMinutiaType;
import com.neurotec.biometrics.standards.BDIFFPPosition;
import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.BDIFTypes;
import com.neurotec.biometrics.standards.FMRCore;
import com.neurotec.biometrics.standards.FMRDelta;
import com.neurotec.biometrics.standards.FMRFinger;
import com.neurotec.biometrics.standards.FMRFingerView;
import com.neurotec.biometrics.standards.FMRMinutia;
import com.neurotec.biometrics.standards.FMRecord;
import com.neurotec.biometrics.standards.swing.FMView;
import com.neurotec.biometrics.standards.swing.SelectedFMRCore;
import com.neurotec.biometrics.standards.swing.SelectedFMRDelta;
import com.neurotec.biometrics.standards.swing.SelectedFMRMinutia;
import com.neurotec.io.NBuffer;
import com.neurotec.io.NFile;
import com.neurotec.lang.NCore;
import com.neurotec.samples.biometrics.standards.BDIFOptionsFrame.BDIFOptionsFormMode;
import com.neurotec.samples.swing.NPropertyGrid;
import com.neurotec.samples.util.LicenseManager;
import com.neurotec.samples.util.Utils;
import com.neurotec.samples.util.Utils.TemplateFileFilter;
import com.neurotec.swing.AboutBox;
import com.neurotec.biometrics.swing.AddFeaturesTool;
import com.neurotec.biometrics.swing.FeatureAddEvent;
import com.neurotec.biometrics.swing.FeatureAddListener;
import com.neurotec.biometrics.swing.IndexSelectionListener;
import com.neurotec.biometrics.swing.PointerTool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;

public final class MainFrame extends JFrame implements ActionListener, TreeSelectionListener, IndexSelectionListener, FeatureAddListener {

	// ==============================================
	// Private types
	// ==============================================

	private static final class FingerTreeObject {

		// ==============================================
		// Private fields
		// ==============================================

		private final Object taggedObject;
		private String nodeText;

		// ==============================================
		// Public constructor
		// ==============================================

		FingerTreeObject(Object taggedObject, String nodeText) {
			super();
			this.taggedObject = taggedObject;
			this.nodeText = nodeText;
		}

		// ==============================================
		// Public getter
		// ==============================================

		public Object getTaggedObject() {
			return taggedObject;
		}

		// ==============================================
		// Overridden methods
		// ==============================================

		@Override
		public String toString() {
			return nodeText;
		}
	}

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JMenuItem menuItemNewFMRecord;
	private JMenuItem menuItemOpenFMRecord;
	private JMenuItem menuItemSaveFMRecord;
	private JMenuItem menuItemExit;
	private JMenuItem menuItemAddFinger;
	private JMenuItem menuItemAddFingerView;
	private JMenuItem menuItemRemoveFingerFingerView;
	private JMenuItem menuItemDeleteSelectedMinutia;
	private JMenuItem menuItemPointerTool;
	private JMenuItem menuItemAddFeatureTool;
	private JMenuItem menuItemConvert;
	private JMenuItem menuItemAbout;
	private DefaultMutableTreeNode fingerTreeRoot;
	private JTree fingerTree;
	private NPropertyGrid displayGrid;
	private JButton btnPointerTool;
	private JButton btnAddFeatureTool;
	private JButton btnDeleteFeature;
	private FMView fmView;
	private final JFileChooser fmRecordOpenFileDialog;
	private final JFileChooser fmRecordSaveFileDialog;

	// ==============================================
	// Private Fields
	// ==============================================

	private FMRecord currentTemplate;
	private String fileName;
	private boolean dialogResultOK;
	private FMRFinger selectedFinger;
	private FMRFingerView selectedFingerView;
	private BDIFStandard currentStandard = BDIFStandard.ANSI;

	private int currentFlags;
	private int selectedFeature;
	private BDIFFPMinutiaType selectedType;
	private final List<String> listofFmRecordProp = Arrays.asList("captureEquipmentCompliance",
																"captureEquipmentCompliance",
																"captureEquipmentId",
																"CBEFFProductId",
																"fingers",
																"fingerViews",
																"flags",
																"resolutionX",
																"resolutionY",
																"sizeX",
																"sizeY",
																"standard");

	private final List<String> listofFingerProp = Arrays.asList("position",
														"fingerViews",
														"flags",
														"owner");

	private final List<String> listofFingerViewProp = Arrays.asList("cores",
															"deltas",
															"position",
															"fingerQuality",
															"flags",
															"hasEightNeighborRidgeCounts",
															"hasFourNeighborRidgeCounts",
															"impressionType",
															"Minutiae",
															"MinutiaeEightNeighbors",
															"MinutiaeFourNeighbors",
															"owner",
															"viewCount",
															"viewNumber");

	private final List<String> listofFeatureProp = Arrays.asList("delta",
														"core",
														"minutia");

	public void setDialogResultOK(boolean dialogResultOK) {
		this.dialogResultOK = dialogResultOK;
	}

	// ==============================================
	// Public constructor
	// ==============================================

	public MainFrame() {
		super();
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initializeComponents();

		menuItemAbout.setText(AboutBox.getName());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					LicenseManager.getInstance().releaseAll();
				} finally {
					NCore.shutdown();
				}
			}

		});
		FileFilter filter = new TemplateFileFilter();
		fmRecordOpenFileDialog = new JFileChooser();
		fmRecordOpenFileDialog.addChoosableFileFilter(filter);
		fmRecordSaveFileDialog = new JFileChooser();
		fmRecordSaveFileDialog.addChoosableFileFilter(filter);
		fileName = "Untitled";

	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		// Initialize the MenuBar
		createMenuBar();

		// Construct the Layout
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		// Tree View
		fingerTreeRoot = new DefaultMutableTreeNode();
		fingerTree = new JTree(fingerTreeRoot);
		fingerTree.addTreeSelectionListener(this);
		fingerTree.setPreferredSize(new Dimension(250, 80));
		JScrollPane faceTreeScrollPane = new JScrollPane(fingerTree);

		// Property Grid
		displayGrid = new NPropertyGrid(true, new FNGPropertiesTable());
		displayGrid.setPreferredSize(new Dimension(250, 243));

		// / fmView
		fmView = new FMView();
		fmView.setBackground(SystemColor.control);
		fmView.setDrawFeatureArea(true);
		fmView.setFeatureAreaColor(Color.LIGHT_GRAY);

		// fmView.setLocation(new Point(100, 100));
		fmView.setMinutiaColor(Color.RED);
		fmView.setName("fmView");
		fmView.setNeighborMinutiaColor(Color.ORANGE);
		fmView.setSelectedMinutiaColor(Color.MAGENTA);
		fmView.setSingularPointColor(Color.RED);
		fmView.addIndexChangeListener(this);

		JScrollPane fmViewScrollPane = new JScrollPane(fmView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fmViewScrollPane.setPreferredSize(new Dimension(800, 800));

		mainSplitPane.setRightComponent(fmViewScrollPane);
		leftSplitPane.setDividerLocation(200);
		leftSplitPane.setLeftComponent(faceTreeScrollPane);

		leftSplitPane.setRightComponent(displayGrid);
		mainSplitPane.setLeftComponent(leftSplitPane);

		mainSplitPane.setDividerLocation(246);
		setSize(960, 580);

		// MainForm
		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		setIconImage(Utils.createIconImage("images/Logo16x16.png"));
		setTitle("FMRecord Editor");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2	- getSize().height / 2);
		pack();
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setLocation(0, 0);
		menuBar.setSize(944, 24);

		// File Menu
		JMenu menuFile = new JMenu("File");
		menuFile.setSize(37, 20);

		// New FMRecord
		menuItemNewFMRecord = new JMenuItem("New FMRecord");
		menuItemNewFMRecord.addActionListener(this);
		menuItemNewFMRecord.setSize(215, 22);
		menuItemNewFMRecord.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menuItemNewFMRecord.setMnemonic(KeyEvent.VK_N);

		// Open FMRecord
		menuItemOpenFMRecord = new JMenuItem("Open FMRecord");
		menuItemOpenFMRecord.addActionListener(this);
		menuItemOpenFMRecord.setSize(215, 22);
		menuItemOpenFMRecord.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menuItemOpenFMRecord.setMnemonic(KeyEvent.VK_O);

		// Save FMRecord
		menuItemSaveFMRecord = new JMenuItem("Save FMRecord");
		menuItemSaveFMRecord.addActionListener(this);
		menuItemSaveFMRecord.setSize(215, 22);
		menuItemSaveFMRecord.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		menuItemSaveFMRecord.setMnemonic(KeyEvent.VK_S);

		// Separator
		JSeparator menuItemSeperator1 = new JSeparator();
		menuItemSeperator1.setSize(212, 6);

		// Exit
		menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);
		menuItemExit.setSize(215, 22);

		// Edit Menu
		JMenu menuEdit = new JMenu("Edit");
		menuEdit.setSize(39, 20);

		// Add Finger
		menuItemAddFinger = new JMenuItem("Add finger...");
		menuItemAddFinger.addActionListener(this);
		menuItemAddFinger.setSize(298, 22);
		menuItemAddFinger.setMnemonic(KeyEvent.VK_A);

		// Add Finger View
		menuItemAddFingerView = new JMenuItem("Add finger view");
		menuItemAddFingerView.addActionListener(this);
		menuItemAddFingerView.setSize(298, 22);
		menuItemAddFingerView.setMnemonic(KeyEvent.VK_D);

		// Remove Finger/FingerView
		menuItemRemoveFingerFingerView = new JMenuItem("Remove finger/fingerView");
		menuItemRemoveFingerFingerView.addActionListener(this);
		menuItemRemoveFingerFingerView.setSize(298, 22);
		menuItemRemoveFingerFingerView.setMnemonic(KeyEvent.VK_R);

		// Delete Selected Minutia
		menuItemDeleteSelectedMinutia = new JMenuItem("Delete selected minutia/core/delta");
		menuItemDeleteSelectedMinutia.addActionListener(this);
		menuItemDeleteSelectedMinutia.setSize(298, 22);
		menuItemDeleteSelectedMinutia.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
		menuItemDeleteSelectedMinutia.setEnabled(false);

		// Separator
		JSeparator seperator2 = new JSeparator();
		seperator2.setSize(295, 6);

		// ActiveTool
		JMenu menuItemActiveTool = new JMenu("Active tool");
		menuItemActiveTool.setMnemonic(KeyEvent.VK_T);
		menuItemActiveTool.setSize(298, 22);

		// Pointer Tool
		menuItemPointerTool = new JMenuItem("Pointer tool");
		menuItemPointerTool.addActionListener(this);
		menuItemPointerTool.setSize(200, 22);
		menuItemPointerTool.setMnemonic(KeyEvent.VK_P);
		menuItemPointerTool.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
		// Add Feature Tool
		menuItemAddFeatureTool = new JMenuItem("Add feature tool");
		menuItemAddFeatureTool.addActionListener(this);
		menuItemAddFeatureTool.setSize(200, 22);
		menuItemAddFeatureTool.setMnemonic(KeyEvent.VK_F);
		menuItemAddFeatureTool.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));

		// Convert
		menuItemConvert = new JMenuItem("Convert...");
		menuItemConvert.addActionListener(this);
		menuItemConvert.setSize(298, 22);
		menuItemConvert.setMnemonic(KeyEvent.VK_C);

		// Help
		JMenu menuHelp = new JMenu("Help");
		menuHelp.setSize(44, 20);

		// About
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(this);
		menuItemAbout.setSize(107, 22);
		menuItemAbout.setMnemonic(KeyEvent.VK_A);

		menuFile.add(menuItemNewFMRecord);
		menuFile.add(menuItemOpenFMRecord);
		menuFile.add(menuItemSaveFMRecord);
		menuFile.add(menuItemSeperator1);
		menuFile.add(menuItemExit);

		menuEdit.add(menuItemAddFinger);
		menuEdit.add(menuItemAddFingerView);
		menuEdit.add(menuItemRemoveFingerFingerView);
		menuEdit.add(menuItemDeleteSelectedMinutia);
		menuEdit.add(seperator2);
		menuItemActiveTool.add(menuItemPointerTool);
		menuItemActiveTool.add(menuItemAddFeatureTool);
		menuEdit.add(menuItemActiveTool);
		menuEdit.add(menuItemConvert);

		menuHelp.add(menuItemAbout);

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuHelp);

		menuBar.add(new JSeparator());

		btnPointerTool = new JButton(Utils.createIcon("images/Pointer.png"));
		btnPointerTool.setToolTipText("Pointer Tool - Use it to move or rotate details");
		btnPointerTool.addActionListener(this);
		btnPointerTool.setSize(23, 20);
		menuBar.add(btnPointerTool);

		btnAddFeatureTool = new JButton(Utils.createIcon("images/AddFeature.png"));
		btnAddFeatureTool.setToolTipText("Add Feature tool - Add new minutiae, cores or deltas");
		btnAddFeatureTool.addActionListener(this);
		btnAddFeatureTool.setSize(23, 20);
		menuBar.add(btnAddFeatureTool);

		btnDeleteFeature = new JButton(Utils.createIcon("images/Delete.png"));
		ToolTipManager.sharedInstance().registerComponent(btnDeleteFeature);
		btnDeleteFeature.setToolTipText("Delete selected - delete unwanted minutiae, cores or deltas");
		btnDeleteFeature.addActionListener(this);
		btnDeleteFeature.setSize(23, 20);
		menuBar.add(btnDeleteFeature);

		setJMenuBar(menuBar);
	}

	private void getOptions(BDIFOptionsFrame.BDIFOptionsFormMode mode, BDIFStandard standard) {
		FMRecordOptionsForm form = new FMRecordOptionsForm(MainFrame.this, "FMRecordOptionsFrame", currentFlags);
		form.setMode(mode);
		form.setStandard(standard);
		showDialogOnScreen(form);
	}

	private void getFeature(AddFeaturesForm form) {
		showDialogOnScreen(form);
	}

	private static FMRecord convertToStandard(FMRecord record,
			BDIFStandard newStandard, int flags) {
		if (record.getStandard() == newStandard) {
			return record;
		}
		return new FMRecord(record, flags, newStandard);
	}

	private DefaultMutableTreeNode addFingerView(DefaultMutableTreeNode fingersNode, FMRFingerView item) {
		int index = fingersNode.getChildCount() + 1;
		DefaultMutableTreeNode fingerViewNode = new DefaultMutableTreeNode(new FingerTreeObject(item, "Finger view " + index));
		fingersNode.add(fingerViewNode);
		fingerTree.updateUI();
		expandFingerTree();
		return fingerViewNode;
	}

	private DefaultMutableTreeNode addFinger(DefaultMutableTreeNode fingersNode, FMRFinger finger) {
		for (FMRFingerView item : finger.getFingerViews()) {
			addFingerView(fingersNode, item);
		}
		return fingersNode;

	}

	private DefaultMutableTreeNode addFingers(DefaultMutableTreeNode templateNode, FMRecord record) {
		for (FMRFinger finger : record.getFingers()) {
			int index = templateNode.getChildCount() + 1;
			FingerTreeObject userObject = new FingerTreeObject(finger, "Finger " + index);
			DefaultMutableTreeNode fingerNode = new DefaultMutableTreeNode(userObject);
			addFinger(fingerNode, finger);
			templateNode.add(fingerNode);
		}
		return templateNode;
	}

	private void onDetailSelected() {
		if ((fmView.getSelectedCoreIndex() == fmView.getSelectedDeltaIndex()) && (fmView.getSelectedCoreIndex() == fmView.getSelectedMinutiaIndex())) {
			btnDeleteFeature.setEnabled(false);
			menuItemDeleteSelectedMinutia.setEnabled(false);
		} else {
			btnDeleteFeature.setEnabled(true);
			menuItemDeleteSelectedMinutia.setEnabled(true);
		}

		if (selectedFingerView != null) {
			FMRFingerView fingerView = selectedFingerView;
			displayGrid.setSupportedProperties(listofFeatureProp);
			int index = fmView.getSelectedDeltaIndex();
			if (index != -1) {
				displayGrid.setSource(new SelectedFMRDelta(fingerView, index));
				return;
			}

			index = fmView.getSelectedCoreIndex();
			if (index != -1) {
				displayGrid.setSource(new SelectedFMRCore(fingerView, index));
				return;
			}

			index = fmView.getSelectedMinutiaIndex();
			if (index != -1) {
				displayGrid
						.setSource(new SelectedFMRMinutia(fingerView, index));
				return;
			}

			displayGrid.setSupportedProperties(listofFingerViewProp);
			displayGrid.setSource(fingerView);
		}
	}

	private void onFeatureAdded(FeatureAddEvent eventArgs) {
		FMRFingerView fView = selectedFingerView;
		if (fView == null || eventArgs.getStart().equals(eventArgs.getEnd())) {
			return;
		}
		if (eventArgs.getStart().getX() < 0 || eventArgs.getStart().getY() < 0) {
			return;
		}
		int x = (int) eventArgs.getStart().getX();
		int y = (int) eventArgs.getStart().getY();
		if ((x >= currentTemplate.getSizeX()) || (y >= currentTemplate.getSizeY()) || (x < 0) || (y < 0)) {
			return;
		}

		AddFeaturesForm form = new AddFeaturesForm(MainFrame.this);
		dialogResultOK = false;
		getFeature(form);
		if (!dialogResultOK) {
			return;
		}

		int w = (int) (eventArgs.getEnd().getX() - eventArgs.getStart().getX());
		int h = (int) (eventArgs.getEnd().getY() - eventArgs.getStart().getY());

		// Angle should be inverted, since Y axis direction is from top to bottom.
		double angle = -1 * Math.atan((float) h / w);
		if (Double.isNaN(angle)) {
			return;
		}

		if (w < 0) {
			angle += Math.PI;
		}

		switch (selectedFeature) {
		case 0:
			FMRMinutia fm = new FMRMinutia((short) x, (short) y, selectedType, angle, currentTemplate.getStandard());
			fView.getMinutiae().add(fm);
			break;
		case 1:
			FMRCore fc = new FMRCore((short) x, (short) y, angle, currentTemplate.getStandard());
			angle = BDIFTypes.angleFromRadians(angle, currentTemplate.getStandard());
			fView.getCores().add(fc);
			break;
		case 2:
			fView.getDeltas().add(new FMRDelta((short) x, (short) y));
			break;
		default:
			throw new AssertionError("Invalid Input");
		}
	}

	private void onSelectedItemChanged() {
		DefaultMutableTreeNode selectedNode;
		if (fingerTree.getSelectionPath() == null) {
			selectedFinger = null;
			selectedFingerView = null;
		} else {
			selectedNode = (DefaultMutableTreeNode) fingerTree.getSelectionPath().getLastPathComponent();
			FingerTreeObject userObject = (FingerTreeObject) selectedNode.getUserObject();

			if (userObject == null) {
				return;
			}

			if (userObject.getTaggedObject() instanceof FMRFinger) {
				selectedFinger = (FMRFinger) userObject.getTaggedObject();
				selectedFingerView = null;
			} else if (userObject.getTaggedObject() instanceof FMRFingerView) {
				selectedFingerView = (FMRFingerView) userObject.getTaggedObject();
				selectedFinger = null;
			} else {
				selectedFingerView = null;
				selectedFinger = null;
			}
		}

		if (selectedFingerView != null) {
			fmView.setTemplate(selectedFingerView);
			btnAddFeatureTool.setEnabled(true);
			btnPointerTool.setEnabled(true);
			menuItemAddFeatureTool.setEnabled(true);
			menuItemPointerTool.setEnabled(true);
		} else {
			fmView.setTemplate(null);
			btnAddFeatureTool.setEnabled(false);
			btnPointerTool.setEnabled(false);
			btnDeleteFeature.setEnabled(false);
			menuItemAddFeatureTool.setEnabled(false);
			menuItemPointerTool.setEnabled(false);
		}

		if (selectedFinger != null) {
			displayGrid.setSupportedProperties(listofFingerProp);
			displayGrid.setSource(selectedFinger);
		} else if (selectedFingerView != null) {
			displayGrid.setSupportedProperties(listofFingerViewProp);
			displayGrid.setSource(selectedFingerView);
		} else if (currentTemplate != null) {
			displayGrid.setSupportedProperties(listofFmRecordProp);
			displayGrid.setSource(currentTemplate);
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, "Warning",	JOptionPane.WARNING_MESSAGE);
	}

	private void setTemplate(FMRecord template) {
		setTemplate(template, "");
	}

	private void setTemplate(FMRecord template, String version) {
		if ((currentTemplate != template)) {
			currentTemplate = template;
			fingerTreeRoot.removeAllChildren();
			FingerTreeObject userObject = new FingerTreeObject(template, fileName + version);
			DefaultMutableTreeNode templateNode = new DefaultMutableTreeNode(userObject);

			if (currentTemplate.getFingers() != null) {
				addFingers(templateNode, template);
			}
			fingerTreeRoot.add(templateNode);
			expandFingerTree();
			if (fingerTree.isRootVisible()) {
				fingerTree.setRootVisible(false);
			}
			fingerTree.setSelectionRow(fingerTree.getRowCount() - 1);
		}
		fingerTree.updateUI();
		expandFingerTree();
	}

	private NBuffer loadTemplate(JFileChooser openFileDialog) throws IOException {
		openFileDialog.setSelectedFile(null);
		if (openFileDialog.showDialog(this, "Open") == JFileChooser.APPROVE_OPTION) {
			fileName = openFileDialog.getSelectedFile().getName();
			String filePath = openFileDialog.getSelectedFile().getAbsolutePath();
			return NFile.readAllBytes(filePath);
		} else {
			return new NBuffer(0);
		}
	}

	private void openTemplate() {
		getOptions(BDIFOptionsFrame.BDIFOptionsFormMode.OPEN, BDIFStandard.ANSI);
		if (!dialogResultOK) {
			return;
		}
		BDIFStandard standard = getCurrentStandard();
		int flags = 0;
		FMRecord template;
		try {
			NBuffer templ = loadTemplate(fmRecordOpenFileDialog);
			template = new FMRecord(templ, flags, standard);
		} catch (Exception e) {
			e.printStackTrace();
			showError("Failed to load template! Reason:\r\n" + e);
			return;
		}
		setTemplate(template);
	}

	private void saveTemplate(JFileChooser saveFileDialog, NBuffer template) throws IOException {
		if (saveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			NFile.writeAllBytes(saveFileDialog.getSelectedFile().getAbsolutePath(), template);
		}
	}

	private void saveTemplate() {
		NBuffer b = currentTemplate.save();
		try {
			saveTemplate(fmRecordSaveFileDialog, b);
		} catch (Exception e) {
			e.printStackTrace();
			showError("Failed to save template! Reason:\r\n" + e);
			return;
		}
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) fingerTreeRoot.getChildAt(0);
		FingerTreeObject obj = (FingerTreeObject) root.getUserObject();

		File savedFile = fmRecordSaveFileDialog.getSelectedFile();
		if (savedFile != null) {
			obj.nodeText = savedFile.getName();
		}
		fingerTree.updateUI();
	}

	private void btnPointerToolClick() {
		menuItemPointerToolClick();
	}

	private void btnAddFeatureToolClick() {
		menuItemAddFeatureClick();
	}

	private void menuItemAddFingerClick() {
		if (currentTemplate == null) {
			JOptionPane.showMessageDialog(this,	"FMRecord is not opened! Open FMRecord first.");
			return;
		}
		AddFingerForm dialog = new AddFingerForm(this);
		dialog.setVisible(true);
	}

	private void menuItemConvertClick() {
		if (currentTemplate == null) {
			showWarning("FMRecord is not opened! Open or open FMRecord first.");
			return;
		}
		currentStandard = (currentTemplate.getStandard() == BDIFStandard.ANSI) ? BDIFStandard.ISO : BDIFStandard.ANSI;
		currentFlags = 0;

		getOptions(BDIFOptionsFormMode.CONVERT, currentStandard);
		if (!dialogResultOK) {
			return;
		}
		setTemplate(convertToStandard(currentTemplate, currentStandard, currentFlags));
	}

	private void menuItemAboutClick() {
		AboutBox.show();
	}

	private void menuItemRemoveFingerFingerViewClick() {

		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) fingerTree.getSelectionPath().getLastPathComponent();
		if ((selected != null) && (selected.getParent() != null)) {
			if (selectedFinger != null) {
				currentTemplate.getFingers().remove(selectedFinger);
			} else if (selectedFingerView != null) {
				FMRFinger finger = selectedFingerView.getOwner();
				FMRFingerView temp = selectedFingerView;
				selectedFingerView = null;
				selectedFinger = finger;
				fmView.setTemplate(null);
				finger.getFingerViews().remove(temp);
			} else {
				showWarning("Finger or fingerView must be selected");
				return;
			}
			fingerTree.setSelectionPath(fingerTree.getSelectionPath().getParentPath());
			onSelectedItemChanged();
			selected.removeFromParent();
			fingerTree.updateUI();
			expandFingerTree();
		} else {
			showWarning("Finger or fingerView must be selected");
		}
	}

	private void menuItemOpenFMRecordClick() {
		openTemplate();
		expandFingerTree();
	}

	private void menuItemSaveFMRecordClick() {
		if (currentTemplate == null) {
			return;
		}
		saveTemplate();
	}

	private void menuItemPointerToolClick() {
		if (fmView.getActiveTool() instanceof PointerTool) {
			btnPointerTool.setSelected(false);
			return;
		}
		if (fmView.getActiveTool() instanceof AddFeaturesTool) {
			((AddFeaturesTool<FMRMinutia, FMRCore, FMRDelta, BDIFFPMinutiaNeighbor>) fmView.getActiveTool()).removeFeatureAddListener(this);

		}
		fmView.setActiveTool(new PointerTool<FMRMinutia, FMRCore, FMRDelta, BDIFFPMinutiaNeighbor>());

	}

	private void menuItemAddFeatureClick() {
		if (fmView.getActiveTool() instanceof AddFeaturesTool) {
			menuItemAddFeatureTool.setSelected(false);
			return;
		}
		AddFeaturesTool<FMRMinutia, FMRCore, FMRDelta, BDIFFPMinutiaNeighbor> tool = new AddFeaturesTool<FMRMinutia, FMRCore, FMRDelta, BDIFFPMinutiaNeighbor>();
		tool.addFeatureAddListener(this);
		fmView.setActiveTool(tool);
		menuItemPointerTool.setSelected(false);
		menuItemAddFeatureTool.setSelected(true);
	}

	private void menuItemNewFMRecordClick() {
		NewRecordForm nf = new NewRecordForm(this, "New Record Form");
		nf.setVisible(true);
	}

	private void menuItemAddFingerViewClick() {
		if (currentTemplate == null) {
			showWarning("FMRecord is not opened! Open FMRecord first");
			return;
		}
		if (fingerTree.getSelectionPath() == null) {
			showWarning("FMRecord is not opened! Open FMRecord first");
			return;
		}
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) fingerTree.getSelectionPath().getLastPathComponent();

		FMRFinger finger;
		if (selectedFinger == null && selectedFingerView != null) {
			finger = selectedFingerView.getOwner();
			selected = (DefaultMutableTreeNode) selected.getParent();
		} else if (selectedFinger != null) {
			finger = selectedFinger;
		} else {
			showWarning("Finger must be selected when adding finger view");
			return;
		}
		try {
			FMRFingerView fview = finger.getFingerViews().add();
			addFingerView(selected, fview);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
		}
	}

	private void btnDeleteFeatureClick() {
		menuItemDeleteSelectedMinutiaClick();
	}

	private void menuItemDeleteSelectedMinutiaClick() {
		if (selectedFingerView != null) {
			FMRFingerView fingerView = selectedFingerView;

			int index = fmView.getSelectedMinutiaIndex();
			if (index != -1) {
				fmView.setSelectedMinutiaIndex(-1);
				fingerView.getMinutiae().remove(index);
				return;
			}

			index = fmView.getSelectedCoreIndex();
			if (index != -1) {
				fmView.setSelectedCoreIndex(-1);
				fingerView.getCores().remove(index);
				return;
			}

			index = fmView.getSelectedDeltaIndex();
			if (index != -1) {
				fmView.setSelectedDeltaIndex(-1);
				fingerView.getDeltas().remove(index);
			}
		}
	}

	private void expandFingerTree() {
		for (int i = fingerTree.getRowCount() - 1; i >= 0; i--) {
			fingerTree.expandRow(i);
		}
	}

	private void showDialogOnScreen(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width / 2 - dialog.getPreferredSize().width / 2,
						screenSize.height / 2 - dialog.getPreferredSize().height / 2);
		dialog.setVisible(true);
	}

	// ==============================================
	// Public Methods
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object sender = e.getSource();
		if (sender == menuItemAbout) {
			menuItemAboutClick();
		} else if (sender == menuItemExit) {
			LicenseManager.getInstance().releaseAll();
			NCore.shutdown();
			System.exit(0);
		} else if (sender == menuItemNewFMRecord) {
			menuItemNewFMRecordClick();
		} else if (sender == menuItemAddFinger) {
			menuItemAddFingerClick();
		} else if (sender == menuItemAddFeatureTool) {
			menuItemAddFeatureClick();
		} else if (sender == menuItemSaveFMRecord) {
			menuItemSaveFMRecordClick();
		} else if (sender == menuItemOpenFMRecord) {
			menuItemOpenFMRecordClick();
		} else if (sender == menuItemAddFingerView) {
			menuItemAddFingerViewClick();
		} else if (sender == menuItemRemoveFingerFingerView) {
			menuItemRemoveFingerFingerViewClick();
		} else if (sender == menuItemPointerTool) {
			menuItemPointerToolClick();
		} else if (sender == menuItemAddFeatureTool) {
			menuItemAddFeatureClick();
		} else if (sender == menuItemDeleteSelectedMinutia) {
			menuItemDeleteSelectedMinutiaClick();
		} else if (sender == menuItemConvert) {
			menuItemConvertClick();
		} else if (sender == btnPointerTool) {
			btnPointerToolClick();
		} else if (sender == btnAddFeatureTool) {
			btnAddFeatureToolClick();
		} else if (sender == btnDeleteFeature) {
			btnDeleteFeatureClick();
		} else if (sender == fmView) {
			onDetailSelected();
		}
	}

	public void addFinger(BDIFFPPosition position, String viewCount) {
		int count;
		try {
			count = Integer.parseInt(viewCount);
			if (count > FMRFinger.MAX_FINGER_VIEW_COUNT) {
				JOptionPane.showMessageDialog(this, "Specified count is more than maximum alowed finger views count");
				return;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,	"Cann't parse Finger views count");
			return;
		}

		try {
			FMRFinger finger = currentTemplate.getFingers().add(position);
			FMRFinger.FingerViewCollection fvc = finger.getFingerViews();
			for (int i = 0; i < count; i++) {
				fvc.add();
			}
			int index = fingerTreeRoot.getChildAt(0).getChildCount() + 1;
			FingerTreeObject f = new FingerTreeObject(finger, "Finger " + index);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) fingerTreeRoot.getChildAt(0);
			root.add(node);
			addFinger(node, finger);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.toString());
		}
		fingerTree.updateUI();
	}

	public void menuItemNewFMRecord(int x, int y, int hR, int vR, BDIFStandard standred) {
		fileName = "Untitled";
		FMRecord template = new FMRecord((short) x, (short) y, (short) hR, (short) vR, standred);
		setTemplate(template);
		selectedFinger = null;
		selectedFingerView = null;
		fingerTree.setSelectionPath(null);
	}

	public void valueChanged(TreeSelectionEvent arg0) {
		onSelectedItemChanged();
	}

	public void updateFeatureFormDetails(int feature, BDIFFPMinutiaType type) {
		selectedFeature = feature;
		selectedType = type;
	}

	public void selectedIndexChanged() {
		onDetailSelected();
	}

	public void featureAddCompleted(FeatureAddEvent e) {
		onFeatureAdded(e);
	}

	public void setCurrentStandard(BDIFStandard currentStandard) {
		this.currentStandard = currentStandard;
	}

	public BDIFStandard getCurrentStandard() {
		return currentStandard;
	}

	public void setCurrentFlags(int currentFlags) {
		this.currentFlags = currentFlags;
	}

}
