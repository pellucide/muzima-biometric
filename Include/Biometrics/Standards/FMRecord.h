#ifndef FM_RECORD_H_INCLUDED
#define FM_RECORD_H_INCLUDED

#include <Biometrics/Standards/BdifTypes.h>
#include <Biometrics/NTemplate.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(FMRecord, NObject)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/FmrFinger.h>
#include <Biometrics/Standards/FmrFingerView.h>

#ifdef N_CPP
extern "C"
{
#endif

typedef enum FmrCaptureEquipmentCompliance_
{
	fmrcecNone = 0,
	fmrcecFbi = 0x80,
	fmrcecIso = 0x01
} FmrCaptureEquipmentCompliance;

N_DECLARE_TYPE(FmrCaptureEquipmentCompliance)

#define FMR_MAX_FINGER_VIEW_COUNT 255
#define FMR_MAX_FINGER_COUNT      FMR_MAX_FINGER_VIEW_COUNT

NResult N_API FMRecordCalculateSize(BdifStandard standard, NInt fingerViewCount, const NSizeType * arFingerViewSizes, NSizeType * pSize);
NResult N_API FMRecordCalculateSizeWithFingers(BdifStandard standard, NInt fingerCount, const NSizeType * arFingerSizes, NSizeType * pSize);

#define FMR_PROCESS_FIRST_FINGER_ONLY      0x00000100
#define FMR_PROCESS_FIRST_FINGER_VIEW_ONLY (FMR_PROCESS_FIRST_FINGER_ONLY | FMRF_PROCESS_FIRST_FINGER_VIEW_ONLY)

NResult N_API FMRecordCreate(NUShort sizeX, NUShort sizeY, NUShort resolutionX, NUShort resolutionY, NUInt flags, BdifStandard standard, HFMRecord * phRecord);
NResult N_API FMRecordCreateFromMemoryN(HNBuffer hBuffer, NUInt flags, BdifStandard standard, NSizeType * pSize, HFMRecord * phRecord);
NResult N_API FMRecordCreateFromMemory(const void * pBuffer, NSizeType bufferSize, NUInt flags, BdifStandard standard, NSizeType * pSize, HFMRecord * phRecord);
NResult N_API FMRecordCreateFromFMRecord(HFMRecord hSrcRecord, NUInt flags, BdifStandard standard, HFMRecord * phRecord);
NResult N_API FMRecordCreateFromNFRecord(HNFRecord hNFRecord, NUInt flags, BdifStandard standard, HFMRecord * phRecord);
NResult N_API FMRecordCreateFromNFTemplate(HNFTemplate hNFTemplate, NUInt flags, BdifStandard standard, HFMRecord * phRecord);

NResult N_API FMRecordGetFingerCount(HFMRecord hRecord, NInt * pValue);
NResult N_API FMRecordGetFingerEx(HFMRecord hRecord, NInt index, HFmrFinger * phValue);
NResult N_API FMRecordGetFingerCapacity(HFMRecord hRecord, NInt * pValue);
NResult N_API FMRecordSetFingerCapacity(HFMRecord hRecord, NInt value);
NResult N_API FMRecordAddFinger(HFMRecord hRecord, BdifFPPosition fingerPosition, NUInt flags, HFmrFinger * phFinger);
NResult N_API FMRecordRemoveFinger(HFMRecord hRecord, NInt index);
NResult N_API FMRecordClearFingers(HFMRecord hRecord);

NResult N_API FMRecordGetFingerViewCount(HFMRecord hRecord, NInt * pValue);
NResult N_API FMRecordGetFingerViewEx(HFMRecord hRecord, NInt index, HFmrFingerView * phValue);
NResult N_API FMRecordRemoveFingerView(HFMRecord hRecord, NInt index);
NResult N_API FMRecordClearFingerViews(HFMRecord hRecord);

NResult N_API FMRecordToNFTemplate(HFMRecord hRecord, NUInt flags, HNFTemplate * phNFTemplate);
NResult N_API FMRecordToNTemplate(HFMRecord hRecord, NUInt flags, HNTemplate * phNTemplate);

NResult N_API FMRecordGetStandard(HFMRecord hRecord, BdifStandard * pValue);
NResult N_API FMRecordGetCbeffProductId(HFMRecord hRecord, NUInt * pValue);
NResult N_API FMRecordSetCbeffProductId(HFMRecord hRecord, NUInt value);
NResult N_API FMRecordGetCaptureEquipmentCompliance(HFMRecord hRecord, FmrCaptureEquipmentCompliance * pValue);
NResult N_API FMRecordSetCaptureEquipmentCompliance(HFMRecord hRecord, FmrCaptureEquipmentCompliance value);
NResult N_API FMRecordGetCaptureEquipmentId(HFMRecord hRecord, NUShort * pValue);
NResult N_API FMRecordSetCaptureEquipmentId(HFMRecord hRecord, NUShort value);
NResult N_API FMRecordGetSizeX(HFMRecord hRecord, NUShort * pValue);
NResult N_API FMRecordGetSizeY(HFMRecord hRecord, NUShort * pValue);
NResult N_API FMRecordGetResolutionX(HFMRecord hRecord, NUShort * pValue);
NResult N_API FMRecordGetResolutionY(HFMRecord hRecord, NUShort * pValue);

#ifdef N_CPP
}
#endif

#endif // !FM_RECORD_H_INCLUDED
