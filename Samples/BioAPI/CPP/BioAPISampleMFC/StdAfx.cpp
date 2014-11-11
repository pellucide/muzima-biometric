// stdafx.cpp : source file that includes just the standard includes
// BioAPISample.MFC.pch will be the pre-compiled header
// stdafx.obj will contain the pre-compiled type information

#include "stdafx.h"

BioAPI_RETURN BioAPI BioAPI_GetPrintableUUID( const BioAPI_UUID *pUUID,
		TCHAR *PrintableUUID )
{
	/* Format the output */
	_stprintf_s( PrintableUUID, BioAPI_PRINTABLE_UUID_LENGTH,
			 BioAPI_UUID_FORMAT_STRING,
			 (*pUUID)[0], (*pUUID)[1], (*pUUID)[2], (*pUUID)[3],
			 (*pUUID)[4], (*pUUID)[5],
			 (*pUUID)[6], (*pUUID)[7],
			 (*pUUID)[8], (*pUUID)[9],
			 (*pUUID)[10],
			 (*pUUID)[11],
			 (*pUUID)[12],
			 (*pUUID)[13],
			 (*pUUID)[14],
			 (*pUUID)[15]);

	return BioAPI_OK;
}

BioAPI_RETURN BioAPI BioAPI_GetPrintableVersion( const BioAPI_VERSION *pVersion,
		TCHAR *PrintableVersion )
{
	/* format the output: any minor version number other than 0 will format as two chars
	   ie. 1.01 rather than 1.1, which is easily confused with 1.10 */
	if ((*pVersion)&0x0f)
		_stprintf_s( PrintableVersion, 8, _T("%d.%02d"), ((*pVersion)&0xf0)>>4, (*pVersion)&0x0f );
	else
		_stprintf_s( PrintableVersion, 8, _T("%d.%d"), ((*pVersion)&0xf0)>>4, (*pVersion)&0x0f );

	return BioAPI_OK;
}

BioAPI_RETURN BioAPI BioAPI_GetStructuredUUID( const TCHAR *PrintableUUID,
		BioAPI_UUID* pUUID )
{
	int nCount;
	int tempUUID[16];
	uint8_t *pbUUID = (uint8_t*) pUUID;

	/* Scan the input into a temporary integer array */
	nCount = _stscanf_s( PrintableUUID, 
					 BioAPI_UUID_FORMAT_STRING,
					 &(tempUUID)[0], &(tempUUID)[1], &(tempUUID)[2], &(tempUUID)[3],
					 &(tempUUID)[4], &(tempUUID)[5],
					 &(tempUUID)[6], &(tempUUID)[7],
					 &(tempUUID)[8], &(tempUUID)[9],
					 &(tempUUID)[10],
					 &(tempUUID)[11],
					 &(tempUUID)[12],
					 &(tempUUID)[13],
					 &(tempUUID)[14],
					 &(tempUUID)[15]);

	if ( nCount != 16 )
	{
		return BioAPIERR_INVALID_DATA;
	}

	/* Copy the integers into the chars */
	for (nCount = 0; nCount < 16; nCount ++)
	{
		pbUUID[nCount] = (uint8_t) tempUUID[nCount];
	}

	return BioAPI_OK;
}

void utf8sprintf_s(TCHAR *out, size_t maxlen, const TCHAR* format, const char* utf8)
{
	size_t length;
	wchar_t *wtmp;

	if( (length = MultiByteToWideChar(CP_UTF8, 0, utf8, -1, NULL, 0)) != 0) // get length in bytes
	{
		if ( (wtmp = (wchar_t*)malloc(sizeof(wchar_t)*(length+1))) != NULL )
		{
			MultiByteToWideChar(CP_UTF8, 0, utf8, -1, (LPWSTR) wtmp, (int)length);
		}
		else
		{
			TRACE0("Memory error!\n");
			return;
		}
	}
	else 
		return;

#if defined(_UNICODE)
	_stprintf_s(out, maxlen, format, wtmp);
	free(wtmp);
#else
	TCHAR* tmp;

	if (tmp = (char*)malloc(length))
	{
		WideCharToMultiByte(CP_UTF8, 0, wtmp, -1, (LPSTR) tmp, (int)length+1, NULL, NULL);
		free(wtmp);
		_stprintf_s(out, maxlen, format, tmp);
		free(tmp);
	}
	else
	{
		free(wtmp);
		printf("Memory error!\n");
		return 1;
	}

#endif

}

