package com.neurotec.samples.biometrics.standards;

import com.neurotec.samples.util.LibraryManager;
import com.neurotec.samples.util.LicenseManager;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

public final class FIRecordSample implements PropertyChangeListener {

	// ===========================================================
	// Private static final fields
	// ===========================================================

	private static final Set<String> LICENSES;

	// ===========================================================
	// Static constructor
	// ===========================================================

	static {
		LICENSES = new HashSet<String>(1);
		LICENSES.add("Biometrics.Standards.Fingers");
	}

	// ===========================================================
	// Public main method
	// ===========================================================

	public static void main(String[] args) {
		LibraryManager.initLibraryPath();
		FIRecordSample sample = new FIRecordSample();
		LicenseManager.getInstance().addPropertyChangeListener(sample);
		try {
			LicenseManager.getInstance().obtain(LICENSES);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new MainFrame();
					Dimension d = new Dimension(960, 580);

					frame.setSize(d);
					frame.setMinimumSize(d);
					frame.setPreferredSize(d);

					frame.setResizable(true);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setTitle("FIRecord Editor");
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

	private FIRecordSample() {
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
