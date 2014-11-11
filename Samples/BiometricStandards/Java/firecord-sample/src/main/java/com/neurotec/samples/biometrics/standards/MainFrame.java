package com.neurotec.samples.biometrics.standards;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.neurotec.biometrics.standards.BDIFFPPosition;
import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.FIRFinger;
import com.neurotec.biometrics.standards.FIRFingerView;
import com.neurotec.biometrics.standards.FIRImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.FIRecord;
import com.neurotec.biometrics.standards.swing.FIView;
import com.neurotec.images.NImage;
import com.neurotec.images.NImageFormat;
import com.neurotec.images.NPixelFormat;
import com.neurotec.io.NBuffer;
import com.neurotec.io.NFile;
import com.neurotec.lang.NCore;
import com.neurotec.samples.swing.NPropertyGrid;
import com.neurotec.samples.util.LicenseManager;
import com.neurotec.samples.util.Utils;
import com.neurotec.swing.AboutBox;

public final class MainFrame extends JFrame implements ActionListener, TreeSelectionListener, MainFrameEventListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JMenuItem menuItemNewFromImage;
	private JMenuItem menuItemOpenFIRecord;
	private JMenuItem menuItemSaveFIRecord;
	private JMenuItem menuItemExit;
	private JMenuItem menuItemAddFinger;
	private JMenuItem menuItemAddFromImage;
	private JMenuItem menuItemAddViewFromImage;
	private JMenuItem menuItemRemove;
	private JMenuItem menuItemSaveAsImage;
	private JMenuItem menuItemConvert;
	private JMenuItem menuItemAbout;

	private DefaultMutableTreeNode fingerTreeRoot;
	private JTree fingerTree;
	private NPropertyGrid propertyGrid;
	private FIView fiView;

	private JFileChooser fiRecordOpenFileDialog;
	private JFileChooser fiRecordSaveFileDialog;
	private JFileChooser imageOpenFileDialog;
	private JFileChooser imageSaveFileDialog;

	// ==============================================
	// Private fields
	// ==============================================

	private FIRecord template;
	private File file;
	private File openFile;
	private BDIFStandard currentStandard;
	private int currentFlags;
	private NImage imageFromFile;
	private boolean isDialogResultOK = false;

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
		initializeFileChoosers();

		menuItemAbout.setText(AboutBox.getName());

		setIconImage(Utils.createIconImage("images/Logo16x16.png"));

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				try {
					if (template != null) {
						template.dispose();
					}
					LicenseManager.getInstance().releaseAll();
				} finally {
					NCore.shutdown();
				}
			}

		});

		onSelectedItemChanged();
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		createMenuBar();

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		fingerTreeRoot = new DefaultMutableTreeNode();
		fingerTree = new JTree(fingerTreeRoot);
		fingerTree.addTreeSelectionListener(this);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		fingerTree.setCellRenderer(renderer);
		fingerTree.setShowsRootHandles(true);

		JScrollPane fingerTreeScrollPane = new JScrollPane(fingerTree);

		propertyGrid = new NPropertyGrid(true, new FIPropertiesTable());

		fiView = new FIView();
		JScrollPane imageScrollPane = new JScrollPane(fiView);

		leftSplitPane.setLeftComponent(fingerTreeScrollPane);
		leftSplitPane.setRightComponent(propertyGrid);
		leftSplitPane.setDividerLocation(185);

		mainSplitPane.setLeftComponent(leftSplitPane);
		mainSplitPane.setRightComponent(imageScrollPane);
		mainSplitPane.setDividerLocation(250);

		this.getContentPane().add(mainSplitPane);
		this.pack();
	}

	private void initializeFileChoosers() {
		fiRecordOpenFileDialog = new JFileChooser();
		fiRecordSaveFileDialog = new JFileChooser();
		imageOpenFileDialog = new JFileChooser();
		imageSaveFileDialog = new JFileChooser();

		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getBMP().getFileFilter(), "BMP files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG().getFileFilter(), "JPEG files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG2K().getFileFilter(), "JPEG 2000 files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getPNG().getFileFilter(), "PNG files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getTIFF().getFileFilter(), "TIFF files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getIHead().getFileFilter(), "NIST IHead files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getWSQ().getFileFilter(), "WSQ files"));
		StringBuilder allImageFilters = new StringBuilder(64);
		for (NImageFormat format : NImageFormat.getFormats()) {
			allImageFilters.append(format.getFileFilter()).append(';');
		}
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(allImageFilters.toString(), "All image files"));

		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getBMP().getFileFilter(), "BMP files"));
		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG().getFileFilter(), "JPEG files"));
		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG2K().getFileFilter(), "JPEG 2000 files"));
		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getTIFF().getFileFilter(), "TIFF files"));
		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getIHead().getFileFilter(), "NIST IHead files"));
		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getWSQ().getFileFilter(), "WSQ files"));
		imageSaveFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getPNG().getFileFilter(), "PNG files"));

		fiRecordOpenFileDialog.addChoosableFileFilter(new Utils.TemplateFileFilter("FCRecord files"));

		fiRecordSaveFileDialog.addChoosableFileFilter(new Utils.TemplateFileFilter("FCRecord files"));
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");

		menuItemNewFromImage = new JMenuItem("New from image...");
		menuItemNewFromImage.addActionListener(this);
		menuItemNewFromImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menuItemNewFromImage.setMnemonic(KeyEvent.VK_N);

		menuItemOpenFIRecord = new JMenuItem("Open FIRecord...");
		menuItemOpenFIRecord.addActionListener(this);
		menuItemOpenFIRecord.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menuItemOpenFIRecord.setMnemonic(KeyEvent.VK_O);

		menuItemSaveFIRecord = new JMenuItem("Save FIRecord...");
		menuItemSaveFIRecord.addActionListener(this);
		menuItemSaveFIRecord.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		menuItemSaveFIRecord.setMnemonic(KeyEvent.VK_S);

		menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);

		menuFile.add(menuItemNewFromImage);
		menuFile.add(menuItemOpenFIRecord);
		menuFile.add(menuItemSaveFIRecord);
		menuFile.addSeparator();
		menuFile.add(menuItemExit);

		JMenu menuEdit = new JMenu("Edit");

		menuItemAddFinger = new JMenuItem("Add finger...");
		menuItemAddFinger.addActionListener(this);

		menuItemAddFromImage = new JMenuItem("Add finger from image...");
		menuItemAddFromImage.addActionListener(this);

		menuItemAddViewFromImage = new JMenuItem("Add fingerView from image...");
		menuItemAddViewFromImage.addActionListener(this);

		menuItemRemove = new JMenuItem("Remove finger/fingerView...");
		menuItemRemove.addActionListener(this);

		menuItemSaveAsImage = new JMenuItem("Save fingerView as image...");
		menuItemSaveAsImage.addActionListener(this);

		menuItemConvert = new JMenuItem("Convert ...");
		menuItemConvert.addActionListener(this);

		menuEdit.add(menuItemAddFinger);
		menuEdit.add(menuItemAddFromImage);
		menuEdit.add(menuItemAddViewFromImage);
		menuEdit.addSeparator();
		menuEdit.add(menuItemRemove);
		menuEdit.add(menuItemSaveAsImage);
		menuEdit.addSeparator();
		menuEdit.add(menuItemConvert);

		JMenu menuHelp = new JMenu("Help");
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(this);
		menuHelp.add(menuItemAbout);

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuHelp);

		this.setJMenuBar(menuBar);
	}

	private boolean getOptions(BDIFOptionsFormMode mode) {
		FIRecordOptionsFrame form = new FIRecordOptionsFrame(this, this);

		form.setStandard(currentStandard);
		form.setFlags(currentFlags);
		form.setMode(mode);
		showDialogOnScreen(form);
		if (!isDialogResultOK) {
			return false;
		}
		return true;
	}

	private byte[] loadTemplate(JFileChooser openFileDialog) {
		openFileDialog.setSelectedFile(null);
		if (openFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			openFile = openFileDialog.getSelectedFile();
			byte[] fileData = new byte[(int) openFile.length()];
			try {
				DataInputStream dis = new DataInputStream(new FileInputStream(openFile));
				dis.readFully(fileData);
				dis.close();
				return fileData;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				showError(e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				showError(e.toString());
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.toString());
			}
		}
		openFile = null;
		return null;
	}

	private void saveTemplate(JFileChooser saveFileDialog, File file, NBuffer template) {
		if (file != null) {
			saveFileDialog.setSelectedFile(file);
		}
		if (saveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				String savePath = saveFileDialog.getSelectedFile().getPath();
				if (saveFileDialog.getFileFilter().getDescription().equals("Data files")) {
					if (savePath.lastIndexOf('.') == -1) {
						savePath += ".dat";
					}
				}
				NFile.writeAllBytes(savePath, template);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				showError(e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				showError(e.toString());
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.toString());
			}
		}
	}

	private FIRecord convertToStandard(FIRecord record, BDIFStandard newStandard, int flags) {
		if (record.getStandard() == newStandard) {
			return record;
		}
		try {
			return new FIRecord(record, flags, newStandard);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
			return null;
		}
	}

	private DefaultMutableTreeNode addFingerView(DefaultMutableTreeNode fingerNode, FIRFingerView fingerView) {
		int index = fingerNode.getChildCount() + 1;
		DefaultMutableTreeNode fingerViewNode = new DefaultMutableTreeNode(new FingerTreeObject(fingerView, "FingerView " + index));
		fingerNode.add(fingerViewNode);
		return fingerViewNode;
	}

	private DefaultMutableTreeNode addFingerViews(DefaultMutableTreeNode fingerNode, FIRFinger finger) {
		for (FIRFingerView item : finger.getFingerViews()) {
			addFingerView(fingerNode, item);
		}
		return fingerNode;
	}

	private DefaultMutableTreeNode addFinger(DefaultMutableTreeNode templateNode, FIRFinger finger) {
		int index = templateNode.getChildCount() + 1;
		DefaultMutableTreeNode fingerNode = new DefaultMutableTreeNode(new FingerTreeObject(finger, "Finger " + index));
		addFingerViews(fingerNode, finger);
		templateNode.add(fingerNode);
		return fingerNode;
	}

	private DefaultMutableTreeNode addFingers(DefaultMutableTreeNode templateNode, FIRecord record) {
		for (FIRFinger finger : record.getFingers()) {
			addFinger(templateNode, finger);
		}
		return templateNode;
	}

	private void onSelectedItemChanged() {
		DefaultMutableTreeNode selectedNode = null;
		if (fingerTree.getSelectionPath() != null) {
			selectedNode = (DefaultMutableTreeNode) fingerTree.getSelectionPath().getLastPathComponent();
		}
		if (selectedNode == null) {
			return;
		}
		Object taggedObject = ((FingerTreeObject) selectedNode.getUserObject()).getTaggedObject();
		FIRFingerView fingerView = null;
		if (taggedObject instanceof FIRFingerView) {
			fingerView = (FIRFingerView) taggedObject;
		}
		if (fingerView == null) {
			fiView.setRecord(null);
			propertyGrid.setSource(((FingerTreeObject) selectedNode.getUserObject()).getTaggedObject());
		} else {
			fiView.setRecord(fingerView);
			propertyGrid.setSource(fingerView);
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, this.getTitle(), JOptionPane.ERROR_MESSAGE);
	}

	private void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, this.getTitle(), JOptionPane.WARNING_MESSAGE);
	}

	private void setTemplate(FIRecord template, File file) {
		setTemplate(template, file, "");
	}

	private void setTemplate(FIRecord template, File file, String version) {
		boolean newTemplate = this.template != template;
		this.template = template;
		this.file = file;
		String fileName = null;
		if (file != null) {
			fileName = file.getName();
		}
		fingerTreeRoot.removeAllChildren();
		if (newTemplate) {
			fingerTree.setRootVisible(true);
			DefaultMutableTreeNode templateNode = new DefaultMutableTreeNode(new FingerTreeObject(template, (fileName == null ? "Untitled" : fileName + version)));

			if (template.getFingers() != null) {
				addFingers(templateNode, template);
			}
			fingerTreeRoot.add(templateNode);
			fingerTree.updateUI();
			expandFingerTree();
			fingerTree.setRootVisible(false);
			fingerTree.setSelectionRow(0);
		}
		fingerTree.updateUI();
	}

	private void expandFingerTree() {
		fingerTree.expandRow(0);
		for (int i = fingerTree.getRowCount() - 1; i > 0; i--) {
			fingerTree.expandRow(i);
		}
		fingerTree.updateUI();
	}

	private void openTemplate() {
		byte[] templ = loadTemplate(fiRecordOpenFileDialog);
		if (templ == null) {
			return;
		}

		currentStandard = BDIFStandard.ANSI;
		currentFlags = 0;
		if (!getOptions(BDIFOptionsFormMode.OPEN)) {
			return;
		}

		FIRecord firecord;
		try {
			firecord = new FIRecord(NBuffer.fromArray(templ), currentFlags, currentStandard);
		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Failed to load template! Reason:\r\n" + ex);
			return;
		}
		setTemplate(firecord, openFile);
	}

	private void saveTemplate() {
		saveTemplate(fiRecordSaveFileDialog, file, template.save());
		((FingerTreeObject) ((DefaultMutableTreeNode) fingerTreeRoot.getChildAt(0)).getUserObject()).setNodeText(fiRecordSaveFileDialog.getSelectedFile().getName());
	}

	private void convert() {
		if (template == null) {
			return;
		}
		currentStandard = (template.getStandard() == BDIFStandard.ANSI) ? BDIFStandard.ISO : BDIFStandard.ANSI;
		currentFlags = 0;

		try {
			if (!getOptions(BDIFOptionsFormMode.CONVERT)) {
				return;
			}
			setTemplate(convertToStandard(template, currentStandard, currentFlags), file, "");
			expandFingerTree();
		} catch (Exception ex) {
			ex.printStackTrace();
			showError(ex.toString());
		}
	}

	private void saveFinger() {
		if (fingerTree.getSelectionPath() == null) {
			showWarning("Please select finger image");
			return;
		}
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) fingerTree.getSelectionPath().getLastPathComponent();
		if ((selected != null) && (selected.getParent() != fingerTreeRoot) && (selected.getParent().getParent() != fingerTreeRoot)) {
			imageSaveFileDialog.setSelectedFile(null);
			if (imageSaveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					FIRFingerView finger = (FIRFingerView) ((FingerTreeObject) selected.getUserObject()).getTaggedObject();
					if (finger != null) {
						NImage image = finger.toNImage();
						FileFilter selectedFilter = imageSaveFileDialog.getFileFilter();
						if (selectedFilter instanceof Utils.ImageFileFilter) {
							image.save(imageSaveFileDialog.getSelectedFile().getPath() + '.' + ((Utils.ImageFileFilter) imageSaveFileDialog.getFileFilter()).getExtensions().get(0));
						} else {
							image.save(imageSaveFileDialog.getSelectedFile().getPath());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					showError("Failed to save image to file!\r\nReason: " + e);
				}
			}
		} else {
			showWarning("Please select finger image");
		}
	}

	private void addOrCreateFinger() {
		currentStandard = BDIFStandard.ANSI;
		currentFlags = 0;
		if (template == null) {
			if (!getOptions(BDIFOptionsFormMode.NEW)) {
				return;
			}
		}
		imageOpenFileDialog.setSelectedFile(null);
		if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				imageFromFile = NImage.fromFile(imageOpenFileDialog.getSelectedFile().getPath());

				AddFingerFrame form = template == null ? new AddNewFingerFromImageFrame(this, this, true) : new AddFingerFrame(this, this, true);
				showDialogOnScreen(form);

			} catch (Exception ex) {
				ex.printStackTrace();
				template = null;
				showError("Failed to add image. Reason:\r\n" + ex);
			}
		}
	}

	private void addFingerFromImage() {
		if (template == null) {
			showWarning("FIRecord must be open. Open or create new FIRecord first.");
			return;
		}
		addOrCreateFinger();
	}

	private void removeFinger() {
		if (fingerTree.getSelectionPath() == null) {
			showWarning("Finger or FingerView must be selected");
			return;
		}
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) fingerTree.getSelectionPath().getLastPathComponent();
		if ((selected != null) && (selected.getParent() != fingerTreeRoot)) {
			Object taggedobject = ((FingerTreeObject) selected.getUserObject()).getTaggedObject();
			FIRFingerView fingerView = null;
			if (taggedobject instanceof FIRFingerView) {
				fingerView = (FIRFingerView) taggedobject;
			}
			try {
				if (fingerView == null) {
					template.getFingers().remove(((FingerTreeObject) selected.getUserObject()).getTaggedObject());
					((DefaultMutableTreeNode) selected.getParent()).remove(selected);
				} else {
					FIRFinger parent = (FIRFinger) ((FingerTreeObject) ((DefaultMutableTreeNode) selected.getParent()).getUserObject()).getTaggedObject();
					if (parent != null) {
						parent.getFingerViews().remove(fingerView.getViewNumber());
					}
					((DefaultMutableTreeNode) selected.getParent()).remove(selected);
				}
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.toString());
			}
			fingerTree.updateUI();
			fingerTree.setSelectionRow(0);
		} else {
			showWarning("Finger or FingerView must be selected");
		}
	}

	private void newFromImage() {
		FIRecord oldTemplate = template;
		template = null;
		fingerTreeRoot.removeAllChildren();
		fingerTree.updateUI();
		addOrCreateFinger();
		if (template == null) {
			setTemplate(oldTemplate, file);
		} else {
			if (oldTemplate != null) {
				oldTemplate.dispose();
			}
		}
		expandFingerTree();
	}

	private void openFIRecord() {
		openTemplate();
		expandFingerTree();
	}

	private void saveFIRecord() {
		if (template == null) {
			return;
		}
		try {
			saveTemplate();
		} catch (Exception ex) {
			ex.printStackTrace();
			showError(ex.toString());
		}

	}

	private void addFinger() {
		if (template == null) {
			showWarning("FIRecord not opened! Open or create new record first");
			return;
		}
		AddFingerFrame form = new AddFingerFrame(this, this, false);
		showDialogOnScreen(form);

	}

	private void addFingerViewFromImage() {
		if (fingerTree.getSelectionPath() == null) {
			showWarning("Finger must be selected before adding fingerView");
			return;
		}
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) fingerTree.getSelectionPath().getLastPathComponent();
		if (selected != null && selected.getParent() != fingerTreeRoot) {
			try {
				imageOpenFileDialog.setSelectedFile(null);
				NImage imgFromFile;
				if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					imgFromFile = NImage.fromFile(imageOpenFileDialog.getSelectedFile().getPath());
				} else {
					return;
				}

				NImage image = NImage.fromImage(NPixelFormat.GRAYSCALE_8U, 0, imgFromFile);
				if (image.isResolutionIsAspectRatio() || image.getHorzResolution() < 250 || image.getVertResolution() < 250) {
					image.setHorzResolution(500);
					image.setVertResolution(500);
					image.setResolutionIsAspectRatio(false);
				}

				Object taggedObject = ((FingerTreeObject) selected.getUserObject()).getTaggedObject();
				FIRFinger finger = null;
				if (taggedObject instanceof FIRFinger) {
					finger = (FIRFinger) taggedObject;
				}
				if (finger == null) {
					FIRFingerView fingerView = (FIRFingerView) ((FingerTreeObject) selected.getUserObject()).getTaggedObject();
					if (fingerView != null) {
						finger = fingerView.getOwner();
					}
					if (finger != null) {
						fingerView = finger.getFingerViews().add(image);
					}
					addFingerView((DefaultMutableTreeNode) selected.getParent(), fingerView);
				} else {
					FIRFingerView added = finger.getFingerViews().add(image);
					addFingerView(selected, added);
				}
				fingerTree.updateUI();
				expandFingerTree();
			} catch (Exception ex) {
				ex.printStackTrace();
				showError(ex.toString());
			}
		} else {
			showWarning("Finger must be selected before adding fingerView");
		}
	}

	private void showDialogOnScreen(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width / 2 - dialog.getPreferredSize().width / 2, screenSize.height / 2 - dialog.getPreferredSize().height / 2);
		dialog.setVisible(true);
	}

	// ===================================================
	// Public interface methods MainFrameEventListenr
	// ===================================================

	public void addOrCreateFinger(BDIFScaleUnits scaleUnits, FIRImageCompressionAlgorithm algorithm, BDIFFPPosition position) {
		NImage image = NImage.fromImage(NPixelFormat.GRAYSCALE_8U, 0, imageFromFile);
		if (image.isResolutionIsAspectRatio() || image.getHorzResolution() < 250 || image.getVertResolution() < 250) {
			image.setHorzResolution(500);
			image.setVertResolution(500);
			image.setResolutionIsAspectRatio(false);
		}
		if (template == null) {
			float horzScanResolution = image.getHorzResolution();
			float vertScanResolution = image.getVertResolution();
			if (scaleUnits != null) {
				if (scaleUnits == BDIFScaleUnits.PIXELS_PER_CENTIMETER) {
					horzScanResolution /= 2.54f;
					vertScanResolution /= 2.54f;
				}
				try {
					FIRecord templ = new FIRecord(image, (short) 20, scaleUnits, (short) Math.round(horzScanResolution), (short) Math.round(vertScanResolution), (byte) 8, algorithm , position, currentStandard);
					setTemplate(templ, imageOpenFileDialog.getSelectedFile());
				} catch (Exception e) {
					e.printStackTrace();
					showError(e.toString());
				}
			}
			expandFingerTree();
		} else {
			try {
				FIRFinger firFinger = template.getFingers().add(position, image);
				addFinger((DefaultMutableTreeNode) fingerTreeRoot.getChildAt(0), firFinger);
				fingerTree.updateUI();
				expandFingerTree();
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.toString());
			}
		}
	}

	public void addFinger(BDIFFPPosition position) {
		try {
			FIRFinger finger = template.getFingers().add(position);
			addFinger((DefaultMutableTreeNode) fingerTreeRoot.getChildAt(0), finger);
			fingerTree.updateUI();
		} catch (Exception ex) {
			ex.printStackTrace();
			showError(ex.toString());
		}
	}

	public void setDialogResultOK(boolean isDialogResultOK) {
		this.isDialogResultOK = isDialogResultOK;
	}

	public void setCurrentStandard(BDIFStandard currentStandard) {
		this.currentStandard = currentStandard;
	}

	public void setCurrentFlags(int currentFlags) {
		this.currentFlags = currentFlags;
	}

	// ==============================================
	// Event handling ActionListener
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == menuItemConvert) {
			convert();
		} else if (source == menuItemAbout) {
			AboutBox.show();
		} else if (source == menuItemSaveFIRecord) {
			saveFIRecord();
		} else if (source == menuItemAddFromImage) {
			addFingerFromImage();
		} else if (source == menuItemRemove) {
			removeFinger();
		} else if (source == menuItemNewFromImage) {
			newFromImage();
		} else if (source == menuItemOpenFIRecord) {
			openFIRecord();
		} else if (source == menuItemSaveAsImage) {
			saveFinger();
		} else if (source == menuItemExit) {
			dispose();
		} else if (source == menuItemAddFinger) {
			addFinger();
		} else if (source == menuItemAddViewFromImage) {
			addFingerViewFromImage();
		}
	}

	// ==============================================
	// Event handling TreeSelectionListener
	// ==============================================

	public void valueChanged(TreeSelectionEvent e) {
		onSelectedItemChanged();

	}

	// ==================================================================
	// Private class to represent UserObject of tree nodes in fingerTree
	// ==================================================================

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
		// Public methods
		// ==============================================

		public Object getTaggedObject() {
			return taggedObject;
		}

		public void setNodeText(String nodeText) {
			this.nodeText = nodeText;
		}

		// ==============================================
		// Overridden methods
		// ==============================================

		@Override
		public String toString() {
			return nodeText;
		}

	}
}
