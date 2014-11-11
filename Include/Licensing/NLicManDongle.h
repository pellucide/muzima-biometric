#ifndef N_LIC_MAN_DONGLE_H_INCLUDED
#define N_LIC_MAN_DONGLE_H_INCLUDED

#include <Licensing/NLicenseProductInfo.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(NLicManDongle, NObject)

NResult N_API NLicManDongleGetDistributorId(HNLicManDongle hDongle, NInt * pDistributorId);
NResult N_API NLicManDongleGetHardwareId(HNLicManDongle hDongle, NUInt * pHardwareId);
NResult N_API NLicManDongleGetLicenses(HNLicManDongle hDongle, HNLicenseProductInfo * * parhProductInfo, NInt * pProductInfoCount);

#ifdef N_CPP
}
#endif

#endif // !N_LIC_MAN_DONGLE_H_INCLUDED
