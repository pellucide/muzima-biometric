package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.BDIFStandard;
import com.neurotec.biometrics.standards.FCRFaceImage;
import com.neurotec.biometrics.standards.FCRFaceImageType;
import com.neurotec.biometrics.standards.FCRImageColorSpace;
import com.neurotec.biometrics.standards.FCRImageDataType;
import com.neurotec.biometrics.standards.FCRecord;
import com.neurotec.biometrics.standards.swing.FCView;
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
import java.awt.GridLayout;
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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JFileChooser imageOpenFileDialog;
	private JFileChooser rawImageOpenFileDialog;
	private JFileChooser saveImageFileDialog;
	private JFileChooser fcRecordOpenFileDialog;
	private JFileChooser fcRecordSaveFileDialog;
	private JFileChooser saveRawFileDialog;

	private JTree faceTree;
	private DefaultMutableTreeNode faceTreeRoot;
	private JPanel panelImage;
	private FCView imageView;
	private NPropertyGrid propertyGrid;

	private JMenuItem menuItemAbout;
	private JMenuItem menuItemSaveAsData;
	private JMenuItem menuItemSaveAsImage;
	private JMenuItem menuItemAddFromImage;
	private JMenuItem menuItemAddFromData;
	private JMenuItem menuItemRemove;
	private JMenuItem menuItemConvert;
	private JMenuItem menuItemNew;
	private JMenuItem menuItemOpen;
	private JMenuItem menuItemSave;
	private JMenuItem menuItemExit;

	// ==============================================
	// Private fields
	// ==============================================

	private FCRecord template;
	private File file;
	private File openedFile;
	private BDIFStandard currentStandard;
	private int currentFlags;
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

		addWindowListener(new WindowAdapter() {

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
		newTemplate(BDIFStandard.ISO);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		createMenuBar();

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		faceTreeRoot = new DefaultMutableTreeNode();
		faceTree = new JTree(faceTreeRoot);
		faceTree.addTreeSelectionListener(this);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		faceTree.setCellRenderer(renderer);
		faceTree.setShowsRootHandles(true);
		JScrollPane faceTreeScrollPane = new JScrollPane(faceTree);

		propertyGrid = new NPropertyGrid(true, new FCPropertiesTable());

		imageView = new FCView();
		JScrollPane imageScrollPane = new JScrollPane();
		panelImage = new JPanel();
		panelImage.setLayout(new GridLayout(1, 1));
		panelImage.add(imageScrollPane);
		imageScrollPane.setViewportView(imageView);

		leftSplitPane.setLeftComponent(faceTreeScrollPane);
		leftSplitPane.setRightComponent(propertyGrid);
		leftSplitPane.setDividerLocation(185);

		mainSplitPane.setLeftComponent(leftSplitPane);
		mainSplitPane.setRightComponent(panelImage);
		mainSplitPane.setDividerLocation(250);

		this.getContentPane().add(mainSplitPane);
		this.pack();
	}

	private void initializeFileChoosers() {
		imageOpenFileDialog = new JFileChooser();
		rawImageOpenFileDialog = new JFileChooser();
		saveImageFileDialog = new JFileChooser();
		fcRecordOpenFileDialog = new JFileChooser();
		fcRecordSaveFileDialog = new JFileChooser();
		saveRawFileDialog = new JFileChooser();

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

		rawImageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG().getFileFilter(), "JPEG files"));
		rawImageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG2K().getFileFilter(), "JPEG 2000 files"));
		rawImageOpenFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG().getFileFilter() + ";" + NImageFormat.getJPEG2K().getFileFilter(), "All supported files"));

		fcRecordSaveFileDialog.addChoosableFileFilter(new Utils.TemplateFileFilter("FCRecord files"));

		fcRecordOpenFileDialog.addChoosableFileFilter(new Utils.TemplateFileFilter("FCRecord files"));
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");

		menuItemNew = new JMenuItem("New ...");
		menuItemNew.addActionListener(this);
		menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menuItemNew.setMnemonic(KeyEvent.VK_N);

		menuItemOpen = new JMenuItem("Open ...");
		menuItemOpen.addActionListener(this);
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menuItemOpen.setMnemonic(KeyEvent.VK_O);

		menuItemSave = new JMenuItem("Save ...");
		menuItemSave.addActionListener(this);
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		menuItemSave.setMnemonic(KeyEvent.VK_S);

		menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);

		menuFile.add(menuItemNew);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		menuFile.addSeparator();
		menuFile.add(menuItemExit);

		JMenu menuEdit = new JMenu("Edit");

		menuItemAddFromImage = new JMenuItem("Add face from image ...");
		menuItemAddFromImage.addActionListener(this);

		menuItemAddFromData = new JMenuItem("Add face from data ...");
		menuItemAddFromData.addActionListener(this);

		menuItemRemove = new JMenuItem("Remove face ...");
		menuItemRemove.addActionListener(this);

		menuItemSaveAsImage = new JMenuItem("Save face as image ...");
		menuItemSaveAsImage.addActionListener(this);
		menuItemSaveAsData = new JMenuItem("Save face as data ...");
		menuItemSaveAsData.addActionListener(this);

		menuItemConvert = new JMenuItem("Convert ...");
		menuItemConvert.addActionListener(this);

		menuEdit.add(menuItemAddFromImage);
		menuEdit.add(menuItemAddFromData);
		menuEdit.addSeparator();
		menuEdit.add(menuItemRemove);
		menuEdit.addSeparator();
		menuEdit.add(menuItemSaveAsImage);
		menuEdit.add(menuItemSaveAsData);
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

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, this.getTitle(), JOptionPane.ERROR_MESSAGE);
	}

	private void setTemplate(FCRecord template, File f) {
		setTemplate(template, f, "");
	}

	private void setTemplate(FCRecord template, File f, String version) {
		boolean newTemplate = this.template != template;
		this.template = template;
		this.file = f;
		String fileName = null;
		if (f != null) {
			fileName = f.getName();
		}
		faceTreeRoot.removeAllChildren();
		if (newTemplate) {
			faceTree.setRootVisible(true);
			FaceTreeObject userObject = new FaceTreeObject(template, (fileName == null ? "Untitled" : version == null ? fileName : fileName + version));
			DefaultMutableTreeNode templateNode = new DefaultMutableTreeNode(userObject);
			if (template.getFaceImages() != null) {
				addFaceImages(templateNode, template);
			}
			faceTreeRoot.add(templateNode);
			faceTree.updateUI();
			expandFaceTree();
			faceTree.setRootVisible(false);
			faceTree.setSelectionRow(0);
		}
		faceTree.updateUI();
	}

	private void newTemplate() {
		currentStandard = BDIFStandard.ANSI;
		currentFlags = 0;
		getOptions(BDIFOptionsFormMode.NEW);
		if (!isDialogResultOK) {
			return;
		}
		newTemplate(currentStandard, currentFlags);
	}

	private void newTemplate(BDIFStandard standard) {
		newTemplate(standard, 0);
	}

	private void newTemplate(BDIFStandard standard, int flags) {
		try {
			setTemplate(new FCRecord(flags, standard), null, null);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
		}
	}

	private void openTemplate() {
		byte[] templ = loadTemplate(fcRecordOpenFileDialog);
		if (templ == null) {
			return;
		}

		currentStandard = BDIFStandard.ANSI;
		currentFlags = 0;
		getOptions(BDIFOptionsFormMode.OPEN);
		if (!isDialogResultOK) {
			return;
		}

		FCRecord fcrecord;
		try {
			fcrecord = new FCRecord(NBuffer.fromArray(templ), currentFlags, currentStandard);
		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Failed to load template! Reason:\r\n" + ex);
			return;
		}
		setTemplate(fcrecord, openedFile);
	}

	private void saveTemplate() {
		saveTemplate(fcRecordSaveFileDialog, file, template.save());
	}

	private void onSelectedItemChanged() {
		FCRFaceImage faceImage;
		if (faceTree.getSelectionPath() == null) {
			faceImage = null;
		} else {
			FaceTreeObject userObject = (FaceTreeObject) ((DefaultMutableTreeNode) faceTree.getSelectionPath().getLastPathComponent()).getUserObject();
			if (userObject.getTaggedObject() instanceof FCRFaceImage) {
				faceImage = (FCRFaceImage) userObject.getTaggedObject();
			} else {
				faceImage = null;
			}
		}
		imageView.setRecord(faceImage);
		if (faceImage == null) {
			if (faceTree.getSelectionPath() != null) {
				propertyGrid.setSource(((FaceTreeObject) ((DefaultMutableTreeNode) faceTree.getSelectionPath().getLastPathComponent()).getUserObject()).getTaggedObject());
			}
		} else {
			propertyGrid.setSource(faceImage);
		}
	}

	private void saveAsData() {
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) faceTree.getSelectionPath().getLastPathComponent();
		if ((selected != null) && (selected.getParent() != faceTreeRoot)) {
			FaceTreeObject userObject = (FaceTreeObject) selected.getUserObject();
			FCRFaceImage img = null;
			if (userObject.getTaggedObject() instanceof FCRFaceImage) {
				img = (FCRFaceImage) userObject.getTaggedObject();
			}
			if (img != null) {
				String extension;
				saveRawFileDialog.setAcceptAllFileFilterUsed(false);
				if (img.getImageDataType() == FCRImageDataType.JPEG) {
					saveRawFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG().getFileFilter(), "JPEG files"));
					extension = NImageFormat.getJPEG().getDefaultFileExtension();
				} else {
					saveRawFileDialog.addChoosableFileFilter(new Utils.ImageFileFilter(NImageFormat.getJPEG2K().getFileFilter(), "JPEG 2000 files"));
					extension = NImageFormat.getJPEG2K().getDefaultFileExtension();
				}

				if (saveRawFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					try {
						String savePath = saveRawFileDialog.getSelectedFile().getPath();
						if (savePath.lastIndexOf('.') == -1) {
							savePath = savePath + '.' + extension;
						}
						NFile.writeAllBytes(savePath, img.getImageData());
					} catch (Exception ex) {
						ex.printStackTrace();
						showError("Failed to save face to data.\r\nReason: " + ex);
					}
				}
			}
		}
	}

	private void saveAsImage() {
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) faceTree.getSelectionPath().getLastPathComponent();
		if ((selected != null) && (selected.getParent() != faceTreeRoot)) {
			if (saveImageFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					FaceTreeObject userObject = (FaceTreeObject) selected.getUserObject();
					FCRFaceImage img = null;
					if (userObject.getTaggedObject() instanceof FCRFaceImage) {
						img = (FCRFaceImage) userObject.getTaggedObject();
					}
					if (img != null) {
						NImage image = img.toNImage();
						FileFilter selectedFilter = saveImageFileDialog.getFileFilter();
						if (selectedFilter instanceof Utils.ImageFileFilter) {
							image.save(saveImageFileDialog.getSelectedFile().getPath() + '.' + ((Utils.ImageFileFilter) saveImageFileDialog.getFileFilter()).getExtensions().get(0));
						} else {
							image.save(saveImageFileDialog.getSelectedFile().getPath());
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					showError("Failed to save image to file!\r\nReason: " + ex);
				}
			}
		}
	}

	private void addFaceFromImage() {
		if (imageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			FCRImageDataType dataType;
			AddFaceImageFrame form = new AddFaceImageFrame(this, this);

			String extension = getExtension(imageOpenFileDialog.getSelectedFile());
			if (extension != null && (NImageFormat.getJPEG2K().getFileFilter()).contains(extension)) {
				dataType = FCRImageDataType.JPEG_2000;
			} else {
				dataType = FCRImageDataType.JPEG;
			}
			form.setImageDataType(dataType);
			showDialogOnScreen(form);

		}
	}

	private String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private void addFaceFromData() {
		if (rawImageOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			FCRImageDataType dataType;

			RawFaceImageOptionsFrame form = new RawFaceImageOptionsFrame(this, this);

			String extension = getExtension(rawImageOpenFileDialog.getSelectedFile());
			if (extension != null && (NImageFormat.getJPEG2K().getFileFilter()).contains(extension)) {
				dataType = FCRImageDataType.JPEG_2000;
			} else {
				dataType = FCRImageDataType.JPEG;
			}
			form.setImageDataType(dataType);
			showDialogOnScreen(form);

		}
	}

	private void remove() {
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode) faceTree.getSelectionPath().getLastPathComponent();
		if ((selected != null) && (selected.getParent() != faceTreeRoot)) {
			try {
				FCRFaceImage img = (FCRFaceImage) ((FaceTreeObject) selected.getUserObject()).getTaggedObject();
				template.getFaceImages().remove(img);
				((DefaultMutableTreeNode) selected.getParent()).remove(selected);
				faceTree.updateUI();
				faceTree.setSelectionRow(0);
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.toString());
			}
		}
	}

	private void convert() {
		currentStandard = (template.getStandard() == BDIFStandard.ANSI) ? BDIFStandard.ISO : BDIFStandard.ANSI;
		currentFlags = 0;
		getOptions(BDIFOptionsFormMode.CONVERT);
		if (!isDialogResultOK) {
			return;
		}
		setTemplate(convertToStandard(template, currentStandard, currentFlags), file, null);
	}

	private void showDialogOnScreen(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width / 2 - dialog.getPreferredSize().width / 2, screenSize.height / 2 - dialog.getPreferredSize().height / 2);
		dialog.setVisible(true);
	}

	private void getOptions(BDIFOptionsFormMode mode) {
		FCRecordOptionsFrame form = new FCRecordOptionsFrame(MainFrame.this, MainFrame.this);

		form.setStandard(currentStandard);
		form.setFlags(currentFlags);
		form.setMode(mode);
		showDialogOnScreen(form);
	}

	private byte[] loadTemplate(JFileChooser openFileDialog) {
		openFileDialog.setSelectedFile(null);
		if (openFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			openedFile = openFileDialog.getSelectedFile();
			byte[] fileData = new byte[(int) openedFile.length()];
			try {
				DataInputStream dis = new DataInputStream(new FileInputStream(openedFile));
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
		openedFile = null;
		return null;
	}

	private void saveTemplate(JFileChooser saveFileDialog, File file, NBuffer template) {
		if (file != null) {
			saveFileDialog.setSelectedFile(file);
		}
		if (saveFileDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				String savePath = saveFileDialog.getSelectedFile().getPath();
				if (saveFileDialog.getFileFilter().getDescription().equals("FCRecord Files (*.dat)")) {
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

	private DefaultMutableTreeNode addFaceImage(DefaultMutableTreeNode facesNode, FCRFaceImage faceImage) {
		int index = facesNode.getChildCount();
		DefaultMutableTreeNode recordNode = new DefaultMutableTreeNode(new FaceTreeObject(faceImage, "FaceImage " + index));
		facesNode.add(recordNode);
		return recordNode;
	}

	private void expandFaceTree() {
		faceTree.expandRow(0);
		for (int i = faceTree.getRowCount() - 1; i > 0; i--) {
			faceTree.expandRow(i);
		}
		faceTree.updateUI();
	}

	private DefaultMutableTreeNode addFaceImages(DefaultMutableTreeNode templateNode, FCRecord record) {
		for (FCRFaceImage faceImage : record.getFaceImages()) {
			addFaceImage(templateNode, faceImage);
		}
		return templateNode;
	}

	private FCRecord convertToStandard(FCRecord record, BDIFStandard newStandard, int flags) {
		if (record.getStandard() == newStandard) {
			return record;
		}

		try {
			return new FCRecord(record, flags, newStandard);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
			return null;
		}
	}

	// ==============================================
	// Public interface methods MainFrameEventListener
	// ==============================================

	public void addImage(FCRFaceImageType imageType, FCRImageDataType dataType) {
		try {
			NImage image = NImage.fromFile(imageOpenFileDialog.getSelectedFile().getPath());

			FCRFaceImage img = template.getFaceImages().add(imageType, dataType, image);
			addFaceImage((DefaultMutableTreeNode) faceTreeRoot.getChildAt(0), img);
			faceTree.updateUI();
			expandFaceTree();
			faceTree.setSelectionRow(faceTree.getRowCount() - 1);

		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Failed to add image. Reason:\r\n" + ex);
		}
	}

	public void addImageFromData(FCRFaceImageType imageType, FCRImageDataType dataType, int width, int height, FCRImageColorSpace imageColorSpace, int vendorImageColorSpace) {
		try {
			File dataFile = rawImageOpenFileDialog.getSelectedFile();

			FileInputStream fIn = new FileInputStream(dataFile);
			FileChannel fChan = fIn.getChannel();
			long fSize = fChan.size();
			ByteBuffer mBuf = ByteBuffer.allocate((int) fSize);
			fChan.read(mBuf);
			mBuf.rewind();

			FCRFaceImage img = template.getFaceImages().add(imageType, dataType, (short) width, (short) height, imageColorSpace, (byte) vendorImageColorSpace, mBuf);
			fChan.close();
			fIn.close();
			addFaceImage((DefaultMutableTreeNode) faceTreeRoot.getChildAt(0), img);
			faceTree.updateUI();
			expandFaceTree();
			faceTree.setSelectionRow(faceTree.getRowCount() - 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Failed to add image. Reason:\r\n" + ex);
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
		if (source == menuItemSaveAsData) {
			saveAsData();
		} else if (source == menuItemSaveAsImage) {
			saveAsImage();
		} else if (source == menuItemAddFromImage) {
			addFaceFromImage();
		} else if (source == menuItemAddFromData) {
			addFaceFromData();
		} else if (source == menuItemRemove) {
			remove();
		} else if (source == menuItemConvert) {
			convert();
		} else if (source == menuItemAbout) {
			AboutBox.show();
		} else if (source == menuItemNew) {
			newTemplate();
		} else if (source == menuItemOpen) {
			openTemplate();
		} else if (source == menuItemSave) {
			try {
				saveTemplate();
			} catch (Exception ex) {
				ex.printStackTrace();
				showError(ex.toString());
			}
		} else if (source == menuItemExit) {
			dispose();
		}

	}

	// ==============================================
	// Event handling TreeSelectionListener
	// ==============================================

	public void valueChanged(TreeSelectionEvent e) {
		onSelectedItemChanged();
	}

	// ==================================================================
	// Private class to represent UserObject of tree nodes in faceTree
	// ==================================================================

	private static final class FaceTreeObject {

		// ==============================================
		// Private fields
		// ==============================================

		private final Object taggedObject;
		private final String nodeText;

		// ==============================================
		// Public constructor
		// ==============================================

		FaceTreeObject(Object taggedObject, String nodeText) {
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
