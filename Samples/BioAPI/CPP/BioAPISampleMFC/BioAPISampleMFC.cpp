// BioAPISample.MFC.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "BioAPISampleMFC.h"
#include "MainFrm.h"

#include "BioAPISampleMFCDoc.h"
#include "BioAPISampleMFCView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

// CBioAPISampleMFCApp

BEGIN_MESSAGE_MAP(CBioAPISampleMFCApp, CWinApp)
	ON_COMMAND(ID_APP_ABOUT, &CBioAPISampleMFCApp::OnAppAbout)
	// Standard file based document commands
	ON_COMMAND(ID_FILE_NEW, &CWinApp::OnFileNew)
	ON_COMMAND(ID_FILE_OPEN, &CWinApp::OnFileOpen)
END_MESSAGE_MAP()

// CBioAPISampleMFCApp construction

CBioAPISampleMFCApp::CBioAPISampleMFCApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
}

// The one and only CBioAPISampleMFCApp object

CBioAPISampleMFCApp theApp;

// CBioAPISampleMFCApp initialization

BOOL CBioAPISampleMFCApp::InitInstance()
{
	// InitCommonControlsEx() is required on Windows XP if an application
	// manifest specifies use of ComCtl32.dll version 6 or later to enable
	// visual styles.  Otherwise, any window creation will fail.
	INITCOMMONCONTROLSEX InitCtrls;
	InitCtrls.dwSize = sizeof(InitCtrls);
	// Set this to include all the common control classes you want to use
	// in your application.
	InitCtrls.dwICC = ICC_WIN95_CLASSES;
	InitCommonControlsEx(&InitCtrls);

	CWinApp::InitInstance();

	// Standard initialization
	// If you are not using these features and wish to reduce the size
	// of your final executable, you should remove from the following
	// the specific initialization routines you do not need
	// Change the registry key under which our settings are stored
	// TODO: You should modify this string to be something appropriate
	// such as the name of your company or organization
	SetRegistryKey(_T("Local AppWizard-Generated Applications"));
	LoadStdProfileSettings(4);  // Load standard INI file options (including MRU)
	// Register the application's document templates.  Document templates
	//  serve as the connection between documents, frame windows and views
	CSingleDocTemplate* pDocTemplate;
	pDocTemplate = new CSingleDocTemplate(
		IDR_MAINFRAME,
		RUNTIME_CLASS(CBioAPISampleMFCDoc),
		RUNTIME_CLASS(CMainFrame),       // main SDI frame window
		RUNTIME_CLASS(CBioAPISampleMFCView));
	if (!pDocTemplate)
		return FALSE;
	AddDocTemplate(pDocTemplate);

	// Enable DDE Execute open
	EnableShellOpen();
	RegisterShellFileTypes(TRUE);

	// Parse command line for standard shell commands, DDE, file open
	CCommandLineInfo cmdInfo;
	ParseCommandLine(cmdInfo);

	BioAPI_RETURN err = BioAPI_Init(0x20);
	if (err != BioAPI_OK)
	{
		TRACE(_T("Could not initialize BioAPI framework. Error code: %d\n"), err);
		return FALSE;      // fail to create
	}

	// Dispatch commands specified on the command line.  Will return FALSE if
	// app was launched with /RegServer, /Register, /Unregserver or /Unregister.
	if (!ProcessShellCommand(cmdInfo))
		return FALSE;

	// The one and only window has been initialized, so show and update it
	m_pMainWnd->ShowWindow(SW_SHOW);
	m_pMainWnd->UpdateWindow();
	// call DragAcceptFiles only if there's a suffix
	//  In an SDI app, this should occur after ProcessShellCommand
	// Enable drag/drop open
	m_pMainWnd->DragAcceptFiles();
	return TRUE;
}

int CBioAPISampleMFCApp::ExitInstance()
{
	// TODO: Add your specialized code here and/or call the base class
	if (BioAPI_Terminate() != BioAPI_OK)
	{
		TRACE0("Failed to uninitialize BioAPI\n");
	}

	return CWinApp::ExitInstance();
}

// CAboutDlg dialog used for App About

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	enum { IDD = IDD_ABOUTBOX };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

// Implementation
protected:
	DECLARE_MESSAGE_MAP()
public:
	CString m_FrameworkInfo;
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
, m_FrameworkInfo(_T(""))
{
	BioAPI_FRAMEWORK_SCHEMA FrameworkSchema;
	TCHAR printableUUID[BioAPI_PRINTABLE_UUID_LENGTH];
	TCHAR printableVersion[BioAPI_PRINTABLE_VERSION_LENGTH];
	TCHAR tmp[1024];

	BioAPI_GetFrameworkInfo(&FrameworkSchema);
	BioAPI_GetPrintableUUID(&FrameworkSchema.FrameworkUuid, printableUUID);

	_stprintf_s (tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Framework UUID: %s\r\n"), (char*)printableUUID);
	m_FrameworkInfo += tmp;
	utf8sprintf_s (tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Description: %s\r\n"), (char*)FrameworkSchema.FwDescription);
	m_FrameworkInfo += tmp;
	utf8sprintf_s (tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Module path: %s\r\n"), (char*)FrameworkSchema.Path);
	m_FrameworkInfo += tmp;

	BioAPI_GetPrintableVersion(&FrameworkSchema.SpecVersion, printableVersion);
	utf8sprintf_s (tmp, sizeof(tmp)/sizeof(tmp[0]), _T("BioAPI Specification version: %s\r\n"), (char*)printableVersion);
	m_FrameworkInfo += tmp;

	utf8sprintf_s(tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Product Version: %s\r\n"), (char*)FrameworkSchema.ProductVersion);
	m_FrameworkInfo += tmp;
	utf8sprintf_s(tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Vendor: %s\r\n"), (char*)FrameworkSchema.Vendor);
	m_FrameworkInfo += tmp;
	BioAPI_GetPrintableUUID(&FrameworkSchema.FwPropertyId, printableUUID);
	_stprintf_s (tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Property UUID: %s\r\n"), (char*)printableUUID);
	m_FrameworkInfo += tmp;

	uint32_t num;
	BioAPI_BSP_SCHEMA *bsps;
	BioAPI_BFP_SCHEMA *bfps;

	BioAPI_EnumBSPs(&bsps, &num);
	BioAPI_Free(bsps);
	_stprintf_s (tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Num of installed BSPs: %d\r\n"), num);
	m_FrameworkInfo += tmp;

	BioAPI_EnumBFPs(&bfps, &num);
	BioAPI_Free(bfps);
	_stprintf_s (tmp, sizeof(tmp)/sizeof(tmp[0]), _T("Num of installed BFPs: %d\r\n"), num);
	m_FrameworkInfo += tmp;

	//BioAPI_DATA FwProperty;       // Address and length of a memory buffer containing the Framework property..
	BioAPI_Free(FrameworkSchema.Path);
	BioAPI_Free(FrameworkSchema.FwProperty.Data);

}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Text(pDX, IDC_EDIT1, m_FrameworkInfo);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
END_MESSAGE_MAP()

// App command to run the dialog
void CBioAPISampleMFCApp::OnAppAbout()
{
	CAboutDlg aboutDlg;
	aboutDlg.DoModal();
}

