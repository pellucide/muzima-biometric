#ifndef FIR_FINGER_VIEW_H_INCLUDED
#define FIR_FINGER_VIEW_H_INCLUDED

#include <Biometrics/Standards/BdifTypes.h>
#include <Images/NImage.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(FirFingerView, NObject)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/FIRecord.h>
#include <Biometrics/Standards/FirFinger.h>

#ifdef N_CPP
extern "C"
{
#endif

NResult N_API FirFingerViewCalculateSize(BdifStandard standard, NSizeType imageDataLength, NSizeType * pSize);

NResult N_API FirFingerViewToNImage(HFirFingerView hFingerView, NUInt flags, HNImage * phImage);

NResult N_API FirFingerViewGetPosition(HFirFingerView hFingerView, BdifFPPosition * pValue);
NResult N_API FirFingerViewGetViewCount(HFirFingerView hFingerView, NInt * pValue);
NResult N_API FirFingerViewGetViewNumber(HFirFingerView hFingerView, NInt * pValue);
NResult N_API FirFingerViewGetImageQuality(HFirFingerView hFingerView, NByte * pValue);
NResult N_API FirFingerViewSetImageQuality(HFirFingerView hFingerView, NByte value);
NResult N_API FirFingerViewGetImpressionType(HFirFingerView hFingerView, BdifFPImpressionType * pValue);
NResult N_API FirFingerViewSetImpressionType(HFirFingerView hFingerView, BdifFPImpressionType value);
NResult N_API FirFingerViewGetHorzLineLength(HFirFingerView hFingerView, NUShort * pValue);
NResult N_API FirFingerViewGetVertLineLength(HFirFingerView hFingerView, NUShort * pValue);
NResult N_API FirFingerViewGetImageDataN(HFirFingerView hFingerView, HNBuffer * phValue);
NResult N_API FirFingerViewSetImageDataN(HFirFingerView hFingerView, HNBuffer hValue);
NResult N_API FirFingerViewSetImageDataEx(HFirFingerView hFingerView, const void * pValue, NSizeType valueSize, NBool copy);

#ifdef N_CPP
}
#endif

#endif // !FIR_FINGER_VIEW_H_INCLUDED
