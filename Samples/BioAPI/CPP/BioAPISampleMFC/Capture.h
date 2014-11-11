#pragma once

#include "BioAPISampleMFCDoc.h"
#include "afxwin.h"
#include "staticcustomdraw.h"
// CCapture dialog

class CCapture : public CDialog
{
	DECLARE_DYNAMIC(CCapture)

public:
	CCapture(CWnd* pParent = NULL);   // standard constructor
	virtual ~CCapture();

	std::vector<CBioAPISampleMFCDoc::BIR>* BIRs;
	bool captured;
	CBioAPISampleMFCDoc::BIR BIR;
	BioAPI_HANDLE hBSP;

	BioAPI_GUI_BITMAP bitmap;
	CMutex Lock;
	bool sampleAvailable;
	BioAPI_GUI_PROGRESS Progress;
	BioAPI_GUI_MESSAGE Message;

// Dialog Data
	enum { IDD = IDD_CAPTURE };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CStaticCustomDraw m_View;
	afx_msg void OnBnClickedButton1();
	afx_msg void OnBnClickedButton7();
	afx_msg void OnBnClickedOk();
	CComboBox m_Purpose;
	virtual BOOL OnInitDialog();
	CString m_Name;
	CString m_State;
};
