package com.neurotec.samples.biometrics.standards.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.nio.ByteBuffer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.neurotec.biometrics.standards.ANImageColorSpace;
import com.neurotec.biometrics.standards.ANImageCompressionAlgorithm;
import com.neurotec.biometrics.standards.ANRecord;
import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANType16Record;
import com.neurotec.biometrics.standards.BDIFScaleUnits;
import com.neurotec.images.NImage;
import com.neurotec.samples.biometrics.standards.events.MainFrameEventListener;

public final class ANType16RecordCreationFrame extends ANRecordCreationFrame {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JTextField txtImageType;
	private ImageLoaderPanel imageLoader;

	// ==============================================
	// Public constructor
	// ==============================================

	public ANType16RecordCreationFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		setPreferredSize(new Dimension(320, 540));
		setTitle("Add Type-16 ANRecord");
		initializeComponents();
		txtImageType.setColumns(ANType16Record.MAX_USER_DEFINED_IMAGE_LENGTH);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {

		JPanel type16Panel = new JPanel();
		GridLayout type16Layout = new GridLayout(1, 2);
		type16Layout.setHgap(4);
		type16Layout.setVgap(4);
		type16Panel.setLayout(type16Layout);

		txtImageType = new JTextField();

		type16Panel.add(new JLabel("User image type:"));
		type16Panel.add(txtImageType);

		imageLoader = new ImageLoaderPanel();
		imageLoader.setBpx(0);
		imageLoader.setColorSpace(ANImageColorSpace.UNSPECIFIED);
		imageLoader.setCompressionAlgorithm(ANImageCompressionAlgorithm.NONE);
		imageLoader.setHasBpx(true);
		imageLoader.setHasColorSpace(true);
		imageLoader.setHll(0);
		imageLoader.setHps(0);
		imageLoader.setScaleUnits(BDIFScaleUnits.NONE);
		imageLoader.setSrc("");
		imageLoader.setVll(0);
		imageLoader.setVps(0);

		Container contentPane = getContentPane();
		contentPane.add(type16Panel);
		contentPane.add(imageLoader);
		type16Panel.setBounds(5, 55, 305, 20);
		imageLoader.setBounds(5, 79, 305, 390);
		getButtonPanel().setBounds(0, 480, 310, 25);
		pack();

	}

	private String getUdi() {
		return txtImageType.getText();
	}

	// ==============================================
	// Protected overridden methods
	// ==============================================

	@Override
	protected ANRecord onCreateRecord(ANTemplate template) {
		String source = imageLoader.getSrc();
		if (source != null) {
			if (imageLoader.isCreateFromImage()) {
				NImage image = imageLoader.getImage();
				if (image != null) {
					return getTemplate().getRecords().addType16(getIdc(), getUdi(), source, imageLoader.getScaleUnits(), imageLoader.getCompressionAlgorithm(), image);
				}
			} else {
				byte[] imageData = imageLoader.getImageData();
				if (imageData != null) {
					return getTemplate().getRecords().addType16(getIdc(), getUdi(), source, (short) imageLoader.getHll(), (short) imageLoader.getVll(), imageLoader.getScaleUnits(), (short) imageLoader.getHps(), (short) imageLoader.getVps(), imageLoader.getCompressionAlgorithm(), (byte) imageLoader.getBpx(), imageLoader.getColorSpace(), ByteBuffer.wrap(imageData));
				}
			}
		}
		return null;
	}

}
