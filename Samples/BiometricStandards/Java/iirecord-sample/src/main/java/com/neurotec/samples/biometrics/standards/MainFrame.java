package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFEyePosition;
import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.IIRImageFormat;
import com.neurotec.biometrics.standards.IIRIris;
import com.neurotec.biometrics.standards.IIRIrisImage;
import com.neurotec.biometrics.standards.IIRecord;
import com.neurotec.biometrics.standards.swing.IIView;
import com.neurotec.images.NImage;
import com.neurotec.images.NImageFormat;
import com.neurotec.io.NBuffer;
import com.neurotec.io.NFile;
import com.neurotec.lang.NCore;
import com.neurotec.samples.swing.NPropertyGrid;
import com.neurotec.samples.util.LicenseManager;
import com.neurotec.samples.util.Utils;
import com.neurotec.swing.AboutBox;

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

public final class MainFrame extends JFrame implements ActionListener, TreeSelectionListener, MainFrameEventListener {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final long serialVersionUID = 1L;

	// ===========================================================
	// Private GUI controls
	// ===========================================================

	private JMenuItem menuItemNewFromImage;
	private JMenuItem menuItemOpen;
	private JMenuItem menuItemSave;
	private JMenuItem menuItemExit;
	private JMenuItem menuItemAddIris;
	private JMenuItem menuItemAddIrisImage;
	private JMenuItem menuItemRemove;
	private JMenuItem menuItemSaveIrisImage;
	private JMenuItem menuItemConvert;
	private JMenuItem menuItemAbout;

	private DefaultMutableTreeNode irisTreeRoot;
	private JTree irisTree;
	private NPropertyGrid propertyGrid;
	private IIView iiView;

	private JFileChooser imageOpenFileDialog;
	private JFileChooser saveImageFileDialog;
	private JFileChooser iiRecordOpenFileDialog;
	private JFileChooser iiRecordSaveFileDialog;

	// ===========================================================
	// Private fields
	// ===========================================================

	private IIRecord template;
	private File file;

	private File openFile;
	private BDIFStandard currentStandard;
	private int currentFlags;
	private boolean isDialogResultOK;
	private NImage imageFromFile;

	// ===========================================================
	// Public constructor
	// ===========================================================

	public MainFrame() {
		super();
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
		}

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
		initializeComponents();
		initializeFileChoosers();

		setIconImage(Utils.createIconImage("images/Logo16x16.png"));

		menuItemAbout.setText(AboutBox.getName());

		onSelectedItemChanged();
	}

	// ===========================================================
	// Private methods
	// ===========================================================

	private void initializeComponents() {
		createMenuBar();

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		irisTreeRoot = new DefaultMutableTreeNode();
		irisTree = new JTree(irisTreeRoot);
		irisTree.addTreeSelectionListener(this);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		irisTree.setCellRenderer(renderer);
		irisTree.setShowsRootHandles(true);

		JScrollPane irisTreeScrollPane = new JScrollPane(irisTree);

		propertyGrid = new NPropertyGrid(true, new IIPropertiesTable());

		iiView = new IIView();
		JScrollPane imageScrollPane = new JScrollPane(iiView);

		leftSplitPane.setLeftComponent(irisTreeScrollPane);
		leftSplitPane.setRightComponent(propertyGrid);
		leftSplitPane.setDividerLocation(185);

		mainSplitPane.setLeftComponent(leftSplitPane);
		mainSplitPane.setRightComponent(imageScrollPane);
		mainSplitPane.setDividerLocation(250);

		this.getContentPane().add(mainSplitPane);
		this.pack();
	}

	private void initializeFileChoosers() {
		imageOpenFileDialog = new JFileChooser();
		saveImageFileDialog = new JFileChooser();
		iiRecordOpenFileDialog = new JFileChooser();
		iiRecordSaveFileDialog = new JFileChooser();

		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getBMP().getFileFilter(), "BMP files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG().getFileFilter(), "JPEG files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG2K().getFileFilter(), "JPEG 2000 files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getPNG().getFileFilter(), "PNG files"));
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getTIFF().getFileFilter(), "TIFF files"));
		StringBuilder allImageFilters = new StringBuilder(64);
		for (NImageFormat format : NImageFormat.getFormats()) {
			allImageFilters.append(format.getFileFilter()).append(';');
		}
		imageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(allImageFilters.toString(), "All image files"));

		saveImageFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getBMP().getFileFilter(), "BMP files"));
		saveImageFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG().getFileFilter(), "JPEG files"));
		saveImageFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG2K().getFileFilter(), "JPEG 2000 files"));
		saveImageFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getTIFF().getFileFilter(), "TIFF files"));
		saveImageFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getPNG().getFileFilter(), "PNG files"));

		iiRecordOpenFileDialog.addChoosableFileFilter(new Utils.TemplateFileFilter("FCRecord files"));

		iiRecordSaveFileDialog.addChoosableFileFilter(new Utils.TemplateFileFilter("FCRecord files"));
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");

		menuItemNewFromImage = new JMenuItem("New from image...");
		menuItemNewFromImage.addActionListener(this);
		menuItemNewFromImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menuItemNewFromImage.setMnemonic(KeyEvent.VK_N);

		menuItemOpen = new JMenuItem("Open...");
		menuItemOpen.addActionListener(this);
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menuItemOpen.setMnemonic(KeyEvent.VK_O);

		menuItemSave = new JMenuItem("Save...");
		menuItemSave.addActionListener(this);
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		menuItemSave.setMnemonic(KeyEvent.VK_S);

		menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);

		menuFile.add(menuItemNewFromImage);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		menuFile.addSeparator();
		menuFile.add(menuItemExit);

		JMenu menuEdit = new JMenu("Edit");

		menuItemAddIris = new JMenuItem("Add iris...");
		menuItemAddIris.addActionListener(this);

		menuItemAddIrisImage = new JMenuItem("Add iris image...");
		menuItemAddIrisImage.addActionListener(this);

		menuItemRemove = new JMenuItem("Remove iris/iris image...");
		menuItemRemove.addActionListener(this);

		menuItemSaveIrisImage = new JMenuItem("Save iris image...");
		menuItemSaveIrisImage.addActionListener(this);

		menuItemConvert = new JMenuItem("Convert ...");
		menuItemConvert.addActionListener(this);

		menuEdit.add(menuItemAddIris);
		menuEdit.add(menuItemAddIrisImage);
		menuEdit.addSeparator();
		menuEdit.add(menuItemRemove);
		menuEdit.add(menuItemSaveIrisImage);
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
		IIRecordOptionsFrame form = new IIRecordOptionsFrame(this, this);
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
		saveFileDialog.setSelectedFile(file);
		if (saveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				String savePath = saveFileDialog.getSelectedFile().getPath();
				if (saveFileDialog.getFileFilter().getDescription().equals("IIRecord Files (*.dat)")) {
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

	private DefaultMutableTreeNode addIrisImage(DefaultMutableTreeNode irisNode, IIRIrisImage image) {
		String prefix = "IrisImage %s";
		int index = irisNode.getChildCount() + 1;
		DefaultMutableTreeNode irisImageNode = new DefaultMutableTreeNode(new IrisTreeObject(image, String.format(prefix, index)));
		irisNode.add(irisImageNode);
		return irisNode;
	}

	private DefaultMutableTreeNode addIrisImages(DefaultMutableTreeNode irisNode, IIRIris iris) {
		for (IIRIrisImage item : iris.getIrisImages()) {
			addIrisImage(irisNode, item);
		}
		return irisNode;
	}

	private DefaultMutableTreeNode addIris(DefaultMutableTreeNode templateNode, IIRIris iris) {
		int index = templateNode.getChildCount() + 1;
		DefaultMutableTreeNode irisNode = new DefaultMutableTreeNode(new IrisTreeObject(iris, "Iris " + index));
		addIrisImages(irisNode, iris);
		templateNode.add(irisNode);
		return irisNode;
	}

	private DefaultMutableTreeNode addIrises(DefaultMutableTreeNode templateNode, IIRecord template) {
		for (IIRIris item : template.getIrises()) {
			addIris(templateNode, item);
		}
		return templateNode;
	}

	private IIRecord convertToStandard(IIRecord record, BDIFStandard newStandard, int flags) {
		if (record.getStandard() == newStandard) {
			return record;
		}
		try {
			return new IIRecord(record, flags, newStandard);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
			return null;
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, this.getTitle(), JOptionPane.ERROR_MESSAGE);
	}

	private void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, this.getTitle(), JOptionPane.WARNING_MESSAGE);
	}

	private void setTemplate(IIRecord template, File file) {
		setTemplate(template, file, "");
	}

	private void setTemplate(IIRecord template, File file, String version) {
		boolean newTemplate = this.template != template;
		this.template = template;
		this.file = file;
		irisTreeRoot.removeAllChildren();
		irisTree.updateUI();
		String fileName = null;
		if (file != null) {
			fileName = file.getName();
		}
		if (newTemplate) {
			irisTree.setRootVisible(true);
			DefaultMutableTreeNode templateNode = new DefaultMutableTreeNode(new IrisTreeObject(template, fileName == null ? "Untitled" : version == null ? fileName : fileName + version));
			if (template.getIrises() != null) {
				addIrises(templateNode, template);
			}
			irisTreeRoot.add(templateNode);
			irisTree.updateUI();
			expandIrisTree();
			irisTree.setRootVisible(false);
			irisTree.setSelectionRow(0);
		}
		irisTree.updateUI();
	}

	private void openTemplate() {
		byte[] templ = loadTemplate(iiRecordOpenFileDialog);
		if (templ == null) {
			return;
		}

		currentStandard = BDIFStandard.ANSI;
		currentFlags = 0;
		if (!getOptions(BDIFOptionsFormMode.OPEN)) {
			return;
		}

		IIRecord iirecord;
		try {
			iirecord = new IIRecord(NBuffer.fromArray(templ), currentFlags, currentStandard);
		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Failed to load template! Reason:\r\n" + ex);
			return;
		}
		setTemplate(iirecord, openFile);
	}

	private void saveTemplate() {
		saveTemplate(iiRecordSaveFileDialog, this.file, template.save());
	}

	private void onSelectedItemChanged() {
		iiView.setRecord(null);

		if (irisTree.getSelectionPath() != null) {
			DefaultMutableTreeNode selected = (DefaultMutableTreeNode) irisTree.getSelectionPath().getLastPathComponent();
			Object taggedObject = ((IrisTreeObject) selected.getUserObject()).getTaggedObject();
			IIRIrisImage image = null;
			if (taggedObject instanceof IIRIrisImage) {
				image = (IIRIrisImage) taggedObject;
			}
			if (image != null) {
				iiView.setRecord(image);
				propertyGrid.setSource(image);
				return;
			}
			propertyGrid.setSource(taggedObject);

		}
	}

	private void showDialogOnScreen(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width / 2 - dialog.getPreferredSize().width / 2, screenSize.height / 2 - dialog.getPreferredSize().height / 2);
		dialog.setVisible(true);
	}

	private void expandIrisTree() {
		irisTree.expandRow(0);
		for (int i = irisTree.getRowCount() - 1; i > 0; i--) {
			irisTree.expandRow(i);
		}
		irisTree.updateUI();
	}

	private void newFromImage() {
		currentStandard = BDIFStandard.ANSI;
		currentFlags = 0;

		if (!getOptions(BDIFOptionsFormMode.NEW)) {
			return;
		}
		try {
			if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				imageFromFile = NImage.fromFile(imageOpenFileDialog.getSelectedFile().getPath());

				AddNewIrisImageFrame imageForm = new AddNewIrisImageFrame(this, this);
				showDialogOnScreen(imageForm);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			showError(ex.toString());
		}
	}

	private void open() {
		openTemplate();
		expandIrisTree();
	}

	private void save() {
		if (this.template == null) {
			showWarning("No template to save");
			return;
		}
		try {
			saveTemplate();
		} catch (Exception ex) {
			ex.printStackTrace();
			showError(ex.toString());
		}
	}

	private void addIrisFromImage() {
		if (this.template == null) {
			showWarning("IIRecord is not opened! Open or create new IIRecord.");
			return;
		}
		if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			AddIrisFrame form = new AddIrisFrame(this, this);
			try {
				imageFromFile = NImage.fromFile(imageOpenFileDialog.getSelectedFile().getPath());
				showDialogOnScreen(form);

			} catch (Exception ex) {
				ex.printStackTrace();
				showError(ex.toString());
			}
		}
	}

	private void convert() {
		if (this.template == null) {
			showWarning("IIRecord is not opened! Open or create new IIRecord first");
			return;
		}
		currentStandard = (this.template.getStandard() == BDIFStandard.ANSI) ? BDIFStandard.ISO : BDIFStandard.ANSI;
		currentFlags = 0;
		if (!getOptions(BDIFOptionsFormMode.CONVERT)) {
			return;
		}
		setTemplate(convertToStandard(this.template, currentStandard, currentFlags), this.file, null);
		expandIrisTree();
	}

	private void remove() {
		if (irisTree.getSelectionPath() == null) {
			showWarning("Iris or IrisImage must be selected!");
			return;
		}
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) irisTree.getSelectionPath().getLastPathComponent();
		int selectedRow = irisTree.getSelectionRows()[0];
		if (selected != null && selected.getParent() != irisTreeRoot) {
			Object taggedObject = ((IrisTreeObject) selected.getUserObject()).getTaggedObject();
			IIRIris iris = null;
			if (taggedObject instanceof IIRIris) {
				iris = (IIRIris) taggedObject;
			}
			try {
				if (iris == null) {
					IIRIrisImage iimage = null;
					if (taggedObject instanceof IIRIrisImage) {
						iimage = (IIRIrisImage) taggedObject;
					}
					if (iimage != null) {
						iris = (IIRIris) ((IrisTreeObject) ((DefaultMutableTreeNode)selected.getParent()).getUserObject()).getTaggedObject();
						iris.getIrisImages().remove(iimage);
					}
					((DefaultMutableTreeNode) selected.getParent()).remove(selected);
				} else {
					this.template.getIrises().remove(iris);
					((DefaultMutableTreeNode) selected.getParent()).remove(selected);
				}
				if (selectedRow > 0) {
					irisTree.setSelectionRow(selectedRow - 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.toString());
			}
			irisTree.updateUI();
		} else {
			showWarning("Iris or IrisImage must be selected!");
		}
	}

	private void saveIris() {
		if (irisTree.getSelectionPath() == null) {
			showWarning("Please select iris image");
			return;
		}
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) irisTree.getSelectionPath().getLastPathComponent();
		if (selected == null || selected.getParent() == irisTreeRoot || selected.getParent().getParent() == irisTreeRoot) {
			showWarning("Please select iris image");
			return;
		}
		if (saveImageFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			Object taggedObject = ((IrisTreeObject) selected.getUserObject()).getTaggedObject();
			IIRIrisImage iris = null;
			if (taggedObject instanceof IIRIrisImage) {
				iris = (IIRIrisImage) taggedObject;
			}
			if (iris != null) {
				try {
					NImage image = iris.toNImage();
					FileFilter selectedFilter = saveImageFileDialog.getFileFilter();
					if (selectedFilter instanceof Utils.ImageFileFilter) {
						image.save(saveImageFileDialog.getSelectedFile().getPath() + '.' + ((Utils.ImageFileFilter) saveImageFileDialog.getFileFilter()).getExtensions().get(0));
					} else {
						image.save(saveImageFileDialog.getSelectedFile().getPath());
					}
				} catch (IOException e) {
					e.printStackTrace();
					showError(e.toString());
				}
			}
		}
	}

	private void addIrisImage() {
		if (this.template == null) {
			showWarning("IIRecord must be opened! Open or create new IIRecord");
			return;
		}

		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) irisTree.getSelectionPath().getLastPathComponent();
		if (selected == null || selected.getParent() == irisTreeRoot) {
			showWarning("Iris must be selected before adding IrisImage!");
			return;
		}

		if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				NImage image = NImage.fromFile(imageOpenFileDialog.getSelectedFile().getPath());

				Object taggedObject = ((IrisTreeObject) selected.getUserObject()).getTaggedObject();

				IIRIris iris = null;
				if (taggedObject instanceof IIRIris) {
					iris = (IIRIris) taggedObject;
				}
				IIRIrisImage iimage = null;
				if (iris == null) {
					if (taggedObject instanceof IIRIrisImage) {
						iimage = (IIRIrisImage) taggedObject;
					}
					if (iimage != null) {
						iris = (IIRIris) ((IrisTreeObject) ((DefaultMutableTreeNode)selected.getParent()).getUserObject()).getTaggedObject();
					}
					selected = (DefaultMutableTreeNode) selected.getParent();
				}
				if (iris != null) {
					iimage = iris.getIrisImages().add(image);
					addIrisImage(selected, iimage);
				}
				irisTree.updateUI();
			} catch (Exception ex) {
				ex.printStackTrace();
				showError(ex.toString());
			}
		}
	}

	// ===========================================================
	// Public interface methods
	// ===========================================================

	public void newFromImage(IIRImageFormat imageFormat, BDIFEyePosition eyePosition) {
		try {
			setTemplate(new IIRecord(imageFromFile, imageFormat, eyePosition, currentFlags, currentStandard), null, null);
			expandIrisTree();
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
		}
	}

	public void addFromImage(BDIFEyePosition eyePosition) {
		int number = irisTreeRoot.getChildCount() + 1;
		try {
			IIRIris iris = template.getIrises().add(eyePosition, imageFromFile);
			DefaultMutableTreeNode irisNode = new DefaultMutableTreeNode(new IrisTreeObject(iris, "Iris " + number));
			addIrisImages(irisNode, iris);
			((DefaultMutableTreeNode) irisTreeRoot.getChildAt(0)).add(irisNode);
			irisTree.updateUI();
			expandIrisTree();
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
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

	// ===========================================================
	// Event handling ActionListener
	// ===========================================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == menuItemAbout) {
			AboutBox.show();
		} else if (source == menuItemNewFromImage) {
			newFromImage();
		} else if (source == menuItemOpen) {
			open();
		} else if (source == menuItemSave) {
			save();
		} else if (source == menuItemExit) {
			dispose();
		} else if (source == menuItemAddIris) {
			addIrisFromImage();
		} else if (source == menuItemConvert) {
			convert();
		} else if (source == menuItemRemove) {
			remove();
		} else if (source == menuItemSaveIrisImage) {
			saveIris();
		} else if (source == menuItemAddIrisImage) {
			addIrisImage();
		}

	}

	// ===========================================================
	// Event handling TreeSelectionListener
	// ===========================================================

	public void valueChanged(TreeSelectionEvent e) {
		onSelectedItemChanged();

	}

	// ==================================================================
	// Private class to represent UserObject of tree nodes in irisTree
	// ==================================================================

	private static final class IrisTreeObject {

		// ==============================================
		// Private fields
		// ==============================================

		private final Object taggedObject;
		private final String nodeText;

		// ==============================================
		// Public constructor
		// ==============================================

		IrisTreeObject(Object taggedObject, String nodeText) {
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

}
