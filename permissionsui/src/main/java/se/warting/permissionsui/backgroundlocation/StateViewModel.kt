

package se.warting.permissionsui.backgroundlocation

import kotlinx.coroutines.flow.StateFlow

internal interface StateViewModel<ViewState, ViewEffect> {
    val uiState: StateFlow<ViewState>
    fun reduce(
        oldViewState: ViewState,
        viewEffect: ViewEffect,
    ): ViewState
}
