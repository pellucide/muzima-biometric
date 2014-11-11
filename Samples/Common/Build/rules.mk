CC        = $(CROSS)gcc
CXX       = $(CROSS)g++
LD        = $(CROSS)ld
STRIP     = $(CROSS)strip
CP        = cp -p

WX_CONFIG ?= wx-config

CFLAGS += -I$(ROOTDIR)Samples/Common/C/
CXXFLAGS += -I$(ROOTDIR)Samples/Common/C/
CXXFLAGS += -I$(ROOTDIR)Samples/Common/CPP/

OBJDIR ?= .obj/$(if $(filter $(DEBUG),yes),debug,release)/$(PLATFORM)/$(TARGET_NAME)$(if $(filter $(STATIC_LIBS),yes),.static)/

# Paths

RES_DIR ?= Resources/
BIN_DIR ?= Bin/$(PLATFORM)/
INC_DIR ?= Include/
LIB_DIR ?= Lib/$(PLATFORM)/

# Path to SDK distribution root

DIST_RESDIR ?= $(ROOTDIR)$(RES_DIR)
DIST_BINDIR ?= $(ROOTDIR)$(BIN_DIR)
DIST_INCDIR ?= $(ROOTDIR)$(INC_DIR)
DIST_LIBDIR ?= $(ROOTDIR)$(LIB_DIR)

PREFIX ?= $(ROOTDIR)
INSTALL_DIR ?= $(PREFIX)$(BIN_DIR)

### Compiling flags ###

ifeq ($(CROSS),)
  ifneq ($(NATIVE_PLATFORM),$(PLATFORM))
    CFLAGS_Linux_x86      += -m32
    CFLAGS_Linux_x86_64   += -m64
    CXXFLAGS_Linux_x86    += -m32
    CXXFLAGS_Linux_x86_64 += -m64
    LDFLAGS_Linux_x86     += -m32
    LDFLAGS_Linux_x86_64  += -m64
  endif
endif

CFLAGS += -Wall $(CFLAGS_WAR) -fno-strict-aliasing
CXXFLAGS += -Wall $(CXXFLAGS_WAR) -fno-strict-aliasing

CFLAGS_Linux += -D__LINUX__
CXXFLAGS_Linux += -D__LINUX__

ifneq ($(USE_STATIC_LIBS),yes)
  CFLAGS_MacOSX += -DN_MAC_OSX_FRAMEWORKS
  CXXFLAGS_MacOSX += -DN_MAC_OSX_FRAMEWORKS
endif

CFLAGS_MacOSX_universal   += -arch i386 -arch x86_64
CXXFLAGS_MacOSX_universal += -arch i386 -arch x86_64
LDFLAGS_MacOSX_universal += -arch i386 -arch x86_64

ifeq ($(STATIC_LIBS),yes)
  CFLAGS += -DN_PRODUCT_LIB
  CXXFLAGS += -DN_PRODUCT_LIB
endif

ifeq ($(N_PRODUCT_HAS_NO_LICENSES),yes)
  CFLAGS += -DN_PRODUCT_HAS_NO_LICENSES
  CXXFLAGS += -DN_PRODUCT_HAS_NO_LICENSES
endif

ifeq ($(DEBUG), yes)
  CFLAGS   += -ggdb -g3 -D_DEBUG -DDEBUG
  CXXFLAGS += -ggdb -g3 -D_DEBUG -DDEBUG
  LDFLAGS  += -ggdb -g3
else
  CFLAGS   += -O3
  CXXFLAGS += -O2
  LDFLAGS  += -O3
endif

CFLAGS   += -DNDEBUG
CXXFLAGS += -DNDEBUG

LDFLAGS_Linux += -Wl,-z,muldefs

CFLAGS   += -I$(DIST_INCDIR)
CXXFLAGS   += -I$(DIST_INCDIR)
LDFLAGS  += -L$(DIST_LIBDIR)

LDFLAGS_Linux  += -Wl,-rpath=\$${ORIGIN}/../../$(LIB_DIR)
LDFLAGS_Linux  += -Wl,-rpath=$(DIST_LIBDIR),-rpath-link=$(DIST_LIBDIR)

LDLIBS += $(LDLIBS_$(OS))
LDLIBS += $(LDLIBS_$(ARCH))
LDLIBS += $(LDLIBS_$(PLATFORM))

LDLIBS += $(patsubst %,$(if $(filter $(OS),MacOSX),-framework ,-l)%,$(LIBS))

ifeq ($(OS),Linux)
LDLIBS_START = -Wl,--start-group
LDLIBS_END = -Wl,--end-group
endif

CXXFLAGS += $(CXXFLAGS_$(OS))
CXXFLAGS += $(CXXFLAGS_$(ARCH))
CXXFLAGS += $(CXXFLAGS_$(PLATFORM))

CFLAGS += $(CFLAGS_$(OS))
CFLAGS += $(CFLAGS_$(ARCH))
CFLAGS += $(CFLAGS_$(PLATFORM))

LDFLAGS += $(LDFLAGS_$(OS))
LDFLAGS += $(LDFLAGS_$(ARCH))
LDFLAGS += $(LDFLAGS_$(PLATFORM))

LDLIBS_COM += $(LDLIBS)
LDLIBS_COM_Linux  += -lpthread -ldl
LDLIBS_COM_MacOSX += -lpthread -ldl

LDLIBS_COM += $(LDLIBS_COM_$(OS))
LDLIBS_COM += $(LDLIBS_COM_$(ARCH))
LDLIBS_COM += $(LDLIBS_COM_$(PLATFORM))

LDLIBS_COM += -lm

## build rules

VPATH := $(abspath ./) $(abspath $(ROOTDIR)Samples/)

OBJS = $(foreach _OBJ,$(CSRCS:.c=.o),$(OBJDIR)$(_OBJ))
OBJS += $(foreach _OBJ,$(CXXSRCS:.cpp=.o),$(OBJDIR)$(_OBJ))

LINKER = $(if $(strip $(CXXSRCS)),$(CXX),$(CC))

ifeq ($(OS),MacOSX)
  TARGET  ?= $(OBJDIR)$(TARGET_NAME)$(if $(filter $(TARGET_BUNDLE),yes),.app)
else
  TARGET  ?= $(OBJDIR)$(TARGET_NAME)
endif

all: $(TARGET)

$(TARGET): | pre-build

pre-build:

$(OBJDIR)$(TARGET_NAME): $(OBJS)
	@$(if $(wildcard $(@D)),,mkdir -p $(@D))
	$(LINKER) $(LDFLAGS) $(OBJS) $(LDLIBS_START) $(LDLIBS_COM) $(LDLIBS_END) -o $@

# rules for application bundle of MacOSX
$(OBJDIR)$(TARGET_NAME).app/Contents/MacOS/$(TARGET_NAME): $(OBJS)
	@$(if $(wildcard $(@D)),,mkdir -p $(@D))
	$(LINKER) $(LDFLAGS) $(OBJS) $(LDLIBS_START) $(LDLIBS_COM) $(LDLIBS_END) -o $@

$(OBJDIR)$(TARGET_NAME).app/Contents/Resources/Neurotechnology.icns: $(DIST_RESDIR)Neurotechnology.icns
	$(if $(wildcard $(@D)),,mkdir -p $(@D))
	$(CP) $< $@

$(OBJDIR)$(TARGET_NAME).app/Contents/Info.plist: Info.plist
	$(if $(wildcard $(@D)),,mkdir -p $(@D))
	$(CP) $< $@

$(OBJDIR)$(TARGET_NAME).app/Contents/PkgInfo:
	$(if $(wildcard $(@D)),,mkdir -p $(@D))
	printf "APPLNEUT" > $@

$(OBJDIR)$(TARGET_NAME).app/Contents/Resources/English.lproj:
	mkdir -p $@

$(OBJDIR)$(TARGET_NAME).app: \
$(OBJDIR)$(TARGET_NAME).app/Contents/MacOS/$(TARGET_NAME) \
$(OBJDIR)$(TARGET_NAME).app/Contents/Info.plist \
$(OBJDIR)$(TARGET_NAME).app/Contents/PkgInfo \
$(OBJDIR)$(TARGET_NAME).app/Contents/Resources/Neurotechnology.icns \
$(OBJDIR)$(TARGET_NAME).app/Contents/Resources/English.lproj

$(OBJDIR)%.o: %.c
	@test -f $(dir $@) || mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(OBJDIR)%.o: %.cpp
	@test -f $(dir $@) || mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS) -c $< -o $@

install: install_target

install_target: $(TARGET)
ifneq ($(wildcard $(PREFIX)),)
	$(if $(wildcard $(INSTALL_DIR)),,mkdir -p $(INSTALL_DIR))
	$(if $(wildcard $(INSTALL_DIR)$(notdir $(TARGET))),$(RM) -r $(INSTALL_DIR)$(notdir $(TARGET)))
	$(CP) -a $(TARGET) $(INSTALL_DIR)
  ifneq ($(DEBUG),yes)
    ifeq ($(TARGET_BUNDLE),yes)
	$(STRIP) $(INSTALL_DIR)$(notdir $(TARGET))/Contents/MacOS/$(TARGET_NAME)
    else
	$(STRIP) $(INSTALL_DIR)$(notdir $(TARGET))
    endif
  endif
endif

clean:
	$(RM) -r $(OBJDIR)
	$(RM) -r $(TARGET)

run: $(TARGET)
	$(TARGET)

-include $(BUILDTOOLS_DIR)internal.mk

.PHONY: all clean install install_target 
# vim:filetype=make
