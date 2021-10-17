

package se.warting.permissionsui.backgroundlocation

import androidx.compose.material.ContentAlpha
import androidx.compose.runtime.Composable

@Composable
internal fun alphaFromListState(listState: ListState) = when (listState) {
    ListState.Disabled -> ContentAlpha.disabled
    ListState.Enabled -> ContentAlpha.medium
    ListState.EnabledRationale -> ContentAlpha.medium
    ListState.Complete -> ContentAlpha.high
}
