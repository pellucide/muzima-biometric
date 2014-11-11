package com.neurotec.samples.licensing.preference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import com.neurotec.samples.R;
import com.neurotec.samples.licensing.LicensingMode;
import com.neurotec.samples.licensing.LicensingServiceManager;
import com.neurotec.samples.preferences.BasePreferenceActivity;

import java.util.EnumSet;

public class LicensingServicePreferences extends BasePreferenceActivity implements OnSharedPreferenceChangeListener {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = LicensingServicePreferences.class.getSimpleName();

	// ===========================================================
	// Public static fields
	// ===========================================================

	public static final String LICENSING_MODE = "licensing_mode";
	public static final String LICENSING_SERVER_ADDRESS = "licensing_server_address";
	public static final String LICENSING_SERVER_PORT = "licensing_server_port";
	public static final String SET_DEFAULT_PREFERENCES = "set_default_preferences";

	// ===========================================================
	// Private fields
	// ===========================================================

	private ListPreference mLicensingMode = null;
	private EditTextPreference mServerAddress = null;
	private EditTextPreference mServerPort = null;

	// ===========================================================
	// Private methods
	// ===========================================================

	private void setLicensingMode(LicensingMode mode) {
		getPreferenceScreen().getSharedPreferences().edit().putString(LICENSING_MODE, mode.toString()).commit();
		mLicensingMode.setValue(mode.toString());
		mLicensingMode.setSummary(mode.getName(this));

		boolean value = mode.isServerConfigurable();
		mServerAddress.setEnabled(value);
		mServerAddress.setSelectable(value);
		mServerPort.setEnabled(value);
		mServerPort.setSelectable(value);
	}

	private void setServerAddress(String value) {
		String v = String.valueOf(value);
		getPreferenceScreen().getSharedPreferences().edit().putString(LICENSING_SERVER_ADDRESS, v).commit();
		mServerAddress.setText(v);
		mServerAddress.setSummary(value);
	}


	private void setServerPort(int value) {
		String v = String.valueOf(value);
		getPreferenceScreen().getSharedPreferences().edit().putString(LICENSING_SERVER_PORT, v).commit();
		mServerPort.setText(v);
		mServerPort.setSummary(v);
	}

	private void loadPreferences() {
		EnumSet<LicensingMode> modes = LicensingMode.getAvailable();
		String[] entries = new String[modes.size()];
		String[] entryValues = new String[modes.size()];
		int i = 0;
		for (LicensingMode mode : modes) {
			entries[i] = mode.getName(this);
			entryValues[i] = mode.toString();
			i++;
		}
		mLicensingMode.setEntries(entries);
		mLicensingMode.setEntryValues(entryValues);
		mLicensingMode.setDefaultValue(LicensingServiceManager.DEFAULT_LICENSING_MODE.toString());

		setLicensingMode(LicensingServiceManager.getLicensingMode(this));
		setServerAddress(LicensingServiceManager.getServerAddress(this));
		setServerPort(LicensingServiceManager.getServerPort(this));
	}

	private void resetPreferences() {
		setLicensingMode(LicensingServiceManager.DEFAULT_LICENSING_MODE);
		setServerAddress(LicensingServiceManager.DEFAULT_SERVER_ADDRESS);
		setServerPort(LicensingServiceManager.DEFAULT_SERVER_PORT);
	}

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.licensing_service_preferences);
		mLicensingMode = (ListPreference) getPreferenceScreen().findPreference(LICENSING_MODE);
		mServerAddress= (EditTextPreference) getPreferenceScreen().findPreference(LICENSING_SERVER_ADDRESS);
//		mServerAddress.getEditText().setFilters(new InputFilter[] {new IPInputFilter()});
		mServerPort = (EditTextPreference) getPreferenceScreen().findPreference(LICENSING_SERVER_PORT);
		mServerPort.getEditText().setFilters(new InputFilter[] {new PortInputFilter()});

		Preference resetDefaults = (Preference) findPreference(SET_DEFAULT_PREFERENCES);
		resetDefaults.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				resetPreferences();
				showToast(R.string.msg_settings_reset);
				return true;
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		loadPreferences();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}


	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
//		try {
//			LicensingManager.getInstance().reobtain(this);
//		} catch (IOException e) {
//			Log.e(TAG, "IOException", e);
//		}
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(LICENSING_MODE)) {
			setLicensingMode(LicensingServiceManager.getLicensingMode(this));
		} else if (key.equals(LICENSING_SERVER_ADDRESS)) {
			setServerAddress(LicensingServiceManager.getServerAddress(this));
		} else if (key.equals(LICENSING_SERVER_PORT)) {
			setServerPort(LicensingServiceManager.getServerPort(this));
		}
	}

	// ===========================================================
	// Private inner classes
	// ===========================================================

	private class IPInputFilter implements InputFilter {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (end > start) {
				String destTxt = dest.toString();
				String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
				if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
					return "";
				} else {
					String[] splits = resultingTxt.split("\\.");
					for (int i=0; i<splits.length; i++) {
						if (Integer.valueOf(splits[i]) > 255) {
							return "";
						}
					}
				}
			}
			return null;
		}
	}

	private class PortInputFilter implements InputFilter {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			try {
				if (end > start) {
					CharSequence strValue = TextUtils.concat(dest, source);
					if (strValue.length() > 5) {
						return "";
					}
					int value = Integer.parseInt(strValue.toString());
					if (value < 0 || value > 65535) {
						return "";
					}
				}
			} catch (Exception e) {
				return "";
			}
			return null;
		}
	}

}
