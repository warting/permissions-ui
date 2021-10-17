

package se.warting.permissionsui.backgroundlocation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class PermissionsUiContracts {

    class RequestBackgroundLocation : ActivityResultContract<Unit?, Boolean>() {
        override fun createIntent(context: Context, input: Unit?) =
            Intent(context, RequestBackgroundLocationActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            if (resultCode != Activity.RESULT_OK) {
                return false
            }
            return true
        }
    }
}
