package se.warting.permissionsui.backgroundlocation

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@SuppressWarnings("LongParameterList")
@Composable
fun CommonAlertDialog(
    title: String,
    text: String,
    confirmButton: String,
    dismissButton: String,
    dismiss: () -> Unit,
    confirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            Text(title)
        },
        text = {
            Text(text)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm()
                    dismiss()
                }
            ) {
                Text(confirmButton)
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismiss
            ) {
                Text(dismissButton)
            }
        }
    )
}
