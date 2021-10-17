

package se.warting.backgroundlocationpermissionrationale

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import se.warting.permissionsui.backgroundlocation.PermissionsUiContracts

class ResultingActivity : AppCompatActivity() {

    var tv: TextView? = null
    private val fetchDataFromSecondActivity =
        registerForActivityResult(
            PermissionsUiContracts.RequestBackgroundLocation()
        ) { isGranted: Boolean ->
            tv?.text = getString(R.string.permissions_granted, isGranted.toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resulting)

        tv = findViewById(R.id.textViewStatus)

        findViewById<Button>(R.id.buttonRequestPermissions).setOnClickListener {
            fetchDataFromSecondActivity.launch(null)
        }
    }
}
