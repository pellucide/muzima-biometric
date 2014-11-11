// LoadBSP.cpp : implementation file
//

#include "stdafx.h"
#include "BioAPISampleMFC.h"
#include "LoadBSP.h"

class bioapi_uuid
{
public:
	BioAPI_UUID uuid;
public:
	bioapi_uuid() {}
	bioapi_uuid(const bioapi_uuid&u) { *this = u; }
	bioapi_uuid(const BioAPI_UUID* u)
	{
		memcpy(uuid, *u, sizeof(uuid));
	}
	bioapi_uuid& operator=(const bioapi_uuid& u)
	{
		memcpy(uuid, u.uuid, sizeof(uuid));
		return *this;
	}
};

static std::vector<bioapi_uuid> sg_uuid;

bool is_loaded(const BioAPI_UUID* uuid)
{
	std::vector<bioapi_uuid>::iterator I;
	I = sg_uuid.begin();

	while (I != sg_uuid.end())
	{
		if (memcmp(*uuid, I->uuid, sizeof(BioAPI_UUID)) == 0)
			return true;
		I++;
	}

	sg_uuid.push_back(bioapi_uuid(uuid));
	return false;
}

// Application's module event handler. Being called by loaded BSPs to notify
// about unit events, i.e. attchment of a unit for biometric operations.
// This event handler can be called either syncronously (i.e. during ModuleLoad)
// or asynchronously on a separate thread. Keep this in mind when communicating 
// with the GUI thread/objects for proper sysncronization and objects passing.
BioAPI_RETURN BioAPI callback(
					const BioAPI_UUID		*BSPUuid,
					BioAPI_UNIT_ID			UnitId,		
					void * AppNotifyCallbackCtx, // the main windows handle
					const BioAPI_UNIT_SCHEMA	*UnitSchema,
					BioAPI_EVENT			 eventType)
{
	CLoadBSP* dlg = (CLoadBSP*)AppNotifyCallbackCtx;
	TCHAR tmp[1024];
	TCHAR tmp1[64];

	if (eventType != BioAPI_NOTIFY_INSERT && eventType != BioAPI_NOTIFY_REMOVE)
		return BioAPI_OK;

	_stprintf_s(tmp1, sizeof(tmp1)/sizeof(tmp1[0]), _T("%d: %%s"), UnitSchema->UnitId);
	utf8sprintf_s(tmp, sizeof(tmp)/sizeof(tmp[0]), tmp1, (char*)UnitSchema->VendorInformation); ///<  The version string of the BSP software.

	if (eventType == BioAPI_NOTIFY_INSERT)
	{
		switch (UnitSchema->UnitCategory)
		{
		case BioAPI_CATEGORY_ARCHIVE:
			dlg->m_ArchiveUnits.AddString(tmp);
			break;
		case BioAPI_CATEGORY_MATCHING_ALG:
			dlg->m_MatchingUnits.AddString(tmp);
			break;
		case BioAPI_CATEGORY_PROCESSING_ALG:
			dlg->m_ProcessingUnits.AddString(tmp);
			break;
		case BioAPI_CATEGORY_SENSOR:
			dlg->m_CaptureUnits.AddString(tmp);
			break;
		}
	}

	UnitId;
	BSPUuid;
	return BioAPI_OK;
}

// CLoadBSP dialog

IMPLEMENT_DYNAMIC(CLoadBSP, CDialog)

CLoadBSP::CLoadBSP(CWnd* pParent /*=NULL*/)
	: CDialog(CLoadBSP::IDD, pParent)
{
	memset(uids, 0xff, sizeof(uids));
}

CLoadBSP::~CLoadBSP()
{
}

void CLoadBSP::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_LIST1, m_CaptureUnits);
	DDX_Control(pDX, IDC_LIST2, m_ProcessingUnits);
	DDX_Control(pDX, IDC_LIST3, m_ArchiveUnits);
	DDX_Control(pDX, IDC_LIST4, m_MatchingUnits);
	DDX_Control(pDX, IDC_COMBO1, m_BSPs);
	DDX_Control(pDX, IDC_LIST5, m_Description);
}

BEGIN_MESSAGE_MAP(CLoadBSP, CDialog)
	ON_CBN_SELCHANGE(IDC_COMBO1, &CLoadBSP::OnCbnSelchangeCombo1)
	ON_LBN_SELCHANGE(IDC_LIST1, &CLoadBSP::OnLbnSelchangeList1)
	ON_LBN_SELCHANGE(IDC_LIST2, &CLoadBSP::OnLbnSelchangeList2)
	ON_LBN_SELCHANGE(IDC_LIST3, &CLoadBSP::OnLbnSelchangeList3)
	ON_LBN_SELCHANGE(IDC_LIST4, &CLoadBSP::OnLbnSelchangeList4)
END_MESSAGE_MAP()

// CLoadBSP message handlers

BOOL CLoadBSP::OnInitDialog()
{
	CDialog::OnInitDialog();
	TCHAR printableUUID[BioAPI_PRINTABLE_UUID_LENGTH];
	TCHAR description[512];
	BioAPI_RETURN bioReturn;

	// TODO:  Add extra initialization here
	bioReturn = BioAPI_EnumBSPs(&sSchema, &numElements);
	if(BioAPI_OK == bioReturn)
	{
		for (uint32_t i = 0; i < numElements; i++)
		{
			CString str;
			BioAPI_GetPrintableUUID(&sSchema[i].BSPUuid, printableUUID);
			utf8sprintf_s(description, sizeof(description) / sizeof(description[0]), _T("%s"), (char *)sSchema[i].BSPDescription);
			str.Format(_T("%s %s"), printableUUID, description);
			m_BSPs.AddString(str);
		}
	}

	loadedBSP = BioAPI_INVALID_HANDLE;

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

void CLoadBSP::cleanup()
{
	for (uint32_t i = 0; i < numElements; i++)
	{
		BioAPI_Free(sSchema[i].Path);
		BioAPI_Free(sSchema[i].BSPSupportedFormats);
	}
	BioAPI_Free(sSchema);
	numElements = 0;
	sSchema = NULL;

//	if ( loadedBSP )
//	{
//		BioAPI_BSPUnload(&loadedUUID, callback, this);
//		loadedBSP = false;
//	}
}

void CLoadBSP::OnCbnSelchangeCombo1()
{
	TCHAR tmp[1024];
	TCHAR printableUUID[BioAPI_PRINTABLE_UUID_LENGTH];
	TCHAR printableVersion[BioAPI_PRINTABLE_VERSION_LENGTH];

	m_Description.ResetContent();
	m_CaptureUnits.ResetContent();
	m_ProcessingUnits.ResetContent();
	m_ArchiveUnits.ResetContent();
	m_MatchingUnits.ResetContent();

	uint32_t i = this->bspIndex = m_BSPs.GetCurSel();

	if (!(i >= 0 && i < numElements))
		return ;

	BioAPI_GetPrintableUUID(&sSchema[i].BSPUuid, printableUUID);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("BSP UUID: %s"), printableUUID);
	m_Description.AddString(tmp);
	utf8sprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Description: %s"), (char *)sSchema[i].BSPDescription); ///<  description of the BSP
	m_Description.AddString(tmp);
	utf8sprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Module path: %s"), (char *)sSchema[i].Path); ///<  Path of the BSP file, including the filename. The path may be a URL.
	m_Description.AddString(tmp);

	BioAPI_GetPrintableVersion(&sSchema[i].SpecVersion, printableVersion);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("BSP Specification version: %s"), printableVersion);
	m_Description.AddString(tmp);

	utf8sprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Product version: %s"), (char *)sSchema[i].ProductVersion); ///<  The version string of the BSP software.
	m_Description.AddString(tmp);
	utf8sprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Vendor: %s"), (char *)sSchema[i].Vendor); ///<  the name of the BSP vendor.
	m_Description.AddString(tmp);

	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Supported formats:"));
	m_Description.AddString(tmp);
	for (uint32_t j = 0; j < sSchema[i].NumSupportedFormats; j++)
	{
		_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("    {Owner: %i, Type %i}"),
			(int) sSchema[i].BSPSupportedFormats[j].FormatOwner,
			(int) sSchema[i].BSPSupportedFormats[j].FormatType);
		m_Description.AddString(tmp);
	}
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Factors Mask: %08x"), sSchema[i].FactorsMask); ///<  biometric types are supported by the BSP.
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Operations: %08x"), sSchema[i].Operations); ///<  operations are supported by the BSP.
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Options: %08x"), sSchema[i].Options); ///<  options are supported by the BSP.
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Payload Policy: %08x"), sSchema[i].PayloadPolicy); ///<  minimum FMR value to release the payload after successful verification.
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Max Payload size: %i"), sSchema[i].MaxPayloadSize); ///<  Maximum payload size (in bytes) that the BSP can accept.
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Default verify timeout: %i"), sSchema[i].DefaultVerifyTimeout); ///<  Milliseconds when no timeout is specified by the application.
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Default identify timeout: %i"), sSchema[i].DefaultIdentifyTimeout);
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Default Capture timeout: %i"), sSchema[i].DefaultCaptureTimeout);
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Default Enroll timeout: %i"), sSchema[i].DefaultEnrollTimeout);
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Default Calibrate timeout: %i"), sSchema[i].DefaultCalibrateTimeout);
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Max BSP Db Size: %i"), sSchema[i].MaxBSPDbSize); ///<  Maximum size of a BSP-controlled BIR database.
	m_Description.AddString(tmp);
	_stprintf_s(tmp, sizeof(tmp) / sizeof(tmp[0]), _T("Max Identity: %i"), sSchema[i].MaxIdentify); ///<  Largest population. Unlimited = FFFFFFFF.
	m_Description.AddString(tmp);

	m_ArchiveUnits.AddString(_T("-1: Don't include"));
	m_MatchingUnits.AddString(_T("-1: Don't include"));
	m_ProcessingUnits.AddString(_T("-1: Don't include"));
	m_CaptureUnits.AddString(_T("-1: Don't include"));
	m_ArchiveUnits.AddString(_T("0: Don't care"));
	m_MatchingUnits.AddString(_T("0: Don't care"));
	m_ProcessingUnits.AddString(_T("0: Don't care"));
	m_CaptureUnits.AddString(_T("0: Don't care"));

	if (!is_loaded(&sSchema[i].BSPUuid))
		BioAPI_BSPLoad(&sSchema[i].BSPUuid, NULL, NULL);

	BioAPI_UNIT_SCHEMA *uSchema = 0;
	uint32_t NumOfElements = 0, j = 0;
	BioAPI_QueryUnits(&sSchema[i].BSPUuid, &uSchema, &NumOfElements);

	for (j = 0; j < NumOfElements; j++)
	{
		callback(&sSchema[i].BSPUuid, uSchema[j].UnitId, this, uSchema + j, BioAPI_NOTIFY_INSERT);
	}

//	BioAPI_BSPUnload(&sSchema[i].BSPUuid, NULL, NULL);

	m_ArchiveUnits.SetCurSel(1);
	m_MatchingUnits.SetCurSel(1);
	m_ProcessingUnits.SetCurSel(1);
	m_CaptureUnits.SetCurSel(1);
	OnLbnSelchangeList1();
	OnLbnSelchangeList2();
	OnLbnSelchangeList3();
	OnLbnSelchangeList4();

	BioAPI_Free(uSchema);
	memcpy(loadedUUID, sSchema[i].BSPUuid, sizeof(loadedUUID));
	loadedBSP = true;
}

void CLoadBSP::OnLbnSelchangeList1()
{ // sensor units
	int id;
	CString str;
	int sel = m_CaptureUnits.GetCurSel();
	if (sel == -1)
	{
		uids[0] = BioAPI_DONT_INCLUDE;
		return ;
	}
	m_CaptureUnits.GetText(sel, str);
	_stscanf_s(str, _T("%d:"), &id);
	uids[0] = id;
}

void CLoadBSP::OnLbnSelchangeList2()
{ // processing units
	int id;
	CString str;
	int sel = m_ProcessingUnits.GetCurSel();
	if (sel == -1)
	{
		uids[1] = BioAPI_DONT_INCLUDE;
		return ;
	}
	m_ProcessingUnits.GetText(sel, str);
	_stscanf_s(str, _T("%d:"), &id);
	uids[1] = id;
}

void CLoadBSP::OnLbnSelchangeList3()
{ // archive units
	int id;
	CString str;
	int sel = m_ArchiveUnits.GetCurSel();
	if (sel == -1)
	{
		uids[2] = BioAPI_DONT_INCLUDE;
		return ;
	}
	m_ArchiveUnits.GetText(sel, str);
	_stscanf_s(str, _T("%d:"), &id);
	uids[2] = id;
}

void CLoadBSP::OnLbnSelchangeList4()
{ // matching units
	int id;
	CString str;
	int sel = m_MatchingUnits.GetCurSel();
	if (sel == -1)
	{
		uids[3] = BioAPI_DONT_INCLUDE;
		return ;
	}
	m_MatchingUnits.GetText(sel, str);
	_stscanf_s(str, _T("%d:"), &id);
	uids[3] = id;
}
