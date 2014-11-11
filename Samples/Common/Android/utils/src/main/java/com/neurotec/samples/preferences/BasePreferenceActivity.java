package com.neurotec.samples.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.neurotec.samples.R;
import com.neurotec.samples.util.ToastManager;

public abstract class BasePreferenceActivity extends PreferenceActivity {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String DIALOG_SHOW_ERROR_MESSAGE_KEY = "error-message";
	private static final String DIALOG_SHOW_ERROR_CLOSE_KEY = "close-application";
	private static final String DIALOG_SHOW_INFO_MESSAGE_KEY = "info-message";

	private ProgressDialog mProgressDialog;

	// ===========================================================
	// Protected methods
	// ===========================================================

	protected void showProgress(int messageId) {
		showProgress(getString(messageId));
	}

	protected void showProgress(String message) {
		hideProgress();
		mProgressDialog = ProgressDialog.show(this, "", message);
	}

	protected void hideProgress() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	protected void showToast(int messageId) {
		ToastManager.show(this, messageId);
	}

	protected void showToast(String message) {
		ToastManager.show(this, message);
	}

	protected void showError(String message, boolean close) {
		Bundle b = new Bundle();
		b.putString(DIALOG_SHOW_ERROR_MESSAGE_KEY, message);
		b.putBoolean(DIALOG_SHOW_ERROR_CLOSE_KEY, close);
		showDialog(R.id.dialog_error, b);
	}

	protected void showError(int messageId) {
		showError(getString(messageId));
	}

	protected void showError(String message) {
		showError(message, false);
	}

	protected void showInfo(String message) {
		Bundle b = new Bundle();
		b.putString(DIALOG_SHOW_INFO_MESSAGE_KEY, message);
		showDialog(R.id.dialog_message, b);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		hideProgress();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		if (id == R.id.dialog_error) {
			return new AlertDialog.Builder(this)
				.setTitle(R.string.msg_error)
				.setMessage("")
				.setPositiveButton(getString(R.string.msg_ok), null)
				.create();
		} else if (id == R.id.dialog_message) {
			return (new AlertDialog.Builder(this)).setMessage("").setNegativeButton(R.string.msg_close, null).create();
		} else {
			return super.onCreateDialog(id, bundle);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		if (id == R.id.dialog_error) {
			if (dialog instanceof AlertDialog) {
				final boolean close = bundle.getBoolean(DIALOG_SHOW_ERROR_CLOSE_KEY);
				((AlertDialog) dialog).setMessage(bundle.getString(DIALOG_SHOW_ERROR_MESSAGE_KEY));
				((AlertDialog) dialog).setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.msg_ok), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (close) {
							finish();
						} else {
							dialog.cancel();
						}
					}
				});
			}
		} else if (id == R.id.dialog_message) {
			((AlertDialog) dialog).setMessage(bundle.getString(DIALOG_SHOW_INFO_MESSAGE_KEY));
		} else {
			super.onPrepareDialog(id, dialog, bundle);
		}
	}
}
