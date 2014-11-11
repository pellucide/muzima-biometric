#pragma once
#include "afxwin.h"

// CLoadBSP dialog

class CLoadBSP : public CDialog
{
	DECLARE_DYNAMIC(CLoadBSP)

public:
	CLoadBSP(CWnd* pParent = NULL);   // standard constructor
	virtual ~CLoadBSP();

// Dialog Data
	enum { IDD = IDD_LOAD_BSP };

	BioAPI_BSP_SCHEMA *sSchema;
	uint32_t numElements;
		// sensor units
		// processing units
		// archive units
		// matching units
	BioAPI_UNIT_ID uids[4];
	bool loadedBSP;
	int bspIndex;
	BioAPI_UUID loadedUUID;

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CListBox m_CaptureUnits;
	CListBox m_ProcessingUnits;
	CListBox m_ArchiveUnits;
	CListBox m_MatchingUnits;
	CComboBox m_BSPs;
	virtual BOOL OnInitDialog();
	CListBox m_Description;
	afx_msg void OnCbnSelchangeCombo1();

	void cleanup();
	afx_msg void OnLbnSelchangeList1();
	afx_msg void OnLbnSelchangeList2();
	afx_msg void OnLbnSelchangeList3();
	afx_msg void OnLbnSelchangeList4();
};
