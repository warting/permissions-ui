package se.warting.permissionsui.backgroundlocation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnableDisabledListItem(
    @StringRes step: Int,
    @StringRes description: Int,
    @StringRes rationale: Int,
    listState: ListState,
    onClick: () -> Unit,
) {
    val iconColor = iconColorFromListState(listState)
    val alphaFromState = alphaFromListState(listState)

    val appName = getApplicationName(LocalContext.current)

    CompositionLocalProvider(LocalContentAlpha provides alphaFromState) {
        ListItem(
            modifier = if (listState == ListState.Enabled || listState == ListState.EnabledRationale) {
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
            secondaryText = if (listState == ListState.EnabledRationale) {
                { Text(stringResource(id = rationale)) }
            } else {
                null
            },
            trailing = if (listState == ListState.Enabled || listState == ListState.EnabledRationale) {
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
    ListState.EnabledRationale -> MaterialTheme.colors.onBackground
    ListState.Complete -> MaterialTheme.colors.secondaryVariant
}

@Composable
private fun alphaFromListState(listState: ListState) = when (listState) {
    ListState.Disabled -> ContentAlpha.disabled
    ListState.Enabled -> ContentAlpha.medium
    ListState.EnabledRationale -> ContentAlpha.medium
    ListState.Complete -> ContentAlpha.high
}
