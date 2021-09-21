package se.warting.permissionsui.backgroundlocation

import androidx.annotation.StringRes
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@SuppressWarnings("LongParameterList")
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
