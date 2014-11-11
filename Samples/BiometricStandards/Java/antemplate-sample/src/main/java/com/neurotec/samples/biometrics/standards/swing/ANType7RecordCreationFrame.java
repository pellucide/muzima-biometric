package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANType1Record;
import com.neurotec.samples.biometrics.standards.ANTemplateSettings;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType7RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private ResolutionEditBox isrResEditBox;
	private ResolutionEditBox irResEditBox;
	private JTextField txtImageDataPath;
	private JButton btnBrowseData;
	private JFileChooser imageDataOpenFileDialog = new JFileChooser();

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType7RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(300, 280));
		setTitle("Add Type-7 ANRecord");
		initializeComponents();

		isrResEditBox.setPpmValue(ANType1Record.MIN_SCANNING_RESOLUTION);
		irResEditBox.setPpmValue(ANType1Record.MIN_SCANNING_RESOLUTION);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		JPanel type7Panel = new JPanel();
		type7Panel.setLayout(new BoxLayout(type7Panel, BoxLayout.Y_AXIS));

		isrResEditBox = new ResolutionEditBox();
		irResEditBox = new ResolutionEditBox();

		JPanel imageDataBrowsePanel = new JPanel();
		imageDataBrowsePanel.setLayout(new BoxLayout(imageDataBrowsePanel, BoxLayout.X_AXIS));

		txtImageDataPath = new JTextField();
		btnBrowseData = new JButton("Browse...");
		btnBrowseData.addActionListener(this);

		imageDataBrowsePanel.add(txtImageDataPath);
		imageDataBrowsePanel.add(Box.createHorizontalStrut(3));
		imageDataBrowsePanel.add(btnBrowseData);

		JLabel lblScanningResolution = new JLabel("Image scanning resolution:");
		type7Panel.add(lblScanningResolution);
		lblScanningResolution.setAlignmentX(LEFT_ALIGNMENT);

		type7Panel.add(isrResEditBox);
		isrResEditBox.setAlignmentX(LEFT_ALIGNMENT);
		type7Panel.add(Box.createVerticalStrut(4));

		JLabel lblNativeResolution = new JLabel("Native resolution:");
		type7Panel.add(lblNativeResolution);
		lblNativeResolution.setAlignmentX(LEFT_ALIGNMENT);

		type7Panel.add(irResEditBox);
		irResEditBox.setAlignmentX(LEFT_ALIGNMENT);
		type7Panel.add(Box.createVerticalStrut(4));

		JLabel lblImageData = new JLabel("Image data:");
		type7Panel.add(lblImageData);
		lblImageData.setAlignmentX(LEFT_ALIGNMENT);

		type7Panel.add(imageDataBrowsePanel);
		imageDataBrowsePanel.setAlignmentX(LEFT_ALIGNMENT);

		getContentPane().add(type7Panel);
		type7Panel.setBounds(10, 55, 280, 120);
		getButtonPanel().setBounds(10, 215, 280, 25);
		pack();

	}

	// ==============================================
	// Protected overridden methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {

		String imagePath = txtImageDataPath.getText();
		if (imagePath == null || imagePath.isEmpty()) {
			JOptionPane.showMessageDialog(this, "File path can not be empty");
			return null;
		}

		File f = new File(imagePath);
		if (f.exists()) {
			byte[] data = new byte[(int) f.length()];
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(new FileInputStream(f));
				dis.readFully(data);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.toString());
			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return template.getRecords().addType7(getIdc(), (int) Math.round(isrResEditBox.getPpmValue()), (int) Math.round(irResEditBox.getPpmValue()), ByteBuffer.wrap(data));
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnBrowseData) {
			ANTemplateSettings settings = ANTemplateSettings.getInstance();
			imageDataOpenFileDialog.setCurrentDirectory(new File(settings.getLastDirectory()));
			if (imageDataOpenFileDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				settings.setLastDirectory(imageDataOpenFileDialog.getSelectedFile().getParentFile().getPath());
				txtImageDataPath.setText(imageDataOpenFileDialog.getSelectedFile().getPath());
			}
		}
		super.actionPerformed(e);
	}

}
