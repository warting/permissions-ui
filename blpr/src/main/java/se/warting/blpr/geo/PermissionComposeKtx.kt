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

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityOptionsCompat
import dev.marcelpinto.permissionktx.MultiplePermissionsLauncher
import dev.marcelpinto.permissionktx.Permission
import dev.marcelpinto.permissionktx.PermissionLauncher


/**
 * A version of [ActivityResultCaller.registerForActivityResult] to use in a composable, that
 * creates a [MultiplePermissionsLauncher] using the provided list of [Permission]s.
 *
 * @see PermissionLauncher
 * @see rememberLauncherForActivityResult
 * @see ActivityResultCaller.registerForActivityResult
 * @see ActivityResultContracts.RequestMultiplePermissions
 */
@Composable
fun rememberLauncherForPermissionsResult(
    types: Array<String>,
    onResult: (Map<Permission, Boolean>) -> Unit = {}
): MultiplePermissionsLauncher {
    val resultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { resultMap ->
            onResult(resultMap.mapKeys { Permission(it.key) })
        }
    )
    // Wrap the generic activity launcher into a permission launcher.
    return remember(resultLauncher) {
        MultiplePermissionsLauncher(
            types = types.map { Permission(it) },
            resultLauncher = object : ActivityResultLauncher<Unit>() {
                override fun launch(input: Unit?, options: ActivityOptionsCompat?) {
                    resultLauncher.launch(types, options)
                }

                override fun unregister() {
                    // Not required. Registration is automatically handled by rememberLauncherForActivityResult
                }

                override fun getContract(): ActivityResultContract<Unit, *> {

                    return object : ActivityResultContract<Unit, Unit>() {
                        override fun createIntent(context: Context, input: Unit?) =
                            resultLauncher.contract.createIntent(context, types)

                        override fun parseResult(resultCode: Int, intent: Intent?) {
                            resultLauncher.contract.parseResult(resultCode, intent)
                        }
                    }
                }
            }
        )
    }
}