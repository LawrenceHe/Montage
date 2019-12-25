package com.zhaodongdb.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionHelper {

    private static final String PERMISSIONSPNAME = "permission_config";
    private static final String PERMISSION_REQUEST_TAG = "CTPermissionHelper_RequestPermission";
    private static final int PERMISSION_REQUEST_CODE = 1992;

    public static class PermissionResult {
        public int grantResult;
        public boolean foreverDenied;

        public PermissionResult(int grantResult, boolean foreverDenied){
            this.grantResult = grantResult;
            this.foreverDenied = foreverDenied;
        }
    }

    /**
     * 权限回调
     * permission 权限名
     * granted 是否授权
     **/
    public interface PermissionCallback {
        void onPermissionCallback(String[] permissions, PermissionResult[] grantResults);
        void onPermissionsError(String errMsg,String[] permissions,PermissionResult[] grantResults);
    }

    public static class PermissionInnerFragment extends Fragment {

        PermissionCallback callback;
        FragmentActivity fragmentActivity;

        public void setPermissionCallback(PermissionCallback callback) {
            this.callback = callback;
        }

        public void setFragmentActivity(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            try {
                if (requestCode == PERMISSION_REQUEST_CODE && callback != null && permissions != null) {
                    boolean foreverDenied;
                    PermissionResult[] grantedStatus = new PermissionResult[permissions.length];
                    for(int i=0;i<permissions.length;i++){
                        foreverDenied = (grantResults[i] == PackageManager.PERMISSION_DENIED && !(ActivityCompat.shouldShowRequestPermissionRationale(fragmentActivity, permissions[i])));
                        grantedStatus[i] = new PermissionResult(grantResults[i], foreverDenied);
                    }
                    callback.onPermissionCallback(permissions, grantedStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, Object> ex = new HashMap<>();
                ex.put("status", "fragmentResult");
                ex.put("message", e.getMessage());
            }
        }

    }

    /**
     * 请求权限
     *
     * @param requestHost FragmentActivity (自定义 onRequestPermissionsResult 中需要调用 super)
     * @param permissions 需要请求的权限
     * @param isShowDialog 解释弹框，不再提示后,true弹出解释框, false 不弹解释框。
     * @param callback 权限回调
     */
    public static void requestPermissions(final Activity requestHost, final String[] permissions, final boolean isShowDialog, final PermissionCallback callback) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            doRequest(requestHost, permissions, isShowDialog, callback);
        } else {
            ThreadUtils.post(new Runnable() {
                @Override
                public void run() {
                    doRequest(requestHost, permissions, isShowDialog, callback);
                }
            });
        }
    }

    private static void checkPermission(final Activity requestHost, final String[] permissions, final PermissionCallback callback) {
        try{
            if (permissions == null || permissions.length == 0) {
                if(callback != null){
                    callback.onPermissionsError("permissions is null or length == 0",permissions,null);
                }
                return;
            }
            if (requestHost == null || requestHost.isDestroyed()) {
                if(callback != null){
                    callback.onPermissionsError("requestHost is null",permissions,null);
                }
                return;
            }
            if (!(requestHost instanceof FragmentActivity)) {
                if(callback != null){
                    callback.onPermissionsError("CTPermissionHelper need FragmentActivity",permissions,null);
                }
                throw new RuntimeException("CTPermissionHelper need FragmentActivity");
            }

            PermissionResult[] grantedStatus = new PermissionResult[permissions.length];

            for(int i=0;i<permissions.length;i++){
                if (PermissionChecker.checkSelfPermission(requestHost, permissions[i]) == PackageManager.PERMISSION_GRANTED) {
                    grantedStatus[i] = new PermissionResult(PackageManager.PERMISSION_GRANTED,false);
                } else {
                    grantedStatus[i] = new PermissionResult(PackageManager.PERMISSION_DENIED,false);
                }
            }
            if(callback != null){
                callback.onPermissionCallback(permissions, grantedStatus);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 只检查权限，不请求
     *
     * @param requestHost FragmentActivity (自定义 onRequestPermissionsResult 中需要调用 super)
     * @param permissions 需要请求的权限
     * @param callback 权限回调
     */
    public static void checkPermissions(final Activity requestHost, final String[] permissions, final PermissionCallback callback) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            checkPermission(requestHost, permissions, callback);
        } else {
            ThreadUtils.post(new Runnable() {
                @Override
                public void run() {
                    checkPermission(requestHost, permissions, callback);
                }
            });
        }
    }

    private static void doRequest(final Activity requestHost, final String[] permissions, boolean mIsShowDialog, final PermissionCallback callback) {
        try {
            if (permissions == null || permissions.length == 0) {
                if(callback != null){
                    callback.onPermissionsError("permissions is null or length == 0",permissions,null);
                }
                return;
            }
            if (requestHost == null || requestHost.isDestroyed()) {
                if(callback != null){
                    callback.onPermissionsError("requestHost is null",permissions,null);
                }
                return;
            }
            if (!(requestHost instanceof FragmentActivity)) {
                if(callback != null){
                    callback.onPermissionsError("CTPermissionHelper need FragmentActivity",permissions,null);
                }
                throw new RuntimeException("CTPermissionHelper need FragmentActivity");
            }

            PermissionUtils.sortGrantedAndDeniedPermissions(requestHost, permissions);
            if(PermissionUtils.getDeniedPermissions().size() < 1){
                if (callback != null && permissions != null) {
                    PermissionResult[] grantedStatus = new PermissionResult[permissions.length];
                    for(int i=0;i<permissions.length;i++){
                        grantedStatus[i] = new PermissionResult(PackageManager.PERMISSION_GRANTED, false);
                    }
                    callback.onPermissionCallback(permissions, grantedStatus);
                }
                return;
            }else{
                List<String> deniedPermissionsList = PermissionUtils.getDeniedPermissions();
                String[] deniedPermissionsArr = deniedPermissionsList.toArray(new String[deniedPermissionsList.size()]);
                if (deniedPermissionsArr.length > 0) {
                    PermissionUtils.sortUnshowPermission(requestHost, deniedPermissionsArr);
                }

                if (PermissionUtils.getUnshowedPermissions().size() > 0) {
                    List<String> unShowPermissionsList = PermissionUtils.getUnshowedPermissions();

                    //如果SharePreference中已存在该permission,说明不是首次检查,可弹出自定义弹框,否则首次只弹系统弹框,不弹自定义弹框
                    int len = PermissionUtils.getUnshowedPermissions().size();
                    boolean isCanShow=false;
                    for (int i=0;i<len;i++){
                        String permission=PermissionUtils.getUnshowedPermissions().get(i);
                        SharedPreferences settings = getSP();
                        if(settings != null && settings.contains(permission)){
                            isCanShow=true;
                        }
                    }

                    if(mIsShowDialog && isCanShow) {
                        StringBuilder message = getUnShowPermissionsMessage(unShowPermissionsList);
                        showMessageGotoSetting(message.toString(), requestHost);
                    }
                }
            }

            // 通过Fragment请求权限
            PermissionInnerFragment innerFragment = new PermissionInnerFragment();
            innerFragment.setPermissionCallback(callback);
            innerFragment.setFragmentActivity((FragmentActivity) requestHost);

            FragmentManager fragmentManager = ((FragmentActivity) requestHost).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(innerFragment, PERMISSION_REQUEST_TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
            innerFragment.requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            for (String permission : permissions) {
                SharedPreferences settings = getSP();
                if(settings != null){
                    settings.edit().putString(permission,"1").commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> ex = new HashMap<>();
            ex.put("status", "startRequest");
            ex.put("message", e.getMessage());
        }
    }

    private static StringBuilder getUnShowPermissionsMessage(List<String> list){
        StringBuilder message =  new StringBuilder("您已关闭了");
        String permisson;
        boolean hasCALENDAR = false;
        boolean hasCAMERA = false;
        boolean hasCONTACTS = false;
        boolean hasLOCATION = false;
        boolean hasMICROPHONE = false;
        boolean hasPHONE = false;
        boolean hasSENSORS = false;
        boolean hasSMS = false;
        boolean hasSTORAGE = false;

        if(list.size() == 1) {
            permisson = list.get(0);
            if(permisson.contains("CALENDAR")) {
                message.append("日历 ");
            } else if(permisson.contains("CAMERA")) {
                message.append("相机 ");

            } else if(permisson.contains("CONTACTS") || permisson.equals("android.permission.GET_ACCOUNTS")) {
                message.append("通讯录 ");

            } else if(permisson.contains("LOCATION")) {
                message.append("定位 ");

            } else if(permisson.equals("android.permission.RECORD_AUDIO")) {
                message.append("耳麦 ");

            } else if(permisson.contains("PHONE")
                    || permisson.contains("CALL_LOG")
                    || permisson.contains("ADD_VOICEMAIL")
                    || permisson.contains("USE_SIP")
                    || permisson.contains("PROCESS_OUTGOING_CALLS")) {
                message.append("电话 ");

            } else if(permisson.contains("BODY_SENSORS")) {
                message.append("身体传感 ");

            } else if(permisson.contains("SMS")
                    || permisson.contains("RECEIVE_WAP_PUSH")
                    || permisson.contains("RECEIVE_MMS")
                    || permisson.contains("READ_CELL_BROADCASTS")) {
                message.append("短信 ");

            } else if(permisson.contains("STORAGE")) {
                message.append("手机存储 ");

            }
        } else {
            for(int i = 0; i< list.size(); i++) {
                permisson = list.get(i);
                if(permisson.contains("CALENDAR") && hasCALENDAR == false) {
                    message.append("日历");
                    hasCALENDAR = true;
                } else if(permisson.contains("CAMERA") && hasCAMERA == false) {
                    message.append("相机");
                    hasCAMERA = true;
                } else if(permisson.contains("CONTACTS")
                        || permisson.equals("android.permission.GET_ACCOUNTS")
                        && hasCONTACTS == false) {
                    message.append("通讯录");
                    hasCONTACTS = true;
                } else if(permisson.contains("LOCATION")  && hasLOCATION == false) {
                    message.append("定位");
                    hasLOCATION = true;
                } else if(permisson.equals("android.permission.RECORD_AUDIO")  && hasMICROPHONE == false) {
                    message.append("耳麦");
                    hasMICROPHONE = true;
                } else if(permisson.contains("PHONE")
                        || permisson.contains("CALL_LOG")
                        || permisson.contains("ADD_VOICEMAIL")
                        || permisson.contains("USE_SIP")
                        || permisson.contains("PROCESS_OUTGOING_CALLS") && hasPHONE == false) {
                    message.append("电话");
                    hasPHONE = true;
                } else if(permisson.contains("BODY_SENSORS")  && hasSENSORS == false) {
                    message.append("身体传感");
                    hasSENSORS = true;
                } else if(permisson.contains("SMS")
                        || permisson.contains("RECEIVE_WAP_PUSH")
                        || permisson.contains("RECEIVE_MMS")
                        || permisson.contains("READ_CELL_BROADCASTS")  && hasSMS == false) {
                    message.append("短信");
                    hasSMS = true;
                } else if(permisson.contains("STORAGE")  && hasSTORAGE == false) {
                    message.append("手机存储");
                    hasSTORAGE = true;
                }
                if(i <  list.size() -1) {
                    message.append(",");
                }
            }
        }

        message.append("访问权限，为了保证功能的正常使用，请前往系统设置页面开启");
        return message;
    }

    private static void showMessageGotoSetting(final String message, final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PermissionConfig.instance().showPermissionDialog(message, act, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gotoPermissionSetting(act);
                    }
                });
            }
        });
    }

    private static void gotoPermissionSetting(Activity act) {
        Uri packageURI = Uri.parse("package:" + act.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        act.startActivity(intent);
    }

    private static SharedPreferences getSP(){
        SharedPreferences settings = FoundationContextHolder.getContext().getSharedPreferences(PERMISSIONSPNAME, Context.MODE_PRIVATE);
        return settings;
    }
}
