package com.neurotec.samples.biometrics.standards;

import com.neurotec.biometrics.standards.FCRImageColorSpace;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class RawFaceImageOptionsFrame extends AddFaceImageFrame implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JComboBox cmbImageColorSpace;
	private JTextField txtWidth;
	private JTextField txtHeight;
	private JTextField txtVendorImageColorSpace;

	// ==============================================
	// Private fields
	// ==============================================

	private RawFaceImageOptionsFormMode mode = RawFaceImageOptionsFormMode.LOAD;

	// ==============================================
	// Public constructor
	// ==============================================

	public RawFaceImageOptionsFrame(Frame owner, MainFrameEventListener listener) {
		super(owner, listener);
		this.setTitle("RawFaceImageOptionsFrame");
		this.setPreferredSize(new Dimension(300, 275));
		initializeComponents();

		for (FCRImageColorSpace imageColrSpace : FCRImageColorSpace.values()) {
			cmbImageColorSpace.addItem(imageColrSpace.name());
		}
		cmbImageColorSpace.setSelectedIndex(0);
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		cmbImageColorSpace = new JComboBox();
		cmbImageColorSpace.addActionListener(this);

		txtWidth = new JTextField("0");
		txtWidth.setInputVerifier(new WidthHeightInputVerifier());

		txtHeight = new JTextField("0");
		txtHeight.setInputVerifier(new WidthHeightInputVerifier());

		txtVendorImageColorSpace = new JTextField("0");
		txtVendorImageColorSpace.setInputVerifier(new VendorImageColorSpaceInputVerifier());

		JPanel comboPanel = getComboPanel();
		getComboPanelLayout().rowHeights = new int[] {20, 25, 25, 25, 25, 25, 25, 20};
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 3;
		comboPanel.add(new JLabel("Face image color space:"), c);

		c.gridx = 1;
		c.gridy = 3;
		comboPanel.add(cmbImageColorSpace, c);

		c.gridx = 0;
		c.gridy = 4;
		comboPanel.add(new JLabel("Width:"), c);

		c.gridx = 1;
		c.gridy = 4;
		comboPanel.add(txtWidth, c);

		c.gridx = 0;
		c.gridy = 5;
		comboPanel.add(new JLabel("Height:"), c);

		c.gridx = 1;
		c.gridy = 5;
		comboPanel.add(txtHeight, c);

		c.gridx = 0;
		c.gridy = 6;
		comboPanel.add(new JLabel("Vendor image color space"), c);

		c.gridx = 1;
		c.gridy = 6;
		comboPanel.add(txtVendorImageColorSpace, c);

		comboPanel.setBounds(12, 5, 270, 200);
		getButtonPanel().setBounds(12, 210, 270, 25);

		this.pack();
	}

	private void onModeChanged() {
		switch (mode) {
		case LOAD:
			setTitle("Add face from data");
			break;
		case SAVE:
			setTitle("Save face as data");
			break;
		default:
			throw new AssertionError("Unknown mode: " + mode);
		}
	}

	// ==============================================
	// Overridden protected methods
	// ==============================================

	@Override
	protected void buttonOKActionPerformed() {
		getOwnerFrameListener().addImageFromData(getFaceImageType(), getImageDataType(), getImageWidth(), getImageHeight(), getImageColorSpace(), getVendorColorSpace());
	}

	// ==============================================
	// Public getters and setters
	// ==============================================

	public RawFaceImageOptionsFormMode getMode() {
		return mode;
	}

	public void setMode(RawFaceImageOptionsFormMode mode) {
		this.mode = mode;
		onModeChanged();
	}

	public FCRImageColorSpace getImageColorSpace() {
		return FCRImageColorSpace.valueOf((String) cmbImageColorSpace.getSelectedItem());
	}

	public void setImageColorSpace(FCRImageColorSpace imageColorSpace) {
		cmbImageColorSpace.setSelectedItem(imageColorSpace.name());
	}

	public int getImageWidth() {
		return Integer.parseInt(txtWidth.getText());
	}

	public void setImageWidth(int value) {
		txtWidth.setText(String.valueOf(value));
	}

	public int getImageHeight() {
		return Integer.parseInt(txtHeight.getText());
	}

	public void setImageHeight(int value) {
		txtHeight.setText(String.valueOf(value));
	}

	public int getVendorColorSpace() {
		return Integer.parseInt(txtVendorImageColorSpace.getText());
	}

	// ==============================================
	// Event handling
	// ==============================================

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == cmbImageColorSpace) {
			if (FCRImageColorSpace.valueOf((String) cmbImageColorSpace.getSelectedItem()) == FCRImageColorSpace.VENDOR) {
				if (!txtVendorImageColorSpace.isEnabled()) {
					txtVendorImageColorSpace.setEnabled(true);
				}
			} else {
				txtVendorImageColorSpace.setEnabled(false);
				txtVendorImageColorSpace.setText("0");
			}
		} else {
			super.actionPerformed(e);
		}

	}

	// ==============================================
	// Private enum
	// ==============================================

	private enum RawFaceImageOptionsFormMode { LOAD, SAVE };

	// ============================================================================
	// Private class extending InputVerifier to verify width and height fields
	// ============================================================================

	private final class WidthHeightInputVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			if (input == null) {
				return false;
			}
			try {
				Integer.parseInt(((JTextField) input).getText());
				return true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(RawFaceImageOptionsFrame.this, "Image width and height must be valid integer numbers.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

	}

	// ================================================================================
	// Private class extending InputVerifier to verify venderImageColorSpace field
	// ================================================================================

	private final class VendorImageColorSpaceInputVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			if (input == null) {
				return false;
			}
			try {
				Integer.parseInt(((JTextField) input).getText());
				return true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(RawFaceImageOptionsFrame.this, "Vendor image color space value must be a valid integer number.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

	}

}
