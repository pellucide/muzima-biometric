package com.neurotec.samples.licensing;

import android.app.Application;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import com.neurotec.samples.report.CrashReporter;

@ReportsCrashes(
formKey = "",
mode = ReportingInteractionMode.NOTIFICATION,
includeDropBoxSystemTags = true,
resToastText = R.string.msg_crash_toast_text,
resNotifTickerText = R.string.msg_crash_notif_ticker_text,
resNotifTitle = R.string.msg_crash_notif_title,
resNotifText = R.string.msg_crash_notif_text,
resNotifIcon = android.R.drawable.stat_notify_error,
resDialogText = R.string.msg_crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info,
resDialogCommentPrompt = R.string.msg_crash_dialog_comment_prompt,
resDialogOkToast = R.string.msg_crash_dialog_ok_toast )

public final class LicensingApplication extends Application {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = LicensingApplication.class.getSimpleName();

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			System.setProperty("jna.nounpack", "true");
			System.setProperty("java.io.tmpdir", getCacheDir().getAbsolutePath());
			ACRA.init(this);
			ErrorReporter.getInstance().setReportSender(new CrashReporter());
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
	}
}
