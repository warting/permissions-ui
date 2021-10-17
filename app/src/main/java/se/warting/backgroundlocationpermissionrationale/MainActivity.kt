

package se.warting.backgroundlocationpermissionrationale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import se.warting.backgroundlocationpermissionrationale.ui.theme.BackgroundLocationPermissionRationaleTheme
import se.warting.permissionsui.backgroundlocation.LocationInBackgroundTutorialView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
    BackgroundLocationPermissionRationaleTheme {
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(it) { data ->
                    Snackbar(
                        snackbarData = data
                    )
                }
            },
            content = { innerPadding ->
                LocationInBackgroundTutorialView(Modifier.padding(innerPadding)) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Permissions approved!")
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BackgroundLocationPermissionRationaleTheme {
        LocationInBackgroundTutorialView {
            // Permissions is approved
        }
    }
}
