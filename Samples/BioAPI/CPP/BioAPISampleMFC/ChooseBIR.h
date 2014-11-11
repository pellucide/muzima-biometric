#pragma once
#include "afxwin.h"

#include "BioAPISampleMFCDoc.h"
// CChooseBIR dialog

class CChooseBIR : public CDialog
{
	DECLARE_DYNAMIC(CChooseBIR)

public:
	CChooseBIR(CWnd* pParent = NULL);   // standard constructor
	virtual ~CChooseBIR();

	std::vector<CBioAPISampleMFCDoc::BIR>* birs;
	CBioAPISampleMFCDoc::BIR bir;
	int birIndex;
	BioAPI_HANDLE hBSP;
	bool loaded;

// Dialog Data
	enum { IDD = IDD_CHOOSEBIR };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CListBox m_List;
	virtual BOOL OnInitDialog();
	afx_msg void OnLbnSelchangeList1();
	afx_msg void OnBnClickedButton1();
};
