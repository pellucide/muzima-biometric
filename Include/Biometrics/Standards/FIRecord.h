#ifndef FI_RECORD_H_INCLUDED
#define FI_RECORD_H_INCLUDED

#include <Biometrics/Standards/BdifTypes.h>
#include <Images/NImage.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(FIRecord, NObject)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/FirFinger.h>
#include <Biometrics/Standards/FirFingerView.h>

#ifdef N_CPP
extern "C"
{
#endif

typedef enum FirImageCompressionAlgorithm_
{
	firicaNotBitPacked = 0,
	firicaBitPacked = 1,
	firicaWsq = 2,
	firicaJpeg = 3,
	firicaJpeg2000 = 4,
	firicaPng = 5
} FirImageCompressionAlgorithm;

N_DECLARE_TYPE(FirImageCompressionAlgorithm)

#define FIR_MAX_FINGER_COUNT      255
#define FIR_MAX_FINGER_VIEW_COUNT (FIR_MAX_FINGER_COUNT * FIRF_MAX_FINGER_VIEW_COUNT)

NResult N_API FIRecordCalculateSize(BdifStandard standard, NInt fingerViewCount, const NSizeType * arFingerViewSizes, NSizeType * pSize);
NResult N_API FIRecordCalculateSizeWithFingers(BdifStandard standard, NInt fingerCount, const NSizeType * arFingerSizes, NSizeType * pSize);

#define FIR_PROCESS_FIRST_FINGER_ONLY      0x00000100
#define FIR_PROCESS_FIRST_FINGER_VIEW_ONLY (FIR_PROCESS_FIRST_FINGER_ONLY | FIRF_PROCESS_FIRST_FINGER_VIEW_ONLY)

NResult N_API FIRecordCreate(NUShort imageAcquisitionLevel, BdifScaleUnits scaleUnits,
	NUShort horzScanResolution, NUShort vertScanResolution, NUShort horzImageResolution, NUShort vertImageResolution,
	NByte pixelDepth, FirImageCompressionAlgorithm imageCompressionAlgorithm,
	NUInt flags, BdifStandard standard, HFIRecord * phRecord);
NResult N_API FIRecordCreateFromMemoryN(HNBuffer hBuffer, NUInt flags, BdifStandard standard, NSizeType * pSize, HFIRecord * phRecord);
NResult N_API FIRecordCreateFromMemory(const void * pBuffer, NSizeType bufferSize, NUInt flags, BdifStandard standard, NSizeType * pSize, HFIRecord * phRecord);
NResult N_API FIRecordCreateFromFIRecord(HFIRecord hSrcRecord, NUInt flags, BdifStandard standard, HFIRecord * phRecord);
NResult N_API FIRecordCreateFromNImage(HNImage hImage,
	NUShort imageAcquisitionLevel, BdifScaleUnits scaleUnits, NUShort horzScanResolution, NUShort vertScanResolution,
	NByte pixelDepth, FirImageCompressionAlgorithm imageCompressionAlgorithm,
	BdifFPPosition fingerPosition, NUInt flags, BdifStandard standard, HFIRecord * phRecord);

NResult N_API FIRecordGetFingerCount(HFIRecord hRecord, NInt * pValue);
NResult N_API FIRecordGetFingerEx(HFIRecord hRecord, NInt index, HFirFinger * phValue);
NResult N_API FIRecordGetFingerCapacity(HFIRecord hRecord, NInt * pValue);
NResult N_API FIRecordSetFingerCapacity(HFIRecord hRecord, NInt value);
NResult N_API FIRecordAddFinger(HFIRecord hRecord, BdifFPPosition fingerPosition, NUInt flags, HFirFinger * phFinger);
NResult N_API FIRecordAddFingerFromNImage(HFIRecord hRecord, BdifFPPosition fingerPosition, HNImage hImage, NUInt flags, HFirFinger * phFinger);
NResult N_API FIRecordRemoveFinger(HFIRecord hRecord, NInt index);
NResult N_API FIRecordClearFingers(HFIRecord hRecord);

NResult N_API FIRecordGetFingerViewCount(HFIRecord hRecord, NInt * pValue);
NResult N_API FIRecordGetFingerViewEx(HFIRecord hRecord, NInt index, HFirFingerView * phValue);
NResult N_API FIRecordRemoveFingerView(HFIRecord hRecord, NInt index);
NResult N_API FIRecordClearFingerViews(HFIRecord hRecord);

NResult N_API FIRecordGetStandard(HFIRecord hRecord, BdifStandard * pValue);
NResult N_API FIRecordGetCbeffProductId(HFIRecord hRecord, NUInt * pValue);
NResult N_API FIRecordSetCbeffProductId(HFIRecord hRecord, NUInt value);
NResult N_API FIRecordGetCaptureDeviceId(HFIRecord hRecord, NUShort * pValue);
NResult N_API FIRecordSetCaptureDeviceId(HFIRecord hRecord, NUShort value);
NResult N_API FIRecordGetImageAcquisitionLevel(HFIRecord hRecord, NUShort * pValue);
NResult N_API FIRecordGetScaleUnits(HFIRecord hRecord, BdifScaleUnits * pValue);
NResult N_API FIRecordGetHorzScanResolution(HFIRecord hRecord, NUShort * pValue);
NResult N_API FIRecordGetVertScanResolution(HFIRecord hRecord, NUShort * pValue);
NResult N_API FIRecordGetHorzImageResolution(HFIRecord hRecord, NUShort * pValue);
NResult N_API FIRecordGetVertImageResolution(HFIRecord hRecord, NUShort * pValue);
NResult N_API FIRecordGetPixelDepth(HFIRecord hRecord, NByte * pValue);
NResult N_API FIRecordGetImageCompressionAlgorithm(HFIRecord hRecord, FirImageCompressionAlgorithm * pValue);

#ifdef N_CPP
}
#endif

#endif // !FI_RECORD_H_INCLUDED
