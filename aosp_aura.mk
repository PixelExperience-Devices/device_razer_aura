#
# Copyright (C) 2018-2020 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

$(call inherit-product, device/razer/aura/device.mk)

# Inherit some common AOSP stuff.
$(call inherit-product, vendor/aosp/config/common_full_phone.mk)

# Device identifier. This must come after all inclusions.
PRODUCT_NAME := aosp_aura
PRODUCT_DEVICE := aura
PRODUCT_BRAND := razer
PRODUCT_MODEL := Phone 2
PRODUCT_MANUFACTURER := Razer

BUILD_FINGERPRINT := "razer/cheryl2/aura:9/P-SMR7-RC003-RZR-210107/3225:user/release-keys"

PRODUCT_BUILD_PROP_OVERRIDES += \
    PRIVATE_BUILD_DESC="aura-user 9 P-SMR7-RC003-RZR-210107 3225 release-keys" \
    PRODUCT_NAME="cheryl2"

PRODUCT_GMS_CLIENTID_BASE := android-razer

TARGET_SUPPORTS_GOOGLE_RECORDER := true
TARGET_SUPPORTS_QUICK_TAP := true

TARGET_BOOT_ANIMATION_RES := 1440