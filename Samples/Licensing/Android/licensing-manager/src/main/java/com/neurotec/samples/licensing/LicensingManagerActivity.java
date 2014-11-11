package com.neurotec.samples.licensing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.neurotec.licensing.NLicensingService;
import com.neurotec.samples.app.BaseTabActivity;
import com.neurotec.samples.licensing.app.ActivationActivity;
import com.neurotec.samples.util.EnvironmentUtils;

public final class LicensingManagerActivity extends BaseTabActivity {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = LicensingManagerActivity.class.getSimpleName();
	private static final String TAB_LICENSING_SERVICE = "LicensingService";
	private static final String TAB_ACTIVATION = "Activation";

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			setContentView(R.layout.info_view);

			if (!EnvironmentUtils.isSdPresent()) {
				showError(R.string.msg_sdcard_not_ready_for_reading);
				finish();
			}

			TabHost tabHost = getTabHost();
			tabHost.addTab(tabHost.newTabSpec(TAB_LICENSING_SERVICE).setIndicator(getString(R.string.msg_service)).setContent(new Intent(this, LicensingServiceActivity.class)));
			if (!NLicensingService.isTrial()) {
				tabHost.addTab(tabHost.newTabSpec(TAB_ACTIVATION).setIndicator(getString(R.string.msg_activation)).setContent(new Intent(this, ActivationActivity.class)));
			}
			tabHost.setCurrentTab(0);
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
			showError(e.getMessage());
		}
	}
}
