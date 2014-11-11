#ifndef FC_RECORD_H_INCLUDED
#define FC_RECORD_H_INCLUDED

#include <Biometrics/Standards/BdifTypes.h>
#include <Images/NImage.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(FCRecord, NObject)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/FcrFaceImage.h>

#ifdef N_CPP
extern "C"
{
#endif

#define FCR_MAX_FACE_IMAGE_COUNT N_USHORT_MAX

NResult N_API FCRecordCalculateSize(BdifStandard standard, NInt faceImageCount, const NSizeType * arFaceImageSizes, NSizeType * pSize);

#define FCR_PROCESS_FIRST_FACE_IMAGE_ONLY 0x00000100

NResult N_API FCRecordCreate(NUInt flags, BdifStandard standard, HFCRecord * phRecord);
NResult N_API FCRecordCreateFromMemoryN(HNBuffer hBuffer, NUInt flags, BdifStandard standard, NSizeType * pSize, HFCRecord * phRecord);
NResult N_API FCRecordCreateFromMemory(const void * pBuffer, NSizeType bufferSize, NUInt flags, BdifStandard standard, NSizeType * pSize, HFCRecord * phRecord);
NResult N_API FCRecordCreateFromFCRecord(HFCRecord hSrcRecord, NUInt flags, BdifStandard standard, HFCRecord * phRecord);
NResult N_API FCRecordCreateFromNImage(HNImage hImage, FcrFaceImageType faceImageType, FcrImageDataType imageDataType, NUInt flags, BdifStandard standard, HFCRecord * phRecord);

NResult N_API FCRecordGetFaceImageCount(HFCRecord hRecord, NInt * pValue);
NResult N_API FCRecordGetFaceImageEx(HFCRecord hRecord, NInt index, HFcrFaceImage * phValue);
NResult N_API FCRecordGetFaceImageCapacity(HFCRecord hRecord, NInt * pValue);
NResult N_API FCRecordSetFaceImageCapacity(HFCRecord hRecord, NInt value);
NResult N_API FCRecordAddFaceImageN(HFCRecord hRecord, FcrFaceImageType faceImageType,
	FcrImageDataType imageDataType, NUShort width, NUShort height, FcrImageColorSpace imageColorSpace, NByte vendorImageColorSpace,
	HNBuffer hImageData, NUInt flags, HFcrFaceImage * phFaceImage);
NResult N_API FCRecordAddFaceImage(HFCRecord hRecord, FcrFaceImageType faceImageType,
	FcrImageDataType imageDataType, NUShort width, NUShort height, FcrImageColorSpace imageColorSpace, NByte vendorImageColorSpace,
	const void * pImageData, NSizeType imageDataLength, NUInt flags, HFcrFaceImage * phFaceImage);
NResult N_API FCRecordAddFaceImageFromNImage(HFCRecord hRecord, HNImage hImage, FcrFaceImageType faceImageType,
	FcrImageDataType imageDataType, NUInt flags, HFcrFaceImage * phFaceImage);
NResult N_API FCRecordRemoveFaceImage(HFCRecord hRecord, NInt index);
NResult N_API FCRecordClearFaceImages(HFCRecord hRecord);

NResult N_API FCRecordGetStandard(HFCRecord hRecord, BdifStandard * pValue);

#ifdef N_CPP
}
#endif

#endif // !FC_RECORD_H_INCLUDED
