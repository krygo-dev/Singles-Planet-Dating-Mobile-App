package com.krygodev.singlesplanet.util

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@ExperimentalPermissionsApi
fun PermissionState.isDenied(): Boolean {
    return !shouldShowRationale && !hasPermission
}