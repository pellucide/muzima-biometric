#pragma once
#include "afxwin.h"

// CMultiselect dialog

class CMultiselect : public CDialog
{
	DECLARE_DYNAMIC(CMultiselect)

public:
	CMultiselect(CWnd* pParent = NULL);   // standard constructor
	virtual ~CMultiselect();

// Dialog Data
	enum { IDD = IDD_MULTISELECT };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	CListBox m_List;
	CString m_Hex;
	bool multiple;

	std::map<int, CString> items;
	int sel;

	virtual BOOL OnInitDialog();
	afx_msg void OnLbnSelchangeList1();
	afx_msg void OnEnChangeEdit1();
};
