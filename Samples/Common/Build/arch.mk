# Try to guess OS and ARCH for Makefiles

NATIVE_ARCH	?= $(shell uname -m | sed 's/i.86/x86/')
NATIVE_OS       ?= $(shell uname | sed 's/Darwin/MacOSX/g')
NATIVE_PLATFORM ?= $(NATIVE_OS)_$(NATIVE_ARCH)

NATIVE_OSTYPE   ?= $(shell uname -o 2>/dev/null || uname)

ifeq ($(and $(if $(CROSS),,no),$(filter $(OS),Android),ok),ok)
  CROSS=arm-linux-androideabi-
  $(if $(wildcard $(shell which $(CROSS)gcc 2> /dev/null)),,$(error Standalone toolchain of android ndk not found. Please, install it with <ANDROID_NDK_HOME>/build/tools/make-standalone-toolchain.sh and add path of it to PATH variable))
endif

ifneq ($(CROSS),)
  ifneq (,$(shell $(CROSS)gcc -dM -E - < /dev/null | grep ANDROID | sed -n '/ANDROID/p'))
    OS ?= Android
    ANDROID_ARCHS ?= armeabi armeabi-v7a
  else ifneq (,$(shell $(CROSS)gcc -dM -E - < /dev/null | grep __arm__))
    ifneq (,$(shell $(CROSS)gcc -dM -E - < /dev/null | grep __ARMEL__))
      ifneq (,$(shell $(CROSS)gcc -dM -E - < /dev/null | grep __SOFTFP__))
        ARCH ?= armel
      endif
      ARCH ?= armhf
    endif
    ARCH ?= arm
  else
    CFW = $(firstword $(subst -, ,$(notdir $(CROSS))))
    ifeq ($(NATIVE_ARCH),x86)
      ARCH ?= x86
    else
      ARCH ?= $(CFW)
    endif
  endif
  ifneq (,$(shell $(CROSS)gcc -dM -E - < /dev/null | grep linux | sed -n '/linux/p'))
    OS ?= Linux
  endif
endif

ifeq ($(NATIVE_OS),AIX)
  OS ?= aix
endif

OS ?= $(NATIVE_OS)

ifeq ($(OS),QNX)
  ARCH = armv7
endif

ifeq ($(OS),MacOSX)
  ifeq ($(UNIVERSAL_BIN),yes)
    ARCH = universal
  else
    ifeq ($(shell uname -p),powerpc)
      ARCH ?= ppc
      APPLE_ARCHS := ppc
    endif
    ifneq ($(shell echo "\#include <stdlib.h>" | $(CROSS)gcc $(CFLAGS) -dM -E - | grep __i386__ | sed -n '/i386/p'),)
      ARCH ?= x86
    endif
    ifneq ($(shell echo "\#include <stdlib.h>" | $(CROSS)gcc $(CFLAGS) -dM -E - | grep __x86_64__ | sed -n '/x86_64/p'),)
      ARCH ?= x86_64
    endif
    ARCH ?= x86
  endif
endif

ifneq ($(shell uname -m | sed -n -e '/arm/p;'),)
  ifneq (,$(shell $(CROSS)gcc -dM -E - < /dev/null | grep __ARMEL__))
    ifneq (,$(shell $(CROSS)gcc -dM -E - < /dev/null | grep __SOFTFP__))
      ARCH ?= armel
    endif
    ARCH ?= armhf
  endif
  ARCH ?= arm
endif

ARCH ?= $(NATIVE_ARCH)

ifneq ($(shell echo "\#include <stdlib.h>" | $(CROSS)gcc -dM -E - | grep __UCLIBC__ | sed -n '/UCLIBC/p'),)
  LIBC ?= uclibc
endif

ifneq ($(OS),Android)
  ifeq ($(ARCH),arm)
    ifneq (,$(shell echo "\#include <stdlib.h>" | $(CROSS)gcc -dM -E - | grep __GLIBC__ | sed -n '/GLIBC/p'))
      LIBC ?= glibc
    endif
    ifeq (,$(LIBC))
      $(error LIBC variable should ve set to "glibc" or "uClibc")
    endif
  endif
endif

ifneq (,$(findstring $(OS),iPhoneOS iPhoneSimulator))
  LIBC ?=
  PLATFORM = $(OS)
endif

ifneq ($(LIBC),)
  PLATFORM ?= $(OS)_$(LIBC)_$(ARCH)
else
  PLATFORM ?= $(OS)_$(ARCH)
endif

ifneq (,$(findstring $(OS),MacOSX iPhoneOS iPhoneSimulator))
  APPLE_BUILD = yes
  ifeq ($(OS),iPhoneOS)
    APPLE_ARCHS := armv6 armv7
  endif
  ifeq ($(OS),iPhoneSimulator)
    APPLE_ARCHS := i386
  endif
  ifneq ($(UNIVERSAL_BIN),yes)
    APPLE_ARCHS ?= $(or $(if $(filter x86,$(ARCH)),i386),$(if $(filter ppc,$(ARCH)),powerpc),$(ARCH))
  endif
  APPLE_ARCHS ?= i386 x86_64
endif

# vim:filetype=make:
