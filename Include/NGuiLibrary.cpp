#ifndef N_GUI_LIBRARY_CPP_INCLUDED
#define N_GUI_LIBRARY_CPP_INCLUDED

#include <NCoreLibrary.cpp>
#include <NMediaLibrary.cpp>

#include <NGui.hpp>

namespace Neurotec { namespace Gui
{

#ifdef N_FRAMEWORK_WX

BEGIN_EVENT_TABLE(wxNView, wxScrolledWindow)
	EVT_ERASE_BACKGROUND(wxNView::OnEraseBackGround)
	EVT_PAINT(wxNView::OnPaint)
	EVT_LEFT_DOWN(wxNView::OnLeftDown)
	EVT_MOTION(wxNView::OnMouseMove)
	EVT_LEFT_UP(wxNView::OnLeftUp)
	EVT_MOUSEWHEEL(wxNView::OnMouseWheel)
	EVT_RIGHT_DOWN(wxNView::OnRightDown)
	EVT_MENU_RANGE(ID_ZOOM_FIT, ID_ZOOM_200, wxNView::OnZoom)
END_EVENT_TABLE()

#endif

}}

#endif // !N_GUI_LIBRARY_CPP_INCLUDED
