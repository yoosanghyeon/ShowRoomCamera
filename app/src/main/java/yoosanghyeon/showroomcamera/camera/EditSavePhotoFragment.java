package yoosanghyeon.showroomcamera.camera;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.security.Permission;
import java.util.ArrayList;

import yoosanghyeon.showroomcamera.R;
import yoosanghyeon.showroomcamera.constant.PermissionDefineConstant;
import yoosanghyeon.showroomcamera.helper.PermissionHelper;
import yoosanghyeon.showroomcamera.helper.PermissionListner;


/**
 *
 */
public class EditSavePhotoFragment extends Fragment {

    public static final String TAG = EditSavePhotoFragment.class.getSimpleName();
    public static final String BITMAP_KEY = "bitmap_byte_array";
    public static final String ROTATION_KEY = "rotation";
    public static final String IMAGE_INFO = "image_info";

    private static final int REQUEST_STORAGE = 1;

    public static Fragment newInstance(byte[] bitmapByteArray, int rotation,
                                       @NonNull ImageParameters parameters) {
        Fragment fragment = new EditSavePhotoFragment();

        Bundle args = new Bundle();
        args.putByteArray(BITMAP_KEY, bitmapByteArray);
        args.putInt(ROTATION_KEY, rotation);
        args.putParcelable(IMAGE_INFO, parameters);

        fragment.setArguments(args);
        return fragment;
    }

    public EditSavePhotoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.showroom_fragment_edit_save_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int rotation = getArguments().getInt(ROTATION_KEY);
        byte[] data = getArguments().getByteArray(BITMAP_KEY);
        ImageParameters imageParameters = getArguments().getParcelable(IMAGE_INFO);

        if (imageParameters == null) {
            return;
        }

        final ImageView photoImageView = (ImageView) view.findViewById(R.id.photo);

        imageParameters.mIsPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        final View topView = view.findViewById(R.id.topView);
        if (imageParameters.mIsPortrait) {
            topView.getLayoutParams().height = imageParameters.mCoverHeight;
        } else {
            topView.getLayoutParams().width = imageParameters.mCoverWidth;
        }

        rotatePicture(rotation, data, photoImageView);

        view.findViewById(R.id.save_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });

        view.findViewById(R.id.camera_save_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }


    private void rotatePicture(int rotation, byte[] data, ImageView photoImageView) {
        Bitmap bitmap = ImageUtility.decodeSampledBitmapFromByte(getActivity(), data);
//        Log.d(TAG, "original bitmap width " + bitmap.getWidth() + " height " + bitmap.getHeight());
        if (rotation != 0) {
            Bitmap oldBitmap = bitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            bitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
            );

            oldBitmap.recycle();
        }

        photoImageView.setImageBitmap(bitmap);
    }

    private PermissionHelper permissionHelper;
    private void savePicture() {




         permissionHelper = PermissionHelper.newInstance(((AppCompatActivity)getActivity()), new PermissionListner() {
            @Override
            public void onPermissionGranted() {
                final View view = getView();
                ImageView photoImageView = (ImageView) view.findViewById(R.id.photo);

                Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
                Uri photoUri = ImageUtility.savePicture(getActivity(), bitmap);

                ((CameraActivity) getActivity()).returnPhotoUri(photoUri);
            }

            @Override
            public void onPermissionDenied() {
                showDialogGuideForPermissionSettingGuide();
            }
        });

        permissionHelper.requestPermission(PermissionDefineConstant.STOREAGE_WRITE_PERMISSION , new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestForPermission() {
        RuntimePermissionActivity.startActivity(EditSavePhotoFragment.this,
                REQUEST_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode , permissions, grantResults);
        Log.e(TAG , "onRequestPermissionsResult excute");

    }

    private void showDialogGuideForPermissionSettingGuide() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("알림");
        builder.setMessage("권한을 허용해주세요.");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(appDetail);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
