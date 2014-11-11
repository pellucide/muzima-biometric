// BioAPISample.MFCDoc.h : interface of the CBioAPISampleMFCDoc class
//

#pragma once
#include "LoadBSP.h"

class CBioAPISampleMFCDoc : public CDocument
{
protected: // create from serialization only
	CBioAPISampleMFCDoc();
	DECLARE_DYNCREATE(CBioAPISampleMFCDoc)

// Attributes
public:
	class BSP
	{
	public:
		BSP() : handle(0)
		{
		}
		BSP(BioAPI_HANDLE h, BioAPI_UUID* uuid, BioAPI_BSP_SCHEMA* schema, BioAPI_UNIT_LIST_ELEMENT *u)
		{
			memcpy(this->uuid, uuid , sizeof(this->uuid));
			memcpy(ID, u, sizeof(ID));
			memcpy(&this->schema, schema, sizeof(this->schema));
			h = handle;
		}
		BSP(const BSP& b)
		{
			*this = b;
		}
		~BSP()
		{
		}

		BSP& operator=(const BSP& b)
		{
			this->handle = b.handle;
			memcpy(this->uuid, b.uuid , sizeof(this->uuid));
			memcpy(this->ID, b.ID, sizeof(this->ID));
			memcpy(&this->schema, &b.schema, sizeof(this->schema));

			return *this;
		}

		BioAPI_UUID uuid;
		BioAPI_HANDLE handle;
		BioAPI_UNIT_LIST_ELEMENT ID[4];
		BioAPI_BSP_SCHEMA schema;

		void cleanup()
		{
			BioAPI_BSPDetach(handle);
			handle = 0;
		}
	};

	enum bir_status
	{
		NOBIR,
		LOADED,
		CAPTURED,
		HBIR,
		PROCESSED
	};

	class BIR
	{
	public:
		BioAPI_BIR* bir;
		BioAPI_BIR_HANDLE hbir;
		CString Path;
		CString Name;
		bir_status status;
		BOOL selected;
	public:
		BIR() : bir(0),status(NOBIR),selected(FALSE) {}
		BIR(const CString& Path, const CString& Name, BioAPI_BIR* bir): Path(Path), Name(Name), bir(bir),status(LOADED),selected(FALSE) {}
		BIR(const BIR& bir)
		{
			*this = bir;
		}
		~BIR() {}

		BIR& operator=(const BIR* bir)
		{
			this->bir = bir->bir;
			this->Path = bir->Path;
			this->Name = bir->Name;
			this->selected = bir->selected;

			return *this;
		}

		void release()
		{
			free(bir->BiometricData.Data);
			free(bir->SecurityBlock.Data);
			free(bir);
			bir = 0;
			//if ( this->status==HBIR )
			//{
			//	BioAPI_FreeBIRHandle();
			//}
		}
	};

	CMutex m_Lock;
	std::vector<BSP> m_BSPs;
	std::vector<BIR> m_BIRs;
	bool NeedToUpdate;

	void Lock() { m_Lock.Lock(); }
	void Unlock() {m_Lock.Unlock(); }

// Operations
public:
	BioAPI_RETURN LoadBSP(BioAPI_UUID* uuid);
	BioAPI_RETURN LoadBSP(const char* uuid);
	BioAPI_RETURN AttachUnits(const int* units/*[4]*/);

	CString FindBIRByPointer(BioAPI_BIR* bir)
	{
		std::vector<CBioAPISampleMFCDoc::BIR>::iterator I;
		I = m_BIRs.begin();
		while ( I!=m_BIRs.end() )
		{
			if ( I->bir->BiometricData.Data==bir->BiometricData.Data ) 
			{
				return I->Name;
			}
			I++;
		}
		return _T("N/A");
	}
// Overrides
public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);

// Implementation
public:
	virtual ~CBioAPISampleMFCDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnEditAddimage();
	afx_msg void OnEditAddbir();
	afx_msg void OnEditEditbir();
	afx_msg void OnEditVerifymatch();
	afx_msg void OnEditIdentifymatch();
	afx_msg void OnEditVerify();
	afx_msg void OnEditIdentify();
	afx_msg void OnEditProcess();
	afx_msg void OnBspsLoadbsp();
	afx_msg void OnBspsUnloadbsp();
	virtual BOOL OnSaveDocument(LPCTSTR lpszPathName);
	virtual BOOL OnOpenDocument(LPCTSTR lpszPathName);
	virtual void OnCloseDocument();
	virtual void DeleteContents();
	afx_msg void OnCaptureCapturefrombsp();
	afx_msg void OnEditDeletebir();
	afx_msg void OnEnrollEnrollfrombsp();
};

