#ifndef FCR_FACE_IMAGE_H_INCLUDED
#define FCR_FACE_IMAGE_H_INCLUDED

#include <Images/NImage.h>
#include <Biometrics/Standards/BdifTypes.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(FcrFaceImage, NObject)

typedef enum FcrFaceImageType_
{
	fcrfitBasic = 0,
	fcrfitFullFrontal = 1,
	fcrfitTokenFrontal = 2,
	fcrfitOther = 3
} FcrFaceImageType;

N_DECLARE_TYPE(FcrFaceImageType)

typedef enum FcrImageDataType_
{
	fcridtJpeg = 0,
	fcridtJpeg2000 = 1
} FcrImageDataType;

N_DECLARE_TYPE(FcrImageDataType)

typedef enum FcrImageColorSpace_
{
	fcricsUnspecified = 0,
	fcricsRgb24Bit = 1,
	fcricsYuv422 = 2,
	fcricsGrayscale8Bit = 3,
	fcricsOther = 4,
	fcricsVendor = 128
} FcrImageColorSpace;

N_DECLARE_TYPE(FcrImageColorSpace)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/FCRecord.h>

#ifdef N_CPP
extern "C"
{
#endif

#define FCRFI_MAX_FEATURE_POINT_COUNT N_USHORT_MAX

NResult N_API FcrFaceImageCalculateSize(BdifStandard standard, NInt featurePointCount, NSizeType imageDataLength, NSizeType * pSize);

#define FCRFI_SKIP_FEATURE_POINTS 0x00010000

NResult N_API FcrFaceImageGetFeaturePointCount(HFcrFaceImage hFaceImage, NInt * pValue);
NResult N_API FcrFaceImageGetFeaturePoint(HFcrFaceImage hFaceImage, NInt index, struct BdifFaceFeaturePoint_ * pValue);
NResult N_API FcrFaceImageSetFeaturePoint(HFcrFaceImage hFaceImage, NInt index, const struct BdifFaceFeaturePoint_ * pValue);
NResult N_API FcrFaceImageGetFeaturePointsEx(HFcrFaceImage hFaceImage, struct BdifFaceFeaturePoint_ * arValue, NInt valueLength);
NResult N_API FcrFaceImageGetFeaturePointCapacity(HFcrFaceImage hFaceImage, NInt * pValue);
NResult N_API FcrFaceImageSetFeaturePointCapacity(HFcrFaceImage hFaceImage, NInt value);
NResult N_API FcrFaceImageAddFeaturePoint(HFcrFaceImage hFaceImage, const struct BdifFaceFeaturePoint_ * pValue);
NResult N_API FcrFaceImageInsertFeaturePoint(HFcrFaceImage hFaceImage, NInt index, const struct BdifFaceFeaturePoint_ * pValue);
NResult N_API FcrFaceImageRemoveFeaturePoint(HFcrFaceImage hFaceImage, NInt index);
NResult N_API FcrFaceImageClearFeaturePoints(HFcrFaceImage hFaceImage);

NResult N_API FcrFaceImageToNImage(HFcrFaceImage hFaceImage, NUInt flags, HNImage * phImage);

NResult N_API FcrFaceImageGetGender(HFcrFaceImage hFaceImage, BdifGender * pValue);
NResult N_API FcrFaceImageSetGender(HFcrFaceImage hFaceImage, BdifGender value);
NResult N_API FcrFaceImageGetEyeColor(HFcrFaceImage hFaceImage, BdifEyeColor * pValue);
NResult N_API FcrFaceImageSetEyeColor(HFcrFaceImage hFaceImage, BdifEyeColor value);
NResult N_API FcrFaceImageGetHairColor(HFcrFaceImage hFaceImage, BdifHairColor * pValue);
NResult N_API FcrFaceImageSetHairColor(HFcrFaceImage hFaceImage, BdifHairColor value);
NResult N_API FcrFaceImageGetProperties(HFcrFaceImage hFaceImage, BdifFaceProperties * pValue);
NResult N_API FcrFaceImageSetProperties(HFcrFaceImage hFaceImage, BdifFaceProperties value);
NResult N_API FcrFaceImageGetExpression(HFcrFaceImage hFaceImage, BdifFaceExpression * pValue);
NResult N_API FcrFaceImageGetVendorExpression(HFcrFaceImage hFaceImage, NUShort * pValue);
NResult N_API FcrFaceImageSetExpression(HFcrFaceImage hFaceImage, BdifFaceExpression value, NUShort vendorValue);
NResult N_API FcrFaceImageGetPoseAngleYaw(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageSetPoseAngleYaw(HFcrFaceImage hFaceImage, NByte value);
NResult N_API FcrFaceImageGetPoseAnglePitch(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageSetPoseAnglePitch(HFcrFaceImage hFaceImage, NByte value);
NResult N_API FcrFaceImageGetPoseAngleRoll(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageSetPoseAngleRoll(HFcrFaceImage hFaceImage, NByte value);
NResult N_API FcrFaceImageGetPoseAngle(HFcrFaceImage hFaceImage, NByte * pYaw, NByte * pPitch, NByte * pRoll);
NResult N_API FcrFaceImageSetPoseAngle(HFcrFaceImage hFaceImage, NByte yaw, NByte pitch, NByte roll);
NResult N_API FcrFaceImageGetPoseAngleUncertaintyYaw(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageGetPoseAngleUncertaintyPitch(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageGetPoseAngleUncertaintyRoll(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageGetPoseAngleUncertainty(HFcrFaceImage hFaceImage, NByte * pYaw, NByte * pPitch, NByte * pRoll);
NResult N_API FcrFaceImageSetPoseAngleUncertainty(HFcrFaceImage hFaceImage, NByte yaw, NByte pitch, NByte roll);
NResult N_API FcrFaceImageGetFaceImageType(HFcrFaceImage hFaceImage, FcrFaceImageType * pValue);
NResult N_API FcrFaceImageGetImageDataType(HFcrFaceImage hFaceImage, FcrImageDataType * pValue);
NResult N_API FcrFaceImageGetWidth(HFcrFaceImage hFaceImage, NUShort * pValue);
NResult N_API FcrFaceImageGetHeight(HFcrFaceImage hFaceImage, NUShort * pValue);
NResult N_API FcrFaceImageGetImageColorSpace(HFcrFaceImage hFaceImage, FcrImageColorSpace * pValue);
NResult N_API FcrFaceImageGetVendorImageColorSpace(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageGetSourceType(HFcrFaceImage hFaceImage, BdifImageSourceType * pValue);
NResult N_API FcrFaceImageGetVendorSourceType(HFcrFaceImage hFaceImage, NByte * pValue);
NResult N_API FcrFaceImageSetSourceType(HFcrFaceImage hFaceImage, BdifImageSourceType value, NByte vendorValue);
NResult N_API FcrFaceImageGetDeviceType(HFcrFaceImage hFaceImage, NUShort * pValue);
NResult N_API FcrFaceImageSetDeviceType(HFcrFaceImage hFaceImage, NUShort value);
NResult N_API FcrFaceImageGetQuality(HFcrFaceImage hFaceImage, NUShort * pValue);
NResult N_API FcrFaceImageSetQuality(HFcrFaceImage hFaceImage, NUShort value);
NResult N_API FcrFaceImageGetImageDataN(HFcrFaceImage hFaceImage, HNBuffer * phValue);
NResult N_API FcrFaceImageSetImageDataN(HFcrFaceImage hFaceImage, HNBuffer hValue);
NResult N_API FcrFaceImageSetImageDataEx(HFcrFaceImage hFaceImage, const void * pValue, NSizeType valueSize, NBool copy);

#ifdef N_CPP
}
#endif

#endif // !FCR_FACE_IMAGE_H_INCLUDED
