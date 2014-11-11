#include <Biometrics/Standards/IirIrisImage.hpp>

#ifndef IIR_IRIS_HPP_INCLUDED
#define IIR_IRIS_HPP_INCLUDED

#include <Biometrics/Standards/BdifTypes.hpp>
#include <Images/NImage.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
using ::Neurotec::Images::HNImage;
#include <Biometrics/Standards/IirIris.h>
}}}

namespace Neurotec { namespace Biometrics { namespace Standards
{

#undef IIRI_MAX_IRIS_IMAGE_COUNT

#undef IIRI_PROCESS_FIRST_IRIS_IMAGE_ONLY

const NInt IIRI_MAX_IRIS_IMAGE_COUNT = N_USHORT_MAX;

const NUInt IIRI_PROCESS_FIRST_IRIS_IMAGE_ONLY = 0x00001000;

class IIRecord;

class IirIris : public NObject
{
	N_DECLARE_OBJECT_CLASS(IirIris, NObject)

public:
	class IrisImageCollection : public ::Neurotec::Collections::NCollectionBase<IirIrisImage, IirIris,
		IirIrisGetIrisImageCount, IirIrisGetIrisImage>
	{
		IrisImageCollection(const IirIris & owner)
		{
			SetOwner(owner);
		}

		friend class IirIris;
	public:
		NInt GetCapacity() const
		{
			NInt value;
			NCheck(IirIrisGetIrisImageCapacity(this->GetOwnerHandle(), &value));
			return value;
		}

		void SetCapacity(NInt value)
		{
			NCheck(IirIrisSetIrisImageCapacity(this->GetOwnerHandle(), value));
		}

		void RemoveAt(NInt index)
		{
			NCheck(IirIrisRemoveIrisImageAt(this->GetOwnerHandle(), index));
		}

		void Clear()
		{
			NCheck(IirIrisClearIrisImages(this->GetOwnerHandle()));
		}

		IirIrisImage Add(const ::Neurotec::IO::NBuffer & imageData, NUInt flags = 0)
		{
			HIirIrisImage hIrisImage;
			NCheck(IirIrisAddIrisImageN(this->GetOwnerHandle(), imageData.GetHandle(), flags | N_OBJECT_REF_RET, &hIrisImage));
			return FromHandle<IirIrisImage>(hIrisImage, true);
		}

		IirIrisImage Add(const void * pImageData, NSizeType imageDataLength, NUInt flags = 0)
		{
			HIirIrisImage hIrisImage;
			NCheck(IirIrisAddIrisImage(this->GetOwnerHandle(), pImageData, imageDataLength, flags | N_OBJECT_REF_RET, &hIrisImage));
			return FromHandle<IirIrisImage>(hIrisImage, true);
		}

		IirIrisImage Add(const ::Neurotec::Images::NImage & image, ::Neurotec::Biometrics::Standards::IirImageFormat imageFormat, NUInt flags = 0)
		{
			HIirIrisImage hIrisImage;
			NCheck(IirIrisAddIrisImageFromNImageEx(this->GetOwnerHandle(), image.GetHandle(), imageFormat, flags | N_OBJECT_REF_RET, &hIrisImage));
			return FromHandle<IirIrisImage>(hIrisImage, true);
		}
	};

private:
	static HIirIris Create(NUInt flags)
	{
		HIirIris handle;
		NCheck(IirIrisCreate(flags, &handle));
		return handle;
	}

public:
	IirIris(NUInt flags = 0)
		: NObject(Create(flags), true)
	{
	}
	
	BdifStandard GetStandard() const
	{
		BdifStandard value;
		NCheck(IirIrisGetStandard(GetHandle(), &value));
		return value;
	}

	void SetStandard(BdifStandard value)
	{
		NCheck(IirIrisSetStandard(GetHandle(), value));
	}

	NVersion GetVersion() const
	{
		NVersion_ value;
		NCheck(IirIrisGetVersion(GetHandle(), &value));
		return NVersion(value);
	}

	void SetVersion(const NVersion value)
	{
		NCheck(IirIrisSetVersion(GetHandle(), value.GetValue()));
	}

	BdifEyePosition GetPosition() const
	{
		BdifEyePosition value;
		NCheck(IirIrisGetPosition(GetHandle(), &value));
		return value;
	}

	void SetPosition(BdifEyePosition value)
	{
		NCheck(IirIrisSetPosition(GetHandle(), value));
	}

	IrisImageCollection GetIrisImages()
	{
		return IrisImageCollection(*this);
	}

	const IrisImageCollection GetIrisImages() const
	{
		return IrisImageCollection(*this);
	}

	IIRecord GetOwner() const;
};

}}}

#include <Biometrics/Standards/IIRecord.hpp>

namespace Neurotec { namespace Biometrics { namespace Standards
{

inline IIRecord IirIris::GetOwner() const
{
	return NObject::GetOwner<IIRecord>();
}

}}}

#endif // !IIR_IRIS_HPP_INCLUDED
