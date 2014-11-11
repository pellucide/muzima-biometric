// BioAPISample.MFCDoc.cpp : implementation of the CBioAPISampleMFCDoc class
//

#include "stdafx.h"
#include <Biometrics/Standards/CbeffBiometricOrganizations.h>
#include <Biometrics/Standards/CbeffBdbFormatIdentifiers.h>
#include "BioAPISampleMFC.h"
#include "fillbir.h"
#include "BioAPISampleMFCDoc.h"
#include "ChooseBSP.h"
#include "ChooseBIR.h"
#include "Multiselect.h"
#include "Capture.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

void ReadTemplate(FILE* fp, BioAPI_BIR* BIR)
{
	fread(&BIR->Header, sizeof(BIR->Header),1,fp);
	fread(&BIR->BiometricData.Length, sizeof(BIR->BiometricData.Length), 1, fp);
	fread(&BIR->SecurityBlock.Length, sizeof(BIR->SecurityBlock.Length), 1, fp);

	BIR->BiometricData.Data = malloc(BIR->BiometricData.Length);
	BIR->SecurityBlock.Data = malloc(BIR->SecurityBlock.Length);

	fread(BIR->BiometricData.Data, BIR->BiometricData.Length, 1, fp);
	fread(BIR->SecurityBlock.Data, BIR->SecurityBlock.Length, 1, fp);
}
// CBioAPISampleMFCDoc

IMPLEMENT_DYNCREATE(CBioAPISampleMFCDoc, CDocument)

BEGIN_MESSAGE_MAP(CBioAPISampleMFCDoc, CDocument)
	ON_COMMAND(ID_EDIT_ADDIMAGE, &CBioAPISampleMFCDoc::OnEditAddimage)
	ON_COMMAND(ID_EDIT_ADDBIR, &CBioAPISampleMFCDoc::OnEditAddbir)
	ON_COMMAND(ID_EDIT_EDITBIR, &CBioAPISampleMFCDoc::OnEditEditbir)
	ON_COMMAND(ID_EDIT_VERIFYMATCH, &CBioAPISampleMFCDoc::OnEditVerifymatch)
	ON_COMMAND(ID_EDIT_IDENTIFYMATCH, &CBioAPISampleMFCDoc::OnEditIdentifymatch)
	ON_COMMAND(ID_EDIT_VERIFY, &CBioAPISampleMFCDoc::OnEditVerify)
	ON_COMMAND(ID_EDIT_IDENTIFY, &CBioAPISampleMFCDoc::OnEditIdentify)
	ON_COMMAND(ID_EDIT_PROCESS, &CBioAPISampleMFCDoc::OnEditProcess)
	ON_COMMAND(ID_BSPS_LOADBSP, &CBioAPISampleMFCDoc::OnBspsLoadbsp)
	ON_COMMAND(ID_BSPS_UNLOADBSP, &CBioAPISampleMFCDoc::OnBspsUnloadbsp)
	ON_COMMAND(ID_CAPTURE_CAPTUREFROMBSP, &CBioAPISampleMFCDoc::OnCaptureCapturefrombsp)
	ON_COMMAND(ID_EDIT_DELETEBIR, &CBioAPISampleMFCDoc::OnEditDeletebir)
	ON_COMMAND(ID_ENROLL_ENROLLFROMBSP, &CBioAPISampleMFCDoc::OnEnrollEnrollfrombsp)
END_MESSAGE_MAP()

// CBioAPISampleMFCDoc construction/destruction

CBioAPISampleMFCDoc::CBioAPISampleMFCDoc()
{
	// TODO: add one-time construction code here

}

CBioAPISampleMFCDoc::~CBioAPISampleMFCDoc()
{
	std::vector<CBioAPISampleMFCDoc::BSP>::iterator I;
	I = m_BSPs.begin();
	while (I != m_BSPs.end())
	{
		BioAPI_BSPUnload(&I->uuid, NULL, NULL);
		I++;
	}
	m_BSPs.clear();
}

BOOL CBioAPISampleMFCDoc::OnNewDocument()
{
	if (!CDocument::OnNewDocument())
		return FALSE;

	// TODO: add reinitialization code here
	// (SDI documents will reuse this document)

	return TRUE;
}

// CBioAPISampleMFCDoc serialization

void CBioAPISampleMFCDoc::Serialize(CArchive& ar)
{

	if (ar.IsStoring())
	{
		std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
		Lock();
		ar << (DWORD)m_BIRs.size();
		I = m_BIRs.begin();
		while (I != m_BIRs.end())
		{
			ar << I->Name;
			ar << I->Path;
			ar << (DWORD)I->status;

			ar.Write(&I->bir->Header, sizeof(BioAPI_BIR_HEADER));
			ar << I->bir->BiometricData.Length;
			ar.Write(I->bir->BiometricData.Data, I->bir->BiometricData.Length);
			ar << I->bir->SecurityBlock.Length;
			if (I->bir->SecurityBlock.Length) 
			{
				ar.Write(I->bir->SecurityBlock.Data, I->bir->SecurityBlock.Length);
			}
			I++;
		}
		Unlock();
	}
	else
	{
		DWORD size, i;

		ar >> size;

		Lock();

		for (i = 0; i < size; i++)
		{
			DWORD tmp;
			CBioAPISampleMFCDoc::BIR bir;

			bir.bir = (BioAPI_BIR *)malloc(sizeof(BioAPI_BIR));
			memset(bir.bir, 0, sizeof(BioAPI_BIR));
			ar >> bir.Name;
			ar >> bir.Path;
			ar >> tmp;
			bir.status = (CBioAPISampleMFCDoc::bir_status)tmp;
			ar.Read(&bir.bir->Header, sizeof(bir.bir->Header));
			ar >> bir.bir->BiometricData.Length;
			bir.bir->BiometricData.Data = malloc(bir.bir->BiometricData.Length);
			ar.Read(bir.bir->BiometricData.Data, bir.bir->BiometricData.Length);
			ar >> bir.bir->SecurityBlock.Length;
			if (bir.bir->SecurityBlock.Length)
			{
				bir.bir->SecurityBlock.Data = malloc(bir.bir->SecurityBlock.Length);
				ar.Read(bir.bir->SecurityBlock.Data, bir.bir->SecurityBlock.Length);
			}

			m_BIRs.push_back(bir);
		}

		Unlock();
		NeedToUpdate = true;
//		UpdateAllViews(NULL, 0,0);
	}
}

// CBioAPISampleMFCDoc diagnostics

#ifdef _DEBUG
void CBioAPISampleMFCDoc::AssertValid() const
{
	CDocument::AssertValid();
}

void CBioAPISampleMFCDoc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
}
#endif // _DEBUG

// CBioAPISampleMFCDoc commands

void CBioAPISampleMFCDoc::OnEditAddimage()
{
	CString fileName;
	CFileDialog load(TRUE, _T(".jpg"), 0, 4 | 2 | OFN_ALLOWMULTISELECT,
		_T("All supported formats (*.fir;*.iir;*.fcr)|*.fir;*.iir;*.fcr|")
//		_T("Windows BITMAP file (*.bmp)|*.bmp|")
//		_T("JPEG file (*.jpg)|*.jpg|")
//		_T("TIFF file (*.tif)|*.tif|")
//		_T("Portable Network file (*.png)|*.png|")
		_T("BDIF Face Image Record (*.fcr)|*.fcr|")
		_T("BDIF Iris Image Record (*.iir)|*.iir|")
		_T("BDIF Finger View Record (*.fir)|*.fir|")
		_T("Any file (*.*)|*.*|")
		_T("||")
		, this->GetRoutingFrame());

	load.GetOFN().lpstrFile = fileName.GetBuffer(1024 * 64);
	load.GetOFN().nMaxFile = 1024 * 64;

	if (load.DoModal() == IDOK)
	{
		POSITION pos;
		pos = load.GetStartPosition();
		CString Path;
		CFillBIR fill(this->GetRoutingFrame());

		while (pos)
		{
			TCHAR fname[256];
			TCHAR ext[256];

			Path = load.GetNextPathName(pos);
			_tsplitpath_s(Path, NULL, 0, NULL, 0, fname, 256, ext, 256);

			fill.BIR = CBioAPISampleMFCDoc::BIR(Path, CString(fname),(BioAPI_BIR *)malloc(sizeof(BioAPI_BIR)));
			memset(fill.BIR.bir, 0, sizeof(*fill.BIR.bir));
		
			FILE *fp = 0;
			_tfopen_s(&fp, Path, _T("rb"));
			if (fp)
			{
				long size;
				fseek(fp, 0, SEEK_END);
				size = ftell(fp);
				fseek(fp, 0, SEEK_SET);

				fill.BIR.bir->BiometricData.Length = size;
				fill.BIR.bir->BiometricData.Data = malloc(size);

				fread(fill.BIR.bir->BiometricData.Data, size, 1, fp);
				fclose(fp);
			}
			else
			{
				MessageBox(GetRoutingFrame()->m_hWnd, _T("Failed opening specified file.\nPlease check whether file exists and try again."), _T("Error"), MB_OK);
				fill.erase();
				break;
			}

			fill.BIR.bir->Header.Format.FormatOwner = CBEFF_BO_ISO_IEC_JTC_1_SC_37_BIOMETRICS;
			if (CString(ext) == _T(".fcr") )
			{
				fill.BIR.bir->Header.Format.FormatType = CBEFF_BDBFI_ISO_IEC_JTC_1_SC_37_BIOMETRICS_FACE_IMAGE;
				fill.BIR.bir->Header.FactorsMask = BioAPI_TYPE_FACIAL_FEATURES;
			}
			else if (CString(ext) == _T(".fir"))
			{
				fill.BIR.bir->Header.Format.FormatType = CBEFF_BDBFI_ISO_IEC_JTC_1_SC_37_BIOMETRICS_FINGER_IMAGE;
				fill.BIR.bir->Header.FactorsMask = BioAPI_TYPE_FINGERPRINT;
			}
			else if (CString(ext) == _T(".iir"))
			{
				fill.BIR.bir->Header.Format.FormatType = CBEFF_BDBFI_ISO_IEC_JTC_1_SC_37_BIOMETRICS_IRIS_IMAGE_RECTILINEAR;
				fill.BIR.bir->Header.FactorsMask = BioAPI_TYPE_IRIS;
			}
			else
			{
				MessageBox(this->GetRoutingFrame()->m_hWnd, _T("Unsupported format"), _T("Error"), MB_OK);
				break;
			}

			if (fill.all || fill.DoModal() == IDOK)
			{
				Lock();
				if ( fill.all )
					memcpy(&fill.BIR.bir->Header, &fill.header, sizeof(fill.BIR.bir->Header));
				m_BIRs.push_back(fill.BIR);
				NeedToUpdate = true;
				Unlock();
			}
			else
			{
				fill.erase();
				break;
			}
		}
		SetModifiedFlag();
		UpdateAllViews(NULL, 0,0);
	}
	fileName.ReleaseBuffer();
}

void CBioAPISampleMFCDoc::OnEditAddbir()
{
	CString fileName;
	CFileDialog load(TRUE, _T(".bir"), 0, 4 | 2 | OFN_ALLOWMULTISELECT
		, _T("Biometric Information Record (*.bir)|*.bir|")
		_T("Any file (*.*)|*.*|")
		_T("||")
		, this->GetRoutingFrame());

	load.GetOFN().lpstrFile = fileName.GetBuffer(1024 * 64);
	load.GetOFN().nMaxFile = 1024 * 64;

	if (load.DoModal() == IDOK)
	{
		POSITION pos;
		pos = load.GetStartPosition();
		CString Path;
		CFillBIR fill(this->GetRoutingFrame());

		while (pos)
		{
			Path = load.GetNextPathName(pos);

			FILE *fp = NULL;
			_tfopen_s(&fp, (LPCTSTR)Path, _T("rb"));
			if (fp)
			{
				TCHAR fname[256];
				BioAPI_BIR *bir;
				bir = (BioAPI_BIR *)malloc(sizeof(BioAPI_BIR));
				ReadTemplate(fp, bir);
				_tsplitpath_s(Path, NULL, 0, NULL, 0, fname, 256, NULL, 0);

				fill.BIR = CBioAPISampleMFCDoc::BIR(Path, CString(fname), bir);
				if (fill.all || fill.DoModal() == IDOK)
				{
					Lock();
					if ( fill.all )
						memcpy(&fill.BIR.bir->Header, &fill.header, sizeof(fill.BIR.bir->Header));
					m_BIRs.push_back(fill.BIR);
					NeedToUpdate = true;
					Unlock();
				}
				else if (!fill.all)
				{
					fill.erase();
					break;
				}

				fclose(fp);
			}
			else
			{
				MessageBox(GetRoutingFrame()->m_hWnd, _T("Error reading a file. Skipping.\n"), _T("Error"), MB_OK );
			}
		} 
		SetModifiedFlag();
		UpdateAllViews(NULL, 0,0);
	}

	fileName.ReleaseBuffer();
}

void CBioAPISampleMFCDoc::OnEditVerifymatch()
{
	CChooseBSP choose(this->GetRoutingFrame());
	CChooseBIR choose1(this->GetRoutingFrame());
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
	BOOL errors = FALSE;

	choose.bsps = &m_BSPs;

	if (choose.DoModal() == IDOK)
	{
		if (choose.bsp.handle)
		{
			choose1.birs = &m_BIRs;
			choose1.hBSP = choose.bsp.handle;

			if (choose1.DoModal() == IDOK)
			{
				Lock();
				I = m_BIRs.begin();
				while (I != m_BIRs.end())
				{
					if (I->selected)
					{
						BioAPI_FMR mfmr = 0x7FFFFFFF / 3, fmr;
						BioAPI_INPUT_BIR pBIR;
						BioAPI_INPUT_BIR rBIR;
						if (I->status != CBioAPISampleMFCDoc::HBIR)
						{
							pBIR.Form = BioAPI_FULLBIR_INPUT;
							pBIR.InputBIR.BIR = I->bir;
						}
						else
						{
							pBIR.InputBIR.BIRinBSP = &I->hbir;
							pBIR.Form = BioAPI_BIR_HANDLE_INPUT;
						}
						if (choose1.bir.status != CBioAPISampleMFCDoc::HBIR)
						{
							rBIR.Form = BioAPI_FULLBIR_INPUT;
							rBIR.InputBIR.BIR = choose1.bir.bir;
						}
						else
						{
							rBIR.InputBIR.BIRinBSP = &choose1.bir.hbir;
							rBIR.Form = BioAPI_BIR_HANDLE_INPUT;
						}
						BioAPI_BOOL result;
						if (BioAPI_VerifyMatch(choose.bsp.handle, mfmr, &pBIR, &rBIR, NULL, &result, &fmr, NULL) == BioAPI_OK)
						{
							CString str;
							str.Format(_T("VerifyMatch completed.\nVerification status: %s\nFMR Achieved: %ud"), 
								result ? _T("Succeeded") : _T("Failed"), (int)fmr);
							MessageBox(this->GetRoutingFrame()->m_hWnd, str, _T("Succeeded"), MB_OK);
						}
						else
						{
							errors = true;
						}
						break;
					}
					I++;
				}
				Unlock();
				if (errors)
				{
					MessageBox(this->GetRoutingFrame()->m_hWnd, _T("There were some errors."), _T("Error"), MB_OK);
				}
			}
		}
	}
	UpdateAllViews(NULL, 0,0);
}

void CBioAPISampleMFCDoc::OnEditIdentifymatch()
{
	CChooseBSP choose(this->GetRoutingFrame());
	CChooseBIR choose1(this->GetRoutingFrame());
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;

	choose.bsps = &m_BSPs;

	if (choose.DoModal() == IDOK)
	{
		if (choose.bsp.handle)
		{
			choose1.birs = &m_BIRs;
			choose1.hBSP = choose.bsp.handle;

			if (choose1.DoModal() == IDOK)
			{
				BioAPI_IDENTIFY_POPULATION Population;
				BioAPI_BIR_ARRAY_POPULATION BIRArray;
				BioAPI_CANDIDATE *Candidate;
				uint32_t numResults, i;
				BioAPI_FMR MaxFMRRequested = 0x7fffffff / 7;
				BioAPI_INPUT_BIR pBIR;

				if (choose1.bir.status != CBioAPISampleMFCDoc::HBIR)
				{
					pBIR.Form = BioAPI_FULLBIR_INPUT;
					pBIR.InputBIR.BIR = choose1.bir.bir;
				}
				else
				{
					pBIR.InputBIR.BIRinBSP = &choose1.bir.hbir;
					pBIR.Form = BioAPI_BIR_HANDLE_INPUT;
				}

				Lock();
				// count selected
				Population.Type = BioAPI_ARRAY_TYPE;
				Population.BIRs.BIRArray = &BIRArray;
				Population.BIRs.BIRArray->NumberOfMembers = 0;
				I = m_BIRs.begin();
				while (I != m_BIRs.end())
				{
					if (I->selected)
						Population.BIRs.BIRArray->NumberOfMembers++;
					I++;
				}

				if (Population.BIRs.BIRArray->NumberOfMembers == 0)
				{
					Unlock();
					return;
				}

				Population.BIRs.BIRArray->Members = new BioAPI_BIR[Population.BIRs.BIRArray->NumberOfMembers];

				// fill array
				I = m_BIRs.begin();
				i = 0;
				while (I != m_BIRs.end())
				{
					if (I->selected)
					{
						memcpy(&Population.BIRs.BIRArray->Members[i++], I->bir, sizeof(BioAPI_BIR));
					}
					I++;
				}
				Unlock();

				if ( BioAPI_IdentifyMatch(choose.bsp.handle, MaxFMRRequested, &pBIR, &Population, 
					Population.BIRs.BIRArray->NumberOfMembers, BioAPI_FALSE, Population.BIRs.BIRArray->NumberOfMembers,
					&numResults, &Candidate, -1) == BioAPI_OK )
				{
					CString str;
					CMultiselect show(this->GetRoutingFrame());
					show.sel = 0;
					for (uint32_t i = 0; i < numResults; i++)
					{
						CString str;
						str.Format(_T("%s, FMR=%d"), FindBIRByPointer(&Population.BIRs.BIRArray->Members[*Candidate[i].BIR.BIRInArray]),
							Candidate[i].FMRAchieved);
						show.items[i] = str;
					}
					show.DoModal();
					BioAPI_Free(Candidate);
				}
				else
				{
					MessageBox(this->GetRoutingFrame()->m_hWnd, _T("Error calling BioAPI_IdentifyMatch."), _T("Error"), MB_OK);
				}

				delete[] Population.BIRs.BIRArray->Members;
			}
		}
	}
	UpdateAllViews(NULL, 0,0);
}

void CBioAPISampleMFCDoc::OnEditVerify()
{
	CChooseBSP choose(this->GetRoutingFrame());
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
	BOOL errors = FALSE;

	choose.bsps = &m_BSPs;

	if (choose.DoModal() == IDOK)
	{
		if (choose.bsp.handle)
		{
			Lock();
			I = m_BIRs.begin();
			while (I != m_BIRs.end())
			{
				if (I->selected)
				{
					BioAPI_FMR mfmr = 0x7FFFFFFF / 3, fmr;
					BioAPI_INPUT_BIR pBIR;
					BioAPI_INPUT_BIR rBIR;
					if (I->status != CBioAPISampleMFCDoc::HBIR)
					{
						pBIR.Form = BioAPI_FULLBIR_INPUT;
						pBIR.InputBIR.BIR = I->bir;
					}
					else
					{
						pBIR.InputBIR.BIRinBSP = &I->hbir;
						pBIR.Form = BioAPI_BIR_HANDLE_INPUT;
					}
					BioAPI_BOOL result;
					if (BioAPI_Verify(choose.bsp.handle, mfmr, &rBIR, NULL, NULL, &result, &fmr, NULL, -1, NULL) == BioAPI_OK)
					{
						CString str;
						str.Format(_T("VerifyMatch completed.\nVerification status: %s\nFMR Achieved: %ud"), 
							result?_T("Succeeded"):_T("Failed"), (int)fmr);
						MessageBox(this->GetRoutingFrame()->m_hWnd, str, _T("Succeeded"), MB_OK);
					}
					else
					{
						errors = true;
					}
					break;
				}
				I++;
			}
			Unlock();
			if (errors)
			{
				MessageBox(this->GetRoutingFrame()->m_hWnd, _T("There were some errors."), _T("Error"), MB_OK);
			}
		}
	}
	UpdateAllViews(NULL, 0,0);
}

void CBioAPISampleMFCDoc::OnEditIdentify()
{
	CChooseBSP choose(this->GetRoutingFrame());
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;

	choose.bsps = &m_BSPs;

	if (choose.DoModal() == IDOK)
	{
		if (choose.bsp.handle)
		{
			BioAPI_IDENTIFY_POPULATION Population;
			BioAPI_BIR_ARRAY_POPULATION BIRArray;
			BioAPI_CANDIDATE *Candidate;
			uint32_t numResults, i;
			BioAPI_FMR MaxFMRRequested = 0x7fffffff / 7;

			Lock();
			// count selected
			Population.Type = BioAPI_ARRAY_TYPE;
			Population.BIRs.BIRArray = &BIRArray;
			Population.BIRs.BIRArray->NumberOfMembers = 0;
			I = m_BIRs.begin();
			while (I != m_BIRs.end())
			{
				if (I->selected)
					Population.BIRs.BIRArray->NumberOfMembers++;
				I++;
			}

			if (Population.BIRs.BIRArray->NumberOfMembers == 0)
			{
				Unlock();
				return;
			}

			Population.BIRs.BIRArray->Members = new BioAPI_BIR[Population.BIRs.BIRArray->NumberOfMembers];

			// fill array
			I = m_BIRs.begin();
			i = 0;
			while (I != m_BIRs.end())
			{
				if (I->selected)
				{
					memcpy(&Population.BIRs.BIRArray->Members[i++], I->bir, sizeof(BioAPI_BIR));
				}
				I++;
			}
			Unlock();

			if (BioAPI_Identify(choose.bsp.handle, MaxFMRRequested, NULL, &Population, 
				Population.BIRs.BIRArray->NumberOfMembers, BioAPI_FALSE, Population.BIRs.BIRArray->NumberOfMembers,
				&numResults, &Candidate, -1, NULL) == BioAPI_OK)
			{
				CString str;
				CMultiselect show(this->GetRoutingFrame());
				show.sel = 0;
				for (uint32_t i = 0; i < numResults; i++)
				{
					CString str;
					str.Format(_T("%s, FMR=%d"), FindBIRByPointer(&Population.BIRs.BIRArray->Members[*Candidate[i].BIR.BIRInArray]),
						Candidate[i].FMRAchieved);
					show.items[i] = str;
				}
				show.DoModal();
				BioAPI_Free(Candidate);
			}
			else
			{
				MessageBox(this->GetRoutingFrame()->m_hWnd, _T("Error calling BioAPI_IdentifyMatch."), _T("Error"), MB_OK);
			}
			delete[] Population.BIRs.BIRArray->Members;
		}
	}
	UpdateAllViews(NULL, 0,0);
}

void CBioAPISampleMFCDoc::OnEditProcess()
{
	CFrameWnd *frame = this->GetRoutingFrame();
	CChooseBSP choose(frame);
	BOOL errors = FALSE;

	choose.bsps = &m_BSPs;

	if (choose.DoModal() == IDOK)
	{
		if (choose.bsp.handle)
		{
			CControlBar *cb = NULL;

			if (frame)
			{
				cb = frame->GetControlBar(AFX_IDW_STATUS_BAR);
				cb->SetWindowText(_T("Processing"));
			}

			Lock();
			std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
			I = m_BIRs.begin();
			while (I != m_BIRs.end())
			{
				if (I->selected)
				{
					BioAPI_INPUT_BIR iBIR;
					iBIR.Form = BioAPI_FULLBIR_INPUT;
					iBIR.InputBIR.BIR = I->bir;

					if (cb)
						cb->SetWindowText(CString(_T("Processing: ")) + I->Name);

					if (BioAPI_Process(choose.bsp.handle, &iBIR, NULL, &I->hbir) == BioAPI_OK)
					{
						BioAPI_BIR bir;
						I->status = CBioAPISampleMFCDoc::HBIR;
						// attempt to get BIR thus invalidating the handle
						if ( BioAPI_GetBIRFromHandle(choose.bsp.handle, I->hbir, &bir) == BioAPI_OK )
						{
							void* data;
							data = malloc(bir.BiometricData.Length);
							memcpy(data, bir.BiometricData.Data, bir.BiometricData.Length);
							free(I->bir->BiometricData.Data);
							BioAPI_Free(bir.BiometricData.Data);
							bir.BiometricData.Data = data;
							memcpy(I->bir, &bir, sizeof(BioAPI_BIR));
							I->status = CBioAPISampleMFCDoc::PROCESSED;
						}
						else
						{
							errors = true;
						}
					}
					else
					{
						errors = true;
					}
				}
				I++;
			}
			Unlock();

			if (cb)
				cb->SetWindowText(_T("Ready"));

			if (errors && frame)
			{
				MessageBox(frame->m_hWnd, _T("There were some errors."), _T("Error"), MB_OK);
			}
		}
	}
	UpdateAllViews(NULL, 0,0);
}

void CBioAPISampleMFCDoc::OnBspsLoadbsp()
{
	// TODO: Add your command handler code here
	CLoadBSP load(this->GetRoutingFrame());

	if (load.DoModal() == IDOK)
	{
		if (load.loadedBSP)
		{
			BioAPI_UNIT_LIST_ELEMENT u[4];
			BioAPI_HANDLE hBSP;
			u[0].UnitCategory = BioAPI_CATEGORY_ARCHIVE;
			u[1].UnitCategory = BioAPI_CATEGORY_MATCHING_ALG;
			u[2].UnitCategory = BioAPI_CATEGORY_PROCESSING_ALG;
			u[3].UnitCategory = BioAPI_CATEGORY_SENSOR;
			u[0].UnitId = load.uids[2];
			u[1].UnitId = load.uids[3];
			u[2].UnitId = load.uids[1];
			u[3].UnitId = load.uids[0];

//			if ( BioAPI_OK==BioAPI_BSPLoad(&load.loadedUUID, NULL, NULL) )
//			{
				if (BioAPI_OK == BioAPI_BSPAttach(&load.loadedUUID, 0x20, u, 4, &hBSP))
				{
					CBioAPISampleMFCDoc::BSP bsp;
					bsp.handle = hBSP;
					memcpy(bsp.uuid, load.loadedUUID, sizeof(bsp.uuid));
					memcpy(bsp.ID, u, sizeof(bsp.ID));
					memcpy(&bsp.schema, &load.sSchema[load.bspIndex], sizeof(bsp.schema));
					m_BSPs.push_back(bsp);
				}
				else
				{
					MessageBox(this->GetRoutingFrame()->m_hWnd, _T("Error attaching BSP."), _T("Error"), MB_OK);
				}
//			}
//			else
//			{
//				MessageBox(this->GetRoutingFrame()->m_hWnd, _T("Error loading BSP."), _T("Error"), MB_OK);
//			}
		}
	}

	load.cleanup();
}

void CBioAPISampleMFCDoc::OnBspsUnloadbsp()
{
	CChooseBSP choose(this->GetRoutingFrame());

	choose.bsps = &m_BSPs;

	if (choose.DoModal() == IDOK)
	{
		BioAPI_BSPDetach(choose.bsp.handle);
		std::vector<CBioAPISampleMFCDoc::BSP>::iterator I;
		I = m_BSPs.begin();
		while (I != m_BSPs.end())
		{
			if (I->handle == choose.bsp.handle)
			{
				BioAPI_BSPUnload(&I->uuid, NULL, NULL);
				m_BSPs.erase(I);
				break;
			}
			I++;
		}
	}
}

BOOL CBioAPISampleMFCDoc::OnSaveDocument(LPCTSTR lpszPathName)
{
	// TODO: Add your specialized code here and/or call the base class

	return CDocument::OnSaveDocument(lpszPathName);
}

BOOL CBioAPISampleMFCDoc::OnOpenDocument(LPCTSTR lpszPathName)
{
	if (!CDocument::OnOpenDocument(lpszPathName))
		return FALSE;

	// TODO:  Add your specialized creation code here

	return TRUE;
}

void CBioAPISampleMFCDoc::OnCloseDocument()
{
	// TODO: Add your specialized code here and/or call the base class

	CDocument::OnCloseDocument();
}

void CBioAPISampleMFCDoc::DeleteContents()
{
	// TODO: Add your specialized code here and/or call the base class
	Lock();
	std::vector<BIR>::iterator I;
	I = m_BIRs.begin();
	while (I != m_BIRs.end())
	{
		I->release();
		I++;
	}
	m_BIRs.clear();
	Unlock();

	CDocument::DeleteContents();
}

void CBioAPISampleMFCDoc::OnCaptureCapturefrombsp()
{
	CChooseBSP choose(this->GetRoutingFrame());
	CCapture capture(this->GetRoutingFrame());

	Lock();
	choose.bsps = &m_BSPs;

	if (choose.DoModal() == IDOK)
	{
		capture.hBSP = choose.bsp.handle;
		capture.BIRs = &m_BIRs;

		capture.DoModal();

		NeedToUpdate = true;
		UpdateAllViews(NULL, 0,0);
	}
	Unlock();
}

void CBioAPISampleMFCDoc::OnEnrollEnrollfrombsp()
{
}

void CBioAPISampleMFCDoc::OnEditEditbir()
{
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
	CFillBIR fill(this->GetRoutingFrame());
	I = m_BIRs.begin();
	while (I != m_BIRs.end())
	{
		if (I->selected)
		{
			fill.BIR = *I;

			if (fill.all || fill.DoModal() == IDOK)
			{
				Lock();
				if (fill.all)
					memcpy(&fill.BIR.bir->Header, &fill.header, sizeof(fill.BIR.bir->Header));
				memcpy(&I->bir->Header, &fill.BIR.bir->Header, sizeof(fill.BIR.bir->Header));
				Unlock();
			}
			else if (!fill.all)
			{
				break;
			}
		}

		I++;
	}
	UpdateAllViews(NULL, 0,0);
}

void CBioAPISampleMFCDoc::OnEditDeletebir()
{
	Lock();
	std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
	I = m_BIRs.begin();
	while (I != m_BIRs.end())
	{
		if (I->selected)
		{
			I->release();
			m_BIRs.erase(I);
			I = m_BIRs.begin();
		}
		else
			I++;
	}
	Unlock();
	UpdateAllViews(NULL, 0,0);
}

