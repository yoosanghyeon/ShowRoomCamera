package yoosanghyeon.showroomcamera.helper;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
//import android.support.v4.app.AppCompatActivity;

import yoosanghyeon.showroomcamera.constant.PermissionDefineConstant;

public class PermissionHelper {

    private AppCompatActivity activity;
    private PermissionListner permissionListner;

    public PermissionHelper(AppCompatActivity activity, PermissionListner permissionListner) {
        this.activity = activity;
        this.permissionListner = permissionListner;
    }

    public static PermissionHelper newInstance(AppCompatActivity activity, PermissionListner permissionListner) {
        return new PermissionHelper(activity, permissionListner);
    }


    private boolean checkSelfPermission(@NonNull String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED;
    }

    public void requestPermission(@NonNull int requestCode, @NonNull String[] permissions, @NonNull String permission) {
        if (checkSelfPermission(permission)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            permissionListner.onPermissionGranted();
        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionDefineConstant.CAMERA_PERMISSION_REQUEST:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionListner.onPermissionGranted();
                } else {
                    permissionListner.onPermissionDenied();
                }
                break;
            case PermissionDefineConstant.STOREAGE_WRITE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionListner.onPermissionGranted();
                } else {
                    permissionListner.onPermissionDenied();
                }
                break;
        }
    }


}