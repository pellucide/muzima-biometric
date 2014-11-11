#pragma once
#include "afxwin.h"

#include "BioAPISampleMFCDoc.h"
// CChooseBSP dialog

class CChooseBSP : public CDialog
{
	DECLARE_DYNAMIC(CChooseBSP)

public:
	CChooseBSP(CWnd* pParent = NULL);   // standard constructor
	virtual ~CChooseBSP();

// Dialog Data
	enum { IDD = IDD_CHOOSEBSP };

	std::vector<CBioAPISampleMFCDoc::BSP>* bsps;
	CBioAPISampleMFCDoc::BSP bsp;

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CComboBox m_BSPList;
	CString m_BSPDescription;
	CString m_ArchiveUnit;
	CString m_MatchingUnit;
	CString m_ProcessingUnit;
	CString m_SensorUnit;
	afx_msg void OnCbnSelchangeCombo1();
protected:
	virtual BOOL OnInitDialog();
};
