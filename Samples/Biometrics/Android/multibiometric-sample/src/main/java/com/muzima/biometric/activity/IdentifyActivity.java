package com.muzima.biometric.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.muzima.biometric.R;
import com.neurotec.biometrics.*;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.io.NBuffer;
import com.neurotec.samples.licensing.LicensingManager;
import com.neurotec.samples.licensing.LicensingState;
import com.neurotec.util.concurrent.CompletionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

public final class IdentifyActivity extends Activity implements LicensingManager.LicensingStateCallback {

    private static final String TAG = IdentifyActivity.class.getSimpleName();
    private static final int REQUEST_CODE_GET_CANDIDATE = 1;
    private static final int REQUEST_CODE_GET_PROBE = 2;
    private NTemplate capturedTemplate;
    private NSubject candidateSubject;
    private NSubject probeSubject;

    private static final String[] LICENSES = {
            LicensingManager.LICENSE_FINGER_MATCHING,
            LicensingManager.LICENSE_FINGER_MATCHING_FAST,
            LicensingManager.LICENSE_FINGER_EXTRACTION
    };

    private TextView mResult;
    private ProgressDialog mProgressDialog;
    private NBiometricClient mBiometricClient;

    private CompletionHandler<NBiometricStatus, NSubject> captureHandler = new CompletionHandler<NBiometricStatus, NSubject>() {
        @Override
        public void completed(NBiometricStatus result, NSubject subject) {
            if (result == NBiometricStatus.OK) {
                showMessage(getString(R.string.msg_template_created));
                NBuffer templateBuffer = subject.getTemplateBuffer();
                capturedTemplate = new NTemplate(templateBuffer);
                try {
                    enrollImageSent();
                } catch (Exception e) {
                    showMessage("Exception: " + e.getLocalizedMessage());
                }
            } else {
                showMessage(getString(R.string.format_extraction_failed, result));
            }
        }

        @Override
        public void failed(Throwable exc, NSubject subject) {
            exc.printStackTrace();
        }
    };

    private CompletionHandler<NBiometricTask, NBiometricOperation> completionHandler = new CompletionHandler<NBiometricTask, NBiometricOperation>() {
        @Override
        public void completed(NBiometricTask result, NBiometricOperation attachment) {
            switch (attachment) {
                case ENROLL:
                    if (result.getStatus() == NBiometricStatus.OK) {
                        showMessage("Enrollment Successful");
                        identify();
                    } else {
                        showMessage("Enrollment not Successful: " + result.getStatus());
                    }
                    break;
                case IDENTIFY:
                    if (result.getStatus() == NBiometricStatus.OK) {
                        showMessage(result.getSubjects().get(0).getMatchingResults().toString());
                        for (NMatchingResult matchingResult : result.getSubjects().get(0).getMatchingResults()) {
                            showMessage(getString(R.string.format_template_identified_score, matchingResult.getId(), matchingResult.getScore()));
                            returnIntent(matchingResult.getId());
                        }
                    } else {
                        showMessage(getString(R.string.format_identification_unsuccessful, result.getStatus()));
                        returnIntent(null);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void failed(Throwable th, NBiometricOperation attachment) {
            showMessage(th.getMessage() != null ? th.getMessage() : th.toString());
        }

    };

    private void returnIntent(String matchingResultId) {
        Intent intent = new Intent();
        intent.putExtra("patientId", matchingResultId);
        setResult(RESULT_OK, intent);
        finish();
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
        mBiometricClient.createTemplate(subject, subject, captureHandler);
    }

    @Override
    public void onLicensingStateChanged(final LicensingState state) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case OBTAINING:
                        Log.i(TAG, getString(R.string.format_obtaining_licenses, Arrays.toString(LICENSES)));
                        mProgressDialog = ProgressDialog.show(context, "", getString(R.string.msg_obtaining_licenses));
                        break;
                    case OBTAINED:
                        mProgressDialog.dismiss();
                        showMessage(getString(R.string.msg_licenses_obtained));
                        init();
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

    private void enrollImageSent() {
        Intent intent = getIntent();
        byte[] templateBuffers = intent.getByteArrayExtra("templateBuffer");
        String id = intent.getStringExtra("patientName");
        NTemplate template = new NTemplate(new NBuffer(templateBuffers));
        try {
            this.candidateSubject = createSubject(template, id);
            NBiometricTask enrollTask = mBiometricClient.createTask(EnumSet.of(NBiometricOperation.ENROLL), this.candidateSubject);
            mBiometricClient.performTask(enrollTask, NBiometricOperation.ENROLL, completionHandler);
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
    }

    private void init() {
        mBiometricClient = new NBiometricClient();
        mBiometricClient.setMatchingThreshold(48);
        mBiometricClient.setFingersMatchingSpeed(NMatchingSpeed.LOW);
        capture();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorials_plain);
        mResult = (TextView) findViewById(R.id.tutorials_results);
        LicensingManager.getInstance().obtain(this, this, Arrays.asList(LICENSES));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LicensingManager.getInstance().release(Arrays.asList(LICENSES));
        if (mBiometricClient != null) {
            mBiometricClient.cancel();
            mBiometricClient.dispose();
            mBiometricClient = null;
        }

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

    private NSubject createSubject(NTemplate template, String id) throws IOException {
        NSubject subject = new NSubject();
        subject.setTemplate(template);
        subject.setId(id);
        return subject;
    }

    private NSubject createSubject(NTemplate template) throws IOException {
        NSubject subject = new NSubject();
        subject.setTemplate(template);
        subject.setId("Sent_Image");
        return subject;
    }

    private void identify() {
        try {
            NSubject subject = createSubject(capturedTemplate);
            NBiometricTask task = mBiometricClient.createTask(EnumSet.of(NBiometricOperation.IDENTIFY), subject);
            mBiometricClient.performTask(task, NBiometricOperation.IDENTIFY, completionHandler);
        } catch (Exception e) {
            showMessage(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }
}