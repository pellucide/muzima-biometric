package com.muzima.biometric.util;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.neurotec.biometrics.NLEDetectionDetails;
import com.neurotec.samples.util.BitmapUtils;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

public final class BiometricUtils {

	// ===========================================================
	// Private methods
	// ===========================================================

	private static NLEDetectionDetails mapDetails(NLEDetectionDetails src, NLEDetectionDetails dst, Matrix matrix) {
		dst.getFace().confidence = src.getFace().confidence;
		RectF originalFace = new RectF(src.getFace().getRectangle());
		matrix.mapRect(originalFace);
		originalFace.round(dst.getFace().getRectangle());

		dst.getFace().getRotation().pitch = src.getFace().getRotation().pitch;
		dst.getFace().getRotation().yaw = src.getFace().getRotation().yaw;
		dst.getFace().getRotation().roll = 0; //Image will be converted according to rotation, therefore rotation becomes 0;

		dst.faceAvailable = src.faceAvailable;
		dst.gender = src.gender;
		dst.genderConfidence = src.genderConfidence;

		dst.getLeftEyeCenter().confidence = src.getLeftEyeCenter().confidence;
		float[] points = new float[]{
				src.getLeftEyeCenter().x, src.getLeftEyeCenter().y,
				src.getRightEyeCenter().x, src.getRightEyeCenter().y,
				src.getMouthCenter().x, src.getMouthCenter().y,
				src.getNoseTip().x, src.getNoseTip().y};

		matrix.mapPoints(points);

		dst.getLeftEyeCenter().x = ((Float) (points[0])).shortValue();
		dst.getLeftEyeCenter().y = ((Float) (points[1])).shortValue();

		dst.getRightEyeCenter().confidence = src.getRightEyeCenter().confidence;
		dst.getRightEyeCenter().x = ((Float) (points[2])).shortValue();
		dst.getRightEyeCenter().y = ((Float) (points[3])).shortValue();

		dst.getMouthCenter().confidence = src.getMouthCenter().confidence;
		dst.getMouthCenter().x = ((Float) (points[4])).shortValue();
		dst.getMouthCenter().y = ((Float) (points[5])).shortValue();

		dst.getNoseTip().confidence = src.getMouthCenter().confidence;
		dst.getNoseTip().x = ((Float) (points[6])).shortValue();
		dst.getNoseTip().y = ((Float) (points[7])).shortValue();
		return dst;
	}

	// ===========================================================
	// Public static methods
	// ===========================================================

	public static int qualityToPercent(byte value) {
		return (2 * value * 100 + 255) / (2 * 255);
	}

	public static byte qualityFromPercent(int value) {
		return (byte) ((2 * value * 255 + 100) / (2 * 100));
	}

	public static String getMatchingThresholdToString(int value) {
		double p = -value / 12.0;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(Math.max(0, (int) Math.ceil(-p) - 2));
		nf.setMinimumIntegerDigits(1);
		return nf.format(Math.pow(10, p));
	}

	public static int getMatchingThresholdFromString(String value) throws ParseException {
		char percent = new DecimalFormatSymbols().getPercent();
		value = value.replace(percent, ' ');
		Number number = NumberFormat.getNumberInstance().parse(value);
		double parse = number.doubleValue();
		double p = Math.log10(Math.max(Double.MIN_VALUE, Math.min(1, parse / 100)));
		return Math.max(0, (int) Math.round(-12 * p));
	}

	public static boolean isMatchingThresholdEqual(int thresholdValue,	String percentString) throws ParseException {
		if (percentString == null) {
			return false;
		}
		int intVal = getMatchingThresholdFromString(percentString);
		return (intVal == thresholdValue);
	}

	public static int getMaximalRotationToDegrees(int value) {
		return (2 * value * 360 + 256) / (2 * 256);
	}

	public static int getMaximalRotationFromDegrees(int value) {
		return (int) ((2 * value * 256 + 360) / (2 * 360));
	}

	public static Bitmap adjustFaceImage(NLEDetectionDetails details, Bitmap faceImage) {
		return adjustFaceImage(details, faceImage, 0, 0, 1);
	}

	public static Bitmap adjustFaceImage(NLEDetectionDetails details, Bitmap faceImage, int containerWidth, int containerHeight, int sampleSize) {
		return adjustFaceImage(details, null, faceImage, containerWidth, containerHeight, sampleSize);
	}

	public static Bitmap adjustFaceImage(NLEDetectionDetails details, NLEDetectionDetails dst, Bitmap faceImage, int containerWidth, int containerHeight, int sampleSize) {
		if (sampleSize < 1) {
			throw new IllegalArgumentException("sampleSize");
		}
		if (details != null && details.getFace() != null && details.getFace().confidence > 0 && faceImage != null) {
			int leftEyeCenterX = details.getLeftEyeCenter().x / sampleSize;
			int leftEyeCenterY = details.getLeftEyeCenter().y / sampleSize;

			int rightEyeCenterX = details.getRightEyeCenter().x / sampleSize;
			int rightEyeCenterY = details.getRightEyeCenter().y / sampleSize;

			double eyeDist = Math.sqrt(
					Math.pow((leftEyeCenterX - rightEyeCenterX), 2)
					+ Math.pow((leftEyeCenterY - rightEyeCenterY), 2));

			int eyesCenterX = (int) ((leftEyeCenterX + rightEyeCenterX) / 2.0f);
			int eyesCenterY = (int) ((leftEyeCenterY + rightEyeCenterY) / 2.0f);

			int halfWidth = (int) (eyeDist * 1.3);
			int halfWidthLeft = Math.min(halfWidth, eyesCenterX);
			int halfWidthRight = Math.min(halfWidth, faceImage.getWidth() - eyesCenterX);

			int halfHeight = (int) (eyeDist * 1.8);
			int halfHeightTop = Math.min(halfHeight, eyesCenterY);
			int halfHeightBottom = Math.min(halfHeight, faceImage.getHeight() - eyesCenterY);

			Rect faceRect = new Rect(eyesCenterX - halfWidthLeft, eyesCenterY - halfHeightTop, eyesCenterX + halfWidthRight, eyesCenterY + halfHeightBottom);

			Matrix resultMatrix = new Matrix();
			Bitmap croppedFaceImage = BitmapUtils.cropRotateScaleImage(faceImage, faceRect, 360 - details.getFace().getRotation().roll, containerWidth, containerHeight, resultMatrix);

			Matrix adjustedDetails = new Matrix();
			adjustedDetails.setScale((float) 1 / sampleSize, (float) 1 / sampleSize);
			adjustedDetails.postConcat(resultMatrix);

			if (dst != null) {
				mapDetails(details, dst, adjustedDetails);
			}

			return croppedFaceImage;
		} else {
			return null;
		}
	}

	// ===========================================================
	// Private constructor
	// ===========================================================

	private BiometricUtils() {
	}

}
