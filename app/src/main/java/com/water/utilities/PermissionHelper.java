package com.water.utilities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;

public class PermissionHelper {
    private static final String TAG = "Permission Helper";

    public static int REQUESTCODE = 1;


    public static void checkPermission(@NonNull Object ctx, @NonNull String permission) {
        checkPermission(ctx, new String[]{permission});
    }

    public static void checkPermission(@NonNull Object ctx, @NonNull String[] permissions) {
        int granted = 0;

        try {
            for (String item :
                    permissions) {
                if (ctx instanceof Activity) {
                    granted += ActivityCompat.checkSelfPermission((Context) ctx, item);
                } else if (ctx instanceof Fragment) {
                    granted += ActivityCompat.checkSelfPermission(((Fragment) ctx).
                            getActivity(), item);
                }
            }

            if (granted != PackageManager.PERMISSION_GRANTED) {

                if (ctx instanceof Activity) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            (Activity) ctx,
                            permissions,
                            REQUESTCODE
                    );
                } else if (ctx instanceof Fragment) {
                    // We don't have permission so prompt the user
                    ((Fragment) ctx).requestPermissions(
                            permissions,
                            REQUESTCODE
                    );
                }

                Log.i(TAG, "修改权限成功！");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static void askForPermission(final @NonNull Context ctx, int requestCode,
                                        @NonNull String[] permissions) {
        final Activity activity = (Activity) ctx;
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getApplication().getPackageName())); // 根据包名打开对应的设置界面

        if (requestCode == PermissionHelper.REQUESTCODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean rp = true;
                for (String item :
                        permissions) {
                    rp = rp & activity.shouldShowRequestPermissionRationale(item);
                }

                if (!rp) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("需要权限");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.startActivity(intent);
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }
}