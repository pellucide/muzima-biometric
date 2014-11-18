package com.muzima.biometric.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.muzima.biometric.R;
import com.neurotec.biometrics.*;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.io.NBuffer;
import com.neurotec.lang.NCore;
import com.neurotec.samples.licensing.LicensingManager;
import com.neurotec.samples.licensing.LicensingState;
import com.neurotec.samples.util.EnvironmentUtils;
import com.neurotec.util.concurrent.CompletionHandler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public final class FingerActivity extends Activity implements LicensingManager.LicensingStateCallback {

    private static final String TAG = FingerActivity.class.getSimpleName();

    private static final String[] LICENSES = {LicensingManager.LICENSE_FINGER_EXTRACTION, LicensingManager.LICENSE_FINGER_DEVICES_SCANNERS};
    private static final String APP_NAME = "mUzima-Biometrics";
    private static final String SAMPLE_DATA_DIR = EnvironmentUtils.getDataDirectoryPath(EnvironmentUtils.SAMPLE_DATA_DIR_NAME, APP_NAME);

    public static final String TUTORIALS_SAMPLES_DATA_DIR = EnvironmentUtils.getDataDirectory(EnvironmentUtils.SAMPLE_DATA_DIR_NAME, APP_NAME, "input").getAbsolutePath();
    public static final String OUTPUT_DATA_DIR_NAME = EnvironmentUtils.getDataDirectory(EnvironmentUtils.SAMPLE_DATA_DIR_NAME, APP_NAME, "output").getAbsolutePath();

    private TextView mResult;
    private ProgressDialog mProgressDialog;
    private NBiometricClient mBiometricClient;

    private List<String> fingerPrintMinutiaeList;

    private CompletionHandler<NBiometricStatus, NSubject> completionHandler = new CompletionHandler<NBiometricStatus, NSubject>() {
        @Override
        public void completed(NBiometricStatus result, NSubject subject) {
            if (result == NBiometricStatus.OK) {
                showMessage(getString(R.string.msg_template_created));

                try {
                    NBuffer templateBuffer = subject.getTemplateBuffer();
                    NTemplate template = new NTemplate(templateBuffer);
                    extractMintiaeValuesFromImage(template);

                } catch (IOException e) {
                }
            } else {
                showMessage(getString(R.string.format_extraction_failed, result));
            }
        }

        @Override
        public void failed(Throwable exc, NSubject subject) {exc.printStackTrace();
        }
    };

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

    private void extractMintiaeValuesFromImage(NTemplate template) throws IOException {
        try {
            showMessage("The finger contains");

            if (template.getFingers() == null) {
                showMessage("0 Fingers");
            } else {
                showMessage(getString(R.string.format_n_fingers, template.getFingers().getRecords().size()));
                for (NFRecord nfRec : template.getFingers().getRecords()) {
                    minutiaeAsPipeSeparatedString(nfRec);
                }
            }
        } finally {
            if (template != null) template.dispose();
        }
    }

    private List<String> minutiaeAsPipeSeparatedString(NFRecord nfRec) {
        List<String> minutiaeCollectionForFinger = new ArrayList<String>();
        NFRecord.MinutiaCollection minutiaeCollection = nfRec.getMinutiae();

        for (NFMinutia minutia : minutiaeCollection) {
            String minutiaString = MessageFormat.format("{0}|{1}|{2}|{3}|{4}", minutia.x, minutia.y, rotationToDegrees(minutia.angle & 0xFF), minutia.g, minutia.curvature);
            minutiaeCollectionForFinger.add(minutiaString);
        }
        printNFRecord(nfRec);
        return minutiaeCollectionForFinger;
    }

    private int rotationToDegrees(int rotation) {
        return (2 * rotation * 360 + 256) / (2 * 256);
    }

    private void printNFRecord(NFRecord nfRec) {
        showMessage(getString(R.string.format_template_g, nfRec.getG()));
        showMessage(getString(R.string.format_impression_type, nfRec.getImpressionType()));
        showMessage(getString(R.string.format_pattern_class, nfRec.getPatternClass()));
        showMessage(getString(R.string.format_cbeff_product_type, nfRec.getCBEFFProductType()));
        showMessage(getString(R.string.format_position, nfRec.getPosition()));
        showMessage(getString(R.string.format_ridge_counts_type, nfRec.getRidgeCountsType()));
        showMessage(getString(R.string.format_width, nfRec.getWidth()));
        showMessage(getString(R.string.format_height, nfRec.getHeight()));
        showMessage(getString(R.string.format_horizontal_resolution, nfRec.getHorzResolution()));
        showMessage(getString(R.string.format_vertical_resolution, nfRec.getVertResolution()));
        showMessage(getString(R.string.format_template_quality, nfRec.getQuality()));
        showMessage(getString(R.string.format_size, nfRec.getSize()));

        showMessage(getString(R.string.format_minutia_count, nfRec.getMinutiae().size()));

        EnumSet<NFMinutiaFormat> minutiaFormat = nfRec.getMinutiaFormat();

        int index = 1;
        for (NFMinutia minutia : nfRec.getMinutiae()) {
            showMessage(getString(R.string.format_minutia_n_of_m, index, nfRec.getMinutiae().size()));
            showMessage(getString(R.string.format_x, minutia.x));
            showMessage(getString(R.string.format_y, minutia.y));
            showMessage(getString(R.string.format_angle, rotationToDegrees(minutia.angle & 0xFF)));
            if (minutiaFormat.contains(NFMinutiaFormat.HAS_QUALITY)) {
                showMessage(getString(R.string.format_minutia_quality, minutia.quality & 0xFF));
            }
            if (minutiaFormat.contains(NFMinutiaFormat.HAS_G)) {
                showMessage(getString(R.string.format_minutia_g, minutia.g));
            }
            if (minutiaFormat.contains(NFMinutiaFormat.HAS_CURVATURE)) {
                showMessage(getString(R.string.format_curvature, minutia.curvature));
            }

            showMessage(System.getProperty("line.separator"));
            index++;
        }

        index = 1;
        for (NFDelta delta : nfRec.getDeltas()) {
            showMessage(getString(R.string.format_delta_n_of_m, index, nfRec.getDeltas().size()));
            showMessage(getString(R.string.format_x, delta.x));
            showMessage(getString(R.string.format_y, delta.y));
            showMessage(getString(R.string.format_angle1, rotationToDegrees(delta.angle1)));
            showMessage(getString(R.string.format_angle2, rotationToDegrees(delta.angle2)));
            showMessage(getString(R.string.format_angle3, rotationToDegrees(delta.angle3)));

            showMessage(System.getProperty("line.separator"));
            index++;
        }

        index = 1;
        for (NFCore core : nfRec.getCores()) {
            showMessage(getString(R.string.format_core_n_of_m, index, nfRec.getCores().size()));
            showMessage(getString(R.string.format_x, core.x));
            showMessage(getString(R.string.format_y, core.y));
            showMessage(getString(R.string.format_angle, rotationToDegrees(core.angle)));

            showMessage(System.getProperty("line.separator"));
            index++;
        }

        index = 1;
        for (NFDoubleCore doubleCore : nfRec.getDoubleCores()) {
            showMessage(getString(R.string.format_double_core_n_of_m, index, nfRec.getDoubleCores().size()));
            showMessage(getString(R.string.format_x, doubleCore.x));
            showMessage(getString(R.string.format_y, doubleCore.y));

            showMessage("");
            index++;
        }
    }
}
