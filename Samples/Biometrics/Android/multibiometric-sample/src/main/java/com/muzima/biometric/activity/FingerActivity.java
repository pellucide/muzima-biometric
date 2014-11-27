package com.muzima.biometric.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.muzima.biometric.R;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NFinger;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NTemplateSize;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.io.NBuffer;
import com.neurotec.lang.NCore;
import com.neurotec.samples.licensing.LicensingManager;
import com.neurotec.samples.licensing.LicensingState;
import com.neurotec.util.concurrent.CompletionHandler;

import java.util.Arrays;
import java.util.EnumSet;

public final class FingerActivity extends Activity implements LicensingManager.LicensingStateCallback {

    private static final String TAG = FingerActivity.class.getSimpleName();

    private static final String[] LICENSES = {LicensingManager.LICENSE_FINGER_EXTRACTION, LicensingManager.LICENSE_FINGER_DEVICES_SCANNERS};

    private TextView mResult;
    private ProgressDialog mProgressDialog;
    private NBiometricClient mBiometricClient;

    private CompletionHandler<NBiometricStatus, NSubject> completionHandler = new CompletionHandler<NBiometricStatus, NSubject>() {
        @Override
        public void completed(NBiometricStatus result, NSubject subject) {
            if (result == NBiometricStatus.OK) {
                showMessage(getString(R.string.msg_template_created));
                subject.getFingers().get(0).getImage();
                NBuffer templateBuffer = subject.getTemplateBuffer();
                returnIntent(templateBuffer);
            } else {
                showMessage(getString(R.string.format_extraction_failed, result));
            }
        }

        @Override
        public void failed(Throwable exc, NSubject subject) {
            exc.printStackTrace();
        }
    };

    private void returnIntent(NBuffer nBuffer) {
        Intent intent = new Intent();
        intent.putExtra("templateBuffer", nBuffer.toByteArray());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onLicensingStateChanged(final LicensingState state) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case OBTAINING:
                        Log.i(TAG, getString(R.string.format_obtaining_licenses, Arrays.asList(LICENSES)));
                        mProgressDialog = ProgressDialog.show(context, "", getString(R.string.msg_obtaining_licenses));
                        break;
                    case OBTAINED:
                        mProgressDialog.dismiss();
                        showMessage(getString(R.string.msg_licenses_obtained));
                        capture();
                        break;
                    case NOT_OBTAINED:
                        mProgressDialog.dismiss();
                        showMessage(getString(R.string.msg_licenses_not_obtained));
                        break;
                    default:
                        throw new AssertionError("Unknown state: " + state);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NCore.setContext(this);
        setContentView(R.layout.tutorials_plain);
        mResult = (TextView) findViewById(R.id.tutorials_results);
        LicensingManager.getInstance().obtain(this, this, Arrays.asList(LICENSES));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBiometricClient != null) {
            mBiometricClient.cancel();
            mBiometricClient.dispose();
            mBiometricClient = null;
        }
        LicensingManager.getInstance().release(Arrays.asList(LICENSES));

        if ((mProgressDialog != null) && (mProgressDialog.isShowing())) {
            mProgressDialog.dismiss();
        }
    }

    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResult.append(message + "\n");
            }
        });
    }

    private void capture() {
        if (!LicensingManager.isActivated(LicensingManager.LICENSE_FINGER_DEVICES_SCANNERS)) {
            showMessage(getString(R.string.format_not_activated, LicensingManager.LICENSE_FINGER_DEVICES_SCANNERS));
            return; // The following operation is not activated, so return
        }
        if (!LicensingManager.isFingerExtractionActivated()) {
            showMessage(getString(R.string.format_not_activated, LicensingManager.LICENSE_FINGER_EXTRACTION));
            return; // The following operation is not activated, so return
        }
        mBiometricClient = new NBiometricClient();
        NSubject subject = new NSubject();
        NFinger finger = new NFinger();

        mBiometricClient.setUseDeviceManager(true);
        NDeviceManager deviceManager = mBiometricClient.getDeviceManager();
        deviceManager.setDeviceTypes(EnumSet.of(NDeviceType.FINGER_SCANNER));
        mBiometricClient.initialize();

        NDeviceManager.DeviceCollection devices = deviceManager.getDevices();
        if (devices.size() > 0) {
            System.out.format("Found %d fingerprint scanner\n", devices.size());
        } else {
            showMessage(getString(R.string.msg_capturing_device_is_unavailable));
            return;
        }

        showMessage(getString(R.string.format_capturing_from_device_put_finger, mBiometricClient.getFingerScanner().getDisplayName()));

        subject.getFingers().add(finger);

        showMessage("Capturing....");
        mBiometricClient.setFingersTemplateSize(NTemplateSize.LARGE);
        mBiometricClient.createTemplate(subject, subject, completionHandler);
    }
}