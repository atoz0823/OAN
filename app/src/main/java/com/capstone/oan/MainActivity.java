package com.capstone.oan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class MainActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private static final String TAG = MainActivity.class.getSimpleName();

    private Session mSession;
    private boolean mUserRequestInstall = true;


    @Override
    @SuppressWarnings({"AndroidApoChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!checkIsSupportedDeviceOrFinish(this)){
            return;
        }
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCameraPermission();

        try {
            if(mSession == null) {
                switch(ArCoreApk.getInstance().requestInstall(this, mUserRequestInstall)) {
                    case INSTALLED:
                        mSession = new Session(this);
                        Toast.makeText(this, "Session is created.", Toast.LENGTH_LONG).show();
                        break;

                    case INSTALL_REQUESTED:
                        mUserRequestInstall = false;
                        Toast.makeText(this, "Session creation is failed", Toast.LENGTH_LONG).show();
                        return;
                }
            }
        } catch(Exception e) {
            Toast.makeText(this, "TODO: Handle exception " + e, Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void requestCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    public static boolean checkIsSupportedDeviceOrFinish(final AppCompatActivity activity){
        // SDK 최소 버전을 만족하지 못하면 False Return
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later.", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }

        // OpenGL 최소 버전을 만족하지 못하면 False Return
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();

        if (Double.parseDouble(openGlVersionString) < 3.0) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            String versionText = "Device Supported OpenGL ES Version = " + openGlVersionString;
            //Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
            //        .show();
            Toast.makeText(activity, versionText, Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}