package com.neurotec.samples.biometrics.standards;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import com.neurotec.samples.util.LibraryManager;
import com.neurotec.samples.util.LicenseManager;

public final class ANTemplateSample implements PropertyChangeListener {

	// ===========================================================
	// Private static final fields
	// ===========================================================

	private static final Set<String> LICENSES;

	// ===========================================================
	// Static constructor
	// ===========================================================

	static {
		LICENSES = new HashSet<String>(1);
		LICENSES.add("Biometrics.Standards.Base");
		LICENSES.add("Biometrics.Standards.Fingers");
		LICENSES.add("Biometrics.Standards.FingerTemplates");
		LICENSES.add("Biometrics.Standards.Palms");
		LICENSES.add("Biometrics.Standards.PalmTemplates");
		LICENSES.add("Biometrics.Standards.Irises");
		LICENSES.add("Biometrics.Standards.Faces");
		LICENSES.add("Biometrics.Standards.FingerCardTemplates");
		LICENSES.add("Biometrics.Standards.Other");
		LICENSES.add("Images.LosslessJPEG");
		LICENSES.add("Images.JPEG2000");
	}

	// ===========================================================
	// Public static methods
	// ===========================================================

	public static void main(String[] args) {
		//		LibraryManager.initLibraryPath();
//		System.setProperty("jna.library.path", "F:\\Neurotec_Biometric_4_5_SDK_2013-11-07\\Neurotec_Biometric_4_5_SDK\\Bin\\Win64_x64"); //TODO used to run locally
		LibraryManager.initLibraryPath();
		ANTemplateSample sample = new ANTemplateSample();
		LicenseManager.getInstance().addPropertyChangeListener(sample);
		try {
			LicenseManager.getInstance().obtain(LICENSES);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new MainFrame();
					Dimension d = new Dimension(835, 485);

					frame.setSize(d);
					frame.setMinimumSize(d);
					frame.setPreferredSize(d);

					frame.setResizable(true);
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					frame.setTitle("ANSI/NIST File Editor");
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.toString());
				}
			}
		});
	}

	// ===========================================================
	// Private fields
	// ===========================================================

	private final ProgressMonitor progressMonitor;

	// ===========================================================
	// Private methods
	// ===========================================================

	private ANTemplateSample() {
		progressMonitor = new ProgressMonitor(null, "License obtain", "", 0, LICENSES.size());
	}

	// ===========================================================
	// Event handling
	// ===========================================================

	public void propertyChange(PropertyChangeEvent evt) {
		if (LicenseManager.PROGRESS_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message = String.format("# of analyzed licenses: %d\n", progress);
			progressMonitor.setNote(message);
		}
	}
}
