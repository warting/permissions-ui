package se.warting.permissionsui.backgroundlocation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun iconColorFromListState(listState: ListState) = when (listState) {
    ListState.Disabled -> MaterialTheme.colors.onBackground
    ListState.Enabled -> MaterialTheme.colors.onBackground
    ListState.EnabledRationale -> MaterialTheme.colors.onBackground
    ListState.Complete -> MaterialTheme.colors.secondaryVariant
}
