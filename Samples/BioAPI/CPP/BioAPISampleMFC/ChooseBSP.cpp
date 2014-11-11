// ChooseBSP.cpp : implementation file
//

#include "stdafx.h"
#include "BioAPISampleMFC.h"
#include "ChooseBSP.h"

// CChooseBSP dialog

IMPLEMENT_DYNAMIC(CChooseBSP, CDialog)

CChooseBSP::CChooseBSP(CWnd* pParent /*=NULL*/)
	: CDialog(CChooseBSP::IDD, pParent)
	, m_BSPDescription(_T(""))
	, m_ArchiveUnit(_T(""))
	, m_MatchingUnit(_T(""))
	, m_ProcessingUnit(_T(""))
	, m_SensorUnit(_T(""))
{

}

CChooseBSP::~CChooseBSP()
{
}

void CChooseBSP::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_COMBO1, m_BSPList);
	DDX_Text(pDX, IDC_STATIC1, m_BSPDescription);
	DDX_Text(pDX, IDC_STATIC2, m_ArchiveUnit);
	DDX_Text(pDX, IDC_STATIC3, m_MatchingUnit);
	DDX_Text(pDX, IDC_STATIC4, m_ProcessingUnit);
	DDX_Text(pDX, IDC_STATIC5, m_SensorUnit);
}

BEGIN_MESSAGE_MAP(CChooseBSP, CDialog)
	ON_CBN_SELCHANGE(IDC_COMBO1, &CChooseBSP::OnCbnSelchangeCombo1)
END_MESSAGE_MAP()

// CChooseBSP message handlers

void CChooseBSP::OnCbnSelchangeCombo1()
{
	// TODO: Add your control notification handler code here
	int sel = m_BSPList.GetCurSel();
	CString str;
	TCHAR tmp[1024];
	if (sel != -1)
	{
		bsp = (*bsps)[sel];
	}

	utf8sprintf_s(tmp, sizeof(tmp)/sizeof(tmp[0]), _T("%s"), (char*)bsp.schema.BSPDescription); ///<  description of the BSP
	m_BSPDescription = tmp;
	m_ArchiveUnit.Format(_T("%d: %s"), bsp.ID[0].UnitId, _T("-"));
	m_MatchingUnit.Format(_T("%d: %s"), bsp.ID[1].UnitId, _T("-"));
	m_ProcessingUnit.Format(_T("%d: %s"), bsp.ID[2].UnitId, _T("-"));
	m_SensorUnit.Format(_T("%d: %s"), bsp.ID[3].UnitId, _T("-"));

	UpdateData(false);
}

BOOL CChooseBSP::OnInitDialog()
{
	CDialog::OnInitDialog();
	TCHAR uuid[128];
	TCHAR descr[512];

	if (bsps == 0)
		return FALSE;

	// TODO:  Add extra initialization here
	std::vector<CBioAPISampleMFCDoc::BSP>::iterator I;
	I = bsps->begin();
	while (I != bsps->end())
	{
		CString str;
		BioAPI_GetPrintableUUID(&I->uuid, uuid);
		utf8sprintf_s(descr, sizeof(descr)/sizeof(descr[0]), _T("%s"), (char*)I->schema.BSPDescription);
		str.Format(_T("%s %s"), uuid, descr);
		m_BSPList.AddString(str);
		I++;
	}

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}
