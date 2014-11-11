package com.neurotec.samples.biometrics.standards;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.neurotec.biometrics.standards.ANTemplate;
import com.neurotec.biometrics.standards.ANValidationLevel;

public final class ValidationFrame extends JDialog implements ActionListener {

	// ==============================================
	// Private static fields
	// ==============================================

	private static final long serialVersionUID = 1L;

	// ==============================================
	// Private fields
	// ==============================================

	private File directory;
	private FileFilter[] filters;
	private ANValidationLevel validationLevel = ANValidationLevel.STANDARD;
	private int flags;
	private String currentFileName;
	private Vector<ValidateErrorInfo> vectorError;
	private BackgroundWorker backgroundWorker = new BackgroundWorker();
	private boolean isCancelled = false;
	private boolean isStopped = false;

	// ==============================================
	// Private GUI controls
	// ==============================================

	private JLabel lblProgress;
	private JProgressBar progressBar;
	private JButton btnStop;
	private JButton btnClose;
	private JList lstError;
	private JTextArea txtError;
	private JScrollPane txtErrorScrollPane;

	// ==============================================
	// Public constructor
	// ==============================================

	public ValidationFrame(Frame owner) {
		super(owner, "Validate", true);
		setPreferredSize(new Dimension(615, 360));
		setMinimumSize(new Dimension(300, 300));
		initializeComponents();
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				backgroundWorker.execute();
			}
		});
	}

	// ==============================================
	// Private methods
	// ==============================================

	private void initializeComponents() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		lblProgress = new JLabel();
		JPanel progressLabelPanel = new JPanel();
		progressLabelPanel.setLayout(new BoxLayout(progressLabelPanel, BoxLayout.X_AXIS));
		progressLabelPanel.add(Box.createHorizontalStrut(5));
		progressLabelPanel.add(new JLabel("Progess:"));
		progressLabelPanel.add(Box.createHorizontalStrut(10));
		progressLabelPanel.add(lblProgress);
		progressLabelPanel.add(Box.createHorizontalGlue());

		progressBar = new JProgressBar();
		JPanel progressBarPanel = new JPanel();
		progressBarPanel.setLayout(new BoxLayout(progressBarPanel, BoxLayout.X_AXIS));
		progressBarPanel.add(Box.createHorizontalStrut(5));
		progressBarPanel.add(progressBar);
		progressBarPanel.add(Box.createHorizontalStrut(5));

		JPanel errorLabelPanel = new JPanel();
		errorLabelPanel.setLayout(new BoxLayout(errorLabelPanel, BoxLayout.X_AXIS));
		errorLabelPanel.add(Box.createHorizontalStrut(5));
		errorLabelPanel.add(new JLabel("Errors:"));
		errorLabelPanel.add(Box.createHorizontalGlue());

		vectorError = new Vector<ValidateErrorInfo>();
		lstError = new JList(vectorError);
		lstError.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				txtError.setText("");
				if (lstError.getSelectedIndices().length > 0) {
					ValidateErrorInfo ei = vectorError.get(lstError.getSelectedIndices()[0]);
					txtError.setText(ei.getError().toString());
				}
			}
		});

		JScrollPane errorScrollPane = new JScrollPane(lstError);
		errorScrollPane.setPreferredSize(new Dimension(285, 190));

		txtError = new JTextArea();
		txtError.setEditable(false);

		txtErrorScrollPane = new JScrollPane(txtError);
		txtErrorScrollPane.setPreferredSize(new Dimension(285, 190));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
		c.gridy = 0;
		contentPane.add(progressLabelPanel, c);

		c.gridy = 2;
		contentPane.add(progressBarPanel, c);

		c.gridy = 3;
		contentPane.add(errorLabelPanel, c);

		JPanel middlePanel = new JPanel(new GridLayout(1, 2, 5, 5));
		middlePanel.add(errorScrollPane);
		middlePanel.add(txtErrorScrollPane);

		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 1;
		contentPane.add(middlePanel, c);

		c.gridy = 5;
		c.weightx = 0;
		c.weighty = 0;
		contentPane.add(createButonPanel(), c);
		pack();

	}

	private JPanel createButonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		btnStop = new JButton("Stop");
		btnStop.addActionListener(this);

		btnClose = new JButton("Close");
		btnClose.setEnabled(false);
		btnClose.addActionListener(this);

		buttonPanel.add(Box.createGlue());
		buttonPanel.add(btnStop);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(btnClose);
		buttonPanel.add(Box.createHorizontalStrut(5));
		return buttonPanel;
	}

	private List<File> examineDir(File directory, FileFilter[] filters, List<File> listFiles) {

		if (isCancelled) {
			return listFiles;
		}

		backgroundWorker.setWorkerProgress(-1, directory.getPath());
		for (FileFilter filter : filters) {
			if (isCancelled) {
				break;
			}

			File[] files = directory.listFiles(filter);
			if (files != null) {
				for (File f : files) {
					if (f.isFile()) {
						if (isCancelled) {
							break;
						}
						listFiles.add(f);
					}
				}
			}
		}

		if (isCancelled) {
			return listFiles;
		}
		File[] allFiles = directory.listFiles();
		if (allFiles != null) {
			for (File f : directory.listFiles()) {
				if (f.isDirectory()) {
					if (isCancelled) {
						break;
					}
					examineDir(f, filters, listFiles);
				}
			}
		}
		return listFiles;

	}

	// ==============================================
	// Public methods
	// ==============================================

	public File getPath() {
		return directory;
	}

	public void setPath(File path) {
		if (path.exists() && path.isDirectory()) {
			directory = path;
		}
	}

	public FileFilter[] getFilter() {
		if (filters != null) {
			return filters.clone();
		}
		return null;
	}

	public void setFilters(FileFilter[] filters) {
		if (filters != null) {
			this.filters = filters.clone();
		} else {
			this.filters = null;
		}
	}

	public ANValidationLevel getValidationLevel() {
		return validationLevel;
	}

	public void setValidationLevel(ANValidationLevel validationLevel) {
		this.validationLevel = validationLevel;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	// ==============================================
	// Event handling
	// ==============================================

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnStop) {
			isStopped = true;
			isCancelled = true;
		} else if (source == btnClose) {
			dispose();
		}
	}

	// ==============================================
	// Private class ValidateErrorInfo
	// ==============================================

	private static final class ValidateErrorInfo {

		// ==============================================
		// Private fields
		// ==============================================

		private final String fileName;
		private final Exception error;

		// ==============================================
		// Package private constructor
		// ==============================================

		ValidateErrorInfo(String fileName, Exception error) {
			this.fileName = fileName;
			this.error = error;
		}

		// ==============================================
		// Private methods
		// ==============================================

		private Exception getError() {
			return error;
		}

		// ==============================================
		// Overridden public methods
		// ==============================================

		@Override
		public String toString() {
			return fileName;
		}

	}

	// ===========================================================
	// Private class BackgroundWorker extending SwingWorker
	// ===========================================================

	private final class BackgroundWorker extends SwingWorker<Integer, File> {

		// ==============================================
		// Private methods
		// ==============================================

		private void setWorkerProgress(int progress, Object obj) {
			try {
				if (progress == -1) {
					lblProgress.setText(String.format("Examing directory: %s", obj));
				} else if (progress == -2) {
					vectorError.add(new ValidateErrorInfo(currentFileName, (Exception) obj));
					lstError.updateUI();
				} else {
					setProgress(progress);
					currentFileName = (String) obj;
					lblProgress.setText(String.format("Examing file: %s", currentFileName));
					progressBar.setValue(progress);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ValidationFrame.this, e.toString());
			}
		}

		// ==============================================
		// Overridden protected methods
		// ==============================================

		@Override
		protected Integer doInBackground() {
			int cc = 0;
			try {
				List<File> files = examineDir(directory, filters, new ArrayList<File>());
				if (!isCancelled) {
					int i = 0, c = files.size(), twoC = c * 2;
					for (File file : files) {
						setWorkerProgress((i + c) / twoC, file.getPath());

						if (isCancelled) {
							break;
						}

						try {
							new ANTemplate(file.getPath(), validationLevel, flags);
						} catch (Exception ex) {
							ex.printStackTrace();
							setWorkerProgress(-2, ex);
						}
						i += 200;
						cc++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ValidationFrame.this, e.toString(), getTitle(), JOptionPane.ERROR_MESSAGE);
			}
			return isCancelled ? -cc - 1 : cc + 1;
		}

		@Override
		protected void done() {
			Integer result = -1;
			try {
				result = get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String status = isStopped ? "Stopped" : "Complete";
			lblProgress.setText(String.format("%s: %s error(s) in %s file(s)", status, vectorError.size(), Math.abs(result) - 1));
			progressBar.setVisible(false);
			btnStop.setEnabled(false);
			btnClose.setEnabled(true);
		}

	}

}
