#ifndef CBEFF_QUALITY_ALGORITHM_IDENTIFIERS_HPP_INCLUDED
#define CBEFF_QUALITY_ALGORITHM_IDENTIFIERS_HPP_INCLUDED

#include <Core/NTypes.hpp>
namespace Neurotec { namespace Biometrics { namespace Standards
{
#include <Biometrics/Standards/CbeffQualityAlgorithmIdentifiers.h>
}}}

namespace Neurotec { namespace Biometrics { namespace Standards
{

#undef CBEFF_QAI_INTECH_QM

#undef CBEFF_QAI_NIST_NFIQ

const NUShort CBEFF_QAI_INTECH_QM = 0x001A;

const NUShort CBEFF_QAI_NIST_NFIQ = 0x377D;

class CbeffQualityAlgorithmIdentifiers
{
	N_DECLARE_STATIC_OBJECT_CLASS(CbeffQualityAlgorithmIdentifiers)
};

}}}

#endif // !CBEFF_QUALITY_ALGORITHM_IDENTIFIERS_HPP_INCLUDED
