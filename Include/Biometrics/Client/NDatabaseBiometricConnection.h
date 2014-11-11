#ifndef N_DATABASE_BIOMETRIC_CONNECTION_H_INCLUDED
#define N_DATABASE_BIOMETRIC_CONNECTION_H_INCLUDED

#include <Biometrics/NBiometricConnection.h>
#include <Biometrics/NBiometricEngine.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(NDatabaseBiometricConnection, NBiometricConnection)

NResult N_API NDatabaseBiometricConnectionBeginLoadSubjects(HNDatabaseBiometricConnection hConnection);
NResult N_API NDatabaseBiometricConnectionEndLoadSubjects(HNDatabaseBiometricConnection hConnection);
NResult N_API NDatabaseBiometricConnectionLoadNextSubject(HNDatabaseBiometricConnection hConnection, HNSubject * phSubject);
NResult N_API NDatabaseBiometricConnectionPerformOperation(HNDatabaseBiometricConnection hConnection, NBiometricOperations operation, HNSubject hSubject);

#ifdef N_CPP
}
#endif

#endif // !N_DATABASE_BIOMETRIC_CONNECTION_H_INCLUDED
