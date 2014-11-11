#include <Biometrics/Client/NDatabaseBiometricConnection.hpp>

#ifndef N_ODBC_BIOMETRIC_CONNECTION_HPP_INCLUDED
#define N_ODBC_BIOMETRIC_CONNECTION_HPP_INCLUDED

namespace Neurotec { namespace Biometrics { namespace Client
{
#include <Biometrics/Client/NOdbcBiometricConnection.h>
}}}

namespace Neurotec { namespace Biometrics { namespace Client
{

class NOdbcBiometricConnection : public NDatabaseBiometricConnection
{
	N_DECLARE_OBJECT_CLASS(NOdbcBiometricConnection, NDatabaseBiometricConnection)

private:
	static HNOdbcBiometricConnection Create()
	{
		HNOdbcBiometricConnection handle;
		NCheck(NOdbcBiometricConnectionCreate(&handle));
		return handle;
	}

public:
	NOdbcBiometricConnection()
		: NDatabaseBiometricConnection(Create(), true)
	{
	}

	NString GetConnectionString() const
	{
		return GetString(NOdbcBiometricConnectionGetConnectionString);
	}

	void SetConnectionString(const NStringWrapper & value)
	{
		SetString(NOdbcBiometricConnectionSetConnectionStringN, value);
	}
	
	NString GetTableName() const
	{
		return GetString(NOdbcBiometricConnectionGetTableName);
	}

	void SetTableName(const NStringWrapper & value)
	{
		SetString(NOdbcBiometricConnectionSetTableNameN, value);
	}
};

}}}

#endif // !N_ODBC_BIOMETRIC_CONNECTION_HPP_INCLUDED
