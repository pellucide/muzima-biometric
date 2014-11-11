#pragma once

// CStaticCustomDraw

typedef void (*SCDraw_callback)(CDC&, CRect&, void*);
typedef void (*SCDraw_callback1)(CDC&, CRect&, void*, int);

class CStaticCustomDraw : public CStatic
{
	DECLARE_DYNAMIC(CStaticCustomDraw)

public:
	CStaticCustomDraw();
	CStaticCustomDraw(SCDraw_callback, void*);
	CStaticCustomDraw(SCDraw_callback1, void*, int);
	virtual ~CStaticCustomDraw();

	SCDraw_callback m_Callback;
	SCDraw_callback1 m_Callback1;
	void *m_Data;
	int m_int;
	bool m_bEnabled;

protected:
	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnPaint();
};

