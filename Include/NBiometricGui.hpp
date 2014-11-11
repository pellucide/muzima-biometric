#ifndef N_BIOMETRIC_GUI_LIBRARY_HPP_INCLUDED
#define N_BIOMETRIC_GUI_LIBRARY_HPP_INCLUDED

#include <Core/NTypes.hpp>

#ifdef N_FRAMEWORK_WX
#include <Biometrics/Gui/wxNFingerView.hpp>
#include <Biometrics/Gui/wxNPalmView.hpp>
#include <Biometrics/Gui/wxNFaceView.hpp>
#include <Biometrics/Gui/wxNIrisView.hpp>
#endif

#ifdef N_FRAMEWORK_QT
#include <Biometrics/Gui/QNFView.hpp>
#include <Biometrics/Gui/QNLView.hpp>
#include <Biometrics/Gui/QNEView.hpp>
#endif

#endif // !N_BIOMETRIC_GUI_LIBRARY_HPP_INCLUDED
