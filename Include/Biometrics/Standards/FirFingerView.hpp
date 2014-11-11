#ifndef FIR_FINGER_VIEW_HPP_INCLUDED
#define FIR_FINGER_VIEW_HPP_INCLUDED

#include <Biometrics/Standards/BdifTypes.hpp>
#include <Images/NImage.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
using ::Neurotec::Images::HNImage;
#include <Biometrics/Standards/FirFingerView.h>
}}}

namespace Neurotec { namespace Biometrics { namespace Standards
{

class FirFinger;

class FirFingerView : public NObject
{
	N_DECLARE_OBJECT_CLASS(FirFingerView, NObject)

public:
	static NSizeType CalculateSize(BdifStandard standard, NSizeType imageDataLength)
	{
		NSizeType size;
		NCheck(FirFingerViewCalculateSize(standard, imageDataLength, &size));
		return size;
	}

	::Neurotec::Images::NImage ToNImage(NUInt flags = 0) const
	{
		HNImage hImage;
		NCheck(FirFingerViewToNImage(GetHandle(), flags, &hImage));
		return FromHandle< ::Neurotec::Images::NImage>(hImage);
	}

	BdifFPPosition GetPosition() const
	{
		BdifFPPosition value;
		NCheck(FirFingerViewGetPosition(GetHandle(), &value));
		return value;
	}

	NInt GetViewCount() const
	{
		NInt value;
		NCheck(FirFingerViewGetViewCount(GetHandle(), &value));
		return value;
	}

	NInt GetViewNumber() const
	{
		NInt value;
		NCheck(FirFingerViewGetViewNumber(GetHandle(), &value));
		return value;
	}

	NByte GetImageQuality() const
	{
		NByte value;
		NCheck(FirFingerViewGetImageQuality(GetHandle(), &value));
		return value;
	}

	void SetImageQuality(NByte value)
	{
		NCheck(FirFingerViewSetImageQuality(GetHandle(), value));
	}

	BdifFPImpressionType GetImpressionType() const
	{
		BdifFPImpressionType value;
		NCheck(FirFingerViewGetImpressionType(GetHandle(), &value));
		return value;
	}

	void SetImpressionType(BdifFPImpressionType value)
	{
		NCheck(FirFingerViewSetImpressionType(GetHandle(), value));
	}

	NUShort GetHorzLineLength() const
	{
		NUShort value;
		NCheck(FirFingerViewGetHorzLineLength(GetHandle(), &value));
		return value;
	}

	NUShort GetVertLineLength() const
	{
		NUShort value;
		NCheck(FirFingerViewGetVertLineLength(GetHandle(), &value));
		return value;
	}

	::Neurotec::IO::NBuffer GetImageData() const
	{
		return GetObject<HandleType, ::Neurotec::IO::NBuffer>(FirFingerViewGetImageDataN, true);
	}

	void SetImageData(const ::Neurotec::IO::NBuffer & value)
	{
		SetObject(FirFingerViewSetImageDataN, value);
	}

	void SetImageData(const void * pValue, NSizeType valueSize, bool copy = true)
	{
		NCheck(FirFingerViewSetImageDataEx(GetHandle(), pValue, valueSize, copy ? NTrue : NFalse));
	}

	FirFinger GetOwner() const;
};

}}}

#include <Biometrics/Standards/FirFinger.hpp>

namespace Neurotec { namespace Biometrics { namespace Standards
{

inline FirFinger FirFingerView::GetOwner() const
{
	return NObject::GetOwner<FirFinger>();
}

}}}

#endif // !FIR_FINGER_VIEW_HPP_INCLUDED
