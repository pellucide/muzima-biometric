package com.neurotec.samples.licensing;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.neurotec.licensing.NLicensingService;
import com.neurotec.licensing.NLicensingServiceStatus;
import com.neurotec.samples.app.BaseActivity;
import com.neurotec.samples.licensing.preference.LicensingServicePreferences;
import com.neurotec.samples.util.EnvironmentUtils;
import com.neurotec.samples.util.IOUtils;
import com.neurotec.samples.util.ResourceUtils;

import java.util.Date;

public final class LicensingServiceActivity extends BaseActivity {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = LicensingServiceActivity.class.getSimpleName();
	private static final int REQUEST_CODE_LICENSE_SERVICE_PREFERENCES = 0;

	// ===========================================================
	// Private fields
	// ===========================================================

	private TextView mStatus = null;
	private TextView mTrial = null;
	private TextView mLog = null;
	private TextView mConf = null;
	private Button mButtonStart;
	private Button mButtonStop;
	private BackgroundTask mTask = null;

	// ===========================================================
	// Private methods
	// ===========================================================

	private void update(LicensingServiceData data) {
		String status = null;
		String trial = null;

		if (data != null) {
			status = ResourceUtils.getEnum(LicensingServiceActivity.this, data.status);
			trial = String.valueOf(data.trial);
			mLog.setText(getString(R.string.msg_format_log, data.log != null ? data.log : ""));
			mConf.setText(getString(R.string.msg_format_configuration, data.conf != null ? data.conf : ""));
		} else {
			status = trial = getString(R.string.msg_unknown);
		}

		mStatus.setText(getString(R.string.msg_format_status, status));
		mTrial.setText(getString(R.string.msg_format_trial, trial));

		boolean running = LicensingServiceManager.getInstance().isRunning();
		mButtonStart.setVisibility(running ? View.GONE : View.VISIBLE);
		mButtonStop.setVisibility(running ? View.VISIBLE : View.GONE);
	}

	private void generateReport() {
		try {
			String fileName = String.format("licensing_service_report_%s.txt", DateFormat.format(EnvironmentUtils.DATE_FORMAT, new Date()));
			String filePath = IOUtils.combinePath(EnvironmentUtils.REPORTS_DIRECTORY_PATH, fileName);
			LicensingServiceManager.generateReport(this, filePath);
			showInfo(getString(R.string.msg_format_licensing_service_report_was_saved, filePath));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showError(e.getMessage());
		}
	}


	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.licensing_service);
			mStatus = (TextView) findViewById(R.id.text_view_licensing_service_status);
			mTrial = (TextView) findViewById(R.id.text_view_licensing_service_trial);
			mLog = (TextView) findViewById(R.id.text_view_licensing_service_log);
			mConf = (TextView) findViewById(R.id.text_view_licensing_service_conf);

			mButtonStart = (Button) findViewById(R.id.button_start);
			mButtonStart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (EnvironmentUtils.isSdPresent()) {
						mTask = new BackgroundTask();
						mTask.startService();
					} else {
						showToast(R.string.msg_sdcard_not_ready_for_reading);
					}
				}
			});

			mButtonStop = (Button) findViewById(R.id.button_stop);
			mButtonStop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (EnvironmentUtils.isSdPresent()) {
						mTask = new BackgroundTask();
						mTask.stopService();
					} else {
						showToast(R.string.msg_sdcard_not_ready_for_reading);
					}
				}
			});

			Button buttonConfigure = (Button) findViewById(R.id.button_configure);
			buttonConfigure.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(LicensingServiceActivity.this, LicensingServicePreferences.class), REQUEST_CODE_LICENSE_SERVICE_PREFERENCES);
				}
			});

			Button buttonGenerateReport = (Button) findViewById(R.id.button_generate_report);
			buttonGenerateReport.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					generateReport();
				}
			});

			if (EnvironmentUtils.isSdPresent()) {
				mTask = new BackgroundTask();
				mTask.updateView();
			} else {
				showToast(R.string.msg_sdcard_not_ready_for_reading);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showError(e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTask != null) {
			mTask.cancel(true);
			mTask = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_LICENSE_SERVICE_PREFERENCES) {
			if (EnvironmentUtils.isSdPresent()) {
				mTask = new BackgroundTask();
				mTask.restartService();
			} else {
				showToast(R.string.msg_sdcard_not_ready_for_reading);
			}
		}
	}

	// ===========================================================
	// Inner classes
	// ===========================================================

	private enum Task {
		START_SERVICE, STOP_SERVICE, RESTART_SERVICE, UPDATE_VIEW
	}

	private class LicensingServiceData {
		NLicensingServiceStatus status;
		boolean trial;
		String log;
		String conf;

		LicensingServiceData(NLicensingServiceStatus mStatus, boolean trial, String log, String conf) {
			super();
			this.status = mStatus;
			this.trial = trial;
			this.log = log;
			this.conf = conf;
		}
	}

	private class BackgroundTask extends AsyncTask<Boolean, Boolean, LicensingServiceData> {

		private Task mTask;

		void startService() {
			mTask = Task.START_SERVICE;
			showProgress(getString(R.string.msg_starting_service));
			execute();
		}

		void stopService() {
			mTask = Task.STOP_SERVICE;
			showProgress(getString(R.string.msg_stopping_service));
			execute();
		}

		void restartService() {
			mTask = Task.RESTART_SERVICE;
			showProgress(getString(R.string.msg_restarting_service));
			execute();
		}

		void updateView() {
			mTask = Task.UPDATE_VIEW;
			showProgress(getString(R.string.msg_updating));
			execute();
		}

		@Override
		protected LicensingServiceData doInBackground(Boolean... params) {
			LicensingServiceData data = null;
			try {
				if (!isCancelled()) {
					switch (mTask) {
					case START_SERVICE:
						LicensingServiceManager.getInstance().start(LicensingServiceActivity.this);
						break;
					case STOP_SERVICE:
						LicensingServiceManager.getInstance().stop();
						break;
					case RESTART_SERVICE:
						LicensingServiceManager.getInstance().restart(LicensingServiceActivity.this);
						break;
					default:
						break;
					}
					data = new LicensingServiceData(NLicensingService.getStatus(), NLicensingService.isTrial(),
							LicensingServiceManager.getInstance().getLog(), LicensingServiceManager.getInstance().getConf());
				}
			} catch (Exception e) {
				Log.e(TAG, "Exception", e);
			}
			return data;
		}

		@Override
		protected void onPostExecute(LicensingServiceData result) {
			super.onPostExecute(result);
			hideProgress();
			update(result);
			mTask = null;
		}
	}
}
