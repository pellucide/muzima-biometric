package com.neurotec.samples.licensing.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.neurotec.samples.R;
import com.neurotec.samples.app.BaseActivity;
import com.neurotec.samples.licensing.License;
import com.neurotec.samples.licensing.LicensingServiceManager;
import com.neurotec.samples.net.ConnectivityHelper;

public final class ActivationActivity extends BaseActivity {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = ActivationActivity.class.getSimpleName();

	// ===========================================================
	// Public static fields
	// ===========================================================

	public static final String CODE_IS_MODIFIED = "code_is_modified";

	// ===========================================================
	// Private fields
	// ===========================================================

	private ListView mListView = null;
	private Button mButtonActivate = null;
	private Button mButtonDeactivate = null;
	private BackgroundTask mBackgroundTask = null;
	private boolean mIsModified = false;

	// ===========================================================
	// Private methods
	// ===========================================================

	private List<License> getSelectedLicenses() {
		List<License> licenses = new ArrayList<License>();
		SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
		if (checkedItems != null) {
			for (int i = 0; i < checkedItems.size(); i++) {
				if (checkedItems.valueAt(i)) {
					licenses.add((License) mListView.getAdapter().getItem(checkedItems.keyAt(i)));
				}
			}
		}
		return licenses;
	}

	@SuppressWarnings("unchecked")
	private void clearView() {
		mListView.clearChoices();
		((ArrayAdapter<License>) mListView.getAdapter()).notifyDataSetChanged();
		updateLicenses();
	}

	private void updateView() {
		boolean activateEnabled = true;
		boolean deactivateEnabled = true;

		List<License> licenses = getSelectedLicenses();

		if (licenses == null || licenses.isEmpty()) {
			activateEnabled = deactivateEnabled = false;
		} else {
			for (License license : licenses) {
				activateEnabled &= !license.isActivated();
				deactivateEnabled &= license.isActivated();
			}
		}

		mButtonActivate.setVisibility(activateEnabled ? View.VISIBLE : View.GONE);
		mButtonDeactivate.setVisibility(deactivateEnabled ? View.VISIBLE : View.GONE);
	}

	private void updateLicenses() {
		try {
			mListView.setAdapter(null);
			List<License> licenses = LicensingServiceManager.getLicenses();
			if (licenses == null || licenses.isEmpty()) {
				showToast(R.string.msg_no_licenses);
			} else {
				mListView.setAdapter(new LicenseListAdapter(this, R.layout.license_list_item, licenses));
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showToast(e.getMessage());
		}
		updateView();
	}

	private void activate() {
		try {
			if (mBackgroundTask == null) {
				mBackgroundTask = new BackgroundTask();
				mBackgroundTask.activate(getSelectedLicenses());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showToast(e.getMessage());
		}
	}

	// ===========================================================
	// Activity events
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activation_view);
			mListView = (ListView) findViewById(R.id.list_view_serial_numbers);
			mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					updateView();
				}
			});
			mButtonActivate = (Button) findViewById(R.id.button_activate);
			mButtonActivate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ConnectivityHelper.isConnected(ActivationActivity.this)) {
						activate();
					} else {
						showDialog(R.id.dialog_question);
					}
				}
			});
			mButtonDeactivate = (Button) findViewById(R.id.button_deactivate);
			mButtonDeactivate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if (ConnectivityHelper.isConnected(ActivationActivity.this)) {
							if (mBackgroundTask == null) {
								mBackgroundTask = new BackgroundTask();
								mBackgroundTask.deactivate(getSelectedLicenses());
							}
						} else {
							showToast(R.string.msg_no_internet_connection);
						}
					} catch (Exception e) {
						Log.e(TAG, e.getMessage(), e);
						showToast(e.getMessage());
					}
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showError(e.getMessage());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateLicenses();
	}

	@Override
	protected void onDestroy() {
		if (mBackgroundTask != null) {
			mBackgroundTask.cancel(true);
			mBackgroundTask = null;
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Intent result = new Intent();
		result.putExtra(CODE_IS_MODIFIED, mIsModified);
		setResult(Activity.RESULT_OK, result);
		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		if (id == R.id.dialog_question) {
			return new AlertDialog.Builder(this)
				.setTitle(R.string.msg_internet_connection)
				.setMessage(R.string.msg_no_internet_connection_still_proceed)
				.setPositiveButton(getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activate();
						dismissDialog(R.id.dialog_question);
					}
				})
				.setNegativeButton(getString(R.string.msg_no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismissDialog(R.id.dialog_question);
					}
				}).create();
		} else {
			return super.onCreateDialog(id, bundle);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		super.onPrepareDialog(id, dialog, bundle);
	}

	private enum Task {
		ACTIVATE, DEACTIVATE
	}

	private final class BackgroundTask extends AsyncTask<Boolean, String, String> {

		private Task mTask;
		private List<License> mLicenses;

		void activate(List<License> licenses) {
			if (licenses == null) throw new NullPointerException("licenses");
			if (licenses.isEmpty()) throw new IllegalArgumentException("licenses < 0");
			mTask = Task.ACTIVATE;
			mLicenses = licenses;
			showProgress(getString(R.string.msg_activating));
			execute();
		}

		void deactivate(List<License> licenses) {
			if (licenses == null) throw new NullPointerException("licenses");
			if (licenses.isEmpty()) throw new IllegalArgumentException("licenses < 0");
			mTask = Task.DEACTIVATE;
			mLicenses = licenses;
			showProgress(getString(R.string.msg_deactivating));
			execute();
		}

		@Override
		protected String doInBackground(Boolean... params) {
			boolean hasInternet = ConnectivityHelper.isConnected(ActivationActivity.this);
			try {
				if (!isCancelled()) {
					switch (mTask) {
					case ACTIVATE:
						if (hasInternet) {
							mIsModified = true;
						}
						for (License license : mLicenses) {
							license.activate(hasInternet);
						}
						return getString(hasInternet ? R.string.msg_activation_succeeded : R.string.msg_proceed_activation_online);
					case DEACTIVATE:
						if (hasInternet) {
							mIsModified = true;
						}
						for (License license : mLicenses) {
							license.deactivate(hasInternet);
						}
						return getString(hasInternet ? R.string.msg_deactivation_succeeded : R.string.msg_proceed_deactivation_online);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Exception", e);
				return e.getMessage();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... messages) {
			for (String message : messages) {
				showProgress(message);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			hideProgress();
			if (result != null) {
				showInfo(result);
			}
			mBackgroundTask = null;
			clearView();
		}
	}

}

class LicenseListAdapter extends ArrayAdapter<License> {
	private View mRow;
	private List<License> mLicenses;
	private int mRes;
	private Context context;

	public LicenseListAdapter(Context context, int textViewResourceId, List<License> licenses) {
		super(context, textViewResourceId, licenses);
		this.mLicenses = licenses;
		this.mRes = textViewResourceId;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mRow = convertView;
		if (mRow == null) { // inflate our custom layout. mRes == R.layout.row_team_layout.xml
			LayoutInflater ll = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mRow = ll.inflate(mRes, parent, false);
		}

		License license = mLicenses.get(position); // Produce a mRow for each Team.

		if (license != null) {
			TextView text = (TextView) mRow.findViewById(R.id.list_item_license_text);
			ImageView image = (ImageView) mRow.findViewById(R.id.list_item_license_image);
			text.setText(license.getName());

			if (license.isActivated()) {
				image.setImageResource(android.R.drawable.ic_input_add);
			} else {
				image.setImageResource(android.R.drawable.ic_menu_help);
			}
		}
		return mRow;
	}
}
