package p.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 7539;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.permission_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWriteExternalPermission();
            }
        });
    }

    private void requestWriteExternalPermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showRationaleDialog(permission);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_CODE);
            }
        } else {
            showPermissionGranted();
        }
    }

    private void showRationaleDialog(String permission) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage("To access this functionality please grant permission to: \n" + permission)
                .setPositiveButton("OK", new StartSettingsClickListener(this))
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE && grantResults.length > 0) {
            int grantResult = grantResults[0];
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                showPermissionGranted();
            } else if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showPermissionGranted() {
        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
    }

    private static class StartSettingsClickListener implements DialogInterface.OnClickListener {

        private final Activity activity;

        public StartSettingsClickListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            activity.startActivityForResult(intent, MainActivity.PERMISSION_CODE);
        }
    }
}
