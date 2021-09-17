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
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val locationWhenUsingAppError = remember { mutableStateOf(false) }

    val locationInBackgrondError = remember { mutableStateOf(false) }


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
                    locationWhenUsingAppError.value = true
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
                    locationInBackgrondError.value = true
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

        if (locationWhenUsingAppError.value) {
            val context = LocalContext.current
            CommonAlertDialog(
                title = R.string.permissions_while_using_the_app_error_title,
                text = R.string.permissions_while_using_the_app_error_description,
                confirmButton = R.string.open_settings,
                dismissButton = R.string.cancel,
                dismiss = {
                    locationWhenUsingAppError.value = false
                },
                confirm = {
                    openSettingsForApp(context)
                }
            )
        }

        if (locationInBackgrondError.value) {
            val context = LocalContext.current
            CommonAlertDialog(
                title = R.string.permissions_in_background_error_title,
                text = R.string.permissions_in_background_error_description,
                confirmButton = R.string.open_settings,
                dismissButton = R.string.cancel,
                dismiss = {
                    locationInBackgrondError.value = false
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

@Composable
fun CommonAlertDialog(
    @StringRes title: Int,
    @StringRes text: Int,
    @StringRes confirmButton: Int,
    @StringRes dismissButton: Int,
    dismiss: () -> Unit,
    confirm: () -> Unit
) {
    val appName = getApplicationName(LocalContext.current)

    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            Text(text = stringResource(title, appName))
        },
        text = {
            Text(
                stringResource(text, appName)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm()
                    dismiss()
                }
            ) {
                Text(stringResource(confirmButton))
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismiss
            ) {
                Text(stringResource(dismissButton))
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnableDisabledListItem(
    @StringRes step: Int,
    @StringRes description: Int,
    listState: ListState,
    onClick: () -> Unit,
) {
    val iconColor = iconColorFromListState(listState)
    val alphaFromState = alphaFromListState(listState)

    val appName = getApplicationName(LocalContext.current)

    CompositionLocalProvider(LocalContentAlpha provides alphaFromState) {
        ListItem(
            modifier = if (listState == ListState.Enabled) {
                Modifier.clickable { onClick() }
            } else {
                Modifier
            }.padding(bottom = 8.dp),
            icon = {
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle, null,
                        modifier = Modifier,
                        tint = iconColor.copy(alpha = alphaFromState)
                    )
                }
            },
            overlineText = {
                CompositionLocalProvider(LocalContentAlpha provides alphaFromState) {
                    Text(stringResource(id = step))
                }
            },
            trailing = if (listState != ListState.Complete) {
                { Icon(Icons.Filled.ChevronRight, null) }
            } else {
                null
            },
            text = {
                CompositionLocalProvider(LocalContentAlpha provides alphaFromState) {
                    Text(stringResource(id = description, appName))
                }
            }
        )
    }
}

@Composable
private fun iconColorFromListState(listState: ListState) = when (listState) {
    ListState.Disabled -> MaterialTheme.colors.onBackground
    ListState.Enabled -> MaterialTheme.colors.onBackground
    ListState.Complete -> MaterialTheme.colors.secondaryVariant
}

@Composable
private fun alphaFromListState(listState: ListState) = when (listState) {
    ListState.Disabled -> ContentAlpha.disabled
    ListState.Enabled -> ContentAlpha.medium
    ListState.Complete -> ContentAlpha.high
}
