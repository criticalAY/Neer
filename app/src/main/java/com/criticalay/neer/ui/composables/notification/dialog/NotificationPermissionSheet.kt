/*
 * Copyright (c) 2026 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.criticalay.neer.ui.composables.notification.dialog

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.criticalay.neer.R
import com.criticalay.neer.utils.PreferencesManager

/**
 * Material3 bottom sheet that asks for POST_NOTIFICATIONS following the
 * Android framework's recommended flow — no third-party permissions library.
 *
 * Path decision at display time:
 *  - API < 33 → permission not needed, fire [onGranted] immediately.
 *  - Already granted → fire [onGranted] immediately, no sheet.
 *  - Never asked before → show sheet with "Allow" → launches the system
 *    permission dialog via the Activity Result API on tap.
 *  - Previously denied + `shouldShowRequestPermissionRationale == true` →
 *    same "Allow" sheet; the system will show the permission dialog again.
 *  - Previously denied + `shouldShowRequestPermissionRationale == false`
 *    (permanent / "don't ask again") → sheet swaps to "Open settings" and
 *    deep-links into the app's notification settings, since a second
 *    `launchPermissionRequest()` would be a no-op in this state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPermissionSheet(
    onGranted: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(Unit) { onGranted() }
        return
    }

    val alreadyGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
    if (alreadyGranted) {
        LaunchedEffect(Unit) { onGranted() }
        return
    }

    val prefs = remember { PreferencesManager(context) }
    val activity = remember(context) { context.findActivity() }
    val hasAskedBefore = remember { mutableStateOf(prefs.hasAskedNotificationPermission()) }

    // `shouldShowRationale` is reliably true only *between* the first denial
    // and "don't ask again". Combined with our "has asked" bit, we can tell
    // a first-time prompt from a permanent denial.
    val shouldShowRationale = remember {
        activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(
                it,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } ?: false
    }
    val permanentlyDenied = hasAskedBefore.value && !shouldShowRationale

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAskedBefore.value = true
        prefs.markNotificationPermissionAsked()
        if (granted) {
            onGranted()
            onDismiss()
        }
        // If denied, we leave the sheet visible; next composition will detect
        // the new shouldShowRationale state and potentially flip the button
        // to "Open settings".
    }

    SheetContent(
        permanentlyDenied = permanentlyDenied,
        onAllow = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
        onOpenSettings = { openAppNotificationSettings(context) },
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetContent(
    permanentlyDenied: Boolean,
    onAllow: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 4.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_notification_active),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.notif_perm_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (permanentlyDenied) R.string.notif_perm_settings_body
                    else R.string.notif_perm_body
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(stringResource(R.string.notif_perm_not_now))
                }
                Button(
                    onClick = if (permanentlyDenied) onOpenSettings else onAllow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        stringResource(
                            if (permanentlyDenied) R.string.notif_perm_open_settings
                            else R.string.notif_perm_allow
                        )
                    )
                }
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private fun openAppNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }.onFailure {
        // Fallback for OEMs that don't resolve the notification settings action.
        val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(fallback) }
    }
}
