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

package se.warting.permissionsui.backgroundlocation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun EnableDisabledListItem(
    @StringRes step: Int,
    description: String,
    rationale: String,
    listState: ListState,
    onClick: () -> Unit,
) {
    val iconColor = iconColorFromListState(listState)
    val alphaFromState = alphaFromListState(listState)

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
                { Text(rationale) }
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
                    Text(description)
                }
            }
        )
    }
}
