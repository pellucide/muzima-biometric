#ifndef N_ODBC_BIOMETRIC_CONNECTION_H_INCLUDED
#define N_ODBC_BIOMETRIC_CONNECTION_H_INCLUDED

#include <Biometrics/Client/NDatabaseBiometricConnection.h>

#ifdef N_CPP
extern "C"
{
#endif

N_DECLARE_OBJECT_TYPE(NOdbcBiometricConnection, NDatabaseBiometricConnection)

NResult N_API NOdbcBiometricConnectionCreate(HNOdbcBiometricConnection * phConnection);

NResult N_API NOdbcBiometricConnectionGetConnectionString(HNOdbcBiometricConnection hConnection, HNString * phValue);
NResult N_API NOdbcBiometricConnectionSetConnectionStringN(HNOdbcBiometricConnection hConnection, HNString hValue);
#ifndef N_NO_ANSI_FUNC
NResult N_API NOdbcBiometricConnectionSetConnectionStringA(HNOdbcBiometricConnection hConnection, const NAChar * szValue);
#endif
#ifndef N_NO_UNICODE
NResult N_API NOdbcBiometricConnectionSetConnectionStringW(HNOdbcBiometricConnection hConnection, const NWChar * szValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API NOdbcBiometricConnectionSetConnectionString(HNOdbcBiometricConnection hConnection, const NChar * szValue);
#endif
#define NOdbcBiometricConnectionSetConnectionString N_FUNC_AW(NOdbcBiometricConnectionSetConnectionString)

NResult N_API NOdbcBiometricConnectionGetTableName(HNOdbcBiometricConnection hConnection, HNString * phValue);
NResult N_API NOdbcBiometricConnectionSetTableNameN(HNOdbcBiometricConnection hConnection, HNString hValue);
#ifndef N_NO_ANSI_FUNC
NResult N_API NOdbcBiometricConnectionSetTableNameA(HNOdbcBiometricConnection hConnection, const NAChar * szValue);
#endif
#ifndef N_NO_UNICODE
NResult N_API NOdbcBiometricConnectionSetTableNameW(HNOdbcBiometricConnection hConnection, const NWChar * szValue);
#endif
#ifdef N_DOCUMENTATION
NResult N_API NOdbcBiometricConnectionSetTableName(HNOdbcBiometricConnection hConnection, const NChar * szValue);
#endif
#define NOdbcBiometricConnectionSetTableName N_FUNC_AW(NOdbcBiometricConnectionSetTableName)

#ifdef N_CPP
}
#endif

#endif // !N_ODBC_BIOMETRIC_CONNECTION_H_INCLUDED
