#include <Biometrics/Standards/FmrFinger.hpp>

#ifndef FM_RECORD_HPP_INCLUDED
#define FM_RECORD_HPP_INCLUDED

#include <Biometrics/Standards/BdifTypes.hpp>
#include <Biometrics/NTemplate.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
#include <Biometrics/Standards/FMRecord.h>
}}}

N_DEFINE_ENUM_TYPE_TRAITS(Neurotec::Biometrics::Standards, FmrCaptureEquipmentCompliance)

namespace Neurotec { namespace Biometrics { namespace Standards
{

#undef FMR_MAX_FINGER_COUNT
#undef FMR_MAX_FINGER_VIEW_COUNT

#undef FMR_PROCESS_FIRST_FINGER_ONLY
#undef FMR_PROCESS_FIRST_FINGER_VIEW_ONLY

const NInt FMR_MAX_FINGER_VIEW_COUNT = 255;
const NInt FMR_MAX_FINGER_COUNT = FMR_MAX_FINGER_VIEW_COUNT;

const NUInt FMR_PROCESS_FIRST_FINGER_ONLY = 0x00000100;
const NUInt FMR_PROCESS_FIRST_FINGER_VIEW_ONLY = (FMR_PROCESS_FIRST_FINGER_ONLY | FMRF_PROCESS_FIRST_FINGER_VIEW_ONLY);

class FMRecord : public NObject
{
	N_DECLARE_OBJECT_CLASS(FMRecord, NObject)

public:
	class FingerCollection : public ::Neurotec::Collections::NCollectionBase<FmrFinger, FMRecord,
		FMRecordGetFingerCount, FMRecordGetFingerEx>
	{
		FingerCollection(const FMRecord & owner)
		{
			SetOwner(owner);
		}

		friend class FMRecord;
	public:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(FMRecordGetFingerCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(FMRecordSetFingerCapacity(this->GetOwnerHandle(), value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(FMRecordRemoveFinger(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FMRecordClearFingers(this->GetOwnerHandle()));
		}

		FmrFinger Add(BdifFPPosition fingerPosition, NUInt flags = 0)
		{
			HFmrFinger hFinger;
			NCheck(FMRecordAddFinger(this->GetOwnerHandle(), fingerPosition, flags | N_OBJECT_REF_RET, &hFinger));
			return FromHandle<FmrFinger>(hFinger, true);
		}
	};

	class FingerViewCollection : public ::Neurotec::Collections::NCollectionBase<FmrFingerView, FMRecord,
		FMRecordGetFingerViewCount, FMRecordGetFingerViewEx>
	{
		FingerViewCollection(const FMRecord & owner)
		{
			SetOwner(owner);
		}

		friend class FMRecord;
	public:
		void RemoveAt(NInt index)
		{
			NCheck(FMRecordRemoveFingerView(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FMRecordClearFingerViews(this->GetOwnerHandle()));
		}
	};

private:
	static HFMRecord Create(NUShort sizeX, NUShort sizeY, NUShort resolutionX, NUShort resolutionY, BdifStandard standard, NUInt flags)
	{
		HFMRecord handle;
		NCheck(FMRecordCreate(sizeX, sizeY, resolutionX, resolutionY, flags, standard, &handle));
		return handle;
	}

	static HFMRecord Create(const ::Neurotec::IO::NBuffer & buffer, BdifStandard standard, NUInt flags, NSizeType * pSize)
	{
		HFMRecord handle;
		NCheck(FMRecordCreateFromMemoryN(buffer.GetHandle(), flags, standard, pSize, &handle));
		return handle;
	}

	static HFMRecord Create(const void * pBuffer, NSizeType bufferSize, BdifStandard standard, NUInt flags, NSizeType * pSize)
	{
		HFMRecord handle;
		NCheck(FMRecordCreateFromMemory(pBuffer, bufferSize, flags, standard, pSize, &handle));
		return handle;
	}

	static HFMRecord Create(const FMRecord & srcRecord, BdifStandard standard, NUInt flags)
	{
		HFMRecord handle;
		NCheck(FMRecordCreateFromFMRecord(srcRecord.GetHandle(), flags, standard, &handle));
		return handle;
	}

	static HFMRecord Create(const NFRecord & nfRecord, BdifStandard standard, NUInt flags)
	{
		HFMRecord handle;
		NCheck(FMRecordCreateFromNFRecord(nfRecord.GetHandle(), flags, standard, &handle));
		return handle;
	}

	static HFMRecord Create(const NFTemplate & nfTemplate, BdifStandard standard, NUInt flags)
	{
		HFMRecord handle;
		NCheck(FMRecordCreateFromNFTemplate(nfTemplate.GetHandle(), flags, standard, &handle));
		return handle;
	}

public:
	static NType FmrCaptureEquipmentComplianceNativeTypeOf()
	{
		return NObject::GetObject<NType>(N_TYPE_OF(FmrCaptureEquipmentCompliance), true);
	}

	static NSizeType CalculateSize(BdifStandard standard, const NSizeType * arFingerViewSizes, NInt fingerViewCount)
	{
		NSizeType size;
		NCheck(FMRecordCalculateSize(standard, fingerViewCount, arFingerViewSizes, &size));
		return size;
	}

	static NSizeType CalculateSizeWithFingers(BdifStandard standard, const NSizeType * arFingerSizes, NInt fingerCount)
	{
		NSizeType size;
		NCheck(FMRecordCalculateSize(standard, fingerCount, arFingerSizes, &size));
		return size;
	}

	FMRecord(NUShort sizeX, NUShort sizeY, NUShort resolutionX, NUShort resolutionY, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(sizeX, sizeY, resolutionX, resolutionY, standard, flags), true)
	{
	}

	FMRecord(const ::Neurotec::IO::NBuffer & buffer, BdifStandard standard, NUInt flags = 0, NSizeType * pSize = NULL)
		: NObject(Create(buffer, standard, flags, pSize), true)
	{
	}

	FMRecord(const void * pBuffer, NSizeType bufferSize, BdifStandard standard, NUInt flags = 0, NSizeType * pSize = NULL)
		: NObject(Create(pBuffer, bufferSize, standard, flags, pSize), true)
	{
	}

	FMRecord(const FMRecord & srcRecord, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(srcRecord, standard, flags), true)
	{
	}

	FMRecord(const NFRecord & nfRecord, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(nfRecord, standard, flags), true)
	{
	}

	FMRecord(const NFTemplate & nfTemplate, BdifStandard standard, NUInt flags = 0)
		: NObject(Create(nfTemplate, standard, flags), true)
	{
	}

	NFTemplate ToNFTemplate(NUInt flags = 0) const
	{
		HNFTemplate hNFTemplate;
		NCheck(FMRecordToNFTemplate(GetHandle(), flags, &hNFTemplate));
		return FromHandle<NFTemplate>(hNFTemplate);
	}

	NTemplate ToNTemplate(NUInt flags = 0) const
	{
		HNTemplate hNTemplate;
		NCheck(FMRecordToNTemplate(GetHandle(), flags, &hNTemplate));
		return FromHandle<NTemplate>(hNTemplate);
	}

	BdifStandard GetStandard() const
	{
		BdifStandard value;
		NCheck(FMRecordGetStandard(GetHandle(), &value));
		return value;
	}

	NUInt GetCbeffProductId() const
	{
		NUInt value;
		NCheck(FMRecordGetCbeffProductId(GetHandle(), &value));
		return value;
	}

	void SetCbeffProductId(NUInt value)
	{
		NCheck(FMRecordSetCbeffProductId(GetHandle(), value));
	}

	FmrCaptureEquipmentCompliance GetCaptureEquipmentCompliance() const
	{
		FmrCaptureEquipmentCompliance value;
		NCheck(FMRecordGetCaptureEquipmentCompliance(GetHandle(), &value));
		return value;
	}

	void SetCaptureEquipmentCompliance(FmrCaptureEquipmentCompliance value)
	{
		NCheck(FMRecordSetCaptureEquipmentCompliance(GetHandle(), value));
	}

	NUShort GetCaptureEquipmentId() const
	{
		NUShort value;
		NCheck(FMRecordGetCaptureEquipmentId(GetHandle(), &value));
		return value;
	}

	void SetCaptureEquipmentId(NUShort value)
	{
		NCheck(FMRecordSetCaptureEquipmentId(GetHandle(), value));
	}

	NUShort GetSizeX() const
	{
		NUShort value;
		NCheck(FMRecordGetSizeX(GetHandle(), &value));
		return value;
	}

	NUShort GetSizeY() const
	{
		NUShort value;
		NCheck(FMRecordGetSizeY(GetHandle(), &value));
		return value;
	}

	NUShort GetResolutionX() const
	{
		NUShort value;
		NCheck(FMRecordGetResolutionX(GetHandle(), &value));
		return value;
	}

	NUShort GetResolutionY() const
	{
		NUShort value;
		NCheck(FMRecordGetResolutionY(GetHandle(), &value));
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

#endif // !FM_RECORD_HPP_INCLUDED
