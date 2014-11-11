// BioAPISample.MFCView.h : interface of the CBioAPISampleMFCView class
//

#pragma once

class CBioAPISampleMFCView : public CListView
{
protected: // create from serialization only
	CBioAPISampleMFCView();
	DECLARE_DYNCREATE(CBioAPISampleMFCView)

// Attributes
public:
	CBioAPISampleMFCDoc* GetDocument() const;

// Operations
public:

// Overrides
public:
	virtual void OnDraw(CDC* pDC);  // overridden to draw this view
	virtual void OnInitialUpdate();
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
protected:
	afx_msg void OnViewSmallIcons();
	afx_msg void OnViewLargeIcons();
	afx_msg void OnViewList();
	afx_msg void OnViewDetails();
	afx_msg void OnViewFullRowDetails();
	afx_msg void OnUpdateViewSmallIcons(CCmdUI* pCmdUI);
	afx_msg void OnUpdateViewLargeIcons(CCmdUI* pCmdUI);
	afx_msg void OnUpdateViewList(CCmdUI* pCmdUI);
	afx_msg void OnUpdateViewDetails(CCmdUI* pCmdUI);
	afx_msg void OnUpdateViewFullRowDetails(CCmdUI* pCmdUI);

	BOOL SetViewType(DWORD dwViewType);
	DWORD GetViewType();
	void CheckItem(int nNewCheckedItem);

// Implementation
public:
	virtual ~CBioAPISampleMFCView();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	DECLARE_MESSAGE_MAP()
	virtual void OnUpdate(CView* /*pSender*/, LPARAM /*lHint*/, CObject* /*pHint*/);
public:
	afx_msg void OnLButtonDblClk(UINT nFlags, CPoint point);
	afx_msg void OnRButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnEditSelect();
	afx_msg void OnEditSelectall();
	afx_msg void OnEditInvertselection();
	afx_msg void OnEditDeselect();
	afx_msg void OnEditDeselectall();
};

#ifndef _DEBUG
inline CBioAPISampleMFCDoc* CBioAPISampleMFCView::GetDocument() const
   { return reinterpret_cast<CBioAPISampleMFCDoc*>(m_pDocument); }
#endif

