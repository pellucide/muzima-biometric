// StaticCustomDraw.cpp : implementation file
//

#include "stdafx.h"
#include "StaticCustomDraw.h"

// CStaticCustomDraw

IMPLEMENT_DYNAMIC(CStaticCustomDraw, CStatic)
CStaticCustomDraw::CStaticCustomDraw()
	: m_Callback(0), m_Callback1(0), m_Data(0), m_int(0), m_bEnabled(false)
{
}
CStaticCustomDraw::CStaticCustomDraw(SCDraw_callback cb, void * data)
	:m_Callback(cb), m_Callback1(0), m_Data(data), m_int(0), m_bEnabled(false)
{
}
CStaticCustomDraw::CStaticCustomDraw(SCDraw_callback1 cb, void * data, int i)
	:m_Callback(0), m_Callback1(cb), m_Data(data), m_int(i), m_bEnabled(false)
{
}

CStaticCustomDraw::~CStaticCustomDraw()
{
}

BEGIN_MESSAGE_MAP(CStaticCustomDraw, CStatic)
	ON_WM_PAINT()
END_MESSAGE_MAP()

// CStaticCustomDraw message handlers

void CStaticCustomDraw::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: Add your message handler code here
	// Do not call CStatic::OnPaint() for painting messages

	CRect rc;
	GetClientRect(&rc);

	if (m_Callback && m_bEnabled)
		(*m_Callback)(dc, rc, m_Data);
	if (m_Callback1 && m_bEnabled)
		(*m_Callback1)(dc, rc, m_Data, m_int);
}
