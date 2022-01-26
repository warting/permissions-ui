package se.warting.backgroundlocationpermissionrationale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kotlin.Unit;
import se.warting.permissionsui.backgroundlocation.PermissionsUiContracts;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    ActivityResultLauncher<Unit> mGetContent = registerForActivityResult(
            new PermissionsUiContracts.RequestBackgroundLocation(),
            success -> ((TextView) (findViewById(R.id.textViewStatus)))
                    .setText(getString(R.string.permissions_granted, success.toString()))
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resulting);
        findViewById(R.id.buttonRequestPermissions)
                .setOnClickListener(view -> mGetContent.launch(null));

    }
}