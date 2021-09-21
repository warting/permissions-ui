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

package se.warting.blpr.geo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import se.warting.blpr.R

@Composable
fun LocationInBackgroundTutorialView(viewModel: GeoTuttiViewModel = viewModel()) {

    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value.projectName) {
        is ViewState.Loading -> LoadingView()
        is ViewState.Success -> GeoTuttiViewLoaded2(status = state.data)
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview()
@Composable
fun LocationInBackgroundTutorialViewDarkPreview() {
    MaterialTheme {
        Surface {
            GeoTuttiViewLoaded2(
                status = PermsStatus(
                    fineGpsPermission = true,
                    coarseGpsPermission = true,
                    backgroundGpsPermission = true
                ),
            )
        }
    }
}

fun getRequiredPermissionsForPreciseLocation(): List<String> =
    listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

fun getRequiredPermissionsForGeoFencing(): List<String> =
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
fun GeoTuttiViewLoaded2(status: PermsStatus) {
    val locationWhenUsingAppRationale = remember { mutableStateOf(false) }

    val hasFailed = remember { mutableStateOf(false) }

    val locationNotUpdatedError = remember { mutableStateOf(false) }

    val appName = getApplicationName(LocalContext.current)

    // Register the permission launcher
    val locationWhileUsingApppermissionLauncher =
        rememberLauncherForPermissionsResult(
            types = getRequiredPermissionsForPreciseLocation().toTypedArray(),
            onResult = { permissions ->
                // check if all permissions was approved
                if (permissions.values.none { b -> !b }) {
                    // all good!
                } else {
                    if (hasFailed.value) {
                        locationNotUpdatedError.value = true
                    } else {
                        hasFailed.value = true
                    }
                }
            }
        )

    // Register the background permission launcher
    val backgroundLocationPermissionsLauncher =
        rememberLauncherForPermissionsResult(
            types = getRequiredPermissionsForGeoFencing().toTypedArray(),
            onResult = { permissions ->
                // check if all permissions was approved
                if (permissions.values.none { b -> !b }) {
                    // all good!
                } else {
                    locationNotUpdatedError.value = true
                }
            }
        )

    Box {

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

            val whileUsingAppState = if (status.coarseGpsPermission && status.fineGpsPermission) {
                ListState.Complete
            } else {
                ListState.Enabled
            }

            EnableDisabledListItem(
                step = R.string.step1,
                description = R.string.permissions_while_using_the_app,
                listState = whileUsingAppState,
                onClick = {
                    locationWhileUsingApppermissionLauncher.safeLaunch(onRequireRational = {
                        locationWhenUsingAppRationale.value = true
                    })
                }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            val alwaysAppState =
                if (status.backgroundGpsPermission == true || status.backgroundGpsPermission == null) {
                    ListState.Complete
                } else if (whileUsingAppState == ListState.Complete) {
                    ListState.Enabled
                } else {
                    ListState.Disabled
                }

            EnableDisabledListItem(
                step = R.string.step2,
                description = R.string.allow_allways_location_permission,
                listState = alwaysAppState,
                onClick = {
                    backgroundLocationPermissionsLauncher.launch(null)
                }
            )
            Divider()
        }

        if (locationWhenUsingAppRationale.value) {

            CommonAlertDialog(
                title = R.string.permissions_while_using_the_app_rationale_title,
                text = R.string.permissions_while_using_the_app_rationale_description,
                confirmButton = R.string.continue_button,
                dismissButton = R.string.cancel,
                dismiss = {
                    locationWhenUsingAppRationale.value = false
                },
                confirm = {
                    locationWhileUsingApppermissionLauncher.launch(null)
                }
            )
        }

        if (locationNotUpdatedError.value) {
            val context = LocalContext.current
            CommonAlertDialog(
                title = R.string.permissions_error_title,
                text = R.string.permissions_error_description,
                confirmButton = R.string.open_settings,
                dismissButton = R.string.cancel,
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

private fun openSettingsForApp(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri =
        Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}

fun getApplicationName(context: Context): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
        stringId
    )
}
