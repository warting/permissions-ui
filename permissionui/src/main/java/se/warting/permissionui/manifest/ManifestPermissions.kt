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

package se.warting.permissionui.manifest

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.marcelpibi.permissionktx.compose.rememberLauncherForPermissionResult
import dev.marcelpinto.permissionktx.Permission
import dev.marcelpinto.permissionktx.PermissionRational
import dev.marcelpinto.permissionktx.PermissionStatus
import kotlinx.coroutines.flow.map


@Composable
fun PermissionsInManifestView(
    modifier: Modifier = Modifier,
) {
    val info: PackageInfo = LocalContext.current.packageManager.getPackageInfo(
        LocalContext.current.packageName,
        PackageManager.GET_PERMISSIONS
    )
    val permissions = info.requestedPermissions


    PermissionsFromManifestLoadedView(
        modifier = modifier,
        permissions = permissions,
    )
}

sealed class PermissionS {
    data class Loading(val permission: String) : PermissionS()
    data class Loaded(val permissionStatus: PermissionStatus) : PermissionS()
}


@Composable
fun PermissionsFromManifestLoadedView(
    modifier: Modifier = Modifier,
    permissions: Array<String>,

    ) {
    LazyColumn(modifier) {
        items(permissions) { perm ->
            val permissionstate: State<PermissionS> =
                Permission(perm).statusFlow.map { PermissionS.Loaded(it) }
                    .collectAsState(initial = PermissionS.Loading(permission = perm))

            val rememberLauncherForPermissionLauncher =
                rememberLauncherForPermissionResult(
                    type = perm,
                    onResult = {

                    }
                )

            val showRationale = remember { mutableStateOf(false) }

            RenderList(permissionstate = permissionstate,
                showRationale = showRationale.value,
                safeLaunch = {
                    rememberLauncherForPermissionLauncher.safeLaunch(onRequireRational = {
                        showRationale.value = true
                    })
                },
                launch = {
                    rememberLauncherForPermissionLauncher.launch(null)
                })


        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RenderList(
    permissionstate: State<PermissionS>,
    showRationale: Boolean,
    safeLaunch: () -> Unit,
    launch: () -> Unit
) {


    return when (val state = permissionstate.value) {


        is PermissionS.Loading -> {
            ListItem(
                text = { Text(text = state.permission) }
            )
        }
        is PermissionS.Loaded -> {
            ListItem(
                modifier = Modifier.clickable {
                    when (val grantedState = state.permissionStatus) {
                        is PermissionStatus.Granted -> {

                        }
                        is PermissionStatus.Revoked -> {
                            when (val r = grantedState.rationale) {
                                PermissionRational.REQUIRED -> {
                                    launch()
                                }
                                PermissionRational.OPTIONAL -> {
                                    safeLaunch()
                                }
                                PermissionRational.UNDEFINED -> TODO()
                            }
                        }
                        else -> {
                            Log.d("", "wtf")
                        }
                    }
                },
                text = { Text(text = state.permissionStatus.type.name) },
                trailing = {


                    when (val grantedState = state.permissionStatus) {
                        is PermissionStatus.Granted -> {

                        }
                        is PermissionStatus.Revoked -> {
                            when (val r = grantedState.rationale) {
                                PermissionRational.REQUIRED -> {
                                    Text(text = "!!!")
                                }
                                PermissionRational.OPTIONAL -> {
                                    Text(text = "s")
                                }
                                PermissionRational.UNDEFINED -> {
                                    Text(text = "u")

                                }
                            }
                        }
                    }
                },
                secondaryText = { Text(text = "Granted: " + state.permissionStatus.isGranted()) },
            )
        }
    }
}

