/*
 * MIT License
 *
 * Copyright (c) 2021 Stefan Wärting
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package se.warting.permissionsui.backgroundlocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.marcelpibi.permissionktx.compose.rememberLauncherForPermissionsResult
import dev.marcelpinto.permissionktx.Permission
import dev.marcelpinto.permissionktx.PermissionRational
import dev.marcelpinto.permissionktx.PermissionStatus
import se.warting.permissionsui.R

private const val PERMANENT_DENIED_TIMEOUT = 500

@Composable
fun LocationInBackgroundTutorialView(
    modifier: Modifier = Modifier,
    permissionsApproved: () -> Unit
) {
    val viewModel: BackgroundLocationTutorialViewModel = viewModel()

    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value.projectName) {
        is ViewState.Loading -> LoadingView()
        is ViewState.Success -> GeoTuttiViewLoaded(
            modifier = modifier,
            status = state.data,
            permissionsApproved = permissionsApproved
        )
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

 @Preview
 @Composable
 internal fun LocationInBackgroundTutorialViewDarkPreview() {
    MaterialTheme {
        Surface {
            GeoTuttiViewLoaded(
                status = RequiredPermissions(
                    permissionsNeededForFineLocation = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                    fineGpsPermission = PermissionStatus.Granted(Permission("")),
                    coarseGpsPermission = PermissionStatus.Granted(Permission("")),
                    backgroundGpsPermission = PermissionStatus.Granted(Permission("")),
                )
            )
        }
    }
 }

private fun getRequiredPermissionsForGeoFencing(): List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        listOf()
    }

@Suppress("LongMethod", "ComplexMethod")
@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun GeoTuttiViewLoaded(
    modifier: Modifier = Modifier,
    status: RequiredPermissions,
    permissionsApproved: () -> Unit = {},
) {

    val locationNotUpdatedError = remember { mutableStateOf(false) }

    val appName = getApplicationName(LocalContext.current)

    var requestedTime = 0L
    fun onPermissionResult(permissions: Map<Permission, Boolean>) {
        // check if any permission was approved
        // we only require one of precise or cource location
        // while requesting background we have only one
        if (permissions.values.all { permissionApproved -> permissionApproved }) {
            // all good!
            Log.d(
                "GeoTuttiViewLoaded",
                "All permissions approved: " + permissions.values.toString()
            )
        } else if (permissions.values.none { permissionApproved -> permissionApproved }) {
            // none was approved!
            Log.d("GeoTuttiViewLoaded", "No permissions approved: " + permissions.values.toString())

            // true if all of the permissions require a rationale (false = Permanent denied??)
            val requireRationale =
                permissions.keys.all { it.status.isRationaleRequired() }

            if (!requireRationale) {
                val timeSinceClicked = System.currentTimeMillis() - requestedTime
                if (timeSinceClicked < PERMANENT_DENIED_TIMEOUT) {
                    // Permanent denied
                    locationNotUpdatedError.value = true
                }
            }
        } else {
            Log.d(
                "GeoTuttiViewLoaded",
                "Some permissions approved: " + permissions.values.toString()
            )
        }
    }

    // Register the permission launcher
    val locationWhileUsingApppermissionLauncher =
        rememberLauncherForPermissionsResult(
            types = status.permissionsNeededForFineLocation.toTypedArray(),
            onResult = ::onPermissionResult
        )

    // Register the background permission launcher
    val backgroundLocationPermissionsLauncher =
        rememberLauncherForPermissionsResult(
            types = getRequiredPermissionsForGeoFencing().toTypedArray(),
            onResult = ::onPermissionResult
        )

    Box(modifier) {

        Column {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Filled.MyLocation, null,
                    modifier = Modifier.size(100.dp),
                )

                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.use_your_location_title),
                    style = MaterialTheme.typography.h4
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.use_your_location_description),
                    style = MaterialTheme.typography.body1
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.use_your_location_disclaimer, appName),
                    style = MaterialTheme.typography.body2
                )
            }

            Divider()

            val whileUsingAppState =
                if (status.coarseGpsPermission.isGranted() && status.fineGpsPermission.isGranted()) {
                    ListState.Complete
                } else {
                    if (status.coarseGpsPermission.isRationaleRequired() ||
                        status.fineGpsPermission.isRationaleRequired()
                    ) {
                        ListState.EnabledRationale
                    } else {
                        ListState.Enabled
                    }
                }

            EnableDisabledListItem(
                step = R.string.step1,
                description = stringResource(
                    id = R.string.permissions_while_using_the_app,
                    appName
                ),
                rationale = stringResource(id = R.string.permissions_while_using_the_app_rationale),
                listState = whileUsingAppState,
                onClick = {
                    requestedTime = System.currentTimeMillis()
                    locationWhileUsingApppermissionLauncher.launch(null)
                }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            val alwaysAppState =
                if (status.backgroundGpsPermission?.isGranted() == true ||
                    status.backgroundGpsPermission?.isGranted() == null
                ) {
                    ListState.Complete
                } else if (whileUsingAppState == ListState.Complete) {
                    if (status.backgroundGpsPermission.isRationaleRequired()) {
                        ListState.EnabledRationale
                    } else {
                        ListState.Enabled
                    }
                } else {
                    ListState.Disabled
                }

            if (alwaysAppState == ListState.Complete) {
                LaunchedEffect(alwaysAppState) {
                    permissionsApproved()
                }
            }

            EnableDisabledListItem(
                step = R.string.step2,
                description = stringResource(
                    id = R.string.allow_allways_location_permission,
                    appName,
                ),
                rationale = stringResource(
                    id = R.string.allow_allways_location_permission_rationale,
                    allTheTime()
                ),
                listState = alwaysAppState,
                onClick = {
                    requestedTime = System.currentTimeMillis()
                    backgroundLocationPermissionsLauncher.launch(null)
                }
            )
            Divider()
        }

        if (locationNotUpdatedError.value) {
            val context = LocalContext.current
            CommonAlertDialog(
                title = stringResource(R.string.permissions_error_title, appName),
                text = stringResource(id = R.string.permissions_error_description, allTheTime()),
                confirmButton = stringResource(R.string.open_settings),
                dismissButton = stringResource(R.string.cancel),
                dismiss = {
                    locationNotUpdatedError.value = false
                },
                confirm = {
                    openSettingsForApp(context)
                }
            )
        }
    }
}

@Composable
private fun allTheTime(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        LocalContext.current.packageManager.backgroundPermissionOptionLabel.toString()
    } else {
        stringResource(id = R.string.all_the_time)
    }
}

private fun PermissionStatus.isRationaleRequired(): Boolean = when (this) {
    is PermissionStatus.Granted -> false
    is PermissionStatus.Revoked -> {
        this.rationale == PermissionRational.REQUIRED
    }
}

private fun openSettingsForApp(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri =
        Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}

private fun getApplicationName(context: Context): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
        stringId
    )
}
