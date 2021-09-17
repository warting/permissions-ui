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
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.marcelpinto.permissionktx.Permission
import dev.marcelpinto.permissionktx.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

enum class ListState {
    Disabled,
    Enabled,
    Complete,
}

data class GeoTuttiViewState(
    val projectName: ViewState<PermsStatus> = ViewState.Loading(),
)

data class PermsStatus(
    val fineGpsPermission: Boolean,
    val coarseGpsPermission: Boolean,
    val backgroundGpsPermission: Boolean?
)

sealed class GeoTuttiViewEffect {
    class StatusUpdated(val statuses: Perms) : GeoTuttiViewEffect()
    object Loading : GeoTuttiViewEffect()
}

data class Perms(
    val fineGpsPermission: PermissionStatus,
    val coarseGpsPermission: PermissionStatus,
    val backgroundGpsPermission: PermissionStatus?
)

class GeoTuttiViewModel : ViewModel(), StateViewModel<GeoTuttiViewState, GeoTuttiViewEffect> {

    private val _state =
        MutableStateFlow(GeoTuttiViewState(projectName = ViewState.Loading()))

    override val uiState: StateFlow<GeoTuttiViewState>
        get() = _state

    private val accessFineLocationPermissionFlow =
        Permission(Manifest.permission.ACCESS_FINE_LOCATION).statusFlow
    private val accessCoarseLocationPermissionFlow =
        Permission(Manifest.permission.ACCESS_COARSE_LOCATION).statusFlow
    private val accessBackgroundLocationPermissionFlow =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Permission(Manifest.permission.ACCESS_BACKGROUND_LOCATION).statusFlow
        } else {
            flowOf(null)
        }

    init {
        viewModelScope.launch {

            combine(
                accessFineLocationPermissionFlow,
                accessCoarseLocationPermissionFlow,
                accessBackgroundLocationPermissionFlow
            ) { a, b, c ->
                Perms(a, b, c)
            }.collect {
                _state.value =
                    reduce(_state.value, GeoTuttiViewEffect.StatusUpdated(it))
            }
        }
    }

    private fun mapit(perms: Perms): PermsStatus {
        return PermsStatus(
            perms.fineGpsPermission.isGranted(),
            perms.coarseGpsPermission.isGranted(),
            perms.backgroundGpsPermission?.isGranted()
        )
    }

    override fun reduce(
        oldViewState: GeoTuttiViewState,
        viewEffect: GeoTuttiViewEffect,
    ) = when (viewEffect) {
        is GeoTuttiViewEffect.StatusUpdated -> oldViewState.copy(
            projectName = ViewState.Success(mapit(viewEffect.statuses)),
        )
        GeoTuttiViewEffect.Loading -> oldViewState.copy(projectName = ViewState.Loading())
    }
}
