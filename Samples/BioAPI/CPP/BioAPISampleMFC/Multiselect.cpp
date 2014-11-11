// Multiselect.cpp : implementation file
//

#include "stdafx.h"
#include "BioAPISampleMFC.h"
#include "Multiselect.h"

// CMultiselect dialog

IMPLEMENT_DYNAMIC(CMultiselect, CDialog)

CMultiselect::CMultiselect(CWnd* pParent /*=NULL*/)
	: CDialog(CMultiselect::IDD, pParent)
	, m_Hex(_T(""))
	, multiple(true)
{

}

CMultiselect::~CMultiselect()
{
}

void CMultiselect::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_LIST1, m_List);
	DDX_Text(pDX, IDC_EDIT1, m_Hex);
}

BEGIN_MESSAGE_MAP(CMultiselect, CDialog)
	ON_LBN_SELCHANGE(IDC_LIST1, &CMultiselect::OnLbnSelchangeList1)
	ON_EN_CHANGE(IDC_EDIT1, &CMultiselect::OnEnChangeEdit1)
END_MESSAGE_MAP()

// CMultiselect message handlers

BOOL CMultiselect::OnInitDialog()
{
	CDialog::OnInitDialog();

	std::map<int, CString>::iterator I;

	I = items.begin();
	while (I != items.end())
	{
		m_List.AddString(I->second);
		I++;
	}

	int i=0;
	I = items.begin();
	while (I != items.end())
	{
		m_List.SetSel(i, (sel & I->first) == sel && (sel & I->first) == I->first);
		I++;
		i++;
	}

	m_Hex.Format(_T("0x%08X"), sel);
	UpdateData(0);

	if (!multiple)
	{
		m_List.ModifyStyle(LBS_MULTIPLESEL, 0);
	}

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

void CMultiselect::OnLbnSelchangeList1()
{
	std::map<int, CString>::iterator I;
	int i = 0;

	I = items.begin();
	sel = 0;
	while (I != items.end())
	{
		if (m_List.GetSel(i))
		{
			sel |= I->first;
		}
		I++;
		i++;
	}

	m_Hex.Format(_T("0x%08X"), sel);
	UpdateData(0);
}

void CMultiselect::OnEnChangeEdit1()
{
	UpdateData();
	sel = _tcstol(m_Hex, 0, 0);

	std::map<int, CString>::iterator I;
	int i = 0;

	I = items.begin();
	while (I != items.end())
	{
		m_List.SetSel(i, (sel & I->first) == sel && (sel & I->first) == I->first);
		I++;
		i++;
	}
}
