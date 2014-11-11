#ifndef FMR_FINGER_VIEW_HPP_INCLUDED
#define FMR_FINGER_VIEW_HPP_INCLUDED

#include <Biometrics/Standards/BdifTypes.hpp>
#include <Biometrics/NFRecord.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
#include <Biometrics/Standards/FmrFingerView.h>
}}}

namespace Neurotec { namespace Biometrics { namespace Standards
{

#undef FMRFV_MAX_DIMENSION
#undef FMRFV_MAX_MINUTIA_COUNT
#undef FMRFV_MAX_CORE_COUNT
#undef FMRFV_MAX_DELTA_COUNT

#undef FMRFV_SKIP_RIDGE_COUNTS
#undef FMRFV_SKIP_SINGULAR_POINTS
#undef FMRFV_PROCESS_ALL_EXTENDED_DATA
#undef FMRFV_OLD_CONVERT
#undef FMRFV_SKIP_NEUROTEC_FIELDS
#undef FMRFV_USE_NEUROTEC_FIELDS

const NUShort FMRFV_MAX_DIMENSION = 16383;
const NInt FMRFV_MAX_MINUTIA_COUNT = 255;
const NInt FMRFV_MAX_CORE_COUNT = 15;
const NInt FMRFV_MAX_DELTA_COUNT = 15;

const NUInt FMRFV_SKIP_RIDGE_COUNTS = NFR_SKIP_RIDGE_COUNTS;
const NUInt FMRFV_SKIP_SINGULAR_POINTS = NFR_SKIP_SINGULAR_POINTS;
const NUInt FMRFV_PROCESS_ALL_EXTENDED_DATA = 0x01000000;
const NUInt FMRFV_OLD_CONVERT = 0x20000000;
const NUInt FMRFV_SKIP_NEUROTEC_FIELDS = 0x40000000;
const NUInt FMRFV_USE_NEUROTEC_FIELDS = 0x80000000;

class FmrMinutia : public FmrMinutia_
{
	N_DECLARE_STRUCT_CLASS(FmrMinutia)

public:
	FmrMinutia(NUShort x, NUShort y, BdifFPMinutiaType type, NByte angle, NByte quality = 0)
	{
		X = x;
		Y = y;
		Type = type;
		Angle = angle;
		Quality = quality;
	}

	NString ToString(const NStringWrapper & format = NString()) const
	{
		HNString hValue;
		NCheck(FmrMinutiaToStringN(this, format.GetHandle(), &hValue));
		return NString(hValue, true);
	}
};

class FmrCore : public FmrCore_
{
	N_DECLARE_STRUCT_CLASS(FmrCore)

public:
	FmrCore(NUShort x, NUShort y, NInt angle = -1)
	{
		X = x;
		Y = y;
		Angle = angle;
	}

	NString ToString(const NStringWrapper & format = NString()) const
	{
		HNString hValue;
		NCheck(FmrCoreToStringN(this, format.GetHandle(), &hValue));
		return NString(hValue, true);
	}
};

class FmrDelta : public FmrDelta_
{
	N_DECLARE_STRUCT_CLASS(FmrDelta)

public:
	FmrDelta(NUShort x, NUShort y, NInt angle1 = -1, NInt angle2 = -1, NInt angle3 = -1)
	{
		X = x;
		Y = y;
		Angle1 = angle1;
		Angle2 = angle2;
		Angle3 = angle3;
	}

	NString ToString(const NStringWrapper & format = NString()) const
	{
		HNString hValue;
		NCheck(FmrDeltaToStringN(this, format.GetHandle(), &hValue));
		return NString(hValue, true);
	}
};

}}}

N_DEFINE_STRUCT_TYPE_TRAITS(Neurotec::Biometrics::Standards, FmrMinutia)
N_DEFINE_STRUCT_TYPE_TRAITS(Neurotec::Biometrics::Standards, FmrCore)
N_DEFINE_STRUCT_TYPE_TRAITS(Neurotec::Biometrics::Standards, FmrDelta)

namespace Neurotec { namespace Biometrics { namespace Standards
{

class FmrFinger;

class FmrFingerView : public NObject
{
	N_DECLARE_OBJECT_CLASS(FmrFingerView, NObject)

public:
	class MinutiaCollection : public ::Neurotec::Collections::NCollectionBase<FmrMinutia, FmrFingerView,
		FmrFingerViewGetMinutiaCount, FmrFingerViewGetMinutia>
	{
		MinutiaCollection(const FmrFingerView & owner)
		{
			SetOwner(owner);
		}

		friend class FmrFingerView;
	protected:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(FmrFingerViewGetMinutiaCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(FmrFingerViewSetMinutiaCapacity(this->GetOwnerHandle(), value));
		}

		NInt GetAll(FmrMinutia * arValues, NInt valuesLength) const
		{
			NInt count;
			NCheck(count = FmrFingerViewGetMinutiaeEx(this->GetOwnerHandle(), arValues, valuesLength));
			return count;
		}

		void Set(NInt index, const FmrMinutia & value)
		{
			NCheck(FmrFingerViewSetMinutia(this->GetOwnerHandle(), index, &value));
		}

		NInt Add(const FmrMinutia & value)
		{
			NInt index = this->GetCount();
			NCheck(FmrFingerViewAddMinutia(this->GetOwnerHandle(), &value));
			return index;
		}

		void Insert(NInt index, const FmrMinutia & value)
		{
			NCheck(FmrFingerViewInsertMinutia(this->GetOwnerHandle(), index, &value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(FmrFingerViewRemoveMinutia(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FmrFingerViewClearMinutiae(this->GetOwnerHandle()));
		}
	};

	class MinutiaFourNeighborsCollection : public ::Neurotec::NObjectPartBase<FmrFingerView>
	{
		MinutiaFourNeighborsCollection(const FmrFingerView & owner)
		{
			SetOwner(owner);
		}

		friend class FmrFingerView;
	protected:
		NInt GetCount() const
		{
			return 4;
		}

		void Get(NInt baseIndex, NInt index, BdifFPMinutiaNeighbor * pValue) const
		{
			NCheck(FmrFingerViewGetMinutiaFourNeighbor(this->GetOwnerHandle(), baseIndex, index, pValue));
		}

		BdifFPMinutiaNeighbor Get(NInt baseIndex, NInt index) const
		{
			BdifFPMinutiaNeighbor value;
			NCheck(FmrFingerViewGetMinutiaFourNeighbor(this->GetOwnerHandle(), baseIndex, index, &value));
			return value;
		}

		NInt GetAll(NInt baseIndex, BdifFPMinutiaNeighbor * arValues, NInt valuesLength) const
		{
			NInt count;
			NCheck(count = FmrFingerViewGetMinutiaFourNeighborsEx(this->GetOwnerHandle(), baseIndex, arValues, valuesLength));
			return count;
		}

		void Set(NInt baseIndex, NInt index, const BdifFPMinutiaNeighbor & value)
		{
			NCheck(FmrFingerViewSetMinutiaFourNeighbor(this->GetOwnerHandle(), baseIndex, index, &value));
		}
	};

	class MinutiaEightNeighborsCollection : public ::Neurotec::NObjectPartBase<FmrFingerView>
	{
		MinutiaEightNeighborsCollection(const FmrFingerView & owner)
		{
			SetOwner(owner);
		}

		friend class FmrFingerView;
	protected:
		NInt GetCount() const
		{
			return 8;
		}

		void Get(NInt baseIndex, NInt index, BdifFPMinutiaNeighbor * pValue) const
		{
			NCheck(FmrFingerViewGetMinutiaEightNeighbor(this->GetOwnerHandle(), baseIndex, index, pValue));
		}

		BdifFPMinutiaNeighbor Get(NInt baseIndex, NInt index) const
		{
			BdifFPMinutiaNeighbor value;
			NCheck(FmrFingerViewGetMinutiaEightNeighbor(this->GetOwnerHandle(), baseIndex, index, &value));
			return value;
		}

		NInt GetAll(NInt baseIndex, BdifFPMinutiaNeighbor * arValues, NInt valuesLength) const
		{
			NInt count;
			NCheck(count = FmrFingerViewGetMinutiaEightNeighborsEx(this->GetOwnerHandle(), baseIndex, arValues, valuesLength));
			return count;
		}

		void Set(NInt baseIndex, NInt index, const BdifFPMinutiaNeighbor & value)
		{
			NCheck(FmrFingerViewSetMinutiaEightNeighbor(this->GetOwnerHandle(), baseIndex, index, &value));
		}
	};

	class CoreCollection : public ::Neurotec::Collections::NCollectionBase<FmrCore, FmrFingerView,
		FmrFingerViewGetCoreCount, FmrFingerViewGetCore>
	{
		CoreCollection(const FmrFingerView & owner)
		{
			SetOwner(owner);
		}

		friend class FmrFingerView;
	protected:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(FmrFingerViewGetCoreCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(FmrFingerViewSetCoreCapacity(this->GetOwnerHandle(), value));
		}

		NInt GetAll(FmrCore * arValues, NInt valuesLength) const
		{
			NInt count;
			NCheck(count = FmrFingerViewGetCoresEx(this->GetOwnerHandle(), arValues, valuesLength));
			return count;
		}

		void Set(NInt index, const FmrCore & value)
		{
			NCheck(FmrFingerViewSetCore(this->GetOwnerHandle(), index, &value));
		}

		NInt Add(const FmrCore & value)
		{
			NInt index = this->GetCount();
			NCheck(FmrFingerViewAddCore(this->GetOwnerHandle(), &value));
			return index;
		}

		void Insert(NInt index, const FmrCore & value)
		{
			NCheck(FmrFingerViewInsertCore(this->GetOwnerHandle(), index, &value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(FmrFingerViewRemoveCore(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FmrFingerViewClearCores(this->GetOwnerHandle()));
		}
	};

	class DeltaCollection : public ::Neurotec::Collections::NCollectionBase<FmrDelta, FmrFingerView,
		FmrFingerViewGetDeltaCount, FmrFingerViewGetDelta>
	{
		DeltaCollection(const FmrFingerView & owner)
		{
			SetOwner(owner);
		}

		friend class FmrFingerView;
	protected:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(FmrFingerViewGetDeltaCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(FmrFingerViewSetDeltaCapacity(this->GetOwnerHandle(), value));
		}

		NInt GetAll(FmrDelta * arValues, NInt valuesLength) const
		{
			NInt count;
			NCheck(count = FmrFingerViewGetDeltasEx(this->GetOwnerHandle(), arValues, valuesLength));
			return count;
		}

		void Set(NInt index, const FmrDelta & value)
		{
			NCheck(FmrFingerViewSetDelta(this->GetOwnerHandle(), index, &value));
		}

		NInt Add(const FmrDelta & value)
		{
			NInt index = this->GetCount();
			NCheck(FmrFingerViewAddDelta(this->GetOwnerHandle(), &value));
			return index;
		}

		void Insert(NInt index, const FmrDelta & value)
		{
			NCheck(FmrFingerViewInsertDelta(this->GetOwnerHandle(), index, &value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(FmrFingerViewRemoveDelta(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FmrFingerViewClearDeltas(this->GetOwnerHandle()));
		}
	};

public:
	static NSizeType GetMaxSize(BdifStandard standard, NInt minutiaCount, NBool hasFourNeighborRidgeCounts, NBool hasEightNeighborRidgeCounts,
		NInt coreWithAngleCount, NInt coreCount, NInt deltaWithAngleCount, NInt deltaCount)
	{
		NSizeType size;
		NCheck(FmrFingerViewGetMaxSize(standard, minutiaCount, hasFourNeighborRidgeCounts, hasEightNeighborRidgeCounts,
			coreWithAngleCount, coreCount, deltaWithAngleCount, deltaCount, &size));
		return size;
	}

	static NSizeType GetMaxSize(BdifStandard standard, NInt minutiaCount, NBool hasFourNeighborRidgeCounts, NBool hasEightNeighborRidgeCounts,
		NInt coreWithAngleCount, NInt coreCount, NInt deltaWithAngleCount, NInt deltaCount,
		NBool hasNeurotecCurvatures, NBool hasNeurotecGs, NInt neurotecBOWidth, NInt neurotecBOHeight)
	{
		NSizeType size;
		NCheck(FmrFingerViewGetMaxSizeEx(standard, minutiaCount, hasFourNeighborRidgeCounts, hasEightNeighborRidgeCounts,
			coreWithAngleCount, coreCount, deltaWithAngleCount, deltaCount, hasNeurotecCurvatures, hasNeurotecGs, neurotecBOWidth, neurotecBOHeight, &size));
		return size;
	}

	NFRecord ToNFRecord(NUInt flags = 0) const
	{
		HNFRecord hNFRecord;
		NCheck(FmrFingerViewToNFRecord(GetHandle(), flags, &hNFRecord));
		return FromHandle<NFRecord>(hNFRecord);
	}

	BdifFPPosition GetFingerPosition() const
	{
		BdifFPPosition value;
		NCheck(FmrFingerViewGetFingerPosition(GetHandle(), &value));
		return value;
	}

	BdifFPImpressionType GetImpressionType() const
	{
		BdifFPImpressionType value;
		NCheck(FmrFingerViewGetImpressionType(GetHandle(), &value));
		return value;
	}

	void SetImpressionType(BdifFPImpressionType value)
	{
		NCheck(FmrFingerViewSetImpressionType(GetHandle(), value));
	}

	NInt GetViewCount() const
	{
		NInt value;
		NCheck(FmrFingerViewGetViewCount(GetHandle(), &value));
		return value;
	}

	NInt GetViewNumber() const
	{
		NInt value;
		NCheck(FmrFingerViewGetViewNumber(GetHandle(), &value));
		return value;
	}

	NByte GetFingerQuality() const
	{
		NByte value;
		NCheck(FmrFingerViewGetFingerQuality(GetHandle(), &value));
		return value;
	}

	void SetFingerQuality(NByte value)
	{
		NCheck(FmrFingerViewSetFingerQuality(GetHandle(), value));
	}

	bool GetHasFourNeighborRidgeCounts() const
	{
		NBool value;
		NCheck(FmrFingerViewHasFourNeighborRidgeCounts(GetHandle(), &value));
		return value != 0;
	}

	void SetHasFourNeighborRidgeCounts(bool value)
	{
		NCheck(FmrFingerViewSetHasFourNeighborRidgeCounts(GetHandle(), value ? NTrue : NFalse));
	}

	bool GetHasEightNeighborRidgeCounts() const
	{
		NBool value;
		NCheck(FmrFingerViewHasEightNeighborRidgeCounts(GetHandle(), &value));
		return value != 0;
	}

	void SetHasEightNeighborRidgeCounts(bool value)
	{
		NCheck(FmrFingerViewSetHasEightNeighborRidgeCounts(GetHandle(), value ? NTrue : NFalse));
	}

	MinutiaCollection GetMinutiae()
	{
		return MinutiaCollection(*this);
	}

	const MinutiaCollection GetMinutiae() const
	{
		return MinutiaCollection(*this);
	}

	MinutiaFourNeighborsCollection GetMinutiaeFourNeighbors()
	{
		return MinutiaFourNeighborsCollection(*this);
	}

	const MinutiaFourNeighborsCollection GetMinutiaeFourNeighbors() const
	{
		return MinutiaFourNeighborsCollection(*this);
	}

	MinutiaEightNeighborsCollection GetMinutiaeEightNeighbors()
	{
		return MinutiaEightNeighborsCollection(*this);
	}

	const MinutiaEightNeighborsCollection GetMinutiaeEightNeighbors() const
	{
		return MinutiaEightNeighborsCollection(*this);
	}

	CoreCollection GetCores()
	{
		return CoreCollection(*this);
	}

	const CoreCollection GetCores() const
	{
		return CoreCollection(*this);
	}

	DeltaCollection GetDeltas()
	{
		return DeltaCollection(*this);
	}

	const DeltaCollection GetDeltas() const
	{
		return DeltaCollection(*this);
	}

	FmrFinger GetOwner() const;
};

}}}

#include <Biometrics/Standards/FmrFinger.hpp>

namespace Neurotec { namespace Biometrics { namespace Standards
{

inline FmrFinger FmrFingerView::GetOwner() const
{
	return NObject::GetOwner<FmrFinger>();
}

}}}

#endif // !FMR_FINGER_VIEW_HPP_INCLUDED
