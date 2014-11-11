// BioAPISample.MFCView.cpp : implementation of the CBioAPISampleMFCView class
//

#include "stdafx.h"
#include "BioAPISampleMFC.h"

#include "BioAPISampleMFCDoc.h"
#include "BioAPISampleMFCView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#define NUM_COLUMNS 7

static _TCHAR *_gszColumnLabel[NUM_COLUMNS] =
{
	_T("Name"), _T("Original path"), _T("Purpose"), _T("Type"), _T("Format"), _T("Data Type"), _T("State")
};

static int _gnColumnFmt[NUM_COLUMNS] =
{
	LVCFMT_LEFT, LVCFMT_RIGHT, LVCFMT_RIGHT, LVCFMT_RIGHT, LVCFMT_RIGHT, LVCFMT_RIGHT, LVCFMT_RIGHT
};

static int _gnColumnWidth[NUM_COLUMNS] =
{
	100, 200, 100, 100, 100, 100, 100
};

CString BIR_GetType(BioAPI_BIR* bir)
{
	CString str = _T("");

	if (bir->Header.FactorsMask & BioAPI_TYPE_MULTIPLE)
		str += _T("M");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_FACIAL_FEATURES)
		str += _T("C");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_VOICE)
		str += _T("V");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_FINGERPRINT)
		str += _T("F");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_IRIS)
		str += _T("I");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_RETINA)
		str += _T("R");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_HAND_GEOMETRY)
		str += _T("G");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_SIGNATURE_DYNAMICS)
		str += _T("S");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_KEYSTOKE_DYNAMICS)
		str += _T("K");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_LIP_MOVEMENT)
		str += _T("L");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_THERMAL_FACE_IMAGE)
		str += _T("T");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_THERMAL_HAND_IMAGE)
		str += _T("t");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_GAIT)
		str += _T("A");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_OTHER)
		str += _T("O");
	else
		str += _T("-");
	if (bir->Header.FactorsMask & BioAPI_TYPE_PASSWORD)
		str += _T("W");
	else
		str += _T("-");

	return str;
}
CString BIR_GetFormat(BioAPI_BIR * bir)
{
	CString str;
	str.Format(_T("0x%04X,0x%04X"), (int)bir->Header.Format.FormatOwner, (int)bir->Header.Format.FormatType);
	return str;
}
CString BIR_GetDataType(BioAPI_BIR * bir)
{
	CString str = _T("");

	if (bir->Header.Type & BioAPI_BIR_DATA_TYPE_RAW)
		str += _T("R");
	else
		str += _T("-");
	if (bir->Header.Type & BioAPI_BIR_DATA_TYPE_INTERMEDIATE)
		str += _T("I");
	else
		str += _T("-");
	if (bir->Header.Type & BioAPI_BIR_DATA_TYPE_PROCESSED)
		str += _T("P");
	else
		str += _T("-");
	if (bir->Header.Type & BioAPI_BIR_DATA_TYPE_ENCRYPTED)
		str += _T("E");
	else
		str += _T("-");
	if (bir->Header.Type & BioAPI_BIR_DATA_TYPE_SIGNED)
		str += _T("S");
	else
		str += _T("-");
	if (bir->Header.Type & BioAPI_BIR_INDEX_PRESENT)
		str += _T("X");
	else
		str += _T("-");

	return str;
}
CString BIR_GetState(CBioAPISampleMFCDoc::BIR & bir)
{
	switch (bir.status)
	{
	case CBioAPISampleMFCDoc::NOBIR:
		return CString(_T("NO BIR"));
	case CBioAPISampleMFCDoc::LOADED:
		return CString(_T("Loaded"));
	case CBioAPISampleMFCDoc::CAPTURED:
		return CString(_T("Captured"));
	case CBioAPISampleMFCDoc::HBIR:
		return CString(_T("By Handle"));
	case CBioAPISampleMFCDoc::PROCESSED:
		return CString(_T("Processed"));
	default:
		return CString(_T("unknown"));
	}
}

CString BIR_GetPurpose(BioAPI_BIR * bir)
{
	switch (bir->Header.Purpose)
	{
	case BioAPI_NO_PURPOSE_AVAILABLE:
		return CString(_T("N/A"));
	case BioAPI_PURPOSE_VERIFY:
		return CString(_T("VERIFY"));
	case BioAPI_PURPOSE_IDENTIFY:
		return CString(_T("IDENTIFY"));
	case BioAPI_PURPOSE_ENROLL:
		return CString(_T("ENROLL"));
	case BioAPI_PURPOSE_ENROLL_FOR_VERIFICATION_ONLY:
		return CString(_T("ENROLL_FOR_VERIFICATION_ONLY"));
	case BioAPI_PURPOSE_ENROLL_FOR_IDENTIFICATION_ONLY:
		return CString(_T("ENROLL_FOR_IDENTIFICATION_ONLY"));
	case BioAPI_PURPOSE_AUDIT:
		return CString(_T("AUDIT"));
	default:
		return CString(_T("Invalid"));
	}
}

// CBioAPISampleMFCView

IMPLEMENT_DYNCREATE(CBioAPISampleMFCView, CListView)

BEGIN_MESSAGE_MAP(CBioAPISampleMFCView, CListView)
	ON_COMMAND(ID_VIEW_SMALLICONS, OnViewSmallIcons)
	ON_COMMAND(ID_VIEW_LARGEICONS, OnViewLargeIcons)
	ON_COMMAND(ID_VIEW_LIST, OnViewList)
	ON_COMMAND(ID_VIEW_DETAILS, OnViewDetails)
	ON_COMMAND(ID_VIEW_ROWDETAILS, OnViewFullRowDetails)
	ON_UPDATE_COMMAND_UI(ID_VIEW_SMALLICONS, OnUpdateViewSmallIcons)
	ON_UPDATE_COMMAND_UI(ID_VIEW_LARGEICONS, OnUpdateViewLargeIcons)
	ON_UPDATE_COMMAND_UI(ID_VIEW_LIST, OnUpdateViewList)
	ON_UPDATE_COMMAND_UI(ID_VIEW_DETAILS, OnUpdateViewDetails)
	ON_UPDATE_COMMAND_UI(ID_VIEW_ROWDETAILS, OnUpdateViewFullRowDetails)
	ON_WM_LBUTTONDBLCLK()
	ON_WM_RBUTTONDOWN()
	ON_WM_LBUTTONDOWN()
	ON_COMMAND(ID_EDIT_SELECT, &CBioAPISampleMFCView::OnEditSelect)
	ON_COMMAND(ID_EDIT_SELECTALL, &CBioAPISampleMFCView::OnEditSelectall)
	ON_COMMAND(ID_EDIT_INVERTSELECTION, &CBioAPISampleMFCView::OnEditInvertselection)
	ON_COMMAND(ID_EDIT_DESELECT, &CBioAPISampleMFCView::OnEditDeselect)
	ON_COMMAND(ID_EDIT_DESELECTALL, &CBioAPISampleMFCView::OnEditDeselectall)
END_MESSAGE_MAP()

// CBioAPISampleMFCView construction/destruction

CBioAPISampleMFCView::CBioAPISampleMFCView()
{
	// TODO: add construction code here

}

CBioAPISampleMFCView::~CBioAPISampleMFCView()
{
}

BOOL CBioAPISampleMFCView::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: Modify the Window class or styles here by modifying
	//  the CREATESTRUCT cs
	

	return CListView::PreCreateWindow(cs);
}

// CBioAPISampleMFCView drawing

void CBioAPISampleMFCView::OnDraw(CDC * /*pDC*/)
{
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	// TODO: add draw code for native data here
}

void CBioAPISampleMFCView::OnInitialUpdate()
{
	CListView::OnInitialUpdate();

	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
	CListCtrl& ListCtrl = GetListCtrl();
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	int i = 0;

// insert columns

	LV_COLUMN lvc;

	lvc.mask = LVCF_FMT | LVCF_WIDTH | LVCF_TEXT | LVCF_SUBITEM;

	ListCtrl.DeleteAllItems();
	for(i = 0; i < NUM_COLUMNS; i++)
		ListCtrl.DeleteColumn(0);

	for(i = 0; i < NUM_COLUMNS; i++)
	{
		lvc.iSubItem = i;
		lvc.pszText = _gszColumnLabel[i];
		lvc.cx = _gnColumnWidth[i];
		lvc.fmt = _gnColumnFmt[i];
		ListCtrl.InsertColumn(i,&lvc);
	}

	SetViewType(LVS_REPORT);
	ListCtrl.SetExtendedStyle(ListCtrl.GetExtendedStyle()|LVS_REPORT|LVS_EX_GRIDLINES|LVS_EX_CHECKBOXES|LVS_EX_FULLROWSELECT);

	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	pDoc->Lock();

	I = pDoc->m_BIRs.begin();

	i=0;
	while (I != pDoc->m_BIRs.end())
	{
		LV_ITEM lvi;
		memset(&lvi, 0, sizeof(lvi));
		lvi.mask = LVIF_TEXT | LVIF_STATE;
		lvi.iItem = i;
		lvi.iSubItem = 0;
		lvi.pszText = (LPTSTR)(LPCTSTR)I->Name;
		lvi.stateMask = LVIS_STATEIMAGEMASK;
		lvi.state = INDEXTOSTATEIMAGEMASK(1);

		ListCtrl.InsertItem(&lvi); // name
		ListCtrl.SetItemText(i, 1, I->Path); // original path
		ListCtrl.SetItemText(i, 2, BIR_GetPurpose(I->bir)); // purpose
		ListCtrl.SetItemText(i, 3, BIR_GetType(I->bir)); // type
		ListCtrl.SetItemText(i, 4, BIR_GetFormat(I->bir)); // format
		ListCtrl.SetItemText(i, 5, BIR_GetDataType(I->bir)); // data type
		ListCtrl.SetItemText(i, 6, BIR_GetState(*I)); // state

		ListCtrl.SetCheck(i, I->selected);

		I++;
		i++;
	}

	pDoc->Unlock();
}

// CBioAPISampleMFCView diagnostics

#ifdef _DEBUG
void CBioAPISampleMFCView::AssertValid() const
{
	CListView::AssertValid();
}

void CBioAPISampleMFCView::Dump(CDumpContext& dc) const
{
	CListView::Dump(dc);
}

CBioAPISampleMFCDoc* CBioAPISampleMFCView::GetDocument() const // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CBioAPISampleMFCDoc)));
	return (CBioAPISampleMFCDoc*)m_pDocument;
}
#endif // _DEBUG

BOOL CBioAPISampleMFCView::SetViewType(DWORD dwViewType)
{
	return(ModifyStyle(LVS_TYPEMASK,dwViewType & LVS_TYPEMASK));
}

DWORD CBioAPISampleMFCView::GetViewType()
{
	return(GetStyle() & LVS_TYPEMASK);
}

void CBioAPISampleMFCView::CheckItem(int nNewCheckedItem)
{
	CListCtrl& ListCtrl = GetListCtrl();
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;
	if (nNewCheckedItem < 0)
		return ;

	pDoc->m_BIRs[nNewCheckedItem].selected = !pDoc->m_BIRs[nNewCheckedItem].selected;
	ListCtrl.SetCheck(nNewCheckedItem, pDoc->m_BIRs[nNewCheckedItem].selected);
}

// CBioAPISampleMFCView message handlers

void CBioAPISampleMFCView::OnViewSmallIcons()
{
	if (GetViewType() != LVS_SMALLICON)
		SetViewType(LVS_SMALLICON);
}

void CBioAPISampleMFCView::OnViewLargeIcons()
{
	if (GetViewType() != LVS_ICON)
		SetViewType(LVS_ICON);
}

void CBioAPISampleMFCView::OnViewList()
{
	if (GetViewType() != LVS_LIST)
		SetViewType(LVS_LIST);
}

void CBioAPISampleMFCView::OnViewDetails()
{
	if (GetViewType() != LVS_REPORT)
		SetViewType(LVS_REPORT);
}

void CBioAPISampleMFCView::OnViewFullRowDetails()
{
	if (GetViewType() != LVS_REPORT)
		SetViewType(LVS_REPORT);
}

void CBioAPISampleMFCView::OnUpdateViewSmallIcons(CCmdUI* pCmdUI)
{
	pCmdUI->SetCheck(GetViewType() == LVS_SMALLICON);
}

void CBioAPISampleMFCView::OnUpdateViewLargeIcons(CCmdUI* pCmdUI)
{
	pCmdUI->SetCheck(GetViewType() == LVS_ICON);
}

void CBioAPISampleMFCView::OnUpdateViewList(CCmdUI* pCmdUI)
{
	pCmdUI->SetCheck(GetViewType() == LVS_LIST);
}

void CBioAPISampleMFCView::OnUpdateViewDetails(CCmdUI* pCmdUI)
{
	pCmdUI->SetCheck(GetViewType() == LVS_REPORT );
}

void CBioAPISampleMFCView::OnUpdateViewFullRowDetails(CCmdUI* pCmdUI)
{
//	pCmdUI->SetCheck((GetViewType() == LVS_REPORT) && GetFullRowSel());
	pCmdUI;
}

void CBioAPISampleMFCView::OnUpdate(CView* /*pSender*/, LPARAM /*lHint*/, CObject* /*pHint*/)
{
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
	CListCtrl& ListCtrl = GetListCtrl();
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	int i = 0;
	pDoc->Lock();

	I = pDoc->m_BIRs.begin();
	ListCtrl.DeleteAllItems();

	i=0;
	while (I != pDoc->m_BIRs.end() )
	{
		LV_ITEM lvi;
		memset(&lvi, 0, sizeof(lvi));
		lvi.mask = LVIF_TEXT | LVIF_STATE;
		lvi.iItem = i;
		lvi.iSubItem = 0;
		lvi.pszText = (LPTSTR)(LPCTSTR)I->Name;
		lvi.stateMask = LVIS_STATEIMAGEMASK;
		lvi.state = INDEXTOSTATEIMAGEMASK(1);

		ListCtrl.InsertItem(&lvi); // name
		ListCtrl.SetItemText(i, 1, I->Path); // original path
		ListCtrl.SetItemText(i, 2, BIR_GetPurpose(I->bir)); // purpose
		ListCtrl.SetItemText(i, 3, BIR_GetType(I->bir)); // type
		ListCtrl.SetItemText(i, 4, BIR_GetFormat(I->bir)); // format
		ListCtrl.SetItemText(i, 5, BIR_GetDataType(I->bir)); // data type
		ListCtrl.SetItemText(i, 6, BIR_GetState(*I)); // state

		ListCtrl.SetCheck(i, I->selected);

		I++;
		i++;
	}

	pDoc->Unlock();
}

void CBioAPISampleMFCView::OnLButtonDblClk(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default

	CListView::OnLButtonDblClk(nFlags, point);
}

void CBioAPISampleMFCView::OnLButtonDown(UINT nFlags, CPoint point)
{
	UINT uFlags = 0;
	int nHitItem = GetListCtrl().HitTest(point, &uFlags);

	// we need additional checking in owner-draw mode
	// because we only get LVHT_ONITEM
	BOOL bHit = FALSE;
	if (uFlags & LVHT_ONITEMSTATEICON)
		bHit = TRUE;

	if (bHit)
		CheckItem(nHitItem);
	else
		CListView::OnLButtonDown(nFlags, point);
}

void CBioAPISampleMFCView::OnRButtonDown(UINT nFlags, CPoint point)
{
	// TODO: Add your message handler code here and/or call default

	CMenu menu;
	CMenu *submenu;

	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	pDoc->Lock();

	menu.LoadMenu(IDR_POPUP);
	ClientToScreen(&point);
	submenu = menu.GetSubMenu(0);
	pDoc->Unlock();

	submenu->TrackPopupMenu(TPM_LEFTALIGN, point.x , point.y , this);

	UNREFERENCED_PARAMETER(nFlags);
}

void CBioAPISampleMFCView::OnEditSelect()
{
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	int i= GetListCtrl().GetSelectionMark();
	if ( i==-1 )
		return ;

	pDoc->Lock();

	pDoc->m_BIRs[i].selected = true;
	GetListCtrl().SetCheck(i, pDoc->m_BIRs[i].selected);

	pDoc->Unlock();
}

void CBioAPISampleMFCView::OnEditSelectall()
{
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;

	pDoc->Lock();
	I = pDoc->m_BIRs.begin();
	int i =0;
	
	while (I != pDoc->m_BIRs.end())
	{
		I->selected = true;
		GetListCtrl().SetCheck(i, I->selected);

		I++;
		i++;
	}
	pDoc->Unlock();
}

void CBioAPISampleMFCView::OnEditInvertselection()
{
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;

	pDoc->Lock();
	I = pDoc->m_BIRs.begin();
	int i = 0;
	
	while (I != pDoc->m_BIRs.end())
	{
		I->selected = !I->selected;
		GetListCtrl().SetCheck(i, I->selected);

		I++;
		i++;
	}
	pDoc->Unlock();
}

void CBioAPISampleMFCView::OnEditDeselect()
{
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	int i= GetListCtrl().GetSelectionMark();
	if (i == -1)
		return ;

	pDoc->Lock();

	pDoc->m_BIRs[i].selected = false;
	GetListCtrl().SetCheck(i, pDoc->m_BIRs[i].selected);

	pDoc->Unlock();
}

void CBioAPISampleMFCView::OnEditDeselectall()
{
	CBioAPISampleMFCDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;

	pDoc->Lock();
	I = pDoc->m_BIRs.begin();
	int i = 0;
	
	while (I != pDoc->m_BIRs.end())
	{
		I->selected = false;
		GetListCtrl().SetCheck(i, I->selected);

		I++;
		i++;
	}
	pDoc->Unlock();
}
