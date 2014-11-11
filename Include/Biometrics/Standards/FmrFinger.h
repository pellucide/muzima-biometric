#ifndef FMR_FINGER_H_INCLUDED
#define FMR_FINGER_H_INCLUDED

#include <Biometrics/Standards/BdifTypes.h>
#include <Biometrics/NFRecord.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(FmrFinger, NObject)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/FMRecord.h>
#include <Biometrics/Standards/FmrFingerView.h>

#ifdef N_CPP
extern "C"
{
#endif

#define FMRF_MAX_FINGER_VIEW_COUNT 16

NResult N_API FmrFingerCalculateSize(BdifStandard standard, NInt fingerViewCount, const NSizeType * arFingerViewSizes, NSizeType * pSize);

#define FMRF_PROCESS_FIRST_FINGER_VIEW_ONLY 0x00001000 // Load/save

NResult N_API FmrFingerGetFingerViewCount(HFmrFinger hFinger, NInt * pValue);
NResult N_API FmrFingerGetFingerViewEx(HFmrFinger hFinger, NInt index, HFmrFingerView * phValue);
NResult N_API FmrFingerGetFingerViewCapacity(HFmrFinger hFinger, NInt * pValue);
NResult N_API FmrFingerSetFingerViewCapacity(HFmrFinger hFinger, NInt value);
NResult N_API FmrFingerAddFingerView(HFmrFinger hFinger, NUInt flags, HFmrFingerView * phFingerView);
NResult N_API FmrFingerRemoveFingerView(HFmrFinger hFinger, NInt index);
NResult N_API FmrFingerClearFingerViews(HFmrFinger hFinger);

NResult N_API FmrFingerGetFingerPosition(HFmrFinger hFinger, BdifFPPosition * pValue);
NResult N_API FmrFingerSetFingerPosition(HFmrFinger hFinger, BdifFPPosition value);

#ifdef N_CPP
}
#endif

#endif // !FMR_FINGER_H_INCLUDED
