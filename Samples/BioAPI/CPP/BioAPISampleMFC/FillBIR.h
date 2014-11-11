#pragma once
#include "afxwin.h"
#include "BioAPISampleMFCDoc.h"

// CFillBIR dialog

class CFillBIR : public CDialog
{
	DECLARE_DYNAMIC(CFillBIR)

public:
	CFillBIR(CWnd* pParent = NULL);   // standard constructor
	virtual ~CFillBIR();

// Dialog Data
	enum { IDD = IDD_FILLBIR };

	CBioAPISampleMFCDoc::BIR BIR;
	BioAPI_BIR_HEADER header;

	void erase()
	{
		free(BIR.bir->BiometricData.Data);
		free(BIR.bir);
	}

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CComboBox m_Purpose;
	CString m_Name;
	BOOL all;
	afx_msg void OnBnClickedOk();
	virtual BOOL OnInitDialog();
	CString m_FactorsMask;
	CString m_FormatOwner;
	CString m_FormatType;
	CString m_Type;
	CString m_ProductType;
	CString m_Quality;
	CString m_ProductID;
	afx_msg void OnBnClickedButton2();
	afx_msg void OnBnClickedButton3();
	afx_msg void OnBnClickedButton5();
	afx_msg void OnBnClickedButton4();
};
