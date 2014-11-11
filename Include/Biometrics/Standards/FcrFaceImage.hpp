#ifndef FCR_FACE_IMAGE_HPP_INCLUDED
#define FCR_FACE_IMAGE_HPP_INCLUDED

#include <Images/NImage.hpp>
#include <Biometrics/Standards/BdifTypes.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
using ::Neurotec::Images::HNImage;
#include <Biometrics/Standards/FcrFaceImage.h>
}}}

N_DEFINE_ENUM_TYPE_TRAITS(Neurotec::Biometrics::Standards, FcrFaceImageType)
N_DEFINE_ENUM_TYPE_TRAITS(Neurotec::Biometrics::Standards, FcrImageDataType)
N_DEFINE_ENUM_TYPE_TRAITS(Neurotec::Biometrics::Standards, FcrImageColorSpace)

namespace Neurotec { namespace Biometrics { namespace Standards
{

#undef FCRFI_MAX_FEATURE_POINT_COUNT

#undef FCRFI_SKIP_FEATURE_POINTS

const NInt FCRFI_MAX_FEATURE_POINT_COUNT = N_USHORT_MAX;

const NUInt FCRFI_SKIP_FEATURE_POINTS = 0x00010000;

class FCRecord;

class FcrFaceImage : public NObject
{
	N_DECLARE_OBJECT_CLASS(FcrFaceImage, NObject)

public:
	class FeaturePointCollection : public ::Neurotec::Collections::NCollectionBase<BdifFaceFeaturePoint, FcrFaceImage,
		FcrFaceImageGetFeaturePointCount, FcrFaceImageGetFeaturePoint>
	{
		FeaturePointCollection(const FcrFaceImage & owner)
		{
			SetOwner(owner);
		}

		friend class FcrFaceImage;
	public:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(FcrFaceImageGetFeaturePointCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(FcrFaceImageSetFeaturePointCapacity(this->GetOwnerHandle(), value));
		}

		NInt GetAll(BdifFaceFeaturePoint * arValues, NInt valuesLength) const
		{
			NInt count;
			NCheck(count = FcrFaceImageGetFeaturePointsEx(this->GetOwnerHandle(), arValues, valuesLength));
			return count;
		}

		void Set(NInt index, const BdifFaceFeaturePoint & value)
		{
			NCheck(FcrFaceImageSetFeaturePoint(this->GetOwnerHandle(), index, &value));
		}

		NInt Add(const BdifFaceFeaturePoint & value)
		{
			NInt index = this->GetCount();
			NCheck(FcrFaceImageAddFeaturePoint(this->GetOwnerHandle(), &value));
			return index;
		}

		void Insert(NInt index, const BdifFaceFeaturePoint & value)
		{
			NCheck(FcrFaceImageInsertFeaturePoint(this->GetOwnerHandle(), index, &value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(FcrFaceImageRemoveFeaturePoint(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(FcrFaceImageClearFeaturePoints(this->GetOwnerHandle()));
		}
	};

public:
	static NType FcrImageDataTypeNativeTypeOf()
	{
		return NObject::GetObject<NType>(N_TYPE_OF(FcrImageDataType), true);
	}

	static NType FcrFaceImageTypeNativeTypeOf()
	{
		return NObject::GetObject<NType>(N_TYPE_OF(FcrFaceImageType), true);
	}

	static NType FcrImageColorSpaceNativeTypeOf()
	{
		return NObject::GetObject<NType>(N_TYPE_OF(FcrImageColorSpace), true);
	}

	static NSizeType CalculateSize(BdifStandard standard, NInt featurePointCount, NSizeType imageDataLength)
	{
		NSizeType size;
		NCheck(FcrFaceImageCalculateSize(standard, featurePointCount, imageDataLength, &size));
		return size;
	}

	::Neurotec::Images::NImage ToNImage(NUInt flags = 0) const
	{
		HNImage hImage;
		NCheck(FcrFaceImageToNImage(GetHandle(), flags, &hImage));
		return FromHandle< ::Neurotec::Images::NImage>(hImage);
	}

	BdifGender GetGender() const
	{
		BdifGender value;
		NCheck(FcrFaceImageGetGender(GetHandle(), &value));
		return value;
	}

	void SetGender(BdifGender value)
	{
		NCheck(FcrFaceImageSetGender(GetHandle(), value));
	}

	BdifEyeColor GetEyeColor() const
	{
		BdifEyeColor value;
		NCheck(FcrFaceImageGetEyeColor(GetHandle(), &value));
		return value;
	}

	void SetEyeColor(BdifEyeColor value)
	{
		NCheck(FcrFaceImageSetEyeColor(GetHandle(), value));
	}

	BdifHairColor GetHairColor() const
	{
		BdifHairColor value;
		NCheck(FcrFaceImageGetHairColor(GetHandle(), &value));
		return value;
	}

	void SetHairColor(BdifHairColor value)
	{
		NCheck(FcrFaceImageSetHairColor(GetHandle(), value));
	}

	BdifFaceProperties GetProperties() const
	{
		BdifFaceProperties value;
		NCheck(FcrFaceImageGetProperties(GetHandle(), &value));
		return value;
	}

	void SetProperties(BdifFaceProperties value)
	{
		NCheck(FcrFaceImageSetProperties(GetHandle(), value));
	}

	BdifFaceExpression GetExpression() const
	{
		BdifFaceExpression value;
		NCheck(FcrFaceImageGetExpression(GetHandle(), &value));
		return value;
	}

	NUShort GetVendorExpression() const
	{
		NUShort value;
		NCheck(FcrFaceImageGetVendorExpression(GetHandle(), &value));
		return value;
	}

	void SetExpression(BdifFaceExpression value, NUShort vendorValue)
	{
		NCheck(FcrFaceImageSetExpression(GetHandle(), value, vendorValue));
	}

	NByte GetPoseAngleYaw() const
	{
		NByte value;
		NCheck(FcrFaceImageGetPoseAngleYaw(GetHandle(), &value));
		return value;
	}

	void SetPoseAngleYaw(NByte value)
	{
		NCheck(FcrFaceImageSetPoseAngleYaw(GetHandle(), value));
	}

	NByte GetPoseAnglePitch() const
	{
		NByte value;
		NCheck(FcrFaceImageGetPoseAnglePitch(GetHandle(), &value));
		return value;
	}

	void SetPoseAnglePitch(NByte value)
	{
		NCheck(FcrFaceImageSetPoseAnglePitch(GetHandle(), value));
	}

	NByte GetPoseAngleRoll() const
	{
		NByte value;
		NCheck(FcrFaceImageGetPoseAngleRoll(GetHandle(), &value));
		return value;
	}

	void SetPoseAngleRoll(NByte value)
	{
		NCheck(FcrFaceImageSetPoseAngleRoll(GetHandle(), value));
	}

	void GetPoseAngle(NByte * pYaw, NByte * pPitch, NByte * pRoll) const
	{
		NCheck(FcrFaceImageGetPoseAngle(GetHandle(), pYaw, pPitch, pRoll));
	}

	void SetPoseAngle(NByte yaw, NByte pitch, NByte roll)
	{
		NCheck(FcrFaceImageSetPoseAngle(GetHandle(), yaw, pitch, roll));
	}

	NByte GetPoseAngleUncertaintyYaw() const
	{
		NByte value;
		NCheck(FcrFaceImageGetPoseAngleUncertaintyYaw(GetHandle(), &value));
		return value;
	}

	NByte GetPoseAngleUncertaintyPitch() const
	{
		NByte value;
		NCheck(FcrFaceImageGetPoseAngleUncertaintyPitch(GetHandle(), &value));
		return value;
	}

	NByte GetPoseAngleUncertaintyRoll() const
	{
		NByte value;
		NCheck(FcrFaceImageGetPoseAngleUncertaintyRoll(GetHandle(), &value));
		return value;
	}

	void GetPoseAngleUncertainty(NByte * pYaw, NByte * pPitch, NByte * pRoll) const
	{
		NCheck(FcrFaceImageGetPoseAngleUncertainty(GetHandle(), pYaw, pPitch, pRoll));
	}

	void SetPoseAngleUncertainty(NByte yaw, NByte pitch, NByte roll)
	{
		NCheck(FcrFaceImageSetPoseAngleUncertainty(GetHandle(), yaw, pitch, roll));
	}

	FcrFaceImageType GetFaceImageType() const
	{
		FcrFaceImageType value;
		NCheck(FcrFaceImageGetFaceImageType(GetHandle(), &value));
		return value;
	}

	FcrImageDataType GetImageDataType() const
	{
		FcrImageDataType value;
		NCheck(FcrFaceImageGetImageDataType(GetHandle(), &value));
		return value;
	}

	NUShort GetWidth() const
	{
		NUShort value;
		NCheck(FcrFaceImageGetWidth(GetHandle(), &value));
		return value;
	}

	NUShort GetHeight() const
	{
		NUShort value;
		NCheck(FcrFaceImageGetHeight(GetHandle(), &value));
		return value;
	}

	FcrImageColorSpace GetImageColorSpace() const
	{
		FcrImageColorSpace value;
		NCheck(FcrFaceImageGetImageColorSpace(GetHandle(), &value));
		return value;
	}

	NByte GetVendorImageColorSpace() const
	{
		NByte value;
		NCheck(FcrFaceImageGetVendorImageColorSpace(GetHandle(), &value));
		return value;
	}

	BdifImageSourceType GetSourceType() const
	{
		BdifImageSourceType value;
		NCheck(FcrFaceImageGetSourceType(GetHandle(), &value));
		return value;
	}

	NByte GetVendorSourceType() const
	{
		NByte value;
		NCheck(FcrFaceImageGetVendorSourceType(GetHandle(), &value));
		return value;
	}

	void SetSourceType(BdifImageSourceType value, NByte vendorValue)
	{
		NCheck(FcrFaceImageSetSourceType(GetHandle(), value, vendorValue));
	}

	NUShort GetDeviceType() const
	{
		NUShort value;
		NCheck(FcrFaceImageGetDeviceType(GetHandle(), &value));
		return value;
	}

	void SetDeviceType(NUShort value)
	{
		NCheck(FcrFaceImageSetDeviceType(GetHandle(), value));
	}

	NUShort GetQuality() const
	{
		NUShort value;
		NCheck(FcrFaceImageGetQuality(GetHandle(), &value));
		return value;
	}

	void SetQuality(NUShort value)
	{
		NCheck(FcrFaceImageSetQuality(GetHandle(), value));
	}

	::Neurotec::IO::NBuffer GetImageData() const
	{
		return GetObject<HandleType, ::Neurotec::IO::NBuffer>(FcrFaceImageGetImageDataN, true);
	}

	void SetImageData(const ::Neurotec::IO::NBuffer & value)
	{
		SetObject(FcrFaceImageSetImageDataN, value);
	}

	void SetImageData(const void * pValue, NSizeType valueSize, bool copy = true)
	{
		NCheck(FcrFaceImageSetImageDataEx(GetHandle(), pValue, valueSize, copy ? NTrue : NFalse));
	}

	FeaturePointCollection GetFeaturePoints()
	{
		return FeaturePointCollection(*this);
	}

	const FeaturePointCollection GetFeaturePoints() const
	{
		return FeaturePointCollection(*this);
	}

	FCRecord GetOwner() const;
};

}}}

#include <Biometrics/Standards/FCRecord.hpp>

namespace Neurotec { namespace Biometrics { namespace Standards
{

inline FCRecord FcrFaceImage::GetOwner() const
{
	return NObject::GetOwner<FCRecord>();
}

}}}

#endif // !FCR_FACE_IMAGE_HPP_INCLUDED
