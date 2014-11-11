ROOTDIR := ../../../../

BUILDTOOLS_DIR := $(dir $(lastword $(MAKEFILE_LIST)))

include $(or $(wildcard $(BUILDTOOLS_DIR)arch.mk),$(ROOTDIR)Build/MakeTools/arch.mk)

# vim:filetype=make
