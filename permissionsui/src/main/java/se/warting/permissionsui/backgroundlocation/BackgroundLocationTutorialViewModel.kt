

package se.warting.permissionsui.backgroundlocation

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

internal enum class ListState {
    Disabled,
    Enabled,
    EnabledRationale,
    Complete,
}

internal data class BackgroundLocationTutorialViewState(
    val projectName: ViewState<RequiredPermissions> = ViewState.Loading(),
)

internal sealed class BackgroundLocationTutorialViewEffect {
    class StatusUpdated(val statuses: RequiredPermissions) : BackgroundLocationTutorialViewEffect()
    object Loading : BackgroundLocationTutorialViewEffect()
}

internal data class RequiredPermissions(
    val permissionsNeededForFineLocation: List<String>,
    val fineGpsPermission: PermissionStatus,
    val coarseGpsPermission: PermissionStatus,
    val backgroundGpsPermission: PermissionStatus?
)

internal class BackgroundLocationTutorialViewModel : ViewModel(),
    StateViewModel<BackgroundLocationTutorialViewState, BackgroundLocationTutorialViewEffect> {

    private val _state =
        MutableStateFlow(BackgroundLocationTutorialViewState(projectName = ViewState.Loading()))

    override val uiState: StateFlow<BackgroundLocationTutorialViewState>
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
            ) { fineGpsPermission, coarseGpsPermission, backgroundGpsPermission ->
                RequiredPermissions(
                    permissionsNeededForFineLocation = listOf(
                        fineGpsPermission,
                        coarseGpsPermission
                    ).filter { !it.isGranted() }.map { it.type.name },
                    fineGpsPermission = fineGpsPermission,
                    coarseGpsPermission = coarseGpsPermission,
                    backgroundGpsPermission = backgroundGpsPermission,
                )
            }.collect {
                _state.value =
                    reduce(_state.value, BackgroundLocationTutorialViewEffect.StatusUpdated(it))
            }
        }
    }

    override fun reduce(
        oldViewState: BackgroundLocationTutorialViewState,
        viewEffect: BackgroundLocationTutorialViewEffect,
    ) = when (viewEffect) {
        is BackgroundLocationTutorialViewEffect.StatusUpdated -> oldViewState.copy(
            projectName = ViewState.Success(viewEffect.statuses),
        )
        BackgroundLocationTutorialViewEffect.Loading -> oldViewState.copy(projectName = ViewState.Loading())
    }
}
