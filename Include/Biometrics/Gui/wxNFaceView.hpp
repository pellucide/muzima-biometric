#ifndef WX_NFACE_VIEW_HPP_INCLUDED
#define WX_NFACE_VIEW_HPP_INCLUDED

#include <Gui/wxNView.hpp>
#include <Biometrics/NFace.hpp>

#include <wx/timer.h>

#define MAX_FPS_ENTRIES 30

namespace Neurotec { namespace Biometrics { namespace Gui
{

wxDECLARE_EVENT(wxEVT_FACE_OBJECT_COLLECTION_CHANGED, wxCommandEvent);
wxDECLARE_EVENT(wxEVT_FACE_PROPERTY_CHANGED, wxCommandEvent);

class wxNFaceView : public Neurotec::Gui::wxNView
{
	#define ID_IMAGE_CHANGED          wxID_HIGHEST + 1
	#define ID_PROPERTY_CHANGED       wxID_HIGHEST + 2
	#define ID_COLLECTION_ITEM_ADD    wxID_HIGHEST + 3
	#define ID_COLLECTION_ITEM_REMOVE wxID_HIGHEST + 4
	#define ID_COLLECTION_ITEM_RESET  wxID_HIGHEST + 5
public:
	wxNFaceView(wxWindow *parent, wxWindowID winid = wxID_ANY)
		: wxNView(parent, winid),
		m_faceRectangleColor(wxColour(0, 255, 0)),
		m_faceRectangleWidth(2),
		m_lastTime(0),
		m_firstTime(0),
		m_frameCount(0),
		m_currentFps(0),
		m_showFps(false),
		m_showEyes(true),
		m_showNose(true),
		m_showMouth(true),
		m_showNoseConfidence(false),
		m_showEyesConfidence(false),
		m_showMouthConfidence(false),
		m_showGender(true),
		m_showGenderConfidence(false),
		m_showExpression(true),
		m_showExpressionConfidence(false),
		m_showProperties(true),
		m_showEmotions(true),
		m_showEmotionConfidence(false),
		m_showPropertiesConfidence(false),
		m_showBaseFeaturePoints(true),
		m_featureWidth(4),
		m_baseFeatureWidth(2),
		m_face(NULL)
	{
		SetScrollRate(1, 1);
		SetBackgroundColour(wxColour(0, 0, 0));
		this->Bind(wxEVT_FACE_PROPERTY_CHANGED, &wxNFaceView::OnPropertyChanged, this);
		this->Bind(wxEVT_FACE_OBJECT_COLLECTION_CHANGED, &wxNFaceView::OnCollectionChanged, this);
	}

	~wxNFaceView()
	{
		SetFace(NULL);
		this->Unbind(wxEVT_FACE_PROPERTY_CHANGED, &wxNFaceView::OnPropertyChanged, this);
		this->Unbind(wxEVT_FACE_OBJECT_COLLECTION_CHANGED, &wxNFaceView::OnCollectionChanged, this);
	}

private:
		static void OnPropertyChangedCallback(::Neurotec::Biometrics::NFace::PropertyChangedEventArgs args)
		{
			if (args.GetPropertyName() == N_T("Image"))
			{
				::Neurotec::Biometrics::NFace face = args.GetObject< ::Neurotec::Biometrics::NFace>();
				::Neurotec::Images::NImage image = face.GetImage();

				wxNFaceView * faceView = static_cast<wxNFaceView *>(args.GetParam());
				wxCommandEvent ev(wxEVT_FACE_PROPERTY_CHANGED, ID_IMAGE_CHANGED);
				if (!image.IsNull())
					ev.SetEventObject(new wxImage(image.ToBitmap()));
				else
					ev.SetEventObject(new wxImage());
				::wxPostEvent(faceView, ev);
			}
		}

		static void OnCollectionChangedCallback(::Neurotec::Collections::CollectionChangedEventArgs< ::Neurotec::Biometrics::NLAttributes> args)
		{
			wxNFaceView * faceView = static_cast<wxNFaceView*>(args.GetParam());
			wxCommandEvent ev(wxEVT_FACE_OBJECT_COLLECTION_CHANGED);
			if (args.GetAction() == ::Neurotec::Collections::nccaAdd)
			{
				if (args.GetNewItems().GetCount() > 1) NThrowNotImplementedException();
				ev.SetClientData(args.GetNewItems()[0].RefHandle());
				ev.SetId(ID_COLLECTION_ITEM_ADD);
			}
			else if (args.GetAction() == ::Neurotec::Collections::nccaRemove)
			{
				NThrowNotImplementedException();
			}
			else if (args.GetAction() == ::Neurotec::Collections::nccaReset)
			{
				ev.SetId(ID_COLLECTION_ITEM_RESET);
			}
			::wxPostEvent(faceView, ev);
		}

		static void OnAttributesPropertyChangedCallback(::Neurotec::Biometrics::NLAttributes::PropertyChangedEventArgs args)
		{
			wxNFaceView * faceView = static_cast<wxNFaceView*>(args.GetParam());
			wxCommandEvent ev(wxEVT_FACE_PROPERTY_CHANGED, ID_PROPERTY_CHANGED);
			::wxPostEvent(faceView, ev);
		}

		void SubscribeToFaceEvents()
		{
			if (!m_face.IsNull())
			{
				m_face.AddPropertyChangedCallback(&wxNFaceView::OnPropertyChangedCallback, this);
				m_face.GetObjects().AddCollectionChangedCallback(&wxNFaceView::OnCollectionChangedCallback, this);
				OnAttributesAdded(m_face.GetObjects().GetAll());
			}
		}

		void UnsubscribeFromFaceEvents()
		{
			if (!m_face.IsNull())
			{
				m_face.RemovePropertyChangedCallback(&wxNFaceView::OnPropertyChangedCallback, this);
				m_face.GetObjects().RemoveCollectionChangedCallback(&wxNFaceView::OnCollectionChangedCallback, this);
				OnAttributesReset();
			}
		}

		void SetImage(const wxImage & image)
		{
			if (image.IsOk())
			{
				m_bitmap = wxBitmap(image);
				SetViewSize(image.GetWidth(), image.GetHeight());
			}
			else
			{
				m_bitmap = wxBitmap();
				SetViewSize(1, 1);
			}
			Refresh(false);
		}

		void OnPropertyChanged(wxEvent & event)
		{
			int id = event.GetId();
			if (id == ID_IMAGE_CHANGED)
			{
				wxImage * pImage = (wxImage*)event.GetEventObject();
				SetImage(*pImage);
				delete pImage;
			}
			else Refresh();
		}

		void OnCollectionChanged(wxCommandEvent & event)
		{
			int id = event.GetId();
			if (id == ID_COLLECTION_ITEM_ADD)
			{
				::Neurotec::Biometrics::NLAttributes attributes(static_cast< ::Neurotec::Biometrics::HNLAttributes>(event.GetClientData()), true);
				std::vector< ::Neurotec::Biometrics::NLAttributes> attrVector;
				attrVector.push_back(attributes);
				NArrayWrapper< ::Neurotec::Biometrics::NLAttributes> attrArray(attrVector.begin(), attrVector.end());
				OnAttributesAdded(attrArray);
			}
			else if (id == ID_COLLECTION_ITEM_RESET)
			{
				OnAttributesReset();
			}
		}

		void OnAttributesAdded(NArrayWrapper< ::Neurotec::Biometrics::NLAttributes> attributes)
		{
			for (int i = 0; i < attributes.GetCount(); i++)
			{
				attributes[i].AddPropertyChangedCallback(&wxNFaceView::OnAttributesPropertyChangedCallback, this);
				m_attributes.push_back(attributes[i]);
			}
			Refresh();
		}

		void OnAttributesReset()
		{
			for (std::vector< ::Neurotec::NObject>::iterator it = m_attributes.begin(); it < m_attributes.end(); it++)
			{
				it->RemovePropertyChangedCallback(&wxNFaceView::OnAttributesPropertyChangedCallback, this);
			}
			m_attributes.clear();
			Refresh();
		}
public:
	void Clear()
	{
		m_bitmap = wxBitmap();
		SetFace(NULL);
		Refresh(false);
	}

	void SetFace(::Neurotec::Biometrics::NFace face)
	{
		if (GetShowFps())
		{
			UpdateFps();
		}

		UnsubscribeFromFaceEvents();

		m_face = face;

		SubscribeToFaceEvents();
		if (!m_face.IsNull())
		{
			::Neurotec::Images::NImage image = m_face.GetImage();
			SetImage(!image.IsNull() ? image.ToBitmap() : wxImage());
		}
		else SetImage(wxImage());
	}

	::Neurotec::Biometrics::NFace GetFace()
	{
		return m_face;
	}

	void SetFaceRectangleColor(const wxColour & value){ m_faceRectangleColor = value; }
	const wxColour & GetFaceRectangleColor(){ return m_faceRectangleColor; }

	void SetFaceRectangleWidth(int value){ m_faceRectangleWidth = value; }
	int GetFaceRectangleWidth(){ return m_faceRectangleWidth; }

	void SetShowFps(bool value){ m_showFps = value; }
	bool GetShowFps(){ return m_showFps; }

	bool GetShowEyes(){ return m_showEyes; }
	void SetShowEyes(bool value){ m_showEyes = value; }
	bool GetShowNose(){ return m_showNose; }
	void SetShowNose(bool value){ m_showNose = value; }
	bool GetShowMouth(){ return m_showMouth; }
	void SetShowMouth(bool value){ m_showMouth = value; }
	bool GetShowMouthConfidence(){ return m_showMouthConfidence; }
	void SetShowMouthConfidence(bool value){ m_showMouthConfidence = value; }
	bool GetShowNoseConfidence(){ return m_showNoseConfidence; }
	void SetShowNoseConfidence(bool value){ m_showNoseConfidence = value; }
	bool GetShowEyesConfidence(){ return m_showEyesConfidence; }
	void SetShowEyesConfidence(bool value){ m_showEyesConfidence = value; }
	void SetShowGender(bool value){ m_showGender = value; }
	bool GetShowGender(){ return m_showGender; }
	void SetShowGenderConfidence(bool value){ m_showGenderConfidence = value; }
	bool GetShowGenderConfidence(){ return m_showGenderConfidence; }
	bool GetShowExpression() { return m_showExpression; }
	void SetShowExpression(bool value) { m_showExpression = value; }
	bool GetShowExpressionConfidence() { return m_showExpressionConfidence; }
	void SetShowExpressionConfidence(bool value) { m_showExpressionConfidence = value; }
	bool GetShowProperties() { return m_showProperties; }
	void SetShowProperties(bool value) { m_showProperties = value; }
	bool GetShowPropertiesConfidence() { return m_showPropertiesConfidence; }
	void SetShowPropertiesConfidence(bool value) { m_showPropertiesConfidence = value; }
	void SetShowBaseFeaturePoints(bool value){ m_showBaseFeaturePoints = value; }
	bool GetShowBaseFeaturePoints(){ return m_showBaseFeaturePoints; }
	int GetFeatureWidth(){ return m_featureWidth; }
	void SetFeatureWidth(int value){ m_featureWidth = value; }
	int GetBaseFeatureWidth(){ return m_baseFeatureWidth; }
	void SetBaseFeatureWidth(int value){ m_baseFeatureWidth = value; }
	bool GetShowEmotions() { return m_showEmotions; }
	void SetShowEmotions(bool value) { m_showEmotions = value; }
	bool GetShowEmotionsConfidence() { return m_showEmotionConfidence; }
	void SetShowEmotionsConfidence(bool value) { m_showEmotionConfidence = value; }
protected:
#if wxUSE_GRAPHICS_CONTEXT == 1
	virtual void OnDraw(wxGraphicsContext *gc)
	{
		if (!m_bitmap.IsOk()) return;
		if (m_bitmap.GetRefData())
		{
			gc->DrawBitmap(m_bitmap, 0, 0, m_bitmap.GetWidth(), m_bitmap.GetHeight());
		}

		gc->Clip(0, 0, m_bitmap.GetWidth(), m_bitmap.GetHeight());

		if (!m_face.IsNull())
		{
			NArrayWrapper< ::Neurotec::Biometrics::NLAttributes> attributes = m_face.GetObjects().GetAll();
			for (int i = 0; i < attributes.GetCount(); i++)
			{
				wxString faceNumber(wxString::Format(wxT("#%d"), i + 1));
				wxString faceConfidence = wxEmptyString;
				::Neurotec::Biometrics::NLAttributes item = attributes[i];
				NByte quality = item.GetQuality();
				if (quality <= 100)
				{
					faceConfidence = wxString::Format(wxT("%d"), quality);
				}
				DrawAttributes(gc, item, faceNumber, faceConfidence);
			}
		}
		gc->ResetClip();

		if (m_showFps
			&& m_currentFps > 0.5f
			&& m_currentFps < 1000.0f)
		{
			wxBrush brush(wxColour(127, 127, 127));
			wxGraphicsBrush graphicsBrush = gc->CreateBrush(brush);
			gc->DrawText(wxString::Format(wxT("%4.1f"), m_currentFps), 5, 5, graphicsBrush);
		}
	}

#else
	virtual void OnDraw(wxDC& dc)
	{
		if (m_bitmap.GetRefData())
		{
			dc.DrawBitmap(m_bitmap, 0, 0, false);
		}

		if (!m_face.IsNull())
		{
			NArrayWrapper< ::Neurotec::Biometrics::NLAttributes> attributes = m_face.GetObjects().GetAll();
			for (int i = 0; i < attributes.GetCount(); i++)
			{
				wxString faceNumber(wxString::Format(wxT("#%d"), i + 1));
				wxString faceConfidence = wxEmptyString;
				::Neurotec::Biometrics::NLAttributes item = attributes[i];
				NByte quality = item.GetQuality();
				if (quality <= 100)
				{
					faceConfidence = wxString::Format(wxT("%d"), quality);
				}
				DrawAttributes(dc, item, faceNumber, faceConfidence);
			}
		}
		if (m_showFps
			&& m_currentFps > 0.5f
			&& m_currentFps < 1000.0f)
		{
			dc.SetTextForeground(wxColour(0, 255, 0));
			dc.DrawText(wxString::Format(wxT("%4.1f"), m_currentFps), 5, 5);
		}
	}

	static inline void RotatePointAt(double x, double y, double centerX, double centerY, double angle, double *ox, double *oy)
	{
		*ox = centerX + cos(angle) * (x - centerX) - sin(angle) * (y - centerY);
		*oy = centerY + sin(angle) * (x - centerX) + cos(angle) * (y - centerY);
	}

#endif

protected:
#if wxUSE_GRAPHICS_CONTEXT == 1
	void DrawAttributes(wxGraphicsContext *gc, const ::Neurotec::Biometrics::NLAttributes & attributes, const wxString& faceNumber, const wxString& faceConfidence)
	{
		::Neurotec::Biometrics::NLFeaturePoint item;

		wxPen pen(m_faceRectangleColor, m_faceRectangleWidth);
		wxBrush solidBrush(m_faceRectangleColor);
		gc->SetPen(pen);

		int half = m_baseFeatureWidth / 2;
		if(m_showBaseFeaturePoints)
		{
			gc->SetBrush(solidBrush);
			NArrayWrapper< ::Neurotec::Biometrics::NLFeaturePoint> points = attributes.GetFeaturePoints().GetAll();
			for(int i = 0; i < points.GetCount(); i++)
			{
				item = points[i];
				if(item.Confidence > 0)
				{
					gc->DrawEllipse(item.X - half, item.Y - half, m_baseFeatureWidth, m_baseFeatureWidth);
				}
			}
		}

		half = m_featureWidth / 2;
		if (m_showEyes)
		{
			::Neurotec::Biometrics::NLFeaturePoint left = attributes.GetLeftEyeCenter();
			::Neurotec::Biometrics::NLFeaturePoint right = attributes.GetRightEyeCenter();
			if ((IsConfidenceOk(left.Confidence) || IsConfidenceOk(right.Confidence)))
			{
				if(IsConfidenceOk(left.Confidence) && IsConfidenceOk(right.Confidence))
				{
					gc->StrokeLine(left.X, left.Y, right.X, right.Y);
				}
				gc->SetBrush(solidBrush);
				if(IsConfidenceOk(left.Confidence))
				{
					gc->DrawEllipse(left.X - half, left.Y - half, m_featureWidth, m_featureWidth);
				}
				if(IsConfidenceOk(right.Confidence))
				{
					gc->DrawEllipse(right.X - half, right.Y - half, m_featureWidth, m_featureWidth);
				}
				gc->SetBrush(wxNullBrush);

				if(m_showEyesConfidence)
				{
					if(IsConfidenceOk(right.Confidence))
					{
						gc->DrawText(wxString::Format(wxT("%d"), right.Confidence), right.X - m_featureWidth, right.Y + m_featureWidth);
					}
					if(IsConfidenceOk(left.Confidence))
					{
						gc->DrawText(wxString::Format(wxT("%d"), left.Confidence), left.X - m_featureWidth, left.Y + m_featureWidth);
					}
				}
			}
		}
		if(m_showNose)
		{
			::Neurotec::Biometrics::NLFeaturePoint nose = attributes.GetNoseTip();
			if (IsConfidenceOk(nose.Confidence))
			{
				gc->SetBrush(solidBrush);
				gc->DrawEllipse(nose.X - half, nose.Y - half, m_featureWidth, m_featureWidth);

				if(m_showNoseConfidence)
				{
					gc->DrawText(wxString::Format(wxT("%d"), nose.Confidence), nose.X - m_featureWidth, nose.Y + m_featureWidth);
				}
				gc->SetBrush(wxNullBrush);
			}
		}
		if(m_showMouth)
		{
			::Neurotec::Biometrics::NLFeaturePoint mouth = attributes.GetMouthCenter();
			if (IsConfidenceOk(mouth.Confidence))
			{
				gc->SetBrush(solidBrush);
				gc->DrawEllipse(mouth.X - half, mouth.Y - half, m_featureWidth, m_featureWidth);
				gc->SetBrush(wxNullBrush);

				if(m_showMouthConfidence)
				{
					gc->DrawText(wxString::Format(wxT("%d"), mouth.Confidence), mouth.X - m_featureWidth, mouth.Y + m_featureWidth);
				}
			}
		}

		gc->SetPen(wxNullPen);

		::Neurotec::Geometry::NRect rect = attributes.GetBoundingRect();
		if (rect.Width > 0 && rect.Height > 0)
		{
			wxPen penGreen(m_faceRectangleColor, m_faceRectangleWidth);
			penGreen.SetCap(wxCAP_PROJECTING);
			penGreen.SetJoin(wxJOIN_MITER);
			gc->SetPen(penGreen);

			wxGraphicsMatrix oldTransform = gc->GetTransform();

			wxGraphicsMatrix matrix = gc->CreateMatrix();
			matrix.Translate(rect.X + rect.Width / 2, rect.Y + rect.Height / 2);
			matrix.Rotate(attributes.GetRoll() * M_PI / 180.0);
			matrix.Translate(-rect.Width / 2, -rect.Height / 2);
			gc->ConcatTransform(matrix);

			wxGraphicsPath path = gc->CreatePath();
			short yaw = attributes.GetYaw();

			path.AddLineToPoint(rect.Width, 0);
			if(yaw < 0)
			{
				path.AddLineToPoint(rect.Width - (rect.Width / 5 * yaw / 45), rect.Height / 2);
			}
			path.AddLineToPoint(rect.Width, rect.Height);
			path.AddLineToPoint(0, rect.Height);
			if(yaw > 0)
			{
				path.AddLineToPoint(-(rect.Width / 5 * yaw / 45), rect.Height / 2);
			}
			path.AddLineToPoint(0, 0);
			path.CloseSubpath();
			gc->StrokePath(path);

			gc->SetPen(wxNullPen);

			wxBrush brush(wxColour(0, 255, 0));
			wxGraphicsBrush graphicsBrush = gc->CreateBrush(brush);
			if (!faceNumber.IsEmpty())
			{
				gc->DrawText(faceNumber, 0, rect.Height + 3, graphicsBrush);
			}
			if (!faceConfidence.IsEmpty())
			{
				double textWidth, textHeight, descent, externalLeading;
				gc->GetTextExtent(faceConfidence, &textWidth, &textHeight, &descent, &externalLeading);
				gc->DrawText(faceConfidence, rect.Width - textWidth,
					rect.Height + 3, graphicsBrush);
			}

			if (m_showGender || m_showProperties || m_showExpression || m_showEmotions)
			{
				wxString value = GetDetailsString(attributes);
				if (value != wxEmptyString)
				{
					double textWidth, textHeight, descent, externalLeading;
					double faceWidth = rect.Width;
					gc->GetTextExtent(value, &textWidth, &textHeight, &descent, &externalLeading);
					double offset = textWidth > faceWidth ? (textWidth - faceWidth) / 2 : 0;
					gc->DrawText(value, 0 - offset, 0 - textHeight - 1, graphicsBrush);
				}
			}

			gc->SetTransform(oldTransform);
		}
	}
#else
	void DrawAttributes(wxDC & dc, const ::Neurotec::Biometrics::NLAttributes & attributes, const wxString& faceNumber, const wxString& faceConfidence)
	{
		::Neurotec::Biometrics::NLFeaturePoint item;

		wxPen pen(m_faceRectangleColor, m_faceRectangleWidth);
		wxBrush solidBrush(m_faceRectangleColor);
		dc.SetPen(pen);

		int half = m_baseFeatureWidth / 2;
		if(m_showBaseFeaturePoints)
		{
			dc.SetBrush(solidBrush);
			NArrayWrapper< ::Neurotec::Biometrics::NLFeaturePoint> points = attributes.GetFeaturePoints().GetAll();
			for(int i = 0; i < points.GetCount(); i++)
			{
				item = points[i];
				if(item.Confidence > 0)
				{
					dc.DrawEllipse(item.X - half, item.Y - half, m_baseFeatureWidth, m_baseFeatureWidth);
				}
			}
		}

		half = m_featureWidth / 2;
		if (m_showEyes)
		{
			::Neurotec::Biometrics::NLFeaturePoint left = attributes.GetLeftEyeCenter();
			::Neurotec::Biometrics::NLFeaturePoint right = attributes.GetRightEyeCenter();
			if ((IsConfidenceOk(left.Confidence) || IsConfidenceOk(right.Confidence)))
			{
				if(IsConfidenceOk(left.Confidence) && IsConfidenceOk(right.Confidence))
				{
					dc.DrawLine(left.X, left.Y, right.X, right.Y);
				}
				dc.SetBrush(solidBrush);
				if(IsConfidenceOk(left.Confidence))
				{
					dc.DrawEllipse(left.X - half, left.Y - half, m_featureWidth, m_featureWidth);
				}
				if(IsConfidenceOk(right.Confidence))
				{
					dc.DrawEllipse(right.X - half, right.Y - half, m_featureWidth, m_featureWidth);
				}
				dc.SetBrush(wxNullBrush);

				if(m_showEyesConfidence)
				{
					if(IsConfidenceOk(right.Confidence))
					{
						dc.DrawText(wxString::Format(wxT("%d"), right.Confidence), right.X - m_featureWidth, right.Y + m_featureWidth);
					}
					if(IsConfidenceOk(left.Confidence))
					{
						dc.DrawText(wxString::Format(wxT("%d"), left.Confidence), left.X - m_featureWidth, left.Y + m_featureWidth);
					}
				}
			}
		}
		if(m_showNose)
		{
			::Neurotec::Biometrics::NLFeaturePoint nose = attributes.GetNoseTip();
			if (IsConfidenceOk(nose.Confidence))
			{
				dc.SetBrush(solidBrush);
				dc.DrawEllipse(nose.X - half, nose.Y - half, m_featureWidth, m_featureWidth);

				if(m_showNoseConfidence)
				{
					dc.DrawText(wxString::Format(wxT("%d"), nose.Confidence), nose.X - m_featureWidth, nose.Y + m_featureWidth);
				}
				dc.SetBrush(wxNullBrush);
			}
		}
		if(m_showMouth)
		{
			::Neurotec::Biometrics::NLFeaturePoint mouth = attributes.GetMouthCenter();
			if (IsConfidenceOk(mouth.Confidence))
			{
				dc.SetBrush(solidBrush);
				dc.DrawEllipse(mouth.X - half, mouth.Y - half, m_featureWidth, m_featureWidth);
				dc.SetBrush(wxNullBrush);

				if(m_showMouthConfidence)
				{
					dc.DrawText(wxString::Format(wxT("%d"), mouth.Confidence), mouth.X - m_featureWidth, mouth.Y + m_featureWidth);
				}
			}
		}

		dc.SetPen(wxNullPen);

		::Neurotec::Geometry::NRect rect = attributes.GetBoundingRect();
		if (rect.Width > 0 && rect.Height > 0)
		{
			wxPen penGreen(m_faceRectangleColor, m_faceRectangleWidth);
			penGreen.SetCap(wxCAP_PROJECTING);
			penGreen.SetJoin(wxJOIN_MITER);
			dc.SetPen(penGreen);
			double angle = (double)attributes.GetRoll() * M_PI / 180.0;
			double pt1x, pt1y;
			RotatePointAt(rect.X, rect.Y,
				(rect.X * 2 + rect.Width) / 2,
				(rect.Y * 2 + rect.Height) / 2,
				angle, &pt1x, &pt1y);
			double pt2x, pt2y;
			RotatePointAt(rect.X + rect.Width,
				rect.Y,
				(rect.X * 2 + rect.Width) / 2,
				(rect.Y * 2 + rect.Height) / 2,
				angle, &pt2x, &pt2y);
			double pt3x, pt3y;
			RotatePointAt(rect.X + rect.Width,
				rect.Y + rect.Height,
				(rect.X * 2 + rect.Width) / 2,
				(rect.Y * 2 + rect.Height) / 2,
				angle, &pt3x, &pt3y);
			double pt4x, pt4y;
			RotatePointAt(rect.X,
				rect.Y + rect.Height,
				(rect.X * 2 + rect.Width) / 2,
				(rect.Y * 2 + rect.Height) / 2,
				angle, &pt4x, &pt4y);
			wxPoint points[] ={ wxPoint((int)pt1x, (int)pt1y),
				wxPoint((int)pt2x, (int)pt2y),
				wxPoint((int)pt3x, (int)pt3y),
				wxPoint((int)pt4x, (int)pt4y),
				wxPoint((int)pt1x, (int)pt1y) };
			dc.DrawLines(5, points, 0, 0);
			dc.SetPen(wxNullPen);

			dc.SetTextForeground(wxColour(0, 255, 0));
			if (!faceNumber.IsEmpty())
			{
				dc.DrawRotatedText(faceNumber, (wxCoord)pt4x, (wxCoord)pt4y, (double) -attributes.GetRoll());
			}
			if (!faceConfidence.IsEmpty())
			{
				dc.DrawRotatedText(faceNumber, (wxCoord)pt3x, (wxCoord)pt3y, (double) -attributes.GetRoll());
			}

			if (m_showGender || m_showExpression || m_showProperties || m_showEmotions)
			{
				wxString value = GetDetailsString(attributes);
				if (value != wxEmptyString)
				{
					wxSize sz = dc.GetTextExtent(value);
					double faceWidth = rect.Width;
					int offset = sz.GetWidth() > faceWidth ? (int)((sz.GetWidth() - faceWidth) / 2) : 0;
					dc.SetTextForeground(wxColour(0, 255, 0));
					dc.DrawRotatedText(value, (wxCoord)(pt1x - offset), (wxCoord)pt1y - sz.GetHeight() - 1, (double) -attributes.GetRoll());
				}
			}
		}
	}
#endif

	wxString GetDetailsString( ::Neurotec::Biometrics::NLAttributes attributes)
	{
		wxString value = wxEmptyString;
		if(m_showGender && IsConfidenceOk(attributes.GetGenderConfidence()))
		{
			value = NEnum::ToString(NBiometricTypes::NGenderNativeTypeOf(), attributes.GetGender());
			if(m_showGenderConfidence) value.Append(wxString::Format(wxT("(%d)"), attributes.GetGenderConfidence()));
		}
		if (m_showExpression && IsExpressionOk(attributes.GetExpression()) && IsConfidenceOk(attributes.GetExpressionConfidence()))
		{
			if (value != wxEmptyString) value.Append(wxT(", "));
			value.Append(NEnum::ToString(NBiometricTypes::NLExpressionNativeTypeOf(), attributes.GetExpression()));
			if (m_showExpressionConfidence) value.Append(wxString::Format(wxT("(%d)"), attributes.GetExpressionConfidence()));
		}
		if (m_showProperties)
		{
			if (IsConfidenceOk(attributes.GetGlassesConfidence()) && IsPropertySet(attributes.GetProperties(), nlpGlasses))
			{
				if (value != wxEmptyString) value.Append(wxT(", "));
				value.append(wxT("Glasses"));
				if (m_showPropertiesConfidence) value.Append(wxString::Format(wxT("(%d)"), attributes.GetGlassesConfidence()));
			}
			if (IsConfidenceOk(attributes.GetMouthOpenConfidence()) && IsPropertySet(attributes.GetProperties(), nlpMouthOpen))
			{
				if (value != wxEmptyString) value.Append(wxT(", "));
				value.append(wxT("Mouth Open"));
				if (m_showPropertiesConfidence) value.Append(wxString::Format(wxT("(%d)"), attributes.GetMouthOpenConfidence()));
			}
			if (IsConfidenceOk(attributes.GetBlinkConfidence()) && IsPropertySet(attributes.GetProperties(), nlpBlink))
			{
				if (value != wxEmptyString) value.Append(wxT(", "));
				value.Append(wxT("Blink"));
				if (m_showPropertiesConfidence) value.Append(wxString::Format(wxT("(%d)"), attributes.GetBlinkConfidence()));
			}
			if (IsConfidenceOk(attributes.GetDarkGlassesConfidence()) && IsPropertySet(attributes.GetProperties(), nlpDarkGlasses))
			{
				if (value != wxEmptyString) value.Append(wxT(", "));
				value.Append(wxT("Dark Glasses"));
				if (m_showPropertiesConfidence) value.Append(wxString::Format(wxT("(%d)"), attributes.GetDarkGlassesConfidence()));
			}
		}
		if (m_showEmotions)
		{
			NByte maxConfidence = 0;
			NByte confidence = 0;
			wxString emotionString = wxEmptyString;

			confidence = attributes.GetEmotionNeutralConfidence();
			if (IsConfidenceOk(confidence) && confidence > maxConfidence)
			{
				maxConfidence = confidence;
				emotionString = wxT("Neutral");
			}
			confidence = attributes.GetEmotionAngerConfidence();
			if (IsConfidenceOk(confidence) && confidence > maxConfidence)
			{
				maxConfidence = confidence;
				emotionString = wxT("Anger");
			}
			confidence = attributes.GetEmotionDisgustConfidence();
			if (IsConfidenceOk(confidence) && confidence > maxConfidence)
			{
				maxConfidence = confidence;
				emotionString = wxT("Disgust");
			}
			confidence = attributes.GetEmotionFearConfidence();
			if (IsConfidenceOk(confidence) && confidence > maxConfidence)
			{
				maxConfidence = confidence;
				emotionString = wxT("Fear");
			}
			confidence = attributes.GetEmotionHappinessConfidence();
			if (IsConfidenceOk(confidence) && confidence > maxConfidence)
			{
				maxConfidence = confidence;
				emotionString = wxT("Happiness");
			}
			confidence = attributes.GetEmotionSadnessConfidence();
			if (IsConfidenceOk(confidence) && confidence > maxConfidence)
			{
				maxConfidence = confidence;
				emotionString = wxT("Sadness");
			}
			confidence = attributes.GetEmotionSurpriseConfidence();
			if (IsConfidenceOk(confidence) && confidence > maxConfidence)
			{
				maxConfidence = confidence;
				emotionString = wxT("Surprise");
			}

			if (emotionString != wxEmptyString)
			{
				if (value != wxEmptyString) value.Append(wxT(", "));
				value.Append(emotionString);
				if (m_showEmotionConfidence) value.Append(wxString::Format(wxT("(%d)"), maxConfidence));
			}
		}
		return value;
	}
	static bool IsExpressionOk(NLExpression expression) { return expression == nleSmile; }
	static bool IsConfidenceOk(NByte confidence) { return confidence > 0 && confidence <= 100; }
	static bool IsPropertySet(NLProperties value, NLProperties flag) { return (value & flag) == flag; }
	void UpdateFps()
	{
		wxLongLong currentTime = ::wxGetLocalTimeMillis();
		float previousFps = m_currentFps;
		m_currentFps = 0.0f;
		if (currentTime - m_lastTime > 5000)
		{
			m_frameCount = 0;
			m_firstTime = currentTime;
		}
		else
		{
			m_frameCount++;
			if (m_frameCount == MAX_FPS_ENTRIES)
			{
				m_currentFps = (float)((m_frameCount.ToDouble() * 1000.0) / (double)(currentTime - m_firstTime).ToDouble());
			}
			else if (m_frameCount > MAX_FPS_ENTRIES)
			{
				float frameFps = (float)(1000.0 / (currentTime - m_lastTime).ToDouble());
				const float newFpsWeight = (float)1.0f / (float)MAX_FPS_ENTRIES;
				m_currentFps = previousFps * (1.0f - newFpsWeight) + frameFps * newFpsWeight;
			}
		}
		m_lastTime = currentTime;
	}
protected:
	wxBitmap m_bitmap;
	wxColour m_faceRectangleColor;
	int m_faceRectangleWidth;
	wxLongLong m_lastTime;
	wxLongLong m_firstTime;
	wxLongLong m_frameCount;
	float m_currentFps;
	bool m_showFps;
	bool m_showEyes;
	bool m_showNose;
	bool m_showMouth;
	bool m_showNoseConfidence;
	bool m_showEyesConfidence;
	bool m_showMouthConfidence;
	bool m_showGender;
	bool m_showGenderConfidence;
	bool m_showExpression;
	bool m_showExpressionConfidence;
	bool m_showProperties;
	bool m_showEmotions;
	bool m_showEmotionConfidence;
	bool m_showPropertiesConfidence;
	bool m_showBaseFeaturePoints;
	int m_featureWidth;
	int m_baseFeatureWidth;
	::Neurotec::Biometrics::NFace m_face;
	std::vector< ::Neurotec::NObject> m_attributes;
};

}}}

#endif // !WX_NFACE_VIEW_HPP_INCLUDED
