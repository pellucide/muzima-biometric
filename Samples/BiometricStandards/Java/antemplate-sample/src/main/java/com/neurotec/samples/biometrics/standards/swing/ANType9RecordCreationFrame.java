package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.neurotec.biometrics.NFRecord;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.BDIFFPImpressionType;
import com.neurotec.samples.biometrics.standards.ANTemplateSettings;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType9RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;
	private static final String NFRECORD_OPEN_FILE_FILTER_STRING = "All Supported Files (*.dat)|*.dat|NFRecord Files (*.dat)|*.dat|All Files (*.*)|*.*";

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JPanel fromNFRecordPanel;
	private JPanel createEmptyPanel;
	private JRadioButton radioFromNFRecord;
	private JRadioButton radioCreateEmpty;
	private JTextField txtNFRecordPath;
	private JCheckBox chkFormatFlag;
	private JComboBox cmbImpressionType;
	private JCheckBox chkContainsStandardMinutiae;
	private JCheckBox chkHasRidgeCountsIndicator;
	private JCheckBox chkContainsRidgeCounts;
	private JButton btnBrowse;
	private JFileChooser nfRecordOpenFileDialog;
	private JLabel lblNFRecordPath;
	private JLabel lblImpressionType;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType9RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(340, 340));
		setTitle("Add Type-9 ANRecord");
		initializeComponents();
		radioFromNFRecord.setSelected(true);

		enableFromNFRecordPanel(radioFromNFRecord.isSelected());
		enableCreateEmptyPanel(radioCreateEmpty.isSelected());

		for (BDIFFPImpressionType value : BDIFFPImpressionType.values()) {
			cmbImpressionType.addItem(value);
		}
		cmbImpressionType.setSelectedIndex(0);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		nfRecordOpenFileDialog = new JFileChooser();
		setFileFilters(nfRecordOpenFileDialog, NFRECORD_OPEN_FILE_FILTER_STRING);

		JPanel type9Panel = new JPanel();
		type9Panel.setLayout(new BoxLayout(type9Panel, BoxLayout.Y_AXIS));

		chkFormatFlag = new JCheckBox("Minutia format is standard");
		chkFormatFlag.setSelected(true);

		radioFromNFRecord = new JRadioButton("From NFRecord:");
		radioFromNFRecord.addActionListener(this);
		radioCreateEmpty = new JRadioButton("Create empty:");
		radioCreateEmpty.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(radioFromNFRecord);
		group.add(radioCreateEmpty);

		fromNFRecordPanel = new JPanel();
		fromNFRecordPanel.setLayout(new BoxLayout(fromNFRecordPanel, BoxLayout.X_AXIS));
		fromNFRecordPanel.setPreferredSize(new Dimension(310, 25));
		fromNFRecordPanel.setMaximumSize(new Dimension(310, 25));

		lblNFRecordPath = new JLabel("NFRecord file:");
		txtNFRecordPath = new JTextField();
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(this);

		fromNFRecordPanel.add(Box.createHorizontalStrut(5));
		fromNFRecordPanel.add(lblNFRecordPath);
		fromNFRecordPanel.add(txtNFRecordPath);
		fromNFRecordPanel.add(Box.createHorizontalStrut(5));
		fromNFRecordPanel.add(btnBrowse);

		createEmptyPanel = new JPanel();
		GridBagLayout emptyPanelLayout = new GridBagLayout();
		emptyPanelLayout.columnWidths = new int[] { 25, 70, 160 };
		createEmptyPanel.setLayout(emptyPanelLayout);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		lblImpressionType = new JLabel("Impression type:");
		cmbImpressionType = new JComboBox();

		chkContainsStandardMinutiae = new JCheckBox("Contains standard minutiae");
		chkContainsStandardMinutiae.setSelected(true);

		chkHasRidgeCountsIndicator = new JCheckBox("Has ridge counts indicator");
		chkHasRidgeCountsIndicator.setSelected(true);

		chkContainsRidgeCounts = new JCheckBox("Contains ridge counts");

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		createEmptyPanel.add(lblImpressionType, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 1.0;
		c.gridwidth = 1;
		createEmptyPanel.add(cmbImpressionType, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weightx = 0;
		createEmptyPanel.add(chkContainsStandardMinutiae, c);

		c.gridx = 0;
		c.gridy = 2;
		createEmptyPanel.add(chkHasRidgeCountsIndicator, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		createEmptyPanel.add(chkContainsRidgeCounts, c);

		type9Panel.add(chkFormatFlag);
		chkFormatFlag.setAlignmentX(LEFT_ALIGNMENT);
		type9Panel.add(Box.createVerticalStrut(10));
		type9Panel.add(radioFromNFRecord);
		radioFromNFRecord.setAlignmentX(LEFT_ALIGNMENT);
		type9Panel.add(fromNFRecordPanel);
		fromNFRecordPanel.setAlignmentX(LEFT_ALIGNMENT);
		type9Panel.add(Box.createVerticalStrut(10));
		type9Panel.add(radioCreateEmpty);
		radioCreateEmpty.setAlignmentX(LEFT_ALIGNMENT);
		type9Panel.add(createEmptyPanel);
		createEmptyPanel.setAlignmentX(LEFT_ALIGNMENT);
		type9Panel.add(Box.createVerticalGlue());

		getContentPane().add(type9Panel);
		type9Panel.setBounds(10, 55, 300, 220);
		getButtonPanel().setBounds(10, 275, 300, 25);
		pack();

	}

	private void enableFromNFRecordPanel(boolean enabled) {
		lblNFRecordPath.setEnabled(enabled);
		txtNFRecordPath.setEnabled(enabled);
		btnBrowse.setEnabled(enabled);
	}

	private void enableCreateEmptyPanel(boolean enabled) {
		lblImpressionType.setEnabled(enabled);
		cmbImpressionType.setEnabled(enabled);
		chkContainsStandardMinutiae.setEnabled(enabled);
		chkHasRidgeCountsIndicator.setEnabled(enabled);
		chkContainsRidgeCounts.setEnabled(enabled);
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

	// ==============================================
	// Protected methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {
		if (radioFromNFRecord.isSelected()) {
			String recordPath = txtNFRecordPath.getText();
			if (recordPath == null || recordPath.isEmpty()) {
				JOptionPane.showMessageDialog(this, "File path can not be empty");
				return null;
			}

			File f = new File(recordPath);
			if (!f.exists()) {
				return null;
			}

			byte[] nfRecordData = new byte[(int) f.length()];
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(new FileInputStream(f));
				dis.readFully(nfRecordData);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, String.format("Could not load NFRecord from %s", recordPath));
			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			NFRecord nfrecord = new NFRecord(ByteBuffer.wrap(nfRecordData));
			return getTemplate().getRecords().addType9(getIdc(), chkFormatFlag.isSelected(), nfrecord);
		}
		return getTemplate().getRecords().addType9(getIdc(), (BDIFFPImpressionType) cmbImpressionType.getSelectedItem(), chkFormatFlag.isSelected(), chkContainsStandardMinutiae.isSelected(), chkContainsRidgeCounts.isSelected(), chkHasRidgeCountsIndicator.isSelected());
	}

	// ==============================================
	// Public methods
	// ==============================================

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == radioFromNFRecord || source == radioCreateEmpty) {
			enableFromNFRecordPanel(radioFromNFRecord.isSelected());
			enableCreateEmptyPanel(radioCreateEmpty.isSelected());
		} else if (source == btnBrowse) {
			ANTemplateSettings settings = ANTemplateSettings.getInstance();
			nfRecordOpenFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
			if (nfRecordOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				settings.setLastDirectory(nfRecordOpenFileDialog.getSelectedFile().getParentFile().getPath());
				txtNFRecordPath.setText(nfRecordOpenFileDialog.getSelectedFile().getPath());
			}
		}
		super.actionPerformed(e);
	}

}
