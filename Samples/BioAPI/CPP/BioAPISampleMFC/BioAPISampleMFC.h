// BioAPISample.MFC.h : main header file for the BioAPISample.MFC application
//
#pragma once

#ifndef __AFXWIN_H__
	#error "include 'stdafx.h' before including this file for PCH"
#endif

#include "resource.h"       // main symbols

// CBioAPISampleMFCApp:
// See BioAPISample.MFC.cpp for the implementation of this class
//

class CBioAPISampleMFCApp : public CWinApp
{
public:
	CBioAPISampleMFCApp();

// Overrides
public:
	virtual BOOL InitInstance();

// Implementation
	afx_msg void OnAppAbout();
	DECLARE_MESSAGE_MAP()
	virtual int ExitInstance();
};

extern CBioAPISampleMFCApp theApp;
