#ifndef IIR_IRIS_H_INCLUDED
#define IIR_IRIS_H_INCLUDED

#include <Biometrics/Standards/BdifTypes.h>
#include <Images/NImage.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(IirIris, NObject)

#ifdef N_CPP
}
#endif

#include <Biometrics/Standards/IIRecord.h>
#include <Biometrics/Standards/IirIrisImage.h>

#ifdef N_CPP
extern "C"
{
#endif

#define IIRI_MAX_IRIS_IMAGE_COUNT N_USHORT_MAX
	
N_DEPRECATED("function is deprecated")
NResult N_API IirIrisCalculateSize(BdifStandard standard, NInt irisImageCount, const NSizeType * arIrisImageSizes, NSizeType * pSize);

#define IIRI_PROCESS_FIRST_IRIS_IMAGE_ONLY 0x00001000

NResult N_API IirIrisCreate(NUInt flags, HIirIris * phIris);
NResult N_API IirIrisGetStandard(HIirIris hIris, BdifStandard * pValue);
NResult N_API IirIrisSetStandard(HIirIris hIris, BdifStandard value);
NResult N_API IirIrisGetVersion(HIirIris hIris, NVersion_ * pValue);
NResult N_API IirIrisSetVersion(HIirIris hIris, NVersion_ value);
NResult N_API IirIrisGetIrisImageCount(HIirIris hIris, NInt * pValue);
NResult N_API IirIrisGetIrisImage(HIirIris hIris, NInt index, HIirIrisImage * phValue);
NResult N_API IirIrisGetIrisImageCapacity(HIirIris hIris, NInt * pValue);
NResult N_API IirIrisSetIrisImageCapacity(HIirIris hIris, NInt value);
NResult N_API IirIrisAddIrisImageN(HIirIris hIris, HNBuffer hImageData, NUInt flags, HIirIrisImage * phIrisImage);
NResult N_API IirIrisAddIrisImage(HIirIris hIris, const void * pImageData, NSizeType imageDataLength, NUInt flags, HIirIrisImage * phIrisImage);
N_DEPRECATED("function is deprecated, use IirIrisAddIrisImageFromNImageEx instead")
NResult N_API IirIrisAddIrisImageFromNImage(HIirIris hIris, HNImage hImage, NUInt flags, HIirIrisImage * phIrisImage);
NResult N_API IirIrisAddIrisImageFromNImageEx(HIirIris hIris, HNImage hImage, IirImageFormat imageFormat, NUInt flags, HIirIrisImage * phIrisImage);
NResult N_API IirIrisRemoveIrisImageAt(HIirIris hIris, NInt index);
NResult N_API IirIrisClearIrisImages(HIirIris hIris);

NResult N_API IirIrisGetPosition(HIirIris hIris, BdifEyePosition * pValue);
NResult N_API IirIrisSetPosition(HIirIris hIris, BdifEyePosition value);

#define IirIrisGetIrisImageEx(hIris, index, phValue) IirIrisGetIrisImage(hIris, index, phValue)
#define IirIrisRemoveIrisImage(hIris, index) IirIrisRemoveIrisImageAt(hIris, index)

#ifdef N_MSVC
	#pragma deprecated("IirIrisGetIrisImageEx")
	#pragma deprecated("IirIrisRemoveIrisImage")
#endif

#ifdef N_CPP
}
#endif

#endif // !IIR_IRIS_H_INCLUDED
