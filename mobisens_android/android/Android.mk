LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := eng

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
	src/edu/cmu/sv/android/mobisens/ISystemLog.aidl

LOCAL_PACKAGE_NAME := SystemSens

include $(BUILD_PACKAGE)
