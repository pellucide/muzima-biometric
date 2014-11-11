package com.neurotec.samples.licensing.preference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.neurotec.licensing.NLicensingService;
import com.neurotec.samples.R;
import com.neurotec.samples.app.BaseListActivity;
import com.neurotec.samples.licensing.LicensingManager;
import com.neurotec.samples.licensing.LicensingManager.LicensingStateCallback;
import com.neurotec.samples.licensing.LicensingServiceManager;
import com.neurotec.samples.licensing.LicensingState;
import com.neurotec.samples.licensing.app.ActivationActivity;

public final class LicensingPreferences extends BaseListActivity implements LicensingStateCallback {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = LicensingPreferences.class.getSimpleName();
	private static final int REQUEST_CODE_LICENSE_SERVICE_PREFERENCES = 0;
	private static final int REQUEST_CODE_ACTIVATION = 1;
	private static final String SERVICE = "Licensing service";
	private static final String ACTIVATION = "Activation";

	// ===========================================================
	// Public static fields
	// ===========================================================

	public static final boolean SHOW_SEEKBAR_MIN_MAX_VALUES = true;

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			String[] values = null;
			if (!NLicensingService.isTrial()) {
				values = new String[] {SERVICE, ACTIVATION};
			} else {
				values = new String[] {SERVICE};
			}
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values));
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
			showError(e.getMessage());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			switch (requestCode) {
			case REQUEST_CODE_LICENSE_SERVICE_PREFERENCES:
				if (LicensingServiceManager.getInstance().isOutdated(this)) {
					LicensingManager.getInstance().reobtain(this, this);
				} break;
			case REQUEST_CODE_ACTIVATION:
				if (data.getBooleanExtra(ActivationActivity.CODE_IS_MODIFIED, true)) {
					LicensingManager.getInstance().reobtain(this, this);
				} break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		if (item.equals(SERVICE)) {
			startActivityForResult(new Intent(this, LicensingServicePreferences.class), REQUEST_CODE_LICENSE_SERVICE_PREFERENCES);
		} else if (item.equals(ACTIVATION)) {
			startActivityForResult(new Intent(this, ActivationActivity.class), REQUEST_CODE_ACTIVATION);
		}
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void onLicensingStateChanged(LicensingState state) {
		switch (state) {
		case OBTAINING:
			showProgress(R.string.msg_obtaining_licenses);
			break;
		case OBTAINED:
			hideProgress();
			showToast(R.string.msg_licenses_obtained);
			break;
		case NOT_OBTAINED:
			hideProgress();
			showToast(R.string.msg_licenses_not_obtained);
			break;
		}
	}

}
