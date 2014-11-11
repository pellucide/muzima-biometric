#include <Biometrics/Standards/FcrFaceImage.hpp>

#ifndef FC_RECORD_HPP_INCLUDED
#define FC_RECORD_HPP_INCLUDED

#include <Biometrics/Standards/BdifTypes.hpp>
#include <Images/NImage.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
using ::Neurotec::Images::HNImage;
#include <Biometrics/Standards/FCRecord.h>
}}}

namespace Neurotec { namespace Biometrics { namespace Standards
{

#undef FCR_MAX_FACE_IMAGE_COUNT

#undef FCR_PROCESS_FIRST_FACE_IMAGE_ONLY

const NInt FCR_MAX_FACE_IMAGE_COUNT = N_USHORT_MAX;

const NUInt FCR_PROCESS_FIRST_FACE_IMAGE_ONLY = 0x00000100;

class FCRecord : public NObject
{
	N_DECLARE_OBJECT_CLASS(FCRecord, NObject)

public:
	class FaceImageCollection : public ::Neurotec::Collections::NCollectionBase<FcrFaceImage, FCRecord,
		FCRecordGetFaceImageCount, FCRecordGetFaceImageEx>
	{
		FaceImageCollection(const FCRecord & owner)
		{
			SetOwner(owner);
		}

		friend class FCRecord;
	public:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(FCRecordGetFaceImageCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(FCRecordSetFaceImageCapacity(this->GetOwnerHandle(), value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(FCRecordRemoveFaceImage(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FCRecordClearFaceImages(this->GetOwnerHandle()));
		}

		FcrFaceImage Add(FcrFaceImageType faceImageType, FcrImageDataType imageDataType, NUShort width, NUShort height, FcrImageColorSpace imageColorSpace, NByte vendorImageColorSpace,
			const ::Neurotec::IO::NBuffer & imageData, NUInt flags = 0)
		{
			HFcrFaceImage hFaceImage;
			NCheck(FCRecordAddFaceImageN(this->GetOwnerHandle(), faceImageType, imageDataType, width, height, imageColorSpace, vendorImageColorSpace, imageData.GetHandle(), flags | N_OBJECT_REF_RET, &hFaceImage));
			return FromHandle<FcrFaceImage>(hFaceImage, true);
		}

		FcrFaceImage Add(FcrFaceImageType faceImageType, FcrImageDataType imageDataType, NUShort width, NUShort height, FcrImageColorSpace imageColorSpace, NByte vendorImageColorSpace,
			const void * pImageData, NSizeType imageDataLength, NUInt flags = 0)
		{
			HFcrFaceImage hFaceImage;
			NCheck(FCRecordAddFaceImage(this->GetOwnerHandle(), faceImageType, imageDataType, width, height, imageColorSpace, vendorImageColorSpace, pImageData, imageDataLength, flags | N_OBJECT_REF_RET, &hFaceImage));
			return FromHandle<FcrFaceImage>(hFaceImage, true);
		}

		FcrFaceImage Add(const ::Neurotec::Images::NImage & image, FcrFaceImageType faceImageType, FcrImageDataType imageDataType, NUInt flags = 0)
		{
			HFcrFaceImage hFaceImage;
			NCheck(FCRecordAddFaceImageFromNImage(this->GetOwnerHandle(), image.GetHandle(), faceImageType, imageDataType, flags | N_OBJECT_REF_RET, &hFaceImage));
			return FromHandle<FcrFaceImage>(hFaceImage, true);
		}
	};

private:
	static HFCRecord Create(BdifStandard standard, NUInt flags)
	{
		HFCRecord handle;
		NCheck(FCRecordCreate(flags, standard, &handle));
		return handle;
	}

	static HFCRecord Create(const ::Neurotec::IO::NBuffer & buffer, BdifStandard standard, NUInt flags, NSizeType * pSize)
	{
		HFCRecord handle;
		NCheck(FCRecordCreateFromMemoryN(buffer.GetHandle(), flags, standard, pSize, &handle));
		return handle;
	}

	static HFCRecord Create(const void * pBuffer, NSizeType bufferSize, BdifStandard standard, NUInt flags, NSizeType * pSize)
	{
		HFCRecord handle;
		NCheck(FCRecordCreateFromMemory(pBuffer, bufferSize, flags, standard, pSize, &handle));
		return handle;
	}

	static HFCRecord Create(const FCRecord & srcRecord, BdifStandard standard, NUInt flags)
	{
		HFCRecord handle;
		NCheck(FCRecordCreateFromFCRecord(srcRecord.GetHandle(), flags, standard, &handle));
		return handle;
	}

	static HFCRecord Create(const ::Neurotec::Images::NImage & image, FcrFaceImageType faceImageType, FcrImageDataType imageDataType, BdifStandard standard, NUInt flags)
	{
		HFCRecord handle;
		NCheck(FCRecordCreateFromNImage(image.GetHandle(), faceImageType, imageDataType, flags, standard, &handle));
		return handle;
	}

public:
	static NSizeType CalculateSize(BdifStandard standard, const NSizeType * arFaceImageSizes, NInt faceImageCount)
	{
		NSizeType size;
		NCheck(FCRecordCalculateSize(standard, faceImageCount, arFaceImageSizes, &size));
		return size;
	}

	explicit FCRecord(BdifStandard standard, NUInt flags = 0)
		: NObject(Create(standard, flags), true)
	{
	}

	FCRecord(const ::Neurotec::IO::NBuffer & buffer, BdifStandard standard, NUInt flags = 0, NSizeType * pSize = NULL)
		: NObject(Create(buffer, standard, flags, pSize), true)
	{
	}

	FCRecord(const void * pBuffer, NSizeType bufferSize, BdifStandard standard, NUInt flags = 0, NSizeType * pSize = NULL)
		: NObject(Create(pBuffer, bufferSize, standard, flags, pSize), true)
	{
	}

	FCRecord(const FCRecord & srcRecord, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(srcRecord, standard, flags), true)
	{
	}

	FCRecord(const ::Neurotec::Images::NImage & image, FcrFaceImageType faceImageType, FcrImageDataType imageDataType, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(image, faceImageType, imageDataType, standard, flags), true)
	{
	}

	BdifStandard GetStandard() const
	{
		BdifStandard value;
		NCheck(FCRecordGetStandard(GetHandle(), &value));
		return value;
	}

	FaceImageCollection GetFaceImages()
	{
		return FaceImageCollection(*this);
	}

	const FaceImageCollection GetFaceImages() const
	{
		return FaceImageCollection(*this);
	}
};

}}}

#endif // !FC_RECORD_HPP_INCLUDED
