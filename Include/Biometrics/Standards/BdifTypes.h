#ifndef BDIF_TYPES_H_INCLUDED
#define BDIF_TYPES_H_INCLUDED

#include <Core/NTypes.h>

#ifdef N_CPP
extern "C"
{
#endif

typedef enum BdifStandard_
{
	bsUnspecified = -1,
	bsIso = 0,
	bsAnsi = 1
} BdifStandard;

N_DECLARE_TYPE(BdifStandard)

typedef enum BdifScaleUnits_
{
	bsuNone = 0,
	bsuPixelsPerInch = 1,
	bsuPixelsPerCentimeter = 2
} BdifScaleUnits;

N_DECLARE_TYPE(BdifScaleUnits)

struct BdifQualityBlock_
{
	NByte qualityScore;
	NUShort qualityAlgorithmVendorId;
	NUShort qualityAlgorithmId;
};
#ifndef BDIF_TYPES_HPP_INCLUDED
typedef struct BdifQualityBlock_ BdifQualityBlock;
#endif

N_DECLARE_TYPE(BdifQualityBlock)

NResult N_API BdifQualityBlockToStringN(const struct BdifQualityBlock_ * pValue, HNString hFormat, HNString * phValue);
NResult N_API BdifQualityBlockToStringA(const struct BdifQualityBlock_ * pValue, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API BdifQualityBlockToStringW(const struct BdifQualityBlock_ * pValue, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API BdifQualityBlockToString(const BdifQualityBlock * pValue, const NChar * szFormat, HNString * phValue);
#endif
#define BdifQualityBlockToString N_FUNC_AW(BdifQualityBlockToString)

struct BdifCertificationBlock_
{
	NUShort certificationAuthorityId;
	NByte certificationSchemeId;
};
#ifndef BDIF_TYPES_HPP_INCLUDED
typedef struct BdifCertificationBlock_ BdifCertificationBlock;
#endif

N_DECLARE_TYPE(BdifCertificationBlock)

NResult N_API BdifCertificationBlockToStringN(const struct BdifCertificationBlock_ * pValue, HNString hFormat, HNString * phValue);
NResult N_API BdifCertificationBlockToStringA(const struct BdifCertificationBlock_ * pValue, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API BdifCertificationBlockToStringW(const struct BdifCertificationBlock_ * pValue, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API BdifCertificationBlockToString(const BdifCertificationBlock * pValue, const NChar * szFormat, HNString * phValue);
#endif
#define BdifQualityBlockToString N_FUNC_AW(BdifQualityBlockToString)

typedef enum BdifFPPosition_
{
	bfppUnknown = 0,
	bfppRightThumb = 1,
	bfppRightIndexFinger = 2,
	bfppRightMiddleFinger = 3,
	bfppRightRingFinger = 4,
	bfppRightLittleFinger = 5,
	bfppLeftThumb = 6,
	bfppLeftIndexFinger = 7,
	bfppLeftMiddleFinger = 8,
	bfppLeftRingFinger = 9,
	bfppLeftLittleFinger = 10,
	bfppPlainRightThumb = 11,
	bfppPlainLeftThumb = 12,
	bfppPlainRightFourFingers = 13,
	bfppPlainLeftFourFingers = 14,
	bfppPlainThumbs = 15,
	bfppMajorCase = 19,
	bfppUnknownPalm = 20,
	bfppRightFullPalm = 21,
	bfppRightWritersPalm = 22,
	bfppLeftFullPalm = 23,
	bfppLeftWritersPalm = 24,
	bfppRightLowerPalm = 25,
	bfppRightUpperPalm = 26,
	bfppLeftLowerPalm = 27,
	bfppLeftUpperPalm = 28,
	bfppRightOther = 29,
	bfppLeftOther = 30,
	bfppRightInterdigital = 31,
	bfppRightThenar = 32,
	bfppRightHypothenar = 33,
	bfppLeftInterdigital = 34,
	bfppLeftThenar = 35,
	bfppLeftHypothenar = 36,
	bfppRightIndexMiddleFingers = 40,
	bfppRightMiddleRingFingers = 41,
	bfppRightRingLittleFingers = 42,
	bfppLeftIndexMiddleFingers = 43,
	bfppLeftMiddleRingFingers = 44,
	bfppLeftRingLittleFingers = 45,
	bfppRightIndexLeftIndexFingers = 46,
	bfppRightIndexMiddleRingFingers = 47,
	bfppRightMiddleRingLittleFingers = 48,
	bfppLeftIndexMiddleRingFingers = 49,
	bfppLeftMiddleRingLittleFingers = 50
} BdifFPPosition;

N_DECLARE_TYPE(BdifFPPosition)

typedef enum BdifFPImpressionType_
{
	bfpitLiveScanPlain = 0,
	bfpitLiveScanRolled = 1,
	bfpitNonliveScanPlain = 2,
	bfpitNonliveScanRolled = 3,
	bfpitLatentImpression = 4,
	bfpitLatentTracing = 5,
	bfpitLatentPhoto = 6,
	bfpitLatentLift = 7,
	bfpitLiveScanVerticalSwipe = 8,
	bfpitLiveScanContactless = 9,
	bfpitLiveScanPalm = 10,
	bfpitNonliveScanPalm = 11,
	bfpitLatentPalmImpression = 12,
	bfpitLatentPalmTracing = 13,
	bfpitLatentPalmPhoto = 14,
	bfpitLatentPalmLift = 15,
	bfpitLiveScanOpticalContactPlain = 20,
	bfpitLiveScanOpticalContactRolled = 21,
	bfpitLiveScanNonOpticalContactPlain = 22,
	bfpitLiveScanNonOpticalContactRolled = 23,
	bfpitLiveScanOpticalContactlessPlain = 24,
	bfpitLiveScanOpticalContactlessRolled = 25,
	bfpitLiveScanNonOpticalContactlessPlain = 26,
	bfpitLiveScanNonOpticalContactlessRolled = 27,
	bfpitOther = 28,
	bfpitUnknown = 29
} BdifFPImpressionType;

N_DECLARE_TYPE(BdifFPImpressionType)

typedef enum BdifFPatternClass_
{
	bfpcUnknown = 0,
	bfpcPlainArch = 1,
	bfpcTentedArch = 2,
	bfpcRadialLoop = 3,
	bfpcUlnarLoop = 4,
	bfpcPlainWhorl = 5,
	bfpcCentralPocketLoop = 6,
	bfpcDoubleLoop = 7,
	bfpcAccidentalWhorl = 8,
	bfpcWhorl = 9,
	bfpcRightSlantLoop = 10,
	bfpcLeftSlantLoop = 11,
	bfpcScar = 12,
	bfpcAmputation = 15,
	bfpcVendor = 128
} BdifFPatternClass;

N_DECLARE_TYPE(BdifFPatternClass)

typedef enum BdifFPMinutiaType_
{
	bfpmtUnspecified = -1,
	bfpmtUnknown = 0,
	bfpmtEnd = 1,
	bfpmtBifurcation = 2,
	bfpmtOther = 3
} BdifFPMinutiaType;

N_DECLARE_TYPE(BdifFPMinutiaType)

struct BdifFPMinutiaNeighbor_
{
	NInt Index;
	NByte RidgeCount;
};
#ifndef BDIF_TYPES_HPP_INCLUDED
typedef struct BdifFPMinutiaNeighbor_ BdifFPMinutiaNeighbor;
#endif

N_DECLARE_TYPE(BdifFPMinutiaNeighbor)

NResult N_API BdifFPMinutiaNeighborToStringN(const struct BdifFPMinutiaNeighbor_ * pValue, HNString hFormat, HNString * phValue);
NResult N_API BdifFPMinutiaNeighborToStringA(const struct BdifFPMinutiaNeighbor_ * pValue, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API BdifFPMinutiaNeighborToStringW(const struct BdifFPMinutiaNeighbor_ * pValue, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API BdifFPMinutiaNeighborToString(const BdifFPMinutiaNeighbor * pValue, const NChar * szFormat, HNString * phValue);
#endif
#define BdifFPMinutiaNeighborToString N_FUNC_AW(BdifFPMinutiaNeighborToString)

typedef enum BdifGender_
{
	bgUnspecified = 0,
	bgMale = 1,
	bgFemale = 2,
	bgUnknown = 255
} BdifGender;

N_DECLARE_TYPE(BdifGender)

typedef enum BdifEyeColor_
{
	becUnspecified = 0,
	becBlack = 1,
	becBlue = 2,
	becBrown = 3,
	becGray = 4,
	becGreen = 5,
	becHazel = 6,
	becMaroon = 7,
	becMultiColored = 8,
	becPink = 9,
	becUnknown = 255
} BdifEyeColor;

N_DECLARE_TYPE(BdifEyeColor)

typedef enum BdifHairColor_
{
	bhcUnspecified = 0,
	bhcBald = 1,
	bhcBlack = 2,
	bhcBlonde = 3,
	bhcBrown = 4,
	bhcGray = 5,
	bhcRed = 6,
	bhcBlue = 7,
	bhcGreen = 8,
	bhcOrange = 9,
	bhcPink = 10,
	bhcPurple = 11,
	bhcSandy = 12,
	bhcAuburn = 13,
	bhcWhite = 14,
	bhcStrawberry = 15,
	bhcUnknown = 255
} BdifHairColor;

N_DECLARE_TYPE(BdifHairColor)

typedef enum BdifFaceProperties_
{
	bfpNotSpecified = 0,
	bfpSpecified = 0x000001,
	bfpGlasses = 0x000002,
	bfpMoustache = 0x000004,
	bfpBeard = 0x000008,
	bfpTeethVisible = 0x000010,
	bfpBlink = 0x000020,
	bfpMouthOpen = 0x000040,
	bfpLeftEyePatch = 0x000080,
	bfpRightEyePatch = 0x000100,
	bfpBothEyePatch = 0x000200,
	bfpDarkGlasses = 0x000400,
	bfpDistortingCondition = 0x000800,
	bfpHat = 0x01000000,
	bfpScarf = 0x02000000,
	bfpNoEar = 0x04000000
} BdifFaceProperties;

N_DECLARE_TYPE(BdifFaceProperties)

typedef enum BdifFaceExpression_
{
	bfeUnspecified = 0,
	bfeNeutral = 1,
	bfeSmile = 2,
	bfeSmileOpenedJaw = 3,
	bfeRaisedBrows = 4,
	bfeEyesAway = 5,
	bfeSquinting = 6,
	bfeFrowning = 7,
	bfeVendor = 0x8000,
	bfeUnknown = 0xFFFF
} BdifFaceExpression;

N_DECLARE_TYPE(BdifFaceExpression)

typedef enum BdifFaceFeaturePointType_
{
	bffptPoint2D = 1
} BdifFaceFeaturePointType;

N_DECLARE_TYPE(BdifFaceFeaturePointType)

struct BdifFaceFeaturePoint_
{
	BdifFaceFeaturePointType Type;
	NByte Code;
	NUShort X;
	NUShort Y;
};
#ifndef BDIF_TYPES_HPP_INCLUDED
typedef struct BdifFaceFeaturePoint_ BdifFaceFeaturePoint;
#endif

N_DECLARE_TYPE(BdifFaceFeaturePoint)

NResult N_API BdifFaceFeaturePointToStringN(const struct BdifFaceFeaturePoint_ * pValue, HNString hFormat, HNString * phValue);
NResult N_API BdifFaceFeaturePointToStringA(const struct BdifFaceFeaturePoint_ * pValue, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API BdifFaceFeaturePointToStringW(const struct BdifFaceFeaturePoint_ * pValue, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API BdifFaceFeaturePointToString(const BdifFaceFeaturePoint * pValue, const NChar * szFormat, HNString * phValue);
#endif
#define BdifFaceFeaturePointToString N_FUNC_AW(BdifFaceFeaturePointToString)

typedef enum BdifImageSourceType_
{
	bistUnspecified = 0,
	bistUnknownPhoto = 1,
	bistDigitalPhoto = 2,
	bistScannedPhoto = 3,
	bistUnknownVideo = 4,
	bistAnalogueVideo = 5,
	bistDigitalVideo = 6,
	bistUnknown = 7,
	bistVendor = 128
} BdifImageSourceType;

N_DECLARE_TYPE(BdifImageSourceType)

#define BDIF_IRIS_DEVICE_UNIQUE_IDENTIFIER_LENGTH 16

typedef enum BdifEyePosition_
{
	bepUnknown = 0,
	bepRight = 1,
	bepLeft = 2
} BdifEyePosition;

N_DECLARE_TYPE(BdifEyePosition)

typedef enum BdifIrisOrientation_
{
	bioUndefined = 0,
	bioBase = 1,
	bioFlipped = 2
} BdifIrisOrientation;

N_DECLARE_TYPE(BdifIrisOrientation)

typedef enum BdifIrisScanType_
{
	bistUndefined = 0,
	bistProgressive = 1,
	bistInterlaceFrame = 2,
	bistInterlaceField = 3,
	bistCorrected = 4
} BdifIrisScanType;

N_DECLARE_TYPE(BdifIrisScanType)

#define BDIF_NON_STRICT_READ               0x00000001
#define BDIF_DO_NOT_CHECK_CBEFF_PRODUCT_ID 0x00000002
#define BDIF_ALLOW_QUALITY                 0x00000004
#define BDIF_ALLOW_OUT_OF_BOUNDS_FEATURES  0x00000008

NBool N_API BdifStandardIsValid(BdifStandard value);
NBool N_API BdifCertificationFlagIsValid(NByte value);

NFloat N_API BdifAngleToDegrees(NInt value, BdifStandard standard);
NInt N_API BdifAngleFromDegrees(NFloat value, BdifStandard standard);
NDouble N_API BdifAngleToRadians(NInt value, BdifStandard standard);
NInt N_API BdifAngleFromRadians(NDouble value, BdifStandard standard);
NResult N_API BdifAngleToStringN(NInt value, HNString hFormat, HNString * phValue);
NResult N_API BdifAngleToStringA(NInt value, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API BdifAngleToStringW(NInt value, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API BdifAngleToString(NInt value, const NChar * szFormat, HNString * phValue);
#endif
#define BdifAngleToString N_FUNC_AW(BdifAngleToString)

NResult N_API BdifQualityToStringN(NByte value, HNString hFormat, HNString * phValue);
NResult N_API BdifQualityToStringA(NByte value, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API BdifQualityToStringW(NByte value, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API BdifQualityToString(NByte value, const NChar * szFormat, HNString * phValue);
#endif
#define BdifQualityToString N_FUNC_AW(BdifQualityToString)

N_DECLARE_STATIC_OBJECT_TYPE(BdifTypes)

#ifdef N_CPP
}
#endif

#endif // !BDIF_TYPES_H_INCLUDED
