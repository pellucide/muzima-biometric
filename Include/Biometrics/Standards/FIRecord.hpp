#include <Biometrics/Standards/FirFinger.hpp>

#ifndef FI_RECORD_HPP_INCLUDED
#define FI_RECORD_HPP_INCLUDED

#include <Biometrics/Standards/BdifTypes.hpp>
#include <Images/NImage.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
using ::Neurotec::Images::HNImage;
#include <Biometrics/Standards/FIRecord.h>
}}}

N_DEFINE_ENUM_TYPE_TRAITS(Neurotec::Biometrics::Standards, FirImageCompressionAlgorithm)

namespace Neurotec { namespace Biometrics { namespace Standards
{

#undef FIR_MAX_FINGER_COUNT
#undef FIR_MAX_FINGER_VIEW_COUNT

#undef FIR_PROCESS_FIRST_FINGER_ONLY
#undef FIR_PROCESS_FIRST_FINGER_VIEW_ONLY

const NInt FIR_MAX_FINGER_COUNT = 255;
const NInt FIR_MAX_FINGER_VIEW_COUNT = (FIR_MAX_FINGER_COUNT * FIRF_MAX_FINGER_VIEW_COUNT);

const NUInt FIR_PROCESS_FIRST_FINGER_ONLY = 0x00000100;
const NUInt FIR_PROCESS_FIRST_FINGER_VIEW_ONLY = (FIR_PROCESS_FIRST_FINGER_ONLY | FIRF_PROCESS_FIRST_FINGER_VIEW_ONLY);

class FIRecord : public NObject
{
	N_DECLARE_OBJECT_CLASS(FIRecord, NObject)

public:
	class FingerCollection : public ::Neurotec::Collections::NCollectionBase<FirFinger, FIRecord,
		FIRecordGetFingerCount, FIRecordGetFingerEx>
	{
		FingerCollection(const FIRecord & owner)
		{
			SetOwner(owner);
		}

		friend class FIRecord;
	protected:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(FIRecordGetFingerCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(FIRecordSetFingerCapacity(this->GetOwnerHandle(), value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(FIRecordRemoveFinger(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FIRecordClearFingers(this->GetOwnerHandle()));
		}

		FirFinger Add(BdifFPPosition fingerPosition, NUInt flags = 0)
		{
			HFirFinger hFinger;
			NCheck(FIRecordAddFinger(this->GetOwnerHandle(), fingerPosition, flags | N_OBJECT_REF_RET, &hFinger));
			return FromHandle<FirFinger>(hFinger, true);
		}

		FirFinger Add(BdifFPPosition fingerPosition, const ::Neurotec::Images::NImage & image, NUInt flags = 0)
		{
			HFirFinger hFinger;
			NCheck(FIRecordAddFingerFromNImage(this->GetOwnerHandle(), fingerPosition, image.GetHandle(), flags | N_OBJECT_REF_RET, &hFinger));
			return FromHandle<FirFinger>(hFinger, true);
		}
	};

	class FingerViewCollection : public ::Neurotec::Collections::NCollectionBase<FirFingerView, FIRecord,
		FIRecordGetFingerViewCount, FIRecordGetFingerViewEx>
	{
		FingerViewCollection(const FIRecord & owner)
		{
			SetOwner(owner);
		}

		friend class FIRecord;
	public:
		void RemoveAt(NInt index)
		{
			NCheck(FIRecordRemoveFingerView(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FIRecordClearFingerViews(this->GetOwnerHandle()));
		}
	};

private:
	static HFIRecord Create(NUShort imageAcquisitionLevel, BdifScaleUnits scaleUnits,
		NUShort horzScanResolution, NUShort vertScanResolution, NUShort horzImageResolution, NUShort vertImageResolution,
		NByte pixelDepth, FirImageCompressionAlgorithm imageCompressionAlgorithm, BdifStandard standard, NUInt flags)
	{
		HFIRecord handle;
		NCheck(FIRecordCreate(imageAcquisitionLevel, scaleUnits,
			horzScanResolution, vertScanResolution, horzImageResolution, vertImageResolution,
			pixelDepth, imageCompressionAlgorithm, flags, standard, &handle));
		return handle;
	}

	static HFIRecord Create(const ::Neurotec::IO::NBuffer & buffer, BdifStandard standard, NUInt flags, NSizeType * pSize)
	{
		HFIRecord handle;
		NCheck(FIRecordCreateFromMemoryN(buffer.GetHandle(), flags, standard, pSize, &handle));
		return handle;
	}

	static HFIRecord Create(const void * pBuffer, NSizeType bufferSize, BdifStandard standard, NUInt flags, NSizeType * pSize)
	{
		HFIRecord handle;
		NCheck(FIRecordCreateFromMemory(pBuffer, bufferSize, flags, standard, pSize, &handle));
		return handle;
	}

	static HFIRecord Create(const FIRecord & srcRecord, BdifStandard standard, NUInt flags)
	{
		HFIRecord handle;
		NCheck(FIRecordCreateFromFIRecord(srcRecord.GetHandle(), flags, standard, &handle));
		return handle;
	}

	static HFIRecord Create(const ::Neurotec::Images::NImage & image, NUShort imageAcquisitionLevel, BdifScaleUnits scaleUnits, NUShort horzScanResolution, NUShort vertScanResolution,
		NByte pixelDepth, FirImageCompressionAlgorithm imageCompressionAlgorithm, BdifFPPosition fingerPosition, BdifStandard standard, NUInt flags)
	{
		HFIRecord handle;
		NCheck(FIRecordCreateFromNImage(image.GetHandle(), imageAcquisitionLevel, scaleUnits, horzScanResolution, vertScanResolution,
			pixelDepth, imageCompressionAlgorithm, fingerPosition, flags, standard, &handle));
		return handle;
	}

public:
	static NType FirImageCompressionAlgorithmNativeTypeOf()
	{
		return NObject::GetObject<NType>(N_TYPE_OF(FirImageCompressionAlgorithm), true);
	}

	static NSizeType CalculateSize(BdifStandard standard, const NSizeType * arFingerViewSizes, NInt fingerViewCount)
	{
		NSizeType size;
		NCheck(FIRecordCalculateSize(standard, fingerViewCount, arFingerViewSizes, &size));
		return size;
	}

	static NSizeType CalculateSizeWithFingers(BdifStandard standard, const NSizeType * arFingerSizes, NInt fingerCount)
	{
		NSizeType size;
		NCheck(FIRecordCalculateSize(standard, fingerCount, arFingerSizes, &size));
		return size;
	}

	FIRecord(NUShort imageAcquisitionLevel, BdifScaleUnits scaleUnits,
		NUShort horzScanResolution, NUShort vertScanResolution, NUShort horzImageResolution, NUShort vertImageResolution,
		NByte pixelDepth, FirImageCompressionAlgorithm imageCompressionAlgorithm, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(imageAcquisitionLevel, scaleUnits, horzScanResolution, vertScanResolution, horzImageResolution, vertImageResolution,
			pixelDepth, imageCompressionAlgorithm, standard, flags), true)
	{
	}

	FIRecord(const ::Neurotec::IO::NBuffer & buffer, BdifStandard standard, NUInt flags = 0, NSizeType * pSize = NULL)
		: NObject(Create(buffer, standard, flags, pSize), true)
	{
	}

	FIRecord(const void * pBuffer, NSizeType bufferSize, BdifStandard standard, NUInt flags = 0, NSizeType * pSize = NULL)
		: NObject(Create(pBuffer, bufferSize, standard, flags, pSize), true)
	{
	}

	FIRecord(const FIRecord & srcRecord, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(srcRecord, standard, flags), true)
	{
	}

	FIRecord(const ::Neurotec::Images::NImage & image, NUShort imageAcquisitionLevel, BdifScaleUnits scaleUnits, NUShort horzScanResolution, NUShort vertScanResolution,
		NByte pixelDepth, FirImageCompressionAlgorithm imageCompressionAlgorithm, BdifFPPosition fingerPosition, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(image, imageAcquisitionLevel, scaleUnits, horzScanResolution, vertScanResolution, pixelDepth, imageCompressionAlgorithm,
			fingerPosition, standard, flags), true)
	{
	}

	BdifStandard GetStandard() const
	{
		BdifStandard value;
		NCheck(FIRecordGetStandard(GetHandle(), &value));
		return value;
	}

	NUInt GetCbeffProductId() const
	{
		NUInt value;
		NCheck(FIRecordGetCbeffProductId(GetHandle(), &value));
		return value;
	}

	void SetCbeffProductId(NUInt value)
	{
		NCheck(FIRecordSetCbeffProductId(GetHandle(), value));
	}

	NUShort GetCaptureDeviceId() const
	{
		NUShort value;
		NCheck(FIRecordGetCaptureDeviceId(GetHandle(), &value));
		return value;
	}

	void SetCaptureDeviceId(NUShort value)
	{
		NCheck(FIRecordSetCaptureDeviceId(GetHandle(), value));
	}

	NUShort GetImageAcquisitionLevel() const
	{
		NUShort value;
		NCheck(FIRecordGetImageAcquisitionLevel(GetHandle(), &value));
		return value;
	}

	BdifScaleUnits GetScaleUnits() const
	{
		BdifScaleUnits value;
		NCheck(FIRecordGetScaleUnits(GetHandle(), &value));
		return value;
	}

	NUShort GetHorzScanResolution() const
	{
		NUShort value;
		NCheck(FIRecordGetHorzScanResolution(GetHandle(), &value));
		return value;
	}

	NUShort GetVertScanResolution() const
	{
		NUShort value;
		NCheck(FIRecordGetVertScanResolution(GetHandle(), &value));
		return value;
	}

	NUShort GetHorzImageResolution() const
	{
		NUShort value;
		NCheck(FIRecordGetHorzImageResolution(GetHandle(), &value));
		return value;
	}

	NUShort GetVertImageResolution() const
	{
		NUShort value;
		NCheck(FIRecordGetVertImageResolution(GetHandle(), &value));
		return value;
	}

	NByte GetPixelDepth() const
	{
		NByte value;
		NCheck(FIRecordGetPixelDepth(GetHandle(), &value));
		return value;
	}

	FirImageCompressionAlgorithm GetImageCompressionAlgorithm() const
	{
		FirImageCompressionAlgorithm value;
		NCheck(FIRecordGetImageCompressionAlgorithm(GetHandle(), &value));
		return value;
	}

	FingerCollection GetFingers()
	{
		return FingerCollection(*this);
	}

	const FingerCollection GetFingers() const
	{
		return FingerCollection(*this);
	}

	FingerViewCollection GetFingerViews()
	{
		return FingerViewCollection(*this);
	}

	const FingerViewCollection GetFingerViews() const
	{
		return FingerViewCollection(*this);
	}
};

}}}

#endif // !FI_RECORD_HPP_INCLUDED
