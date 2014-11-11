// Capture.cpp : implementation file
//

#include "stdafx.h"
#include "BioAPISampleMFC.h"
#include "Capture.h"
#include <math.h>

void painter(CDC&, CRect&, void*);

// CCapture dialog

IMPLEMENT_DYNAMIC(CCapture, CDialog)

CCapture::CCapture(CWnd* pParent /*=NULL*/)
	: CDialog(CCapture::IDD, pParent)
	, m_Name(_T(""))
	, m_State(_T(""))
{
	captured = false;

	BIR.Path = _T("Captured");

	m_View.m_Data = this;
	m_View.m_Callback = painter;
	m_View.m_bEnabled = true;
	sampleAvailable = false;
	Progress = 0;
	Message = 0;

	bitmap.Width = 0;
	bitmap.Height = 0;
	bitmap.Bitmap.Data = malloc(1024 * 1024 * 3); // hopefully the maximum size
}

CCapture::~CCapture()
{
	if (bitmap.Bitmap.Data)
	{
		free(bitmap.Bitmap.Data);
	}

	if (captured)
	{
		BIR.release();
		captured = false;
	}
}

void CCapture::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_VIEW, m_View);
	DDX_Control(pDX, IDC_COMBO1, m_Purpose);
	DDX_Text(pDX, IDC_EDIT2, m_Name);
	DDX_Text(pDX, IDC_STATE, m_State);
}

BEGIN_MESSAGE_MAP(CCapture, CDialog)
	ON_BN_CLICKED(IDC_BUTTON1, &CCapture::OnBnClickedButton1)
	ON_BN_CLICKED(IDC_BUTTON7, &CCapture::OnBnClickedButton7)
	ON_BN_CLICKED(IDOK, &CCapture::OnBnClickedOk)
END_MESSAGE_MAP()

// CCapture message handlers

void CCapture::OnBnClickedButton1()
{
	// capture
	if (captured)
	{
		BIR.release();
	}

	BioAPI_BIR_PURPOSE purpose = (BioAPI_BIR_PURPOSE)m_Purpose.GetCurSel();

	if (purpose < 0)
		return;

	BioAPI_BIR_HANDLE hcaptured;

	bitmap.Width = 0;
	bitmap.Height = 0;

	if (BioAPI_Capture(this->hBSP, purpose, 0, NULL, &hcaptured, -1, NULL) == BioAPI_OK)
	{
		BioAPI_BIR bir;
		memset(&bir, 0, sizeof(bir));
		if (BioAPI_OK == BioAPI_GetBIRFromHandle(this->hBSP, hcaptured, &bir))
		{
			BIR.bir = (BioAPI_BIR*)malloc(sizeof(BioAPI_BIR));
			memcpy(BIR.bir, &bir, sizeof(BioAPI_BIR));
			BIR.bir->BiometricData.Data = malloc(bir.BiometricData.Length);
			memcpy(BIR.bir->BiometricData.Data, bir.BiometricData.Data, bir.BiometricData.Length);

			BioAPI_Free(bir.BiometricData.Data);
			UpdateData();
			BIR.Name = m_Name;
			BIR.status = CBioAPISampleMFCDoc::CAPTURED;

			captured = true;
		}
		else
		{
			BioAPI_FreeBIRHandle(this->hBSP, hcaptured);
		}
	}
	else
	{
		MessageBox(_T("Error capturing using selected BSP.\n")
			_T("Please check whether selected BSP supports BIR capturing and appropriate unit is attached."),
			_T("Error"),MB_OK);
	}

	m_View.Invalidate();
}

void CCapture::OnBnClickedButton7()
{
	// add bir
	if (captured)
	{
		BIRs->push_back(BIR);
		captured = false;
	}
	else
	{
		MessageBox(_T("Please press 'Capture' button first."), _T("Warning"), MB_OK);
	}
}

void CCapture::OnBnClickedOk()
{
	OnOK();
}

BioAPI_RETURN BioAPI GuiStateCallback(void *GuiStateCallbackCtx,
    BioAPI_GUI_STATE GuiState,
    BioAPI_GUI_RESPONSE *Response,
    BioAPI_GUI_MESSAGE Message,
    BioAPI_GUI_PROGRESS Progress,
    const BioAPI_GUI_BITMAP *SampleBuffer)
{
	CCapture* capture = (CCapture *)GuiStateCallbackCtx;

	if (GuiState & BioAPI_SAMPLE_AVAILABLE)
	{
		capture->sampleAvailable = true;
	}

	if (GuiState & BioAPI_MESSAGE_PROVIDED)
	{
		capture->Message = Message;
	}

	if (GuiState & BioAPI_PROGRESS_PROVIDED)
	{
		capture->Progress = Progress;
	}

	SampleBuffer;
	Response;
	return BioAPI_OK;
}

BioAPI_RETURN BioAPI GuiStreamingCallback(void *GuiStreamingCallbackCtx,
    const BioAPI_GUI_BITMAP *Bitmap)
{
	CCapture* capture = (CCapture *)GuiStreamingCallbackCtx;

	capture->Lock.Lock();

	if (capture->bitmap.Bitmap.Data == NULL)
	{
		capture->Lock.Unlock();
		return BioAPIERR_MEMORY_ERROR;
	}

	capture->bitmap.Width = Bitmap->Width;
	capture->bitmap.Height = Bitmap->Height;
	memcpy(capture->bitmap.Bitmap.Data, Bitmap->Bitmap.Data, Bitmap->Bitmap.Length);
	capture->Lock.Unlock();

	return BioAPI_OK;
}

BOOL CCapture::OnInitDialog()
{
	CDialog::OnInitDialog();

	m_Purpose.AddString(_T("NO_PURPOSE_AVAILABLE"));
	m_Purpose.AddString(_T("VERIFY"));
	m_Purpose.AddString(_T("IDENTIFY"));
	m_Purpose.AddString(_T("ENROLL"));
	m_Purpose.AddString(_T("ENROLL_FOR_VERIFICATION_ONLY"));
	m_Purpose.AddString(_T("ENROLL_FOR_IDENTIFICATION_ONLY"));
	m_Purpose.AddString(_T("AUDIT"));
	m_Purpose.SetCurSel(1);

	BioAPI_SetGUICallbacks(hBSP, GuiStreamingCallback, this, GuiStateCallback, this);

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

void painter(CDC&_dc, CRect&rc, void* data)
{
	CCapture* cap = (CCapture *)data;
	int w, h, i, j;
	unsigned char* d;
	double c, ch;

	if (cap->bitmap.Bitmap.Data == 0 || cap->bitmap.Width == 0)
	{
		_dc.FillSolidRect(rc, RGB(255,255,255));
		return;
	}

	CBitmap bmp, *old;
	CDC dc;
	dc.CreateCompatibleDC(&_dc);
	bmp.CreateCompatibleBitmap(&_dc, cap->bitmap.Width, cap->bitmap.Height);

	old = dc.SelectObject(&bmp);

	dc.FillSolidRect(rc, RGB(255,255,255));

	w = cap->bitmap.Width;
	h = cap->bitmap.Height;
	d = (unsigned char *)cap->bitmap.Bitmap.Data;

	// calculate scaling factors
	c = (double)rc.Width() / w;
	ch = (double)rc.Height() / h;
	// choose the bigger one
	if (c > ch)
		c = ch;

	// paint using solid rectangles
	for (i = 0; i < h; i++)
	{
		for (j = 0; j < w; j++)
		{
			unsigned char b = d[j + w * i];
			int x, y;

			x = (int)ceil(c * j);
			y = (int)ceil(c * i);
			dc.FillSolidRect(x, y, (int)ceil(c), (int)ceil(c), RGB(b, b, b));
		}
	}

	_dc.BitBlt(0,0, rc.Width(), rc.Height(), &dc, 0, 0, SRCCOPY);

	dc.SelectObject(old);
	dc.DeleteDC();
}

