

package se.warting.permissionsui.backgroundlocation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

public class RequestBackgroundLocationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RequestBackgroundLocationActivityMain {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    @Composable
    private fun RequestBackgroundLocationActivityMain(permissionsGranted: () -> Unit) {
        MaterialTheme {
            Scaffold(
                content = { innerPadding ->
                    LocationInBackgroundTutorialView(Modifier.padding(innerPadding)) {
                        permissionsGranted()
                    }
                }
            )
        }
    }
}
