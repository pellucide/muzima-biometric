#ifndef FMR_FINGER_VIEW_H_INCLUDED
#define FMR_FINGER_VIEW_H_INCLUDED

#include <Biometrics/Standards/BdifTypes.h>
#include <Biometrics/NFRecord.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(FmrFingerView, NObject)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/FMRecord.h>
#include <Biometrics/Standards/FmrFinger.h>

#ifdef N_CPP
extern "C"
{
#endif

#define FMRFV_MAX_DIMENSION 16383
#define FMRFV_MAX_MINUTIA_COUNT 255
#define FMRFV_MAX_CORE_COUNT 15
#define FMRFV_MAX_DELTA_COUNT 15

struct FmrMinutia_
{
	NUShort X;
	NUShort Y;
	BdifFPMinutiaType Type;
	NByte Angle;
	NByte Quality;
};
#ifndef FMR_FINGER_VIEW_HPP_INCLUDED
typedef struct FmrMinutia_ FmrMinutia;
#endif

N_DECLARE_TYPE(FmrMinutia)

NResult N_API FmrMinutiaToStringN(const struct FmrMinutia_ * pValue, HNString hFormat, HNString * phValue);
NResult N_API FmrMinutiaToStringA(const struct FmrMinutia_ * pValue, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API FmrMinutiaToStringW(const struct FmrMinutia_ * pValue, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API FmrMinutiaToString(const FmrMinutia * pValue, const NChar * szFormat, HNString * phValue);
#endif
#define FmrMinutiaToString N_FUNC_AW(FmrMinutiaToString)

struct FmrCore_
{
	NUShort X;
	NUShort Y;
	NInt Angle;
};
#ifndef FMR_FINGER_VIEW_HPP_INCLUDED
typedef struct FmrCore_ FmrCore;
#endif

N_DECLARE_TYPE(FmrCore)

NResult N_API FmrCoreToStringN(const struct FmrCore_ * pValue, HNString hFormat, HNString * phValue);
NResult N_API FmrCoreToStringA(const struct FmrCore_ * pValue, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API FmrCoreToStringW(const struct FmrCore_ * pValue, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API FmrCoreToString(const FmrCore * pValue, const NChar * szFormat, HNString * phValue);
#endif
#define FmrCoreToString N_FUNC_AW(FmrCoreToString)

struct FmrDelta_
{
	NUShort X;
	NUShort Y;
	NInt Angle1;
	NInt Angle2;
	NInt Angle3;
};
#ifndef FMR_FINGER_VIEW_HPP_INCLUDED
typedef struct FmrDelta_ FmrDelta;
#endif

N_DECLARE_TYPE(FmrDelta)

NResult N_API FmrDeltaToStringN(const struct FmrDelta_ * pValue, HNString hFormat, HNString * phValue);
NResult N_API FmrDeltaToStringA(const struct FmrDelta_ * pValue, const NAChar * szFormat, HNString * phValue);
#ifndef N_NO_UNICODE
NResult N_API FmrDeltaToStringW(const struct FmrDelta_ * pValue, const NWChar * szFormat, HNString * phValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API FmrDeltaToString(const FmrDelta * pValue, const NChar * szFormat, HNString * phValue);
#endif
#define FmrDeltaToString N_FUNC_AW(FmrDeltaToString)

NResult N_API FmrFingerViewGetMaxSize(BdifStandard standard, NInt minutiaCount, NBool hasFourNeighborRidgeCounts, NBool hasEightNeighborRidgeCounts,
	NInt coreWithAngleCount, NInt coreCount, NInt deltaWithAngleCount, NInt deltaCount, NSizeType * pSize);
NResult N_API FmrFingerViewGetMaxSizeEx(BdifStandard standard, NInt minutiaCount, NBool hasFourNeighborRidgeCounts, NBool hasEightNeighborRidgeCounts,
	NInt coreWithAngleCount, NInt coreCount, NInt deltaWithAngleCount, NInt deltaCount,
	NBool hasNeurotecCurvatures, NBool hasNeurotecGs, NInt neurotecBOWidth, NInt neurotecBOHeight, NSizeType * pSize);

#define FMRFV_SKIP_RIDGE_COUNTS         NFR_SKIP_RIDGE_COUNTS
#define FMRFV_SKIP_SINGULAR_POINTS      NFR_SKIP_SINGULAR_POINTS
#define FMRFV_PROCESS_ALL_EXTENDED_DATA 0x01000000
#define FMRFV_OLD_CONVERT               0x20000000
#define FMRFV_SKIP_NEUROTEC_FIELDS      0x40000000
#define FMRFV_USE_NEUROTEC_FIELDS       0x80000000

NResult N_API FmrFingerViewGetMinutiaCount(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewGetMinutia(HFmrFingerView hFingerView, NInt index, struct FmrMinutia_ * pValue);
NResult N_API FmrFingerViewSetMinutia(HFmrFingerView hFingerView, NInt index, const struct FmrMinutia_ * pValue);
NResult N_API FmrFingerViewGetMinutiaeEx(HFmrFingerView hFingerView, struct FmrMinutia_ * arValue, NInt valueLength);
NResult N_API FmrFingerViewGetMinutiaCapacity(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewSetMinutiaCapacity(HFmrFingerView hFingerView, NInt value);
NResult N_API FmrFingerViewAddMinutia(HFmrFingerView hFingerView, const struct FmrMinutia_ * pValue);
NResult N_API FmrFingerViewInsertMinutia(HFmrFingerView hFingerView, NInt index, const struct FmrMinutia_ * pValue);
NResult N_API FmrFingerViewRemoveMinutia(HFmrFingerView hFingerView, NInt index);
NResult N_API FmrFingerViewClearMinutiae(HFmrFingerView hFingerView);

NResult N_API FmrFingerViewGetMinutiaFourNeighbor(HFmrFingerView hFingerView, NInt minutiaIndex, NInt index, struct BdifFPMinutiaNeighbor_ * pValue);
NResult N_API FmrFingerViewSetMinutiaFourNeighbor(HFmrFingerView hFingerView, NInt minutiaIndex, NInt index, const struct BdifFPMinutiaNeighbor_ * pValue);
NResult N_API FmrFingerViewGetMinutiaFourNeighborsEx(HFmrFingerView hFingerView, NInt minutiaIndex, struct BdifFPMinutiaNeighbor_ * arValue, NInt valueLength);

NResult N_API FmrFingerViewGetMinutiaEightNeighbor(HFmrFingerView hFingerView, NInt minutiaIndex, NInt index, struct BdifFPMinutiaNeighbor_ * pValue);
NResult N_API FmrFingerViewSetMinutiaEightNeighbor(HFmrFingerView hFingerView, NInt minutiaIndex, NInt index, const struct BdifFPMinutiaNeighbor_ * pValue);
NResult N_API FmrFingerViewGetMinutiaEightNeighborsEx(HFmrFingerView hFingerView, NInt minutiaIndex, struct BdifFPMinutiaNeighbor_ * arValue, NInt valueLength);

NResult N_API FmrFingerViewGetCoreCount(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewGetCore(HFmrFingerView hFingerView, NInt index, struct FmrCore_ * pValue);
NResult N_API FmrFingerViewSetCore(HFmrFingerView hFingerView, NInt index, const struct FmrCore_ * pValue);
NResult N_API FmrFingerViewGetCoresEx(HFmrFingerView hFingerView, struct FmrCore_ * arValue, NInt valueLength);
NResult N_API FmrFingerViewGetCoreCapacity(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewSetCoreCapacity(HFmrFingerView hFingerView, NInt value);
NResult N_API FmrFingerViewAddCore(HFmrFingerView hFingerView, const struct FmrCore_ * pValue);
NResult N_API FmrFingerViewInsertCore(HFmrFingerView hFingerView, NInt index, const struct FmrCore_ * pValue);
NResult N_API FmrFingerViewRemoveCore(HFmrFingerView hFingerView, NInt index);
NResult N_API FmrFingerViewClearCores(HFmrFingerView hFingerView);

NResult N_API FmrFingerViewGetDeltaCount(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewGetDelta(HFmrFingerView hFingerView, NInt index, struct FmrDelta_ * pValue);
NResult N_API FmrFingerViewSetDelta(HFmrFingerView hFingerView, NInt index, const struct FmrDelta_ * pValue);
NResult N_API FmrFingerViewGetDeltasEx(HFmrFingerView hFingerView, struct FmrDelta_ * arValue, NInt valueLength);
NResult N_API FmrFingerViewGetDeltaCapacity(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewSetDeltaCapacity(HFmrFingerView hFingerView, NInt value);
NResult N_API FmrFingerViewAddDelta(HFmrFingerView hFingerView, const struct FmrDelta_ * pValue);
NResult N_API FmrFingerViewInsertDelta(HFmrFingerView hFingerView, NInt index, const struct FmrDelta_ * pValue);
NResult N_API FmrFingerViewRemoveDelta(HFmrFingerView hFingerView, NInt index);
NResult N_API FmrFingerViewClearDeltas(HFmrFingerView hFingerView);

NResult N_API FmrFingerViewToNFRecord(HFmrFingerView hFingerView, NUInt flags, HNFRecord * phNFRecord);

NResult N_API FmrFingerViewGetFingerPosition(HFmrFingerView hFingerView, BdifFPPosition * pValue);
NResult N_API FmrFingerViewGetImpressionType(HFmrFingerView hFingerView, BdifFPImpressionType * pValue);
NResult N_API FmrFingerViewSetImpressionType(HFmrFingerView hFingerView, BdifFPImpressionType value);
NResult N_API FmrFingerViewGetViewCount(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewGetViewNumber(HFmrFingerView hFingerView, NInt * pValue);
NResult N_API FmrFingerViewGetFingerQuality(HFmrFingerView hFingerView, NByte * pValue);
NResult N_API FmrFingerViewSetFingerQuality(HFmrFingerView hFingerView, NByte value);
NResult N_API FmrFingerViewHasFourNeighborRidgeCounts(HFmrFingerView hFingerView, NBool * pValue);
NResult N_API FmrFingerViewSetHasFourNeighborRidgeCounts(HFmrFingerView hFingerView, NBool value);
NResult N_API FmrFingerViewHasEightNeighborRidgeCounts(HFmrFingerView hFingerView, NBool * pValue);
NResult N_API FmrFingerViewSetHasEightNeighborRidgeCounts(HFmrFingerView hFingerView, NBool value);

#ifdef N_CPP
}
#endif

#endif // !FMR_FINGER_VIEW_H_INCLUDED
