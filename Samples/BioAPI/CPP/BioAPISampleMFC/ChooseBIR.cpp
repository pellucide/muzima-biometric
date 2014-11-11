// ChooseBIR.cpp : implementation file
//

#include "stdafx.h"
#include "BioAPISampleMFC.h"
#include "ChooseBIR.h"
#include "fillbir.h"

// CChooseBIR dialog

IMPLEMENT_DYNAMIC(CChooseBIR, CDialog)

CChooseBIR::CChooseBIR(CWnd* pParent /*=NULL*/)
	: CDialog(CChooseBIR::IDD, pParent)
{
	loaded = false;
}

CChooseBIR::~CChooseBIR()
{
}

void CChooseBIR::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_LIST1, m_List);
}

BEGIN_MESSAGE_MAP(CChooseBIR, CDialog)
	ON_LBN_SELCHANGE(IDC_LIST1, &CChooseBIR::OnLbnSelchangeList1)
	ON_BN_CLICKED(IDC_BUTTON1, &CChooseBIR::OnBnClickedButton1)
END_MESSAGE_MAP()

// CChooseBIR message handlers

BOOL CChooseBIR::OnInitDialog()
{
	CDialog::OnInitDialog();

	// TODO:  Add extra initialization here
	birIndex = -1;
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;

	I = birs->begin();
	while (I != birs->end())
	{
		CString str;
		str.Format(_T("%s: %s"), I->Name, I->Path);
		m_List.AddString(str);
		I++;
	}

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

void CChooseBIR::OnLbnSelchangeList1()
{
	// TODO: Add your control notification handler code here
	if (loaded)
	{
		free(bir.bir->BiometricData.Data);
		free(bir.bir);
		loaded = false;
	}
	birIndex = m_List.GetCurSel();
	bir = (*birs)[birIndex];
}

extern void ReadTemplate(FILE* fp, BioAPI_BIR* BIR);

void CChooseBIR::OnBnClickedButton1()
{
	// TODO: Add your control notification handler code here
	CFileDialog open(TRUE, _T("bir"), 0, 4|2, _T("Biometric Information Record (*.bir)|*.bir||||"), this);

	if (open.DoModal() == IDOK)
	{
		FILE* fp = NULL;
		_tfopen_s(&fp, open.GetFileName(), _T("rb"));
		if (fp)
		{
			TCHAR fname[256];
			BioAPI_BIR *bir;
			bir = new BioAPI_BIR;
			ReadTemplate(fp, bir);
			_tsplitpath_s(open.GetFileName(), NULL, 0, NULL, 0, fname, 256, NULL, 0);

			if (loaded)
			{
				free(this->bir.bir->BiometricData.Data);
				free(this->bir.bir);
				loaded = false;
			}

			this->bir = CBioAPISampleMFCDoc::BIR(open.GetFileName(), CString(fname), bir);
			fclose(fp);
			this->bir.status = CBioAPISampleMFCDoc::LOADED;

			CFillBIR fill(this);

			fill.BIR = this->bir;

			if (fill.DoModal() == IDOK)
			{
				this->bir = fill.BIR;
			}

			if (this->bir.bir->Header.Type == BioAPI_BIR_DATA_TYPE_INTERMEDIATE)
			{
				BioAPI_BIR_HANDLE h;
				BioAPI_BIR tmpbir;
				BioAPI_INPUT_BIR birTemplate; 

				birTemplate.InputBIR.BIR = this->bir.bir;
				birTemplate.Form = BioAPI_FULLBIR_INPUT;

				if (BioAPI_OK == BioAPI_Process(this->hBSP, &birTemplate, NULL, &h))
				{
					if (BioAPI_GetBIRFromHandle(hBSP, h, &tmpbir) == BioAPI_OK)
					{
						void* data;
						data = new BYTE[tmpbir.BiometricData.Length];
						memcpy(data, tmpbir.BiometricData.Data, tmpbir.BiometricData.Length);
						free(this->bir.bir->BiometricData.Data);
						BioAPI_Free(tmpbir.BiometricData.Data);
						tmpbir.BiometricData.Data = data;
						memcpy(this->bir.bir, &tmpbir, sizeof(BioAPI_BIR));
						this->bir.status = CBioAPISampleMFCDoc::PROCESSED;
					}
				}
				else
				{
				}
			}

			loaded = true;
		}
		else
		{
			MessageBox(_T("Error reading a file. Please check if the file exists and try again\n"), _T("Error"), MB_OK );
		}
	}
}
